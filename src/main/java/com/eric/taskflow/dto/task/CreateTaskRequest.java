package com.eric.taskflow.dto.task;

import com.eric.taskflow.model.Priority;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.Instant;

@Data
public class CreateTaskRequest {
    @NotBlank
    private String title;
    private String description;
    private Long parentTaskId;
    private Priority priority;
    private Instant dueDate;
}