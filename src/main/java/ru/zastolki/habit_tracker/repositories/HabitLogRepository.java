package ru.zastolki.habit_tracker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zastolki.habit_tracker.entitys.HabitLog;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {

    List<HabitLog> findByHabitId(Long habitId);

    Optional<HabitLog> findByHabitIdAndDate(Long habitId, LocalDate date);
}