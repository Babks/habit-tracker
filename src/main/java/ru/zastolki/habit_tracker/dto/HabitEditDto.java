package ru.zastolki.habit_tracker.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.zastolki.habit_tracker.enums.HabitFrequency;

@Data
public class HabitEditDto {
    @Size(min = 3, max = 100, message = "Habit name must be between 3 and 100 characters")
    private String name;

    @Size(max = 500, message = "Habit description can't be bigger than 500 symbols")
    private String description;

    private HabitFrequency frequency;

    private Boolean active;
}
