package ru.zastolki.habit_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class HabitCreateDto {
    @NotNull
    @NotBlank(message = "Habit name can't be blank!")
    @Size(min = 3, max = 100, message = "Habit name must be between 3 and 100 characters")
    private String name;

    @Size(max = 500, message = "Habit description can't be bigger than 500 symbols")
    private String description;

    @NotNull(message = "Frequency can't be null")
    @NotBlank(message = "Frequency can't be blank!")
    @Pattern(regexp = "DAILY|WEEKLY")
    private String frequency;
}
