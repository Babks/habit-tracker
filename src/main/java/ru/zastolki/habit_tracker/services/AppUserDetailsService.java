package ru.zastolki.habit_tracker.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.zastolki.habit_tracker.repositories.AppUserRepository;

import java.util.List;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(AppUserDetailsService.class);

    private final AppUserRepository appUserRepository;

    public AppUserDetailsService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Начата загрузка пользователя для аутентификации: имяПользователя={}", username);

        var appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.info("Пользователь для аутентификации не найден: имяПользователя={}", username);
                    return new UsernameNotFoundException("Пользователь не найден");
                });

        log.debug("Пользователь для аутентификации загружен: идентификаторПользователя={} роль={}",
                appUser.getId(),
                appUser.getRole());

        return new User(
                appUser.getUsername(),
                appUser.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + appUser.getRole())));
    }
}
