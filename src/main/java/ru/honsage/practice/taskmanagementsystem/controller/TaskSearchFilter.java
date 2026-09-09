package ru.honsage.practice.taskmanagementsystem.controller;

public record TaskSearchFilter(
        Long creatorId,
        Long assignedUserId,
        Integer pageSize,
        Integer pageNumber
) {
}
