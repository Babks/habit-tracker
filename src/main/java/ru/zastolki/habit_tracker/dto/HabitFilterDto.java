package ru.zastolki.habit_tracker.dto;

import ru.zastolki.habit_tracker.enums.HabitFrequency;

import java.time.LocalDate;

public record HabitFilterDto(
        HabitFrequency frequency,
        Boolean active,
        LocalDate createdFrom,
        LocalDate createdTo,
        String search,
        String username
) {
}
