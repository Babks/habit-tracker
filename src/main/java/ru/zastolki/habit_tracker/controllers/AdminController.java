package ru.zastolki.habit_tracker.controllers;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.zastolki.habit_tracker.dto.AdminSummaryDto;
import ru.zastolki.habit_tracker.dto.HabitEditDto;
import ru.zastolki.habit_tracker.dto.HabitFilterDto;
import ru.zastolki.habit_tracker.dto.HabitResponseDto;
import ru.zastolki.habit_tracker.dto.UserResponseDto;
import ru.zastolki.habit_tracker.dto.UserStatsDto;
import ru.zastolki.habit_tracker.entitys.Habit;
import ru.zastolki.habit_tracker.enums.HabitFrequency;
import ru.zastolki.habit_tracker.services.HabitLogService;
import ru.zastolki.habit_tracker.services.HabitService;
import ru.zastolki.habit_tracker.services.UserService;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final UserService userService;
    private final HabitService habitService;
    private final HabitLogService habitLogService;

    public AdminController(UserService userService, HabitService habitService, HabitLogService habitLogService) {
        this.userService = userService;
        this.habitService = habitService;
        this.habitLogService = habitLogService;
    }

    @PostMapping("/bootstrap")
    public UserResponseDto bootstrapAdmin(Principal principal) {
        log.info("Получен HTTP-запрос на первичное получение прав администратора: имяПользователя={}",
                principal.getName());
        return userService.makeCurrentUserAdminIfNoAdmins(principal.getName());
    }

    @PostMapping("/users/{username}/grant-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDto grantAdmin(@PathVariable String username) {
        log.info("Получен HTTP-запрос администратора на выдачу прав: имяПользователя={}", username);
        return userService.grantAdmin(username);
    }

    @PostMapping("/users/{username}/revoke-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDto revokeAdmin(@PathVariable String username) {
        log.info("Получен HTTP-запрос администратора на отзыв прав: имяПользователя={}", username);
        return userService.revokeAdmin(username);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponseDto> getUsers() {
        log.info("Получен HTTP-запрос администратора на получение списка пользователей");
        return userService.getUsers();
    }

    @DeleteMapping("/users/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable String username, Principal principal) {
        log.info("Получен HTTP-запрос администратора на удаление пользователя: имяПользователя={}", username);
        userService.deleteUser(username, principal.getName());
    }

    @GetMapping("/users/leaderboard")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponseDto> getLeaderboard() {
        log.info("Получен HTTP-запрос администратора на получение рейтинга пользователей");
        return userService.getLeaderboard();
    }

    @GetMapping("/users/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserStatsDto> getUserStats() {
        log.info("Получен HTTP-запрос администратора на получение статистики пользователей");
        return userService.getUserStats();
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public AdminSummaryDto getSummary() {
        log.info("Получен HTTP-запрос администратора на получение общей сводки");
        return new AdminSummaryDto(
                userService.countUsers(),
                userService.countAdmins(),
                habitService.countAllHabits(),
                habitLogService.countAllLogs());
    }

    @GetMapping("/habits")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<HabitResponseDto> getAllHabits(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String frequency,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdTo,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        log.info("Получен HTTP-запрос администратора на получение всех привычек");

        var filter = new HabitFilterDto(
                HabitFrequency.fromString(frequency),
                active,
                createdFrom,
                createdTo,
                search,
                username);
        return habitService.getAllPageableForAdmin(filter, pageable).map(this::convertHabitToDto);
    }

    @PutMapping("/habits/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public HabitResponseDto editHabit(@PathVariable Long id, @Valid @RequestBody HabitEditDto editDto) {
        log.info("Получен HTTP-запрос администратора на изменение привычки: идентификаторПривычки={}", id);
        return convertHabitToDto(
                habitService.editAny(
                        id,
                        editDto.getName(),
                        editDto.getDescription(),
                        HabitFrequency.fromString(editDto.getFrequency()),
                        editDto.getActive()));
    }

    @DeleteMapping("/habits/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteHabit(@PathVariable Long id) {
        log.info("Получен HTTP-запрос администратора на удаление привычки: идентификаторПривычки={}", id);
        habitService.deleteAny(id);
    }

    private HabitResponseDto convertHabitToDto(Habit habit) {
        var dto = new HabitResponseDto();
        dto.setId(habit.getId());
        dto.setName(habit.getName());
        dto.setDescription(habit.getDescription());
        dto.setFrequency(habit.getFrequency());
        dto.setCreatedAt(habit.getCreatedAt());
        dto.setActive(habit.getActive());
        dto.setOwnerUsername(habit.getUser().getUsername());
        dto.setStreak(habitLogService.calculateStreakForAdmin(habit.getId(), habit.getUser().getUsername()));
        return dto;
    }
}
