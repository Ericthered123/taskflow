package com.eric.taskflow.dto.task;

import com.eric.taskflow.model.Priority;
import com.eric.taskflow.model.Task;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private Task.Status status;
    private Priority priority;
    private List<TaskResponse> subtasks;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant dueDate;
    private Long ownerId;
    private Long parentTaskId;
}
