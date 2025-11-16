package com.eric.taskflow.mapper;

import com.eric.taskflow.dto.task.TaskResponse;
import com.eric.taskflow.dto.task.UpdateTaskRequest;
import com.eric.taskflow.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import com.eric.taskflow.dto.task.TaskResponse;
import com.eric.taskflow.dto.task.UpdateTaskRequest;
import com.eric.taskflow.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;

import java.util.List;
@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "subtasks", expression = "java(mapSubtasks(task.getSubtasks()))")
    @Mapping(target = "parentTaskId", source = "parentTask.id")
    TaskResponse toTaskResponse(Task task);

    void updateTaskFromDto(UpdateTaskRequest dto, @MappingTarget Task task);

    default List<TaskResponse> mapSubtasks(List<Task> subtasks) {
        if (subtasks == null) return null;
        return subtasks.stream()
                .map(this::toTaskResponse)
                .toList();
    }
}