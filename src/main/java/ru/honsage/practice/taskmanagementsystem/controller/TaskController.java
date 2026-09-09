package ru.honsage.practice.taskmanagementsystem.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.honsage.practice.taskmanagementsystem.domain.Task;
import ru.honsage.practice.taskmanagementsystem.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(
            @RequestParam(name = "creatorId", required = false) Long creatorId,
            @RequestParam(name = "assignedUserId", required = false) Long assignedUserId,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "pageNumber", required = false) Integer pageNumber
    ) {
        log.info(
                "Method 'getAllTasks' is invoked with creatorId = {}, assignedUserId = {} and page: size = {}, number = {}",
                creatorId,
                assignedUserId,
                pageSize,
                pageNumber
        );
        var filter = new TaskSearchFilter(
                creatorId,
                assignedUserId,
                pageSize,
                pageNumber
        );

        return ResponseEntity.ok().body(taskService.searchAllByFilter(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable("id") Long id) {
        log.info("Method 'getTaskById' is invoked with id = {}", id);
        return ResponseEntity.ok().body(taskService.getTaskById(id));
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task taskToCreate) {
        log.info("Method 'createTask' is invoked with task = {}", taskToCreate);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(taskToCreate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @PathVariable("id") Long id,
            @RequestBody Task taskToUpdate
    ) {
        log.info("Method 'updateTask' is invoked with id = {}, task = {}", id, taskToUpdate);
        return ResponseEntity.ok().body(taskService.updateTask(id, taskToUpdate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") Long id) {
        log.info("Method 'deleteTask' is invoked with id = {}", id);
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<Task> startTask(@PathVariable("id") Long id) {
        log.info("Method 'makeTaskProgress' is invoked with id = {}", id);
        return ResponseEntity.ok(taskService.makeTaskInProgress(id));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<Task> completeTask(@PathVariable("id") Long id) {
        log.info("Method 'completeTask' is invoked with id = {}", id);
        return ResponseEntity.ok(taskService.makeTaskComplete(id));
    }
}
