/*
 * Copyright (C) 2018  José Miguel García Urrutia <josemgu91@gmail.com>
 *
 * This file is part of HabitTune.
 *
 * HabitTune is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HabitTune is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.josemgu91.habittune.domain.entities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.Objects;

public class Routine {

    @NonNull
    private final String id;
    @NonNull
    private final String name;
    @NonNull
    private final String description;
    private final int color;
    private final int numberOfDays;
    @Nullable
    private final List<RoutineEntry> routineEntries;

    public Routine(@NonNull String id, @NonNull String name, @NonNull String description, int color, int numberOfDays, @Nullable List<RoutineEntry> routineEntries) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.color = color;
        this.numberOfDays = numberOfDays;
        this.routineEntries = routineEntries;
    }

    public Routine(@NonNull String name, @NonNull String description, int color, int numberOfDays, @Nullable List<RoutineEntry> routineEntries) {
        this.id = "";
        this.name = name;
        this.description = description;
        this.color = color;
        this.numberOfDays = numberOfDays;
        this.routineEntries = routineEntries;
    }

    public Routine(@NonNull String id) {
        this.id = id;
        this.name = "";
        this.description = "";
        this.color = 0;
        this.numberOfDays = 0;
        this.routineEntries = null;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    public int getColor() {
        return color;
    }

    public int getNumberOfDays() {
        return numberOfDays;
    }

    @Nullable
    public List<RoutineEntry> getRoutineEntries() {
        return routineEntries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Routine routine = (Routine) o;
        return color == routine.color &&
                numberOfDays == routine.numberOfDays &&
                Objects.equals(id, routine.id) &&
                Objects.equals(name, routine.name) &&
                Objects.equals(description, routine.description) &&
                Objects.equals(routineEntries, routine.routineEntries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, color, numberOfDays, routineEntries);
    }

    @Override
    public String toString() {
        return "Routine{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", color=" + color +
                ", numberOfDays=" + numberOfDays +
                ", routineEntries=" + routineEntries +
                '}';
    }
}
