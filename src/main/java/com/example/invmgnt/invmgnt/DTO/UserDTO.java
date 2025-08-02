package com.example.invmgnt.invmgnt.DTO;

import com.example.invmgnt.invmgnt.model.User;
import com.example.invmgnt.invmgnt.model.UserStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {

    String name;
    String mail;
    String empId;
    UserStatus status;

    public  UserDTO(String name, String mail, String empId, UserStatus status){
        this.name = name;
        this.mail = mail;
        this.empId = empId;
        this.status = status;
    }

    public static  UserDTO UserEntityToDTO(User user){
        return new UserDTO(
                        user.getName(),
                        user.getMail(),
                        user.getEmpId(),
                        user.getStatus()    );
    }


}
