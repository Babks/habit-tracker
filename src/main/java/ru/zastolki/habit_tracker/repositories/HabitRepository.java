package ru.zastolki.habit_tracker.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.zastolki.habit_tracker.entitys.Habit;

import java.util.Optional;

public interface HabitRepository extends JpaRepository<Habit, Long>, JpaSpecificationExecutor<Habit> {

    Page<Habit> findAllByUserUsername(String username, Pageable pageable);

    Optional<Habit> findByIdAndUserUsername(Long id, String username);

    long countByUserUsername(String username);
}
