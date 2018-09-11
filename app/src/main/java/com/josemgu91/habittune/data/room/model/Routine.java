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

package com.josemgu91.habittune.data.room.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Objects;

@Entity(
        tableName = "routines",
        indices = @Index(
                value = "name",
                unique = true
        )
)
public class Routine {

    @PrimaryKey(autoGenerate = true)
    public final long id;
    @NonNull
    public final String name;
    @NonNull
    public final String description;
    public final int color;
    public final int numberOfDays;

    public Routine(@NonNull String name, @NonNull String description, int color, int numberOfDays) {
        this.id = 0;
        this.name = name;
        this.description = description;
        this.color = color;
        this.numberOfDays = numberOfDays;
    }

    public Routine(long id, @NonNull String name, @NonNull String description, int color, int numberOfDays) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.color = color;
        this.numberOfDays = numberOfDays;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Routine routine = (Routine) o;
        return id == routine.id &&
                color == routine.color &&
                numberOfDays == routine.numberOfDays &&
                Objects.equals(name, routine.name) &&
                Objects.equals(description, routine.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, color, numberOfDays);
    }

    @Override
    public String toString() {
        return "Routine{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", color=" + color +
                ", numberOfDays=" + numberOfDays +
                '}';
    }
}
