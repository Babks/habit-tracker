package ru.zastolki.habit_tracker.entitys;

import jakarta.persistence.*;
import lombok.Data;
import ru.zastolki.habit_tracker.enums.HabitFrequency;

import java.time.LocalDate;

@Entity
@Data
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private HabitFrequency frequency;

    private LocalDate createdAt;

    private Boolean active;
}