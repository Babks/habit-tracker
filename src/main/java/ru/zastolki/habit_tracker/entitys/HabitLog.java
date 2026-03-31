package ru.zastolki.habit_tracker.entitys;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class HabitLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private Boolean completed;

    @ManyToOne
    @JoinColumn(name = "habit_id")
    private Habit habit;
}