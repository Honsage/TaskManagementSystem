package ru.honsage.practice.taskmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.honsage.practice.taskmanagementsystem.domain.TaskStatus;

import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    List<TaskEntity> findAllByAssignedUserIdAndStatus(Long assignedUserId, TaskStatus status);
}
