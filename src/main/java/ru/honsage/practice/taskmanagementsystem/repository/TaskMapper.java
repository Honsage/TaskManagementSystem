package ru.honsage.practice.taskmanagementsystem.repository;

import org.springframework.stereotype.Component;
import ru.honsage.practice.taskmanagementsystem.domain.Task;

@Component
public class TaskMapper {

    public Task toDomain(TaskEntity entity) {
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

    public TaskEntity toEntity(Task task) {
        return new TaskEntity(
                task.id(),
                task.creatorId(),
                task.assignedUserId(),
                task.status(),
                task.createDateTime(),
                task.deadlineDate(),
                task.priority()
        );
    }
}
