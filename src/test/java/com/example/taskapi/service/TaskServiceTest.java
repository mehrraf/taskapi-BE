package com.example.taskapi.service;

import com.example.taskapi.exception.ResourceNotFoundException;
import com.example.taskapi.model.Status;
import com.example.taskapi.model.Task;
import com.example.taskapi.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskService service;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleTask = new Task();
        sampleTask.setId(UUID.randomUUID());
        sampleTask.setTitle("Test Task");
        sampleTask.setDescription("Test Desc");
        sampleTask.setStatus(Status.TODO);
    }

    @Test
    void testCreate() {
        when(repository.save(any(Task.class))).thenReturn(sampleTask);

        Task result = service.create(sampleTask);

        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
    }

    @Test
    void testGetAll() {
        when(repository.findAll()).thenReturn(List.of(sampleTask));

        List<Task> tasks = service.getAll();

        assertEquals(1, tasks.size());
        assertEquals("Test Task", tasks.get(0).getTitle());
    }

    @Test
    void testGetByIdSuccess() {
        when(repository.findById(sampleTask.getId())).thenReturn(Optional.of(sampleTask));

        Task result = service.getById(sampleTask.getId());

        assertNotNull(result);
        assertEquals(sampleTask.getTitle(), result.getTitle());
    }

    @Test
    void testGetByIdFailure() {
        UUID randomId = UUID.randomUUID();
        when(repository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(randomId));
    }

    @Test
    void testUpdateSuccess() {
        Task updated = new Task();
        updated.setTitle("Updated Task");
        updated.setDescription("Updated Desc");
        updated.setStatus(Status.IN_PROGRESS);

        when(repository.findById(sampleTask.getId())).thenReturn(Optional.of(sampleTask));
        when(repository.save(any(Task.class))).thenReturn(updated);

        Task result = service.update(sampleTask.getId(), updated);

        assertEquals("Updated Task", result.getTitle());
        assertEquals(Status.IN_PROGRESS, result.getStatus());
    }

    @Test
    void testUpdateFailure() {
        UUID missingId = UUID.randomUUID();
        when(repository.findById(missingId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(missingId, sampleTask));
    }

    @Test
    void testDeleteSuccess() {
        UUID id = sampleTask.getId();
        when(repository.existsById(id)).thenReturn(true);

        service.delete(id);

        verify(repository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteFailure() {
        UUID missingId = UUID.randomUUID();
        when(repository.existsById(missingId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(missingId));
    }
}
