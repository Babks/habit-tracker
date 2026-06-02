package ru.zastolki.habit_tracker.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.zastolki.habit_tracker.dto.HabitFilterDto;
import ru.zastolki.habit_tracker.entitys.Habit;
import ru.zastolki.habit_tracker.enums.HabitFrequency;
import ru.zastolki.habit_tracker.repositories.AppUserRepository;
import ru.zastolki.habit_tracker.repositories.HabitRepository;
import ru.zastolki.habit_tracker.repositories.HabitSpecifications;

import java.time.LocalDate;

@Service
public class HabitService {

    private static final Logger log = LoggerFactory.getLogger(HabitService.class);

    private final HabitRepository habitRepository;
    private final AppUserRepository appUserRepository;

    public HabitService(HabitRepository habitRepository, AppUserRepository appUserRepository) {
        this.habitRepository = habitRepository;
        this.appUserRepository = appUserRepository;
    }

    public Habit create(Habit habit, String username) {
        log.debug("Начато создание привычки: имяПользователя={} частота={}", username, habit.getFrequency());

        var user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Пользователь не найден"));

        habit.setUser(user);
        habit.setCreatedAt(LocalDate.now());
        habit.setActive(true);

        var savedHabit = habitRepository.save(habit);
        log.info("Привычка создана: идентификаторПривычки={} имяПользователя={} активна={}",
                savedHabit.getId(),
                username,
                savedHabit.getActive());
        return savedHabit;
    }

    public Habit edit(Long id, String username, String name, String description, HabitFrequency frequency, Boolean active) {
        log.debug(
                "Начато изменение привычки: идентификаторПривычки={} имяПользователя={} переданоНазвание={} переданоОписание={} переданаЧастота={} переданСтатусАктивности={}",
                id,
                username,
                name != null,
                description != null,
                frequency != null,
                active != null);

        var habit = findOwnedHabit(id, username);
        updateHabitFields(habit, name, description, frequency, active);

        var savedHabit = habitRepository.save(habit);
        log.info("Привычка изменена: идентификаторПривычки={} имяПользователя={} активна={} частота={}",
                savedHabit.getId(),
                username,
                savedHabit.getActive(),
                savedHabit.getFrequency());
        return savedHabit;
    }

    public Habit editAny(Long id, String name, String description, HabitFrequency frequency, Boolean active) {
        log.debug(
                "Администратор начал изменение привычки: идентификаторПривычки={} переданоНазвание={} переданоОписание={} переданаЧастота={} переданСтатусАктивности={}",
                id,
                name != null,
                description != null,
                frequency != null,
                active != null);

        var habit = habitRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Привычка не найдена"));
        updateHabitFields(habit, name, description, frequency, active);

        var savedHabit = habitRepository.save(habit);
        log.info("Администратор изменил привычку: идентификаторПривычки={} владелец={}",
                savedHabit.getId(),
                savedHabit.getUser().getUsername());
        return savedHabit;
    }

    public Page<Habit> getAllPageable(String username, HabitFilterDto filter, Pageable pageable) {
        log.debug("Запрошена страница привычек пользователя: имяПользователя={} страница={} размер={} сортировка={}",
                username,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        var userFilter = new HabitFilterDto(
                filter.frequency(),
                filter.active(),
                filter.createdFrom(),
                filter.createdTo(),
                filter.search(),
                username);
        var habitsPage = habitRepository.findAll(HabitSpecifications.byFilters(userFilter), pageable);

        log.info("Страница привычек пользователя получена: имяПользователя={} страница={} размер={} всегоЭлементов={}",
                username,
                habitsPage.getNumber(),
                habitsPage.getSize(),
                habitsPage.getTotalElements());
        return habitsPage;
    }

    public Page<Habit> getAllPageableForAdmin(HabitFilterDto filter, Pageable pageable) {
        log.debug("Администратор запросил страницу привычек: страница={} размер={} сортировка={}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        var habitsPage = habitRepository.findAll(HabitSpecifications.byFilters(filter), pageable);
        log.info("Администратор получил страницу привычек: страница={} размер={} всегоЭлементов={}",
                habitsPage.getNumber(),
                habitsPage.getSize(),
                habitsPage.getTotalElements());
        return habitsPage;
    }

    public void delete(Long id, String username) {
        log.debug("Начато удаление привычки: идентификаторПривычки={} имяПользователя={}", id, username);

        var habit = findOwnedHabit(id, username);
        habitRepository.delete(habit);
        log.info("Привычка удалена: идентификаторПривычки={} имяПользователя={}", id, username);
    }

    public void deleteAny(Long id) {
        log.debug("Администратор начал удаление привычки: идентификаторПривычки={}", id);

        var habit = habitRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Привычка не найдена"));
        var owner = habit.getUser().getUsername();
        habitRepository.delete(habit);
        log.info("Администратор удалил привычку: идентификаторПривычки={} владелец={}", id, owner);
    }

    public Habit findOwnedHabit(Long id, String username) {
        return habitRepository.findByIdAndUserUsername(id, username)
                .orElseThrow(() -> {
                    log.info("Привычка не найдена среди привычек пользователя: идентификаторПривычки={} имяПользователя={}",
                            id,
                            username);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Привычка не найдена");
                });
    }

    public long countAllHabits() {
        return habitRepository.count();
    }

    public long countUserHabits(String username) {
        return habitRepository.countByUserUsername(username);
    }

    private void updateHabitFields(Habit habit, String name, String description, HabitFrequency frequency, Boolean active) {
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
    }
}
