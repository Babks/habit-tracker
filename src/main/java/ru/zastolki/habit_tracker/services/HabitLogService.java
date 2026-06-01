package ru.zastolki.habit_tracker.services;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.zastolki.habit_tracker.entitys.Habit;
import ru.zastolki.habit_tracker.entitys.HabitLog;
import ru.zastolki.habit_tracker.enums.HabitFrequency;
import ru.zastolki.habit_tracker.repositories.AppUserRepository;
import ru.zastolki.habit_tracker.repositories.HabitLogRepository;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Service
public class HabitLogService {

    private static final Logger log = LoggerFactory.getLogger(HabitLogService.class);

    private final HabitLogRepository habitLogRepository;
    private final HabitService habitService;
    private final AppUserRepository appUserRepository;

    public HabitLogService(
            HabitLogRepository habitLogRepository,
            HabitService habitService,
            AppUserRepository appUserRepository) {
        this.habitLogRepository = habitLogRepository;
        this.habitService = habitService;
        this.appUserRepository = appUserRepository;
    }

    @Transactional
    public HabitLog markDone(Long habitId, String username) {
        var today = LocalDate.now();
        log.debug("Начата отметка выполнения привычки: идентификаторПривычки={} имяПользователя={} дата={}",
                habitId,
                username,
                today);

        var habit = habitService.findOwnedHabit(habitId, username);

        if (habitLogRepository
                .findByHabitIdAndHabitUserUsernameAndDate(habitId, username, today)
                .isPresent()) {
            log.info("Повторная отметка выполнения отклонена: идентификаторПривычки={} имяПользователя={} дата={}",
                    habitId,
                    username,
                    today);
            throw new RuntimeException("Привычка уже отмечена сегодня");
        }

        var pointsAwarded = calculatePoints(habit);
        var habitLog = new HabitLog();
        habitLog.setHabit(habit);
        habitLog.setDate(today);
        habitLog.setCompleted(true);
        habitLog.setPointsAwarded(pointsAwarded);

        var user = habit.getUser();
        user.setPoints(user.getPoints() + pointsAwarded);
        appUserRepository.save(user);

        var savedLog = habitLogRepository.save(habitLog);
        log.info("Выполнение привычки отмечено: идентификаторПривычки={} имяПользователя={} идентификаторЗаписи={} дата={} начисленоБаллов={} всегоБаллов={}",
                habitId,
                username,
                savedLog.getId(),
                savedLog.getDate(),
                pointsAwarded,
                user.getPoints());
        return savedLog;
    }

    public List<HabitLog> getLogs(Long habitId, String username) {
        log.debug("Запрошен журнал выполнения привычки: идентификаторПривычки={} имяПользователя={}",
                habitId,
                username);

        habitService.findOwnedHabit(habitId, username);

        var logs = habitLogRepository.findByHabitIdAndHabitUserUsername(habitId, username);
        log.info("Журнал выполнения привычки получен: идентификаторПривычки={} имяПользователя={} количествоЗаписей={}",
                habitId,
                username,
                logs.size());
        return logs;
    }

    public int calculateStreak(Long habitId, String username) {
        log.debug("Начат расчет стрика привычки: идентификаторПривычки={} имяПользователя={}", habitId, username);

        habitService.findOwnedHabit(habitId, username);
        return calculateStreakInternal(habitId, username);
    }

    public int calculateStreakForAdmin(Long habitId, String username) {
        return calculateStreakInternal(habitId, username);
    }

    public long countAllLogs() {
        return habitLogRepository.count();
    }

    public long countUserLogs(String username) {
        return habitLogRepository.countByHabitUserUsername(username);
    }

    private int calculateStreakInternal(Long habitId, String username) {
        var completedDates = new HashSet<>(
                habitLogRepository.findCompletedDatesByHabitIdAndUsernameOrderByDateDesc(habitId, username));
        var currentDate = LocalDate.now();

        if (!completedDates.contains(currentDate)) {
            currentDate = currentDate.minusDays(1);
        }

        var streak = 0;
        while (completedDates.contains(currentDate)) {
            streak++;
            currentDate = currentDate.minusDays(1);
        }

        log.info("Стрик привычки рассчитан: идентификаторПривычки={} имяПользователя={} стрикВДнях={}",
                habitId,
                username,
                streak);
        return streak;
    }

    private int calculatePoints(Habit habit) {
        return habit.getFrequency() == HabitFrequency.WEEKLY ? 30 : 10;
    }
}
