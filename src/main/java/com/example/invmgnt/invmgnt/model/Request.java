package com.example.invmgnt.invmgnt.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper=true)
@Table(name="requests")
public class Request extends Base{

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(nullable = false)
    LocalDateTime requestedDate;
    RequestStatus requestStatus;

    LocalDateTime deviceAssignedDate;
    LocalDateTime returnRequestedDate;
    LocalDateTime returnedDate;
    ReturnStatus returnStatus;

    Long requestedDays;
    String reason;

    @ManyToOne
    @JoinColumn(name = "reqProcessedBy")
    private User reqProcessedBy;

    @ManyToOne
    @JoinColumn(name = "retProcessedBy")
    private User retProcessedBy;
}
