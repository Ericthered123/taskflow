package com.eric.taskflow.service;

import com.eric.taskflow.dto.user.UpdateUserRequest;
import com.eric.taskflow.dto.user.UserResponse;
import com.eric.taskflow.model.User;

import java.util.List;

public interface UserService {


    UserResponse getById(Long id);

    List<UserResponse> getAll();

    UserResponse update(Long id, UpdateUserRequest request, User currentUser);

    void delete(Long id);


}
