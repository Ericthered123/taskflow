package com.eric.taskflow;

import com.eric.taskflow.dto.task.CreateTaskRequest;
import com.eric.taskflow.dto.task.TaskResponse;
import com.eric.taskflow.dto.task.UpdateTaskRequest;
import com.eric.taskflow.exception.ApiException;
import com.eric.taskflow.mapper.TaskMapper;
import com.eric.taskflow.model.*;
import com.eric.taskflow.repository.TaskRepository;
import com.eric.taskflow.service.implementations.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl service;

    private User user;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        user = User.builder().id(1L).role(Role.USER).build();
    }

    @Test
    void testGetTaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ApiException.class, () -> service.getTask(1L, user));
    }

    @Test
    void testCreateTask() {
        CreateTaskRequest req = new CreateTaskRequest();
        req.setTitle("New Task");

        Task saved = Task.builder()
                .id(1L)
                .title("New Task")
                .owner(user)
                .priority(Priority.MEDIUM)
                .createdAt(Instant.now())
                .build();

        when(taskRepository.save(any(Task.class))).thenReturn(saved);
        when(taskMapper.toTaskResponse(any(Task.class)))
                .thenReturn(TaskResponse.builder().id(1L).title("New Task").build());

        TaskResponse res = service.createTask(req, user);

        assertEquals("New Task", res.getTitle());
    }

    @Test
    void testUpdateThrowsIfNotOwner() {
        User other = User.builder().id(99L).role(Role.USER).build();

        Task task = Task.builder().id(1L).owner(other).build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(RuntimeException.class,
                () -> service.updateTask(1L, new UpdateTaskRequest(), user));
    }
}
