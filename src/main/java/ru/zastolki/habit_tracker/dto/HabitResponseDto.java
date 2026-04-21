package ru.zastolki.habit_tracker.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HabitResponseDto {
    private Long id;
    private String name;
    private String description;
    private String frequency;
    private LocalDate createdAt;
    private Boolean active;
}
