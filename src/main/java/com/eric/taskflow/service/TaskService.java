package com.eric.taskflow.service;

import com.eric.taskflow.dto.task.CreateTaskRequest;
import com.eric.taskflow.dto.task.TaskResponse;
import com.eric.taskflow.dto.task.UpdateTaskRequest;
import com.eric.taskflow.model.User;

import java.util.List;

public interface TaskService {
    TaskResponse createTask(CreateTaskRequest request, User currentUser);
    TaskResponse getTask(Long id, User currentUser);
    List<TaskResponse> getAllTasks(User currentUser);
    TaskResponse updateTask(Long id, UpdateTaskRequest request, User currentUser);
    void deleteTask(Long id, User currentUser);
}
