package ru.zastolki.habit_tracker.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.zastolki.habit_tracker.entitys.Habit;
import ru.zastolki.habit_tracker.enums.HabitFrequency;
import ru.zastolki.habit_tracker.repositories.HabitRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class HabitService {

    private static final Logger log = LoggerFactory.getLogger(HabitService.class);

    private final HabitRepository habitRepository;

    public HabitService(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    public Habit create(Habit habit) {
        log.debug("Начато создание привычки: частота={}", habit.getFrequency());
        habit.setCreatedAt(LocalDate.now());
        habit.setActive(true);

        var savedHabit = habitRepository.save(habit);
        log.info("Привычка создана: идентификаторПривычки={} активна={}", savedHabit.getId(), savedHabit.getActive());
        return savedHabit;
    }

    public Habit edit(Long id, String name, String description, HabitFrequency frequency, Boolean active) {
        log.debug(
                "Начато изменение привычки: идентификаторПривычки={} переданоНазвание={} переданоОписание={} переданаЧастота={} переданСтатусАктивности={}",
                id,
                name != null,
                description != null,
                frequency != null,
                active != null);

        var habit = habitRepository.getReferenceById(id);
        if (name != null) {
            habit.setName(name);
        }

        if (description != null) {
            habit.setDescription(description);
        }

        if (frequency != null) {
            habit.setFrequency(frequency);
        }

        if (active != null) {
            habit.setActive(active);
        }

        var savedHabit = habitRepository.save(habit);
        log.info("Привычка изменена: идентификаторПривычки={} активна={} частота={}",
                savedHabit.getId(),
                savedHabit.getActive(),
                savedHabit.getFrequency());
        return savedHabit;
    }

    public Page<Habit> getAllPageable(Pageable pageable) {
        log.debug("Запрошена страница привычек: страница={} размер={} сортировка={}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        var habitsPage = habitRepository.findAll(pageable);
        log.info("Страница привычек получена: страница={} размер={} всегоЭлементов={}",
                habitsPage.getNumber(),
                habitsPage.getSize(),
                habitsPage.getTotalElements());
        return habitsPage;
    }

    public void delete(Long id) {
        log.debug("Начато удаление привычки: идентификаторПривычки={}", id);
        habitRepository.deleteById(id);
        log.info("Привычка удалена: идентификаторПривычки={}", id);
    }
}
