package ru.honsage.practice.taskmanagementsystem.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.honsage.practice.taskmanagementsystem.domain.Task;
import ru.honsage.practice.taskmanagementsystem.domain.TaskPriority;
import ru.honsage.practice.taskmanagementsystem.domain.TaskStatus;
import ru.honsage.practice.taskmanagementsystem.repository.TaskEntity;
import ru.honsage.practice.taskmanagementsystem.repository.TaskMapper;
import ru.honsage.practice.taskmanagementsystem.repository.TaskRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    @Test
    void getTaskByIdTest() {
        Long taskId = 1L;
        LocalDateTime createdTime = LocalDateTime.now();
        LocalDate deadlineDate = LocalDate.now().plusDays(2);
        TaskEntity taskEntity = new TaskEntity(
                taskId,
                1L,
                1L,
                TaskStatus.CREATED,
                createdTime,
                deadlineDate,
                TaskPriority.HIGH
        );
        Task task = new Task(
                taskId,
                1L,
                1L,
                TaskStatus.CREATED,
                createdTime,
                deadlineDate,
                TaskPriority.HIGH
        );

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(taskMapper.toDomain(taskEntity)).thenReturn(task);

        Task result = taskService.getTaskById(taskId);

        assertEquals(result, task);
    }

}
