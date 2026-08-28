package ru.honsage.practice.taskmanagementsystem.controller;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        String message,
        String detailedMessage,
        LocalDateTime errorTime
) {}
