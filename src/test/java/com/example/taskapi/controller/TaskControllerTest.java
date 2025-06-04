package com.example.taskapi.controller;

import com.example.taskapi.model.Status;
import com.example.taskapi.model.Task;
import com.example.taskapi.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    UUID sampleId = UUID.randomUUID();

    @Test
    void shouldCreateTask() throws Exception {
        Task mockTask = new Task(sampleId, "Test Task", "desc", Status.TODO);
        Mockito.when(taskService.create(any(Task.class))).thenReturn(mockTask);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "title": "Test Task",
                              "description": "desc",
                              "status": "TODO"
                            }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.status").value("TODO"));
    }

    @Test
    void shouldGetAllTasks() throws Exception {
        List<Task> tasks = List.of(
                new Task(sampleId, "Task 1", "desc", Status.TODO),
                new Task(UUID.randomUUID(), "Task 2", "desc2", Status.DONE)
        );
        Mockito.when(taskService.getAll()).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Task 1"));
    }

    @Test
    void shouldGetTaskById() throws Exception {
        Task task = new Task(sampleId, "Single Task", "", Status.IN_PROGRESS);
        Mockito.when(taskService.getById(sampleId)).thenReturn(task);

        mockMvc.perform(get("/api/tasks/" + sampleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Single Task"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void shouldUpdateTask() throws Exception {
        Task updated = new Task(sampleId, "Updated Task", "Updated Desc", Status.DONE);
        Mockito.when(taskService.update(eq(sampleId), any(Task.class))).thenReturn(updated);

        mockMvc.perform(put("/api/tasks/" + sampleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "title": "Updated Task",
                              "description": "Updated Desc",
                              "status": "DONE"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    void shouldDeleteTask() throws Exception {
        Mockito.doNothing().when(taskService).delete(sampleId);

        mockMvc.perform(delete("/api/tasks/" + sampleId))
                .andExpect(status().isNoContent());
    }
}
