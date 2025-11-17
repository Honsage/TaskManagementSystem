package ru.honsage.practice.taskmanagementsystem.service;

import org.springframework.stereotype.Service;
import ru.honsage.practice.taskmanagementsystem.Task;
import ru.honsage.practice.taskmanagementsystem.TaskPriority;
import ru.honsage.practice.taskmanagementsystem.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TaskService {
    private final Map<Long, Task> mockTaskMap = Map.of(
            1L, new Task(
                    1L,
                    1L,
                    2L,
                    TaskStatus.DONE,
                    LocalDateTime.of(2025, 11, 4, 16, 23),
                    LocalDate.now().minusDays(3),
                    TaskPriority.MEDIUM
            ),
            2L, new Task(
                    2L,
                    2L,
                    2L,
                    TaskStatus.IN_PROGRESS,
                    LocalDateTime.of(2025, 11, 16, 00, 23),
                    LocalDate.of(2025, 11, 27),
                    TaskPriority.HIGH
            ),
            3L, new Task(
                    3L,
                    1L,
                    1L,
                    TaskStatus.CREATED,
                    LocalDateTime.of(2025, 11, 17, 18, 14),
                    LocalDate.now().plusDays(1),
                    TaskPriority.LOW
            )
    );
}
