package com.eric.taskflow.service.implementations;

import com.eric.taskflow.dto.user.UpdateUserRequest;
import com.eric.taskflow.dto.user.UserResponse;
import com.eric.taskflow.exception.ApiException;
import com.eric.taskflow.mapper.UserMapper;
import com.eric.taskflow.model.Role;
import com.eric.taskflow.model.User;
import com.eric.taskflow.repository.UserRepository;
import com.eric.taskflow.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
/*Gestión de usuarios: obtener, listar, actualizar y eliminar.
* Usa UserMapper para mapear entidad ↔ DTO.
* Valida roles y campos antes de guardar.*/
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException("User not found"));
        return userMapper.toUserResponse(user);
    }


    @Override
    public List<UserResponse> getAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    @Override
    public UserResponse update(Long id, UpdateUserRequest request, User currentUser) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException("User not found"));

        // Si no es admin y trata de actualizar otro usuario → error
        if (currentUser.getRole() != Role.ADMIN && !currentUser.getId().equals(id)) {
            throw new ApiException("Access denied");
        }

        // Solo admins pueden cambiar role
        if (request.getRole() != null && currentUser.getRole() != Role.ADMIN) {
            throw new ApiException("Only admins can change roles");
        }

        // Usuarios normales solo pueden cambiar email/username de sí mismos
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ApiException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }

        // Admins sí pueden actualizar el rol
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    @Override
    public void delete(Long id) {

        if (!userRepository.existsById(id)) {
            throw new ApiException("User not found");
        }

        userRepository.deleteById(id);
    }
}
