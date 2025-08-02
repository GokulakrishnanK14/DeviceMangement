package com.example.invmgnt.invmgnt.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
@Table(name = "users")
public class User extends Base {
    @Column(nullable = false)
    private String name;
    @Column(unique = true)
    private String empId;
    @Column(nullable = false,unique = true)
    private String mail;
    @Column(nullable = false)
    private String password;
    private UserStatus status;
    private UserRole role = UserRole.STANDARD;
    @OneToMany(mappedBy = "user")
    private List<Request> requests;

}
