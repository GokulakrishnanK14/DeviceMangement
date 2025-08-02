package com.example.invmgnt.invmgnt.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@Entity
@Data
@EqualsAndHashCode(callSuper=true)
@Table(name ="devices")
public class Device extends Base{
    @Column(nullable = false)
    String name;
    String deviceType;
    @Column(unique = true)
    String serialNumber;
    @Column(unique = true)
    String assetId;
    @Column(nullable = false)
    DeviceStatus status;
    String description;
    String manufacturer;
    String imgUrl;
    @OneToMany(mappedBy = "device")
    private List<Request> requests;
    String devicePhysicalLocation;

}
