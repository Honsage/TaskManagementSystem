package ru.honsage.practice.taskmanagementsystem.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.honsage.practice.taskmanagementsystem.Task;
import ru.honsage.practice.taskmanagementsystem.service.TaskService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        log.info("Method 'getAllTasks' is invoked");
        return ResponseEntity.status(HttpStatus.OK).body(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable("id") Long id) {
        log.info("Method 'getTaskById' is invoked with id = {}", id);
        try {
            return ResponseEntity.status(HttpStatus.OK).body(taskService.getTaskById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task taskToCreate) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(taskService.createTask(taskToCreate));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @PathVariable("id") Long id,
            @RequestBody Task taskToUpdate
    ) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(taskService.updateTask(id, taskToUpdate));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") Long id) {
        try {
            taskService.deleteTask(id);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/progress")
    public ResponseEntity<Task> makeTaskProgress(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(taskService.setTaskProgress(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
