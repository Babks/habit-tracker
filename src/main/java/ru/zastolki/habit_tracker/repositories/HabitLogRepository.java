package ru.zastolki.habit_tracker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.zastolki.habit_tracker.entitys.HabitLog;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {

    List<HabitLog> findByHabitIdAndHabitUserUsername(Long habitId, String username);

    Optional<HabitLog> findByHabitIdAndHabitUserUsernameAndDate(Long habitId, String username, LocalDate date);

    long countByHabitUserUsername(String username);

    @Query("""
            select log.date
            from HabitLog log
            where log.habit.id = :habitId
              and log.habit.user.username = :username
              and log.completed = true
            order by log.date desc
            """)
    List<LocalDate> findCompletedDatesByHabitIdAndUsernameOrderByDateDesc(
            @Param("habitId") Long habitId,
            @Param("username") String username);
}
