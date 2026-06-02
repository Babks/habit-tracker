package ru.zastolki.habit_tracker.enums;

public enum HabitFrequency {
    DAILY,
    WEEKLY;

    public static HabitFrequency fromString(String value) {
        if (value == null) {
            return null;
        }

        for (HabitFrequency frequency : HabitFrequency.values()) {
            if (frequency.name().equalsIgnoreCase(value)) {
                return frequency;
            }
        }

        throw new IllegalArgumentException("Unknown HabitFrequency: " + value);
    }
}
