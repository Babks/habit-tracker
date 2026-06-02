package ru.zastolki.habit_tracker.dto;

public record UserStatsDto(
        Long userId,
        String username,
        Integer points,
        long habitsCount,
        long completedLogsCount
) {
}
