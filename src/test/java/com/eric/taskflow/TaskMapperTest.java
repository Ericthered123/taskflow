package com.eric.taskflow;

import com.eric.taskflow.dto.task.TaskResponse;
import com.eric.taskflow.mapper.TaskMapper;
import com.eric.taskflow.model.Priority;
import com.eric.taskflow.model.Task;
import com.eric.taskflow.model.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class TaskMapperTest {

    private final TaskMapper mapper = Mappers.getMapper(TaskMapper.class);

    @Test
    void testEntityToResponse() {
        User owner = User.builder().id(10L).build();

        Task task = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Description")
                .priority(Priority.HIGH)
                .owner(owner)
                .createdAt(Instant.now())
                .subtasks(List.of())
                .build();

        TaskResponse response = mapper.toTaskResponse(task);

        assertEquals(task.getId(), response.getId());
        assertEquals(task.getTitle(), response.getTitle());
        assertEquals(task.getPriority(), response.getPriority());
        assertEquals(owner.getId(), response.getOwnerId());
    }

    @Test
    void testUpdateTaskFromDto() {
        Task task = Task.builder()
                .id(1L)
                .title("Old title")
                .description("Old desc")
                .priority(Priority.LOW)
                .build();

        var dto = new com.eric.taskflow.dto.task.UpdateTaskRequest();
        dto.setTitle("New title");
        dto.setDescription("New desc");
        dto.setPriority(Priority.HIGH);

        mapper.updateTaskFromDto(dto, task);

        assertEquals("New title", task.getTitle());
        assertEquals("New desc", task.getDescription());
        assertEquals(Priority.HIGH, task.getPriority());
    }

}
