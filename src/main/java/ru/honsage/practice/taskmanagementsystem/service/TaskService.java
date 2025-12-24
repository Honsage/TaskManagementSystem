package ru.honsage.practice.taskmanagementsystem.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import ru.honsage.practice.taskmanagementsystem.domain.Task;
import ru.honsage.practice.taskmanagementsystem.domain.TaskStatus;
import ru.honsage.practice.taskmanagementsystem.repository.TaskEntity;
import ru.honsage.practice.taskmanagementsystem.repository.TaskRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskService {
    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public List<Task> getAllTasks() {
        List<TaskEntity> allEntities = repository.findAll();
        return allEntities.stream()
                .map(this::toDomainTask).toList();
    }

    public Task getTaskById(Long id) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Task with id: %d is not found", id)
                ));
        return toDomainTask(entity);
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
        var entityToSave = new TaskEntity(
                null,
                taskToCreate.creatorId(),
                taskToCreate.assignedUserId(),
                TaskStatus.CREATED,
                LocalDateTime.now(),
                taskToCreate.deadlineDate(),
                taskToCreate.priority()
        );
        var savedEntity = repository.save(entityToSave);
        return toDomainTask(savedEntity);
    }

    public Task updateTask(Long id, Task taskToUpdate) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException(String.format("Task with id: %d is not found", id));
        }
        var taskEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Task with id: %d is not found", id)
                ));

        if (taskEntity.getStatus() == TaskStatus.DONE) {
            throw new IllegalStateException("Cannot modify task that is done!");
        }
        if (taskToUpdate.createDateTime() != null) {
            throw new IllegalArgumentException("Creation DateTime should be empty!");
        }
        var entityToUpdate = new TaskEntity(
                taskEntity.getId(),
                taskToUpdate.creatorId(),
                taskToUpdate.assignedUserId(),
                TaskStatus.CREATED,
                taskEntity.getCreateDateTime(),
                taskToUpdate.deadlineDate(),
                taskToUpdate.priority()
        );
        var updatedEntity = repository.save(entityToUpdate);
        return toDomainTask(updatedEntity);
    }

    public void deleteTask(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException(String.format("Task with id: %d is not found", id));
        }
        repository.deleteById(id);
    }

    public Task makeTaskProgress(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException(String.format("Task with id: %d is not found", id));
        }
        var taskEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Task with id: %d is not found", id)
                ));
        if (isTimeConflict(taskEntity)) {
            throw new IllegalStateException(
                    String.format("Task with id: %d, status: %s is overdue!", id, taskEntity.getStatus())
            );
        }
        taskEntity.setStatus(TaskStatus.IN_PROGRESS);
        var updatedEntity = repository.save(taskEntity);
        return toDomainTask(updatedEntity);
    }

    private boolean isTimeConflict(TaskEntity taskEntity) {
        return taskEntity.getDeadlineDate().isBefore(LocalDate.now());
    }

    private Task toDomainTask(TaskEntity entity) {
        return new Task(
                entity.getId(),
                entity.getCreatorId(),
                entity.getAssignedUserId(),
                entity.getStatus(),
                entity.getCreateDateTime(),
                entity.getDeadlineDate(),
                entity.getPriority()
        );
    }
}
