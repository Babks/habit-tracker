package ru.zastolki.habit_tracker.dto;

import ru.zastolki.habit_tracker.enums.UserRole;

public record UserResponseDto(
        Long id,
        String username,
        UserRole role,
        Integer points
) {
}
