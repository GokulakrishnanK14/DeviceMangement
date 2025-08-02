package com.example.invmgnt.invmgnt.DTO;

import com.example.invmgnt.invmgnt.model.Device;
import com.example.invmgnt.invmgnt.model.DeviceStatus;
import lombok.Data;

@Data
public class DeviceDTO {
    Long id;
    String name;
    String deviceType;
    String serialNumber;
    String assetId;
    String description;
    String manufacturer;
    DeviceStatus status;
    String imgUrl;
    String devicePhysicalLocation;

    public static Device createDeviceFromDeviceDTO(DeviceDTO deviceDTO){
        Device device = new Device();
        device.setName(deviceDTO.getName());
        device.setDeviceType(deviceDTO.getDeviceType());
        device.setSerialNumber(deviceDTO.getSerialNumber());
        device.setAssetId(deviceDTO.getAssetId());
        device.setManufacturer(deviceDTO.getManufacturer());
        device.setStatus(DeviceStatus.AVAILABLE);
        device.setDescription(deviceDTO.getDescription());
        device.setImgUrl(deviceDTO.getImgUrl());
        device.setDevicePhysicalLocation(deviceDTO.getDevicePhysicalLocation());
        return device;
    }
}
