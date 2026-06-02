package ru.zastolki.habit_tracker.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.zastolki.habit_tracker.entitys.AppUser;
import ru.zastolki.habit_tracker.enums.UserRole;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String username);

    boolean existsByUsername(String username);

    long countByRole(UserRole role);

    List<AppUser> findAllByOrderByPointsDesc();

    List<AppUser> findAllByRole(UserRole role, Sort sort);
}
