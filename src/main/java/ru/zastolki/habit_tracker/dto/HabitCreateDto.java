package ru.zastolki.habit_tracker.dto;

import lombok.Data;

@Data
public class HabitCreateDto {
    private String name;
    private String description;
    private String frequency;
    private Boolean active;
}
