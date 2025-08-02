package com.example.invmgnt.invmgnt.model;

public enum DeviceStatus {
    AVAILABLE("Available"),
    NOT_AVAILABLE("Not Available"),
    ASSIGNED("Assigned"),
    MAINTENANCE("Maintenance");

    private final String label;
    DeviceStatus(String label) { this.label = label; }
    public String label() { return label; }
}
