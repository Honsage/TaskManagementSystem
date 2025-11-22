package ru.honsage.practice.taskmanagementsystem.service;

import org.springframework.stereotype.Service;
import ru.honsage.practice.taskmanagementsystem.Task;
import ru.honsage.practice.taskmanagementsystem.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskService {
    private final AtomicLong idCounter;
    private final Map<Long, Task> taskMap;

    public TaskService() {
        idCounter = new AtomicLong();
        taskMap = new HashMap<>();
    }

    public List<Task> getAllTasks() {
        return taskMap.values().stream().toList();
    }

    public Task getTaskById(Long id) {
        if (!taskMap.containsKey(id)) {
            throw new NoSuchElementException(String.format("Task with id: %d is not found", id));
        }
        return taskMap.get(id);
    }

    public Task createTask(Task taskToCreate) {
        if (taskToCreate.id() != null) {
            throw new IllegalArgumentException("Id should be empty!");
        }
        if (taskToCreate.status() != null) {
            throw new IllegalArgumentException("Status should be empty!");
        }
        if (taskToCreate.createDateTime() != null) {
            throw new IllegalArgumentException("Creation DateTime should be empty!");
        }
        var newTask = new Task(
                idCounter.incrementAndGet(),
                taskToCreate.creatorId(),
                taskToCreate.assignedUserId(),
                TaskStatus.CREATED,
                LocalDateTime.now(),
                taskToCreate.deadlineDate(),
                taskToCreate.priority()
        );
        taskMap.put(newTask.id(), newTask);
        return newTask;
    }

    public Task updateTask(Long id, Task taskToUpdate) {
        if (!taskMap.containsKey(id)) {
            throw new NoSuchElementException(String.format("Task with id: %d is not found", id));
        }
        Task task = taskMap.get(id);
        if (task.status() == TaskStatus.DONE) {
            throw new IllegalStateException("Cannot modify task that is done!");
        }
        Task updatedTask = new Task(
                task.id(),
                taskToUpdate.creatorId(),
                taskToUpdate.assignedUserId(),
                TaskStatus.CREATED,
                taskToUpdate.createDateTime(),
                taskToUpdate.deadlineDate(),
                taskToUpdate.priority()
        );
        taskMap.put(task.id(), updatedTask);
        return updatedTask;
    }

    public void deleteTask(Long id) {
        if (!taskMap.containsKey(id)) {
            throw new NoSuchElementException(String.format("Task with id: %d is not found", id));
        }
        taskMap.remove(id);
    }

    public Task makeTaskProgress(Long id) {
        if (!taskMap.containsKey(id)) {
            throw new NoSuchElementException(String.format("Task with id: %d is not found", id));
        }
        Task task = taskMap.get(id);
        if (isTimeConflict(task)) {
            throw new IllegalStateException(String.format("Task with id: %d, status: %s is overdue!", id, task.status()));
        }
        Task progressedTask = new Task(
                task.id(),
                task.creatorId(),
                task.assignedUserId(),
                TaskStatus.IN_PROGRESS,
                task.createDateTime(),
                task.deadlineDate(),
                task.priority()
        );
        taskMap.put(task.id(), progressedTask);
        return progressedTask;
    }

    private boolean isTimeConflict(Task task) {
        return task.deadlineDate().isBefore(LocalDate.now());
    }
}
