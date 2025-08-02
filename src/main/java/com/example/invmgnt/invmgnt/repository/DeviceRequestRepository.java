package com.example.invmgnt.invmgnt.repository;

import com.example.invmgnt.invmgnt.model.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceRequestRepository extends JpaRepository<Request,Long> {
    Optional<List<Request>> findByRequestStatus(RequestStatus status);
    Optional<Request> findById(Long id);
    Optional<List<Request>> findByUser(User user);
    Optional<List<Request>> ReturnStatus(ReturnStatus status);
    Optional<Request> findByUserAndDeviceAndRequestStatus(User user, Device device,RequestStatus requestStatus);
    Page<Request> findByUserAndRequestStatus(User user, RequestStatus requestStatus, Pageable pageable);
    Page<Request> findByUserAndRequestStatusAndReturnStatus(User user, RequestStatus requestStatus, ReturnStatus returnStatus,Pageable pageable);
    Page<Request> findByUserAndReturnStatus(User user,ReturnStatus returnStatus,Pageable pageable);
    Optional<List<Request>> findByDeviceAndRequestStatusAndReturnStatusNot(Device device,RequestStatus requestStatus, ReturnStatus returnStatus);
    Page<Request> findByRequestStatusAndReturnStatusNot(RequestStatus requestStatus, ReturnStatus returnStatus,Pageable pageable);
}
