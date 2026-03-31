package ru.zastolki.habit_tracker.entitys;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String frequency; // DAILY / WEEKLY

    private LocalDate createdAt;

    private Boolean active;
}