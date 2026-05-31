package ru.zastolki.habit_tracker.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.zastolki.habit_tracker.dto.RegisterDto;
import ru.zastolki.habit_tracker.entitys.AppUser;
import ru.zastolki.habit_tracker.enums.UserRole;
import ru.zastolki.habit_tracker.repositories.AppUserRepository;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
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

        var savedUser = appUserRepository.save(appUser);
        log.info("Пользователь зарегистрирован: идентификаторПользователя={} имяПользователя={} роль={}",
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getRole());
        return savedUser;
    }

    public AppUser getUserByName(String name) {
        var user = appUserRepository.findByUsername(name);
        return user.orElseThrow(
                () -> {
                    log.warn("Пользовтель не найден: имяПользователя={}", name);

                    return new IllegalArgumentException(
                            "Пользовтель не найден: " + name
                    );
                }
        );
    }
}
