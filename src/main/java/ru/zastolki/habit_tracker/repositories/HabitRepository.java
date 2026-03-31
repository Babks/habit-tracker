package ru.zastolki.habit_tracker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zastolki.habit_tracker.entitys.Habit;

public interface HabitRepository extends JpaRepository<Habit, Long> {
}