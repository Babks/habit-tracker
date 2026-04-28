package ru.zastolki.habit_tracker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zastolki.habit_tracker.entitys.AppUser;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String username);

    boolean existsByUsername(String username);
}
