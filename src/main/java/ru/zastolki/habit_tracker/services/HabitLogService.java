package ru.zastolki.habit_tracker.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.zastolki.habit_tracker.entitys.Habit;
import ru.zastolki.habit_tracker.entitys.HabitLog;
import ru.zastolki.habit_tracker.repositories.HabitLogRepository;
import ru.zastolki.habit_tracker.repositories.HabitRepository;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Service
public class HabitLogService {

    private static final Logger log = LoggerFactory.getLogger(HabitLogService.class);

    private final HabitLogRepository habitLogRepository;
    private final HabitRepository habitRepository;

    public HabitLogService(HabitLogRepository habitLogRepository,
                           HabitRepository habitRepository) {
        this.habitLogRepository = habitLogRepository;
        this.habitRepository = habitRepository;
    }

    public HabitLog markDone(Long habitId) {
        var today = LocalDate.now();
        log.debug("Начата отметка выполнения привычки: идентификаторПривычки={} дата={}", habitId, today);

        if (habitLogRepository
                .findByHabitIdAndDate(habitId, today)
                .isPresent()) {
            log.info("Повторная отметка выполнения отклонена: идентификаторПривычки={} дата={}", habitId, today);
            throw new RuntimeException("Привычка уже отмечена сегодня");
        }

        Habit habit = habitRepository.findById(habitId)
                .orElseThrow();

        HabitLog habitLog = new HabitLog();
        habitLog.setHabit(habit);
        habitLog.setDate(today);
        habitLog.setCompleted(true);

        var savedLog = habitLogRepository.save(habitLog);
        log.info("Выполнение привычки отмечено: идентификаторПривычки={} идентификаторЗаписи={} дата={}",
                habitId,
                savedLog.getId(),
                savedLog.getDate());
        return savedLog;
    }

    public List<HabitLog> getLogs(Long habitId) {
        log.debug("Запрошен журнал выполнения привычки: идентификаторПривычки={}", habitId);

        var logs = habitLogRepository.findByHabitId(habitId);
        log.info("Журнал выполнения привычки получен: идентификаторПривычки={} количествоЗаписей={}",
                habitId,
                logs.size());
        return logs;
    }

    public int calculateStreak(Long habitId) {
        log.debug("Начат расчет стрика привычки: идентификаторПривычки={}", habitId);

        var completedDates = new HashSet<>(habitLogRepository.findCompletedDatesByHabitIdOrderByDateDesc(habitId));
        var currentDate = LocalDate.now();

        if (!completedDates.contains(currentDate)) {
            currentDate = currentDate.minusDays(1);
        }

        var streak = 0;
        while (completedDates.contains(currentDate)) {
            streak++;
            currentDate = currentDate.minusDays(1);
        }

        log.info("Стрик привычки рассчитан: идентификаторПривычки={} стрикВДнях={}", habitId, streak);
        return streak;
    }
}