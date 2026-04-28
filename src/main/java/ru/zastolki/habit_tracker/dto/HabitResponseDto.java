package ru.zastolki.habit_tracker.dto;

import lombok.Data;
import ru.zastolki.habit_tracker.enums.HabitFrequency;

import java.time.LocalDate;

@Data
public class HabitResponseDto {
    private Long id;
    private String name;
    private String description;
    private HabitFrequency frequency;
    private LocalDate createdAt;
    private Boolean active;
}
