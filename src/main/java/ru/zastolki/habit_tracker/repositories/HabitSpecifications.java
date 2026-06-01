package ru.zastolki.habit_tracker.repositories;

import org.springframework.data.jpa.domain.Specification;
import ru.zastolki.habit_tracker.dto.HabitFilterDto;
import ru.zastolki.habit_tracker.entitys.Habit;

public final class HabitSpecifications {

    private HabitSpecifications() {
    }

    public static Specification<Habit> byFilters(HabitFilterDto filter) {
        return Specification.allOf(
                hasUsername(filter.username()),
                hasFrequency(filter.frequency()),
                hasActive(filter.active()),
                createdFrom(filter.createdFrom()),
                createdTo(filter.createdTo()),
                search(filter.search()));
    }

    private static Specification<Habit> hasUsername(String username) {
        return (root, query, criteriaBuilder) -> username == null || username.isBlank()
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.get("user").get("username"), username);
    }

    private static Specification<Habit> hasFrequency(Object frequency) {
        return (root, query, criteriaBuilder) -> frequency == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.get("frequency"), frequency);
    }

    private static Specification<Habit> hasActive(Boolean active) {
        return (root, query, criteriaBuilder) -> active == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.get("active"), active);
    }

    private static Specification<Habit> createdFrom(java.time.LocalDate createdFrom) {
        return (root, query, criteriaBuilder) -> createdFrom == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), createdFrom);
    }

    private static Specification<Habit> createdTo(java.time.LocalDate createdTo) {
        return (root, query, criteriaBuilder) -> createdTo == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), createdTo);
    }

    private static Specification<Habit> search(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            var pattern = "%" + search.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern));
        };
    }
}
