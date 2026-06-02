package ru.zastolki.habit_tracker.entitys;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.zastolki.habit_tracker.enums.HabitFrequency;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "habits")
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private HabitFrequency frequency;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(nullable = false)
    private Boolean active = true;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private AppUser user;

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<HabitLog> logs;
}
