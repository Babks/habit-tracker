package ru.zastolki.habit_tracker.services;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.zastolki.habit_tracker.dto.RegisterDto;
import ru.zastolki.habit_tracker.dto.UserResponseDto;
import ru.zastolki.habit_tracker.dto.UserStatsDto;
import ru.zastolki.habit_tracker.entitys.AppUser;
import ru.zastolki.habit_tracker.enums.UserRole;
import ru.zastolki.habit_tracker.repositories.AppUserRepository;

import java.util.List;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final HabitService habitService;
    private final HabitLogService habitLogService;

    public UserService(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            HabitService habitService,
            HabitLogService habitLogService) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.habitService = habitService;
        this.habitLogService = habitLogService;
    }

    public AppUser register(RegisterDto registerDto) {
        var username = registerDto.getUsername();
        log.debug("Начата регистрация пользователя: имяПользователя={}", username);

        if (appUserRepository.existsByUsername(username)) {
            log.info("Регистрация отклонена, пользователь уже существует: имяПользователя={}", username);
            throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        }

        var appUser = new AppUser();
        appUser.setUsername(username);
        appUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        appUser.setRole(UserRole.USER);
        appUser.setPoints(0);

        var savedUser = appUserRepository.save(appUser);
        log.info("Пользователь зарегистрирован: идентификаторПользователя={} имяПользователя={} роль={}",
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getRole());
        return savedUser;
    }

    public AppUser getUserByName(String name) {
        return findByUsername(name);
    }

    public UserResponseDto makeCurrentUserAdminIfNoAdmins(String username) {
        log.debug("Запрошено получение прав администратора: имяПользователя={}", username);

        if (appUserRepository.countByRole(UserRole.ADMIN) > 0) {
            log.info("Получение прав администратора отклонено, администратор уже существует: имяПользователя={}", username);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Администратор уже существует");
        }

        var user = findByUsername(username);
        user.setRole(UserRole.ADMIN);
        var savedUser = appUserRepository.save(user);
        log.info("Пользователь получил права администратора: идентификаторПользователя={} имяПользователя={}",
                savedUser.getId(),
                savedUser.getUsername());
        return toDto(savedUser);
    }

    public UserResponseDto grantAdmin(String username) {
        log.debug("Администратор выдает права администратора: имяПользователя={}", username);

        var user = findByUsername(username);
        user.setRole(UserRole.ADMIN);
        var savedUser = appUserRepository.save(user);
        log.info("Права администратора выданы: идентификаторПользователя={} имяПользователя={}",
                savedUser.getId(),
                savedUser.getUsername());
        return toDto(savedUser);
    }

    public UserResponseDto revokeAdmin(String username) {
        log.debug("Администратор отзывает права администратора: имяПользователя={}", username);

        if (appUserRepository.countByRole(UserRole.ADMIN) <= 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нельзя удалить последнего администратора");
        }

        var user = findByUsername(username);
        user.setRole(UserRole.USER);
        var savedUser = appUserRepository.save(user);
        log.info("Права администратора отозваны: идентификаторПользователя={} имяПользователя={}",
                savedUser.getId(),
                savedUser.getUsername());
        return toDto(savedUser);
    }

    @Transactional
    public void deleteUser(String username, String currentUsername) {
        log.debug("Администратор удаляет пользователя: имяПользователя={} текущийАдминистратор={}",
                username,
                currentUsername);

        if (username.equals(currentUsername)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нельзя удалить текущего пользователя");
        }

        var user = findByUsername(username);
        appUserRepository.delete(user);
        log.info("Пользователь удален: идентификаторПользователя={} имяПользователя={}", user.getId(), username);
    }

    public List<UserResponseDto> getUsers() {
        return appUserRepository.findAll(Sort.by(Sort.Direction.ASC, "username"))
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<UserResponseDto> getLeaderboard() {
        return appUserRepository.findAllByOrderByPointsDesc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<UserStatsDto> getUserStats() {
        return appUserRepository.findAll(Sort.by(Sort.Direction.ASC, "username"))
                .stream()
                .map(user -> new UserStatsDto(
                        user.getId(),
                        user.getUsername(),
                        user.getPoints(),
                        habitService.countUserHabits(user.getUsername()),
                        habitLogService.countUserLogs(user.getUsername())))
                .toList();
    }

    public long countUsers() {
        return appUserRepository.count();
    }

    public long countAdmins() {
        return appUserRepository.countByRole(UserRole.ADMIN);
    }

    private AppUser findByUsername(String username) {
        return appUserRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Пользователь не найден: имяПользователя={}", username);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
                });
    }

    private UserResponseDto toDto(AppUser user) {
        return new UserResponseDto(user.getId(), user.getUsername(), user.getRole(), user.getPoints());
    }
}
