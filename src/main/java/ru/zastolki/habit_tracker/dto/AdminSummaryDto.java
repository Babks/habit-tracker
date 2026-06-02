package ru.zastolki.habit_tracker.dto;

public record AdminSummaryDto(
        long usersCount,
        long adminsCount,
        long habitsCount,
        long completedLogsCount
) {
}
