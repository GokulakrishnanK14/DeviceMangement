package com.example.invmgnt.invmgnt.controller;

import com.example.invmgnt.invmgnt.DTO.DeviceDTO;
import com.example.invmgnt.invmgnt.SecurityService.CustomUserDetails;
import com.example.invmgnt.invmgnt.Util.AuthorizationUtil;
import com.example.invmgnt.invmgnt.Util.ExceptionUtil;
import com.example.invmgnt.invmgnt.model.*;
import com.example.invmgnt.invmgnt.service.DeviceRequestService;
import com.example.invmgnt.invmgnt.service.DeviceService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/devices")
public class DeviceController {
    private DeviceService deviceService;
    private DeviceRequestService deviceRequestService;

    public DeviceController(DeviceService deviceService,
                            DeviceRequestService deviceRequestService){
        this.deviceService = deviceService;
        this.deviceRequestService = deviceRequestService;
    }

    @GetMapping(value = "/addNew", produces = "text/html")
    public String showNewDeviceForm(Model model, @AuthenticationPrincipal CustomUserDetails currentUser){
        try{
            AuthorizationUtil.checkForAdminAccess(currentUser);
            model.addAttribute("deviceDTO", new DeviceDTO());
            return "NewDeviceForm";

        } catch (Exception e) {
            return ExceptionUtil.returnErrPage(model, e);
        }
    }

    @PostMapping(value = "/addNew")
    public String addNewDevice(@ModelAttribute("deviceDTO") DeviceDTO deviceDTO, @AuthenticationPrincipal CustomUserDetails currentUser, Model model){
        try {
            AuthorizationUtil.checkForAdminAccess(currentUser);
            deviceService.saveDevice(deviceDTO);
            return "redirect:/devices/allDevice";
        }catch (Exception e){
            return ExceptionUtil.returnErrPage(model,e);
        }
    }

    @GetMapping(value = "/allDevice")
    public String showAllDevice(Model model){
        try{
            List<Device> deviceList = deviceService.getAllDevices();

            Map<String, List<Device>> devicesByType = deviceList.stream()
                    .collect(Collectors.groupingBy(Device::getDeviceType));

            model.addAttribute("devicesByType", devicesByType);

            return "AllDeviceTableView";

        }catch (Exception e){
            model.addAttribute("errorMessage",e.getMessage());
            return "Error";
        }
    }

    @GetMapping(value = "/details")
    public String deviceDetails(@RequestParam("id") Long id,
                                @RequestParam("name") String name,
                                @RequestParam("serialNumber") String serialNumber,
                                @RequestParam("assetId") String assetId,
                                @RequestParam("status") DeviceStatus status,
                                @AuthenticationPrincipal CustomUserDetails currentUser,
                                Model model
                                ){
        try{
            model.addAttribute("id",id);
            model.addAttribute("name",name);
            model.addAttribute("serialNumber",serialNumber);
            model.addAttribute("assetId",assetId);
            model.addAttribute("status",status);

            if(currentUser.isAdmin()){
                model.addAttribute("isAdmin",true);
                if(status == DeviceStatus.ASSIGNED) {
                    model.addAttribute("isAssigned",true);
                    Request request =
                            deviceRequestService.getDeviceAssingedUserName(id);
                    model.addAttribute("assignedUserName",request.getUser().getName());
                    model.addAttribute("assignedUserMail", request.getUser().getMail());
                    model.addAttribute("devicePhysicalLocation",request.getDevice().getDevicePhysicalLocation());
                    //if more details need to be shown we can add heree
                }else {
                    Device device = deviceService.getDeviceById(id);
                    model.addAttribute("devicePhysicalLocation",device.getDevicePhysicalLocation());
                }
            }
            return "DeviceDetails";
        }catch (Exception e){
            return ExceptionUtil.returnErrPage(model,e);
        }
    }

    @GetMapping("/edit")
    public String showDeviceEditForm(@RequestParam("deviceId")Long device_id,
                                     @AuthenticationPrincipal CustomUserDetails currentUser,
                                     Model model){
        try{
            AuthorizationUtil.checkForAdminAccess(currentUser);
            Device device = deviceService.getDeviceById(device_id);
            model.addAttribute("device",device);
            return "DeviceEditForm";
        } catch (Exception e) {
            return ExceptionUtil.returnErrPage(model,e);
        }
    }

    /*
    Edit device submit button will hit here to update the  changes in DB
     */
    @PostMapping("/update")
    public String updateDeviceDetails(@ModelAttribute("DeviceDTO")DeviceDTO deviceDTO,
                                      @AuthenticationPrincipal CustomUserDetails currentUser,
                                      Model model) {
        try {
            Device resDevice = deviceService.updateDeviceDetails(deviceDTO);
            String url = "redirect:/devices/details?id=" + resDevice.getId()
                    +"&name="+ resDevice.getName()
                    +"&serialNumber="+resDevice.getSerialNumber()
                    +"&assetId="+resDevice.getAssetId()
                    +"&status="+resDevice.getStatus();

            return url;
        } catch (Exception e) {
            return ExceptionUtil.returnErrPage(model,e);
        }
    }
}
