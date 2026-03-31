package ru.zastolki.habit_tracker.services;

import org.springframework.stereotype.Service;
import ru.zastolki.habit_tracker.entitys.Habit;
import ru.zastolki.habit_tracker.repositories.HabitRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class HabitService {

    private final HabitRepository habitRepository;

    public HabitService(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    public Habit create(Habit habit) {
        habit.setCreatedAt(LocalDate.now());
        habit.setActive(true);
        return habitRepository.save(habit);
    }

    public List<Habit> getAll() {
        return habitRepository.findAll();
    }

    public void delete(Long id) {
        habitRepository.deleteById(id);
    }
}