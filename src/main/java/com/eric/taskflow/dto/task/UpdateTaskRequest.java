package com.eric.taskflow.dto.task;

import com.eric.taskflow.model.Priority;
import com.eric.taskflow.model.Task;
import lombok.Data;

import java.time.Instant;

@Data
public class UpdateTaskRequest {
    private String title;
    private String description;
    private Task.Status status;
    private Instant dueDate;
    private Priority priority;
    private Long parentTaskId;
}
