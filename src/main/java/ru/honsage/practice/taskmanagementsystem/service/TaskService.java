package ru.honsage.practice.taskmanagementsystem.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.honsage.practice.taskmanagementsystem.controller.TaskSearchFilter;
import ru.honsage.practice.taskmanagementsystem.domain.Task;
import ru.honsage.practice.taskmanagementsystem.domain.TaskStatus;
import ru.honsage.practice.taskmanagementsystem.repository.TaskEntity;
import ru.honsage.practice.taskmanagementsystem.repository.TaskMapper;
import ru.honsage.practice.taskmanagementsystem.repository.TaskRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository repository;
    private final TaskMapper mapper;

    public TaskService(TaskRepository repository, TaskMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }


    public List<Task> searchAllByFilter(
            TaskSearchFilter filter
    ) {
        int pageSize = filter.pageSize();
        int pageNumber = filter.pageNumber();

        var pageable = Pageable.ofSize(pageSize).withPage(pageNumber);

        List<TaskEntity> allEntities = repository.searchAllByFilter(
                filter.creatorId(),
                filter.assignedUserId(),
                pageable
        );

        return allEntities.stream()
                .map(mapper::toDomain).toList();
    }

    public Task getTaskById(Long id) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Task with id: %d is not found", id)
                ));
        return mapper.toDomain(entity);
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

        var entityToSave = mapper.toEntity(taskToCreate);
        entityToSave.setStatus(TaskStatus.CREATED);
        entityToSave.setCreateDateTime(LocalDateTime.now());

        var savedEntity = repository.save(entityToSave);
        return mapper.toDomain(savedEntity);
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

        var entityToUpdate = mapper.toEntity(taskToUpdate);
        entityToUpdate.setId(taskEntity.getId());
        entityToUpdate.setStatus(TaskStatus.CREATED);
        entityToUpdate.setCreateDateTime(taskEntity.getCreateDateTime());

        var updatedEntity = repository.save(entityToUpdate);
        return mapper.toDomain(updatedEntity);
    }

    public void deleteTask(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException(String.format("Task with id: %d is not found", id));
        }
        repository.deleteById(id);
    }

    public Task makeTaskInProgress(Long id) {
        var taskEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Task with id: %d is not found", id)
                ));
        var assignedUserId = taskEntity.getAssignedUserId();
        if (assignedUserId == null) {
            throw new IllegalArgumentException(String.format("Task with id: %d has no assigned user", id));
        }
        var tasks = repository.findAllByAssignedUserIdAndStatus(assignedUserId, TaskStatus.IN_PROGRESS);
        if (tasks.size() > 4) {
            throw new IllegalStateException(
                    String.format("User with id: %d already has more than 4 tasks", assignedUserId)
            );
        }
        if (isTimeConflict(taskEntity)) {
            throw new IllegalStateException(
                    String.format("Task with id: %d, status: %s is overdue!", id, taskEntity.getStatus())
            );
        }
        taskEntity.setStatus(TaskStatus.IN_PROGRESS);
        var updatedEntity = repository.save(taskEntity);
        return mapper.toDomain(updatedEntity);
    }

    public Task makeTaskComplete(Long id) {
        var taskEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Task with id: %d is not found", id)
                ));
        var assignedUserId = taskEntity.getAssignedUserId();
        if (assignedUserId == null) {
            throw new IllegalArgumentException(String.format("Task with id: %d has no assigned user", id));
        }
        var deadlineDate = taskEntity.getDeadlineDate();
        if (deadlineDate == null) {
            throw new IllegalArgumentException(String.format("Task with id: %d has no deadline date", id));
        }

        taskEntity.setStatus(TaskStatus.DONE);
        var updatedEntity = repository.save(taskEntity);
        return mapper.toDomain(updatedEntity);
    }

    private boolean isTimeConflict(TaskEntity taskEntity) {
        return taskEntity.getDeadlineDate().isBefore(LocalDate.now());
    }
}
