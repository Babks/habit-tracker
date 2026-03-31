package ru.zastolki.habit_tracker.controllers;

import org.springframework.web.bind.annotation.*;
import ru.zastolki.habit_tracker.entitys.Habit;
import ru.zastolki.habit_tracker.services.HabitService;

import java.util.List;

@RestController
@RequestMapping("/habits")
public class HabitController {

    private final HabitService habitService;

    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    @PostMapping
    public Habit create(@RequestBody Habit habit) {
        return habitService.create(habit);
    }

    @GetMapping
    public List<Habit> getAll() {
        return habitService.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        habitService.delete(id);
    }
}