package ru.zastolki.habit_tracker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.zastolki.habit_tracker.entitys.HabitLog;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {

    List<HabitLog> findByHabitId(Long habitId);

    Optional<HabitLog> findByHabitIdAndDate(Long habitId, LocalDate date);

    @Query("select log.date from HabitLog log where log.habit.id = :habitId and log.completed = true order by log.date desc")
    List<LocalDate> findCompletedDatesByHabitIdOrderByDateDesc(@Param("habitId") Long habitId);
}