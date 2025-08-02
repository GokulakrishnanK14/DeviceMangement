package com.example.invmgnt.invmgnt.service;

import com.example.invmgnt.invmgnt.DTO.RequestFormDTO;
import com.example.invmgnt.invmgnt.Exception.RequestExceptions;
import com.example.invmgnt.invmgnt.Util.ExceptionUtil;
import com.example.invmgnt.invmgnt.model.*;
import com.example.invmgnt.invmgnt.repository.DeviceRequestRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DeviceRequestService {
    private DeviceRequestRepository deviceRequestRepository;
    private UserService userService;
    private DeviceService deviceService;

    public DeviceRequestService(DeviceRequestRepository deviceRequestRepository,
                                UserService userService,
                                DeviceService deviceService){
        this.deviceRequestRepository = deviceRequestRepository;
        this.userService = userService;
        this.deviceService = deviceService;
    }

    Device validateDevice(Long device_id){
        try{
            Device device = deviceService.getDeviceById(device_id);
            return device;
        } catch (Exception e) {
            throw e;
        }
    }
    User validateUser(Long user_id){
        try{
            User user = userService.getUserById(user_id);
            return user;
        }catch (Exception e){
            throw e;
        }
    }
    boolean checkForDuplicateOpenRequest(Long user_id,Long device_id){
        try{
            User user = validateUser(user_id);
            Device device = validateDevice(device_id);
            Optional<Request> optionalRequest =
                    deviceRequestRepository.findByUserAndDeviceAndRequestStatus(user,device,RequestStatus.PENDING);
            if(optionalRequest.isPresent())
                return  true;

            return false;
        } catch (Exception e) {
            throw e;
        }
    }
    public boolean processNewRequest(Long user_id, Long device_id, RequestFormDTO requestFormDTO){
        try{
            User user = validateUser(user_id);
            Device device = validateDevice(device_id);

            if(checkForDuplicateOpenRequest(user_id,device_id))
                throw new RequestExceptions("You already have open request for this same device");

            Request request = new Request();
            request.setDevice(device);
            request.setUser(user);
            request.setRequestedDate(LocalDateTime.now());
            request.setRequestStatus(RequestStatus.PENDING);
            request.setRequestedDays(requestFormDTO.getDays());
            request.setReason(requestFormDTO.getReason());
            deviceRequestRepository.save(request);

            return true;
        } catch (Exception e) {
            String msg = ExceptionUtil.constructExpMsg(e,e.getMessage());
            throw new RequestExceptions(msg);
        }
    }

    public List<Request> getAllOpenRequest(){
        try{
            Optional<List<Request>> optionalRequestList = deviceRequestRepository.findByRequestStatus(RequestStatus.PENDING);
            if(optionalRequestList.isPresent()){
                List<Request> reqList = optionalRequestList.get();
                reqList.sort((a, b) -> b.getRequestedDate().compareTo(a.getRequestedDate()));
                return reqList;
            }
            else
                return new ArrayList<Request>();
        } catch (Exception e) {
            String msg = ExceptionUtil.constructExpMsg(e,e.getMessage());
            throw new RequestExceptions(msg);
        }
    }

    public DeviceTransactionStatus updateRequestStatus(Long req_id, Long adminAction,Long adminUserId ){
        try{
            DeviceTransactionStatus ret = DeviceTransactionStatus.FAILURE;
            User admin_user = validateUser(adminUserId);
            Optional<Request> optionalRequest = deviceRequestRepository.findById(req_id);

            if(optionalRequest.isEmpty())
                throw  new RequestExceptions("can't find req with id: "+req_id+" to update status");

            Request request = optionalRequest.get();

            if(adminAction == 1 || adminAction == 2){
                if(request.getDevice().getStatus() != DeviceStatus.AVAILABLE)
                    return ret;

                if(adminAction == 1){
                    request.setRequestStatus(RequestStatus.APPROVED);
                    request.setDeviceAssignedDate(LocalDateTime.now());
                    request.setReturnStatus(ReturnStatus.OPEN);
                    deviceService.changeDeviceStatus(request.getDevice().getId(), DeviceStatus.ASSIGNED);
                    ret = DeviceTransactionStatus.DEVICE_ASSIGNED;
                }else{
                    request.setRequestStatus(RequestStatus.REJECTED);
                    ret = DeviceTransactionStatus.DEVICE_REJECTED;
                }
                request.setReqProcessedBy(admin_user);
            }else {
                if(request.getDevice().getStatus() != DeviceStatus.ASSIGNED ||
                            request.getReturnStatus() == ReturnStatus.CLOSE ){
                    return ret;
                }
                request.setRetProcessedBy(admin_user);
                request.setReturnedDate(LocalDateTime.now());
                request.getDevice().setStatus(DeviceStatus.AVAILABLE);
                request.setReturnStatus(ReturnStatus.CLOSE);
                ret = DeviceTransactionStatus.DEVICE_ACQUIRED;
            }
            deviceRequestRepository.save(request);

            return ret;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public List<Request> getRequestsOfUser(Long user_id){
        try{
            User user = validateUser(user_id);
            Optional<List<Request>> optionalRequestList = deviceRequestRepository.findByUser(user);
            if(optionalRequestList.isEmpty())
                return  new ArrayList<Request>();
            return optionalRequestList.get();
        } catch (Exception e) {
            throw new RequestExceptions(e.getMessage());
        }
    }

    public boolean updateReturnRequest(Long req_id){
        try{
            Optional<Request> optionalRequest = deviceRequestRepository.findById(req_id);
            if(optionalRequest.isEmpty())
                throw new RequestExceptions("Request ID "+req_id+" not found");

            Request request = optionalRequest.get();
            request.setReturnRequestedDate(LocalDateTime.now());
            request.setReturnStatus(ReturnStatus.RETURN_REQUESTED);
            deviceRequestRepository.save(request);

            return  true;
        } catch (Exception e) {
            throw new RequestExceptions(ExceptionUtil.constructExpMsg(e,e.getMessage()));
        }
    }

    public List<Request> getAllReturnRequest(){
        try{
            Optional<List<Request>> optionalRequestList =
                    deviceRequestRepository.ReturnStatus(ReturnStatus.RETURN_REQUESTED);

            if(optionalRequestList.isEmpty())
                return new ArrayList<Request>();
            return optionalRequestList.get();

        } catch (Exception e) {
            throw new RequestExceptions(ExceptionUtil.constructExpMsg(e,e.getMessage()));
        }
    }

    public Page<Request> getUserPendingRequest(Long userId,int page){
        try{
            User user = validateUser(userId);
            Pageable pageable = PageRequest.of(page,10, Sort.by(Sort.Direction.DESC,"requestedDate"));

            Page<Request> requestPage =
                    deviceRequestRepository.findByUserAndRequestStatus(user,RequestStatus.PENDING,pageable);
            return requestPage;

        } catch (Exception e) {
            throw new RequestExceptions(ExceptionUtil.constructExpMsg(e,e.getMessage()));
        }
    }


    public Page<Request> getDeviceAssignedRequestForUser(Long userId, int page){
        try{
            User user = validateUser(userId);
            Pageable pageable = PageRequest.of(page,10, Sort.by(Sort.Direction.DESC,"deviceAssignedDate"));
            Page<Request> requestPage =
                    deviceRequestRepository.findByUserAndRequestStatusAndReturnStatus(user,RequestStatus.APPROVED, ReturnStatus.OPEN,pageable);
            return requestPage;

        } catch (Exception e) {
            throw new RequestExceptions(ExceptionUtil.constructExpMsg(e,e.getMessage()));
        }
    }

    public Page<Request> getDeviceRejectedRequestForUser(Long userId, int page){
        try{
            User user = validateUser(userId);
            Pageable pageable = PageRequest.of(page,10, Sort.by(Sort.Direction.DESC,"requestedDate"));
            Page<Request> requestPage =
                    deviceRequestRepository.findByUserAndRequestStatus(user,RequestStatus.REJECTED,pageable);
            return requestPage;

        } catch (Exception e) {
            throw new RequestExceptions(ExceptionUtil.constructExpMsg(e,e.getMessage()));
        }
    }

    public Page<Request> getDeviceReturnedRequestForUser(Long userId, int page){
        try{
            User user = validateUser(userId);
            Pageable pageable = PageRequest.of(page,10, Sort.by(Sort.Direction.DESC,"returnedDate"));
            Page<Request> requestPage =
                        deviceRequestRepository.findByUserAndReturnStatus(user, ReturnStatus.CLOSE,pageable);
            return requestPage;
        } catch (Exception e) {
            throw new RequestExceptions(ExceptionUtil.constructExpMsg(e,e.getMessage()));
        }
    }
    public Page<Request> getOpenReturnRequestForUser(Long userId, int page){
        try{
            User user = validateUser(userId);
            Pageable pageable = PageRequest.of(page,10, Sort.by(Sort.Direction.DESC,"returnRequestedDate"));
            Page<Request> requestPage =
                    deviceRequestRepository.findByUserAndReturnStatus(user, ReturnStatus.RETURN_REQUESTED,pageable);
            return requestPage;
        } catch (Exception e) {
            throw new RequestExceptions(ExceptionUtil.constructExpMsg(e,e.getMessage()));
        }
    }

    public Request getDeviceAssingedUserName(Long device_id){
        try{
            Device device = validateDevice(device_id);
            if(device.getStatus() != DeviceStatus.ASSIGNED)
                throw new RequestExceptions(device.getName()+" not assigned to anyone");

            Optional<List<Request>> optionalRequestList =
                    deviceRequestRepository.findByDeviceAndRequestStatusAndReturnStatusNot(device,RequestStatus.APPROVED,ReturnStatus.CLOSE);
            if(optionalRequestList.isEmpty() || optionalRequestList.get().size()!=1) {
                System.out.println("Some issue with device assigning logic");
                throw new RequestExceptions(device.getName() + " not assigned to anyone");
            }

            if(optionalRequestList.get().size() == 1){
                Request request = optionalRequestList.get().get(0);
                return request;
            }
            return new Request();//Will not hit

        } catch (Exception e) {
            throw new RequestExceptions(ExceptionUtil.constructExpMsg(e,e.getMessage()));
        }
    }

    public Page<Request> getAllAssignedDevices(int page){
        try{
            Pageable pageable = PageRequest.of(page,10, Sort.by(Sort.Direction.DESC,"deviceAssignedDate"));
            Page<Request> assginedRequest =
                    deviceRequestRepository.findByRequestStatusAndReturnStatusNot(RequestStatus.APPROVED, ReturnStatus.CLOSE,pageable);
            return assginedRequest;
        } catch (Exception e) {
            throw new RequestExceptions(ExceptionUtil.constructExpMsg(e,e.getMessage()));
        }
    }

}
