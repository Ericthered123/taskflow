package com.eric.taskflow;
import com.eric.taskflow.model.Priority;
import com.eric.taskflow.model.Task;
import com.eric.taskflow.model.User;
import com.eric.taskflow.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository repository;

    @Test
    void testSaveAndFind() {
        User owner = User.builder().id(10L).build();

        Task task = Task.builder()
                .title("Repo Test")
                .priority(Priority.MEDIUM)
                .owner(owner)
                .createdAt(Instant.now())
                .build();

        Task saved = repository.save(task);

        assertNotNull(saved.getId());
        assertEquals("Repo Test", repository.findById(saved.getId()).get().getTitle());
    }

    @Test
    void testFindByOwnerId() {
        User owner = User.builder().id(5L).build();

        Task task = Task.builder()
                .title("Task 1")
                .priority(Priority.LOW)
                .owner(owner)
                .createdAt(Instant.now())
                .build();

        repository.save(task);

        List<Task> tasks = repository.findByOwnerId(5L);
        assertEquals(1, tasks.size());
    }
}
