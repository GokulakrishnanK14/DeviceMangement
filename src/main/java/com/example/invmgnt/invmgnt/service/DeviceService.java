package com.example.invmgnt.invmgnt.service;

import com.example.invmgnt.invmgnt.DTO.DeviceDTO;
import com.example.invmgnt.invmgnt.Exception.DeviceExceptions;
import com.example.invmgnt.invmgnt.Util.ExceptionUtil;
import com.example.invmgnt.invmgnt.model.Device;
import com.example.invmgnt.invmgnt.model.DeviceStatus;
import com.example.invmgnt.invmgnt.repository.DeviceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceService {
    private DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    boolean validateNewDevice(DeviceDTO deviceDTO) {
        int flag = 0;
        if (deviceDTO.getSerialNumber().equals("")) {
            flag++;
            deviceDTO.setSerialNumber(null);
        }
        if (deviceDTO.getAssetId().equals("")) {
            flag++;
            deviceDTO.setAssetId(null);
        }
        return flag != 2 ? true : false;
    }

    public boolean saveDevice(DeviceDTO deviceDTO) {
        try {
            if (!validateNewDevice(deviceDTO)) {
                throw new DeviceExceptions("Empty name or duplicate serial no. or asset id");
            }
            Device device = DeviceDTO.createDeviceFromDeviceDTO(deviceDTO);
            deviceRepository.save(device);
            return true;
        } catch (Exception e) {
            String msg = ExceptionUtil.constructExpMsg(e, e.getMessage());
            throw new DeviceExceptions(msg);
        }
    }

    public List<Device> getAllDevices() {
        try {
            List<Device> deviceList = deviceRepository.findAll();
            return deviceList;
        } catch (Exception e) {
            String msg = ExceptionUtil.constructExpMsg(e, "Fetching device form DB failed  " + e.getMessage());
            throw new DeviceExceptions(msg);
        }
    }

    public Device getDeviceById(Long id) {
        try {
            Optional<Device> optionalDevice = deviceRepository.findById(id);
            if (optionalDevice.isPresent())
                return optionalDevice.get();
            else
                throw new DeviceExceptions("No Device Found with ID " + id);
        } catch (Exception e) {
            throw new DeviceExceptions(ExceptionUtil.constructExpMsg(e, e.getMessage()));
        }
    }

    public boolean changeDeviceStatus(Long device_id, DeviceStatus status) {
        try {
            Optional<Device> optionalDevice = deviceRepository.findById(device_id);
            if (optionalDevice.isEmpty()) {
                throw new DeviceExceptions("Device Not Found with id " + device_id);
            }
            Device device = optionalDevice.get();
            device.setStatus(status);
            deviceRepository.save(device);
            return true;
        } catch (Exception e) {
            throw new DeviceExceptions(e.getMessage());
        }
    }

    public Device updateDeviceDetails(DeviceDTO deviceDTO) {
        System.out.println(deviceDTO.toString());
        try{
            Optional<Device> optionalDevice = deviceRepository.findById(deviceDTO.getId());
            if(optionalDevice.isEmpty())
                throw new DeviceExceptions("Not able to find device in DB ,so failed to update");
            Device device = optionalDevice.get();
            {
                device.setName(deviceDTO.getName());
                device.setDeviceType(deviceDTO.getDeviceType());
                device.setSerialNumber(deviceDTO.getSerialNumber());
                device.setAssetId(deviceDTO.getAssetId());
                device.setStatus(deviceDTO.getStatus());
                device.setDescription(deviceDTO.getDescription());
                device.setImgUrl(device.getImgUrl());
                device.setManufacturer(deviceDTO.getManufacturer());
                device.setDevicePhysicalLocation(deviceDTO.getDevicePhysicalLocation());
            }
            deviceRepository.save(device);
            return device;
        } catch (Exception e) {
            throw new DeviceExceptions(e.getMessage());
        }
    }

}
