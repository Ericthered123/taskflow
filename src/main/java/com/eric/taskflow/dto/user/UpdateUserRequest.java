package com.eric.taskflow.dto.user;

import com.eric.taskflow.model.Role;
import lombok.Data;

@Data
public class UpdateUserRequest {

    private String username;
    private String email;
    // esto solo debería poder cambiarlo un ADMIN
    private Role role;
}
