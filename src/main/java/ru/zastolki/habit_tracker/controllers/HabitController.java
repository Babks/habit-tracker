package ru.zastolki.habit_tracker.controllers;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.zastolki.habit_tracker.dto.HabitCreateDto;
import ru.zastolki.habit_tracker.dto.HabitEditDto;
import ru.zastolki.habit_tracker.dto.HabitResponseDto;
import ru.zastolki.habit_tracker.entitys.Habit;
import ru.zastolki.habit_tracker.enums.HabitFrequency;
import ru.zastolki.habit_tracker.services.HabitLogService;
import ru.zastolki.habit_tracker.services.HabitService;
import ru.zastolki.habit_tracker.services.UserService;

@RestController
@RequestMapping("/habits")
public class HabitController {

    private static final Logger log = LoggerFactory.getLogger(HabitController.class);

    private final UserService userService;
    private final HabitService habitService;
    private final HabitLogService habitLogService;

    public HabitController(UserService userService, HabitService habitService, HabitLogService habitLogService) {
        this.userService = userService;
        this.habitService = habitService;
        this.habitLogService = habitLogService;
    }

    @PostMapping
    public HabitResponseDto create(
            Authentication authentication,
            @Valid @RequestBody HabitCreateDto createDto) {
        log.info("Получен HTTP-запрос на создание привычки: метод=POST путь=/habits частота={}",
                createDto.getFrequency());

        var username = authentication.getName();
        var user = userService.getUserByName(username);

        var newHabit = new Habit();
        newHabit.setName(createDto.getName());
        newHabit.setDescription(createDto.getDescription());
        newHabit.setFrequency(HabitFrequency.fromString(createDto.getFrequency()));
        newHabit.setUser(user);

        return convertHabitToDto(habitService.create(newHabit));
    }

    @PutMapping("/{id}")
    public HabitResponseDto edit(
            @PathVariable Long id,
            @Valid @RequestBody HabitEditDto editDto) {
        log.info(
                "Получен HTTP-запрос на изменение привычки: метод=PUT путь=/habits/{} переданоНазвание={} переданоОписание={} переданаЧастота={} переданСтатусАктивности={}",
                id,
                editDto.getName() != null,
                editDto.getDescription() != null,
                editDto.getFrequency() != null,
                editDto.getActive() != null);

        return convertHabitToDto(
                habitService.edit(
                        id,
                        editDto.getName(),
                        editDto.getDescription(),
                        HabitFrequency.fromString(editDto.getFrequency()),
                        editDto.getActive()));
    }

    @GetMapping
    public Page<HabitResponseDto> getAll(Pageable pageable) {
        log.info("Получен HTTP-запрос на получение списка привычек: метод=GET путь=/habits страница={} размер={} сортировка={}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        return habitService.getAllPageable(pageable).map(this::convertHabitToDto);
    }

    @GetMapping("/{id}/streak")
    public int getStreak(@PathVariable Long id) {
        log.info("Получен HTTP-запрос на получение стрика привычки: метод=GET путь=/habits/{}/streak", id);
        return habitLogService.calculateStreak(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Получен HTTP-запрос на удаление привычки: метод=DELETE путь=/habits/{}", id);
        habitService.delete(id);
    }

    private HabitResponseDto convertHabitToDto(Habit habit) {
        var dto = new HabitResponseDto();
        dto.setId(habit.getId());
        dto.setName(habit.getName());
        dto.setDescription(habit.getDescription());
        dto.setFrequency(habit.getFrequency());
        dto.setCreatedAt(habit.getCreatedAt());
        dto.setActive(habit.getActive());
        dto.setStreak(habitLogService.calculateStreak(habit.getId()));
        return dto;
    }
}