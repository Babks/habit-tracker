package ru.zastolki.habit_tracker.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.zastolki.habit_tracker.dto.HabitCreateDto;
import ru.zastolki.habit_tracker.dto.HabitResponseDto;
import ru.zastolki.habit_tracker.entitys.Habit;
import ru.zastolki.habit_tracker.services.HabitService;

import java.time.LocalDate;

@RestController
@RequestMapping("/habits")
public class HabitController {

    private final HabitService habitService;

    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    @PostMapping
    public HabitResponseDto create(@RequestBody HabitCreateDto createDto) {
        var newHabit = new Habit();
        newHabit.setName(createDto.getName());
        newHabit.setDescription(createDto.getDescription());
        newHabit.setFrequency(createDto.getFrequency());

        return convertHabitToDto(habitService.create(newHabit));
    }

    @GetMapping
    public Page<HabitResponseDto> getAll(Pageable pageable) {
        return habitService.getAllPageable(pageable).map(this::convertHabitToDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        habitService.delete(id);
    }

    private HabitResponseDto convertHabitToDto(Habit habit){
        var dto = new HabitResponseDto();
        dto.setId(habit.getId());
        dto.setName(habit.getName());
        dto.setDescription(habit.getDescription());
        dto.setFrequency(habit.getFrequency());
        dto.setCreatedAt(habit.getCreatedAt());
        dto.setActive(habit.getActive());
        return dto;
    }
}