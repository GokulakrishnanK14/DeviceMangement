package com.example.invmgnt.invmgnt.repository;

import com.example.invmgnt.invmgnt.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device,Long> {
    Optional<Device> findById(Long id);
}
