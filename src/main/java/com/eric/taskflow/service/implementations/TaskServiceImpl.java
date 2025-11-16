package com.eric.taskflow.service.implementations;

import com.eric.taskflow.dto.task.CreateTaskRequest;
import com.eric.taskflow.dto.task.TaskResponse;
import com.eric.taskflow.dto.task.UpdateTaskRequest;
import com.eric.taskflow.exception.ApiException;
import com.eric.taskflow.mapper.TaskMapper;
import com.eric.taskflow.model.Priority;
import com.eric.taskflow.model.Role;
import com.eric.taskflow.model.Task;
import com.eric.taskflow.model.User;
import com.eric.taskflow.repository.TaskRepository;
import com.eric.taskflow.service.TaskService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional  // Las operaciones publicas seran transaccionales
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Override
    public TaskResponse createTask(CreateTaskRequest request, User currentUser) {

        // Validación de dueDate
        if (request.getDueDate() != null && request.getDueDate().isBefore(Instant.now())) {
            throw new ApiException("dueDate cannot be in the past");
        }

        // Obtener parentTask si se indicó
        Task parentTask = null;
        if (request.getParentTaskId() != null) {
            parentTask = taskRepository.findById(request.getParentTaskId())
                    .orElseThrow(() -> new ApiException("Parent task not found"));
        }

        // Construcción de la tarea
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority() != null ? request.getPriority() : Priority.MEDIUM)
                .dueDate(request.getDueDate())
                .owner(currentUser)
                .parentTask(parentTask)
                .build();

        taskRepository.save(task);

        return taskMapper.toTaskResponse(task);
    }

    @Override
    public TaskResponse getTask(Long id, User currentUser) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ApiException("Task not found"));

        if (!currentUser.getRole().equals(Role.ADMIN) && !task.getOwner().getId().equals(currentUser.getId())) {
            throw new ApiException("Access denied");
        }

        return taskMapper.toTaskResponse(task);
    }

    @Override
    public List<TaskResponse> getAllTasks(User currentUser) {
        if (currentUser.getRole().equals(Role.ADMIN)) {
            return taskRepository.findAll().stream()
                    .map(taskMapper::toTaskResponse)
                    .toList();
        } else {
            return taskRepository.findByOwnerId(currentUser.getId())
                    .stream()
                    .map(taskMapper::toTaskResponse)
                    .toList();
        }
    }

    @Override
    public TaskResponse updateTask(Long id, UpdateTaskRequest request, User currentUser) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ApiException("Task not found"));

        if (!currentUser.getRole().equals(Role.ADMIN) && !task.getOwner().getId().equals(currentUser.getId())) {
            throw new ApiException("Access denied");
        }

        // Actualizar campos simples
        taskMapper.updateTaskFromDto(request, task);

        // Validar y actualizar parentTask
        if (request.getParentTaskId() != null) {
            if (request.getParentTaskId().equals(task.getId())) {
                throw new ApiException("A task cannot be a subtask of itself");
            }

            Task parentTask = taskRepository.findById(request.getParentTaskId())
                    .orElseThrow(() -> new ApiException("Parent task not found"));

            // Validación para evitar ciclos
            Task current = parentTask;
            while (current != null) {
                if (current.getId().equals(task.getId())) {
                    throw new ApiException("Cannot create cyclic task hierarchy");
                }
                current = current.getParentTask();
            }

            task.setParentTask(parentTask);
        } else {
            task.setParentTask(null);
        }

        taskRepository.save(task);

        return taskMapper.toTaskResponse(task);
    }

    @Override
    public void deleteTask(Long id, User currentUser) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ApiException("Task not found"));

        // Admin puede borrar cualquier tarea, usuarios solo sus propias
        if (!currentUser.getRole().equals(Role.ADMIN)
                && !task.getOwner().getId().equals(currentUser.getId())) {
            throw new ApiException("Only admins or task owner can delete this task");
        }

        taskRepository.delete(task);
    }
}
