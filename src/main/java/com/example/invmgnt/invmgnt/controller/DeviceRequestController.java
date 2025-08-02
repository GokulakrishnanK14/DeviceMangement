package com.example.invmgnt.invmgnt.controller;

import com.example.invmgnt.invmgnt.DTO.RequestFormDTO;
import com.example.invmgnt.invmgnt.Exception.RequestExceptions;
import com.example.invmgnt.invmgnt.SecurityService.CustomUserDetails;
import com.example.invmgnt.invmgnt.Util.AuthorizationUtil;
import com.example.invmgnt.invmgnt.Util.ExceptionUtil;
import com.example.invmgnt.invmgnt.model.DeviceTransactionStatus;
import com.example.invmgnt.invmgnt.model.Request;
import com.example.invmgnt.invmgnt.model.User;
import com.example.invmgnt.invmgnt.repository.DeviceRequestRepository;
import com.example.invmgnt.invmgnt.service.DeviceRequestService;
import com.example.invmgnt.invmgnt.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/request")
public class DeviceRequestController {
    private DeviceRequestService deviceRequestService;
    private final DeviceRequestRepository deviceRequestRepository;
    private final UserService userService;
    public DeviceRequestController(DeviceRequestService deviceRequestService,
                                   DeviceRequestRepository deviceRequestRepository,
                                   UserService userService){
        this.deviceRequestService = deviceRequestService;
        this.deviceRequestRepository = deviceRequestRepository;
        this.userService = userService;
    }

    /*
    Single Device Request
     */
    @PostMapping(params = "deviceId", produces = "text/html")
    public String requestDevice(@RequestParam("deviceId") Long deviceId,
                                @AuthenticationPrincipal CustomUserDetails currentUser,
                                @ModelAttribute RequestFormDTO requestFormDTO,
                                Model model){
        try{
            Long userId = currentUser.getId();
            deviceRequestService.processNewRequest(userId,deviceId,requestFormDTO);
            return "redirect:/home";
        }catch (Exception e){
            return ExceptionUtil.returnErrPage(model,e);
        }
    }

    /*
    Admin page to approve or reject user requests
     */
    @GetMapping("/admin/handleRequest")
    public String viewAllOpenRequest(@AuthenticationPrincipal CustomUserDetails currentUser,
                                     @RequestParam(value = "result", required = false) String result,
                                     Model model){
        try{
            AuthorizationUtil.checkForAdminAccess(currentUser);
            List<Request> requestList = deviceRequestService.getAllOpenRequest();

            if(requestList.size()>0){
                model.addAttribute("requestList", requestList);
                model.addAttribute("reqAvailable",true);
            } else {
                model.addAttribute("reqAvailable",false);
            }
            if(result != null){
                model.addAttribute("result",result);
            }
            return "ShowOpenRequest";
        } catch (Exception e) {
            model.addAttribute("errorMessage",e.getMessage());
            return "Error";
        }
    }

    /*
    Process Approve or Reject
     */
    //It should be PostMapping since using button <a> not able to use Post
    @GetMapping(params = {"adminAction","reqId"})
    public String updateRequest(@AuthenticationPrincipal CustomUserDetails currentUser,
                                @RequestParam("adminAction") Long adminAction,
                                @RequestParam("reqId") Long reqId,
                                Model model){
        try{
            AuthorizationUtil.checkForAdminAccess(currentUser);

            Long userId = currentUser.getId();
            DeviceTransactionStatus status = deviceRequestService.updateRequestStatus(reqId,adminAction,userId);
            if(status == DeviceTransactionStatus.DEVICE_ASSIGNED){
                return "redirect:/request/admin/handleRequest?result=success";
            }
            else if(status == DeviceTransactionStatus.DEVICE_REJECTED){
                return  "redirect:/request/admin/handleRequest?result=rejected";
            }else if(adminAction <=2 && status == DeviceTransactionStatus.FAILURE){
                return "redirect:/request/admin/handleRequest?result=failed";
            }
            else if(status == DeviceTransactionStatus.DEVICE_ACQUIRED){
                return "redirect:/request/admin/handleReturn?result=success";
            }
            return "redirect:/home";
        } catch (Exception e) {
            return ExceptionUtil.returnErrPage(model,e);
        }
    }

    @GetMapping("/user/types")
    public String showRequestTypes(){
        try{
            return "RequestTypes";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/user/pending")
    public String showPendingRequest(@AuthenticationPrincipal CustomUserDetails currentUser,
                                     Model model,
                                     @RequestParam(value = "page", defaultValue = "0") int page
                                     ){
        try {
            Page<Request> pageRequest = deviceRequestService.getUserPendingRequest(currentUser.getId(),page);
            getListFromPageAndSetModelAttributes(pageRequest,model,page,"requests");
            return "UserPendingRequest";
        } catch (Exception e) {
            return ExceptionUtil.returnErrPage(model,e);
        }
    }

    @GetMapping("/user/assigned")
    public String showUserAssignedDevices(@AuthenticationPrincipal CustomUserDetails currentUser,
                                      Model model,
                                      @RequestParam(value = "page", defaultValue = "0") int page,
                                          @RequestParam(value = "userId", defaultValue = "-1") Long userId ){
        try{
            if(userId != -1){//If admin checking devices assigned for particular user from /user/admin/allUsers
                AuthorizationUtil.checkForAdminAccess(currentUser);
                model.addAttribute("disableReturn",true);
            }else{
                userId = currentUser.getId();
                model.addAttribute("disableReturn",false);
            }
            User user = userService.getUserById(userId);
            model.addAttribute("DeviceAssignedForUser",user.getName());

            Page<Request>  pageRequest = deviceRequestService.getDeviceAssignedRequestForUser(userId,page);
            getListFromPageAndSetModelAttributes(pageRequest,model,page,"assigedReq");
            return "UserAssignedRequest";

        } catch (Exception e) {
            return ExceptionUtil.returnErrPage(model,e);
        }
    }

    @GetMapping("/user/rejected")
    public String showUserRejectedDevice(@AuthenticationPrincipal CustomUserDetails currentUser,
                                         Model model,
                                         @RequestParam(value = "page", defaultValue = "0") int page){
        try{
            Page<Request> pageRequest =
                    deviceRequestService.getDeviceRejectedRequestForUser(currentUser.getId(),page);
            getListFromPageAndSetModelAttributes(pageRequest,model,page,"rejectedReqList");
            return "UserRejectedRequest";
        } catch (Exception e) {
            return ExceptionUtil.returnErrPage(model,e);
        }
    }

    @GetMapping("/user/openReturnRequest")
    public String showUserOpenReturnRequest(@AuthenticationPrincipal CustomUserDetails currentUser,
                                            Model model,
                                            @RequestParam(value = "page", defaultValue = "0") int page) {
        try{
            Page<Request> requestPage =
                    deviceRequestService.getOpenReturnRequestForUser(currentUser.getId(),page);
            getListFromPageAndSetModelAttributes(requestPage,model,page,"openReturnRequests");
            return  "UserOpenReturnRequest";

        } catch (Exception e) {
            return ExceptionUtil.returnErrPage(model,e);
        }
    }

    @GetMapping("/user/returned")
    public String showUserReturnedRequests(@AuthenticationPrincipal CustomUserDetails currentUser,
                                          Model model,
                                          @RequestParam(value = "page", defaultValue = "0") int page) {
        try{
            Page<Request> requestPage =
                    deviceRequestService.getDeviceReturnedRequestForUser(currentUser.getId(),page);
            getListFromPageAndSetModelAttributes(requestPage,model,page,"closedReqList");
            return "UserReturnedRequest";
        } catch (Exception e) {
            return ExceptionUtil.returnErrPage(model,e);
        }
    }
    @GetMapping("/user/return")
    public String updateReturnRequest(@RequestParam("reqId") Long reqId,Model model){
        try{
            deviceRequestService.updateReturnRequest(reqId);
            return "redirect:/request/user/assigned";
        } catch (Exception e) {
            return ExceptionUtil.returnErrPage(model,e);
        }
    }

    @GetMapping("/admin/handleReturn")
    public String viewAllReturnRequest(@AuthenticationPrincipal CustomUserDetails currentUser,
                                       @RequestParam(value = "result", required = false) String result,
                                       Model model){
        try{
            AuthorizationUtil.checkForAdminAccess(currentUser);
            List<Request> requestList = deviceRequestService.getAllReturnRequest();
            model.addAttribute("ReturnStatus",requestList);
            if(result != null){
                model.addAttribute("result",result);
            }
            return "ShowOpenReturnRequest";
        } catch (Exception e) {
            return ExceptionUtil.returnErrPage(model,e);
        }
    }

    @GetMapping("/admin/allAssignedDevice")
    public String showAllAssignedDevices(@AuthenticationPrincipal CustomUserDetails currentUser,
                                         @RequestParam(value = "page", defaultValue = "0") int page,
                                         Model model){
        try{
            AuthorizationUtil.checkForAdminAccess(currentUser);
            Page<Request> requestPage = deviceRequestService.getAllAssignedDevices(page);
            getListFromPageAndSetModelAttributes(requestPage,model,page,"activeApprovedReq");
            return  "AllAssignedDeviceView";
        } catch (Exception e) {
            return ExceptionUtil.returnErrPage(model,e);
        }
    }

    void getListFromPageAndSetModelAttributes(Page<Request> requestPage, Model model,int page,String reqListName){
        try{
            List<Request> requestList;
            if(requestPage.hasContent())
                requestList = requestPage.getContent();
            else
                requestList = new ArrayList<Request>();
            
            model.addAttribute("currentPage",page);
            model.addAttribute("totalPages", requestPage.getTotalPages());
            model.addAttribute(reqListName,requestList);
        } catch (Exception e) {
            throw new RequestExceptions(ExceptionUtil.constructExpMsg(e,"Processing paged result and adding into model failed"));
        }
    }
}
