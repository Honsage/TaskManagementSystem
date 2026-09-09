package ru.honsage.practice.taskmanagementsystem.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.honsage.practice.taskmanagementsystem.domain.TaskStatus;

import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    @Query("""
           select count(t) from TaskEntity t
           where t.assignedUserId = :assignedUserId
           and t.status = :status                                                 
           """)
    Long countOfTasksByAssignedUserIdAndStatus(
            @Param("assignedUserId") Long assignedUserId,
            @Param("status") TaskStatus status
    );

    @Query("""
           select t.id from TaskEntity t
           where (:creatorId is null or t.creatorId = :creatorId)
           and (:assignedUserId is null or t.assignedUserId = :assignedUserId)
           """)
    List<TaskEntity> searchAllByFilter(
            @Param("creatorId") Long creatorId,
            @Param("assignedUserId") Long assignedUserId,
            Pageable pageable
    );
}
