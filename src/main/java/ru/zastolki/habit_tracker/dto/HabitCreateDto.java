package ru.zastolki.habit_tracker.dto;

import lombok.Data;
import ru.zastolki.habit_tracker.enums.HabitFrequency;

@Data
public class HabitCreateDto {
    private String name;
    private String description;
    private HabitFrequency frequency;
}
