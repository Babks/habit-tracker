package ru.zastolki.habit_tracker.controllers;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.zastolki.habit_tracker.dto.HabitCreateDto;
import ru.zastolki.habit_tracker.dto.HabitEditDto;
import ru.zastolki.habit_tracker.dto.HabitFilterDto;
import ru.zastolki.habit_tracker.dto.HabitResponseDto;
import ru.zastolki.habit_tracker.entitys.Habit;
import ru.zastolki.habit_tracker.enums.HabitFrequency;
import ru.zastolki.habit_tracker.services.HabitLogService;
import ru.zastolki.habit_tracker.services.HabitService;

import java.security.Principal;
import java.time.LocalDate;

@RestController
@RequestMapping("/habits")
public class HabitController {

    private static final Logger log = LoggerFactory.getLogger(HabitController.class);

    private final HabitService habitService;
    private final HabitLogService habitLogService;

    public HabitController(HabitService habitService, HabitLogService habitLogService) {
        this.habitService = habitService;
        this.habitLogService = habitLogService;
    }

    @PostMapping
    public HabitResponseDto create(@Valid @RequestBody HabitCreateDto createDto, Principal principal) {
        var username = principal.getName();
        log.info("Получен HTTP-запрос на создание привычки: метод=POST путь=/habits имяПользователя={} частота={}",
                username,
                createDto.getFrequency());

        var newHabit = new Habit();
        newHabit.setName(createDto.getName());
        newHabit.setDescription(createDto.getDescription());
        newHabit.setFrequency(HabitFrequency.fromString(createDto.getFrequency()));

        return convertHabitToDto(habitService.create(newHabit, username), username);
    }

    @PutMapping("/{id}")
    public HabitResponseDto edit(
            @PathVariable Long id,
            @Valid @RequestBody HabitEditDto editDto,
            Principal principal) {
        var username = principal.getName();
        log.info(
                "Получен HTTP-запрос на изменение привычки: метод=PUT путь=/habits/{} имяПользователя={} переданоНазвание={} переданоОписание={} переданаЧастота={} переданСтатусАктивности={}",
                id,
                username,
                editDto.getName() != null,
                editDto.getDescription() != null,
                editDto.getFrequency() != null,
                editDto.getActive() != null);

        return convertHabitToDto(
                habitService.edit(
                        id,
                        username,
                        editDto.getName(),
                        editDto.getDescription(),
                        HabitFrequency.fromString(editDto.getFrequency()),
                        editDto.getActive()),
                username);
    }

    @GetMapping
    public Page<HabitResponseDto> getAll(
            @RequestParam(required = false) String frequency,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdTo,
            @RequestParam(required = false) String search,
            Pageable pageable,
            Principal principal) {
        var username = principal.getName();
        log.info("Получен HTTP-запрос на получение списка привычек: метод=GET путь=/habits имяПользователя={} страница={} размер={} сортировка={}",
                username,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        var filter = new HabitFilterDto(
                HabitFrequency.fromString(frequency),
                active,
                createdFrom,
                createdTo,
                search,
                null);
        return habitService.getAllPageable(username, filter, pageable).map(habit -> convertHabitToDto(habit, username));
    }

    @GetMapping("/{id}/streak")
    public int getStreak(@PathVariable Long id, Principal principal) {
        var username = principal.getName();
        log.info("Получен HTTP-запрос на получение стрика привычки: метод=GET путь=/habits/{}/streak имяПользователя={}",
                id,
                username);
        return habitLogService.calculateStreak(id, username);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Principal principal) {
        var username = principal.getName();
        log.info("Получен HTTP-запрос на удаление привычки: метод=DELETE путь=/habits/{} имяПользователя={}",
                id,
                username);
        habitService.delete(id, username);
    }

    private HabitResponseDto convertHabitToDto(Habit habit, String username) {
        var dto = new HabitResponseDto();
        dto.setId(habit.getId());
        dto.setName(habit.getName());
        dto.setDescription(habit.getDescription());
        dto.setFrequency(habit.getFrequency());
        dto.setCreatedAt(habit.getCreatedAt());
        dto.setActive(habit.getActive());
        dto.setOwnerUsername(habit.getUser().getUsername());
        dto.setStreak(habitLogService.calculateStreakForAdmin(habit.getId(), username));
        return dto;
    }
}
