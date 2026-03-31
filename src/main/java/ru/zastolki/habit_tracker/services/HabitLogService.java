package ru.zastolki.habit_tracker.services;

import org.springframework.stereotype.Service;
import ru.zastolki.habit_tracker.entitys.Habit;
import ru.zastolki.habit_tracker.entitys.HabitLog;
import ru.zastolki.habit_tracker.repositories.HabitLogRepository;
import ru.zastolki.habit_tracker.repositories.HabitRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class HabitLogService {

    private final HabitLogRepository habitLogRepository;
    private final HabitRepository habitRepository;

    public HabitLogService(HabitLogRepository habitLogRepository,
                           HabitRepository habitRepository) {
        this.habitLogRepository = habitLogRepository;
        this.habitRepository = habitRepository;
    }

    public HabitLog markDone(Long habitId) {

        if (habitLogRepository
                .findByHabitIdAndDate(habitId, LocalDate.now())
                .isPresent()) {
            throw new RuntimeException("Already marked today");
        }

        Habit habit = habitRepository.findById(habitId)
                .orElseThrow();

        HabitLog log = new HabitLog();
        log.setHabit(habit);
        log.setDate(LocalDate.now());
        log.setCompleted(true);

        return habitLogRepository.save(log);
    }

    public List<HabitLog> getLogs(Long habitId) {
        return habitLogRepository.findByHabitId(habitId);
    }
}