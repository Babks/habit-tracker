package ru.zastolki.habit_tracker.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.zastolki.habit_tracker.entitys.Habit;
import ru.zastolki.habit_tracker.enums.HabitFrequency;
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

    public Habit edit(Long id, String name, String description, HabitFrequency frequency, Boolean active){
        var habit = habitRepository.getReferenceById(id);
        habit.setName(name);
        habit.setDescription(description);
        habit.setFrequency(frequency);
        habit.setActive(active);
        return habit;
    }

    public List<Habit> getAll() {
        return habitRepository.findAll();
    }

    public Page<Habit> getAllPageable(Pageable pageable) {
        return habitRepository.findAll(pageable);
    }

    public void delete(Long id) {
        habitRepository.deleteById(id);
    }
}