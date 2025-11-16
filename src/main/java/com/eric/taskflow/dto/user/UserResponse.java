package com.eric.taskflow.dto.user;

import com.eric.taskflow.model.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private Role role;
}//No se devuelven psw
