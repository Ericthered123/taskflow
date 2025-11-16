package com.eric.taskflow.mapper;

import com.eric.taskflow.dto.user.UpdateUserRequest;
import com.eric.taskflow.dto.user.UserResponse;
import com.eric.taskflow.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Entidad to DTO
    UserResponse toUserResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UpdateUserRequest dto, @MappingTarget User user); //Actualizar usuarios
}
