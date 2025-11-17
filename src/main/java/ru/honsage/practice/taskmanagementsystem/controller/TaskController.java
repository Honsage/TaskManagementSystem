package ru.honsage.practice.taskmanagementsystem.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.honsage.practice.taskmanagementsystem.Task;
import ru.honsage.practice.taskmanagementsystem.service.TaskService;

import java.util.List;

@RestController
public class TaskController {
    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/tasks")
    public List<Task> getAllTasks() {
        log.info("Method 'getAllTasks' is invoked");
        return taskService.getAllTasks();
    }

    @GetMapping("/tasks/{id}")
    public Task getTaskById(@PathVariable("id") Long id) {
        log.info("Method 'getTaskById' is invoked with id = {}", id);
        return taskService.getTaskById(id);
    }
}
