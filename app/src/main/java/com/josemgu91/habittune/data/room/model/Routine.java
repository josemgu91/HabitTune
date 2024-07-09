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

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

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
    public final long startDateTimestamp;
    public final boolean enabled;
    public final long creationDateTimestamp;
    public final long deactivationDateTimestamp;


    @Ignore
    public Routine(long id) {
        this.id = id;
        this.name = "";
        this.description = "";
        this.color = 0;
        this.numberOfDays = 0;
        this.startDateTimestamp = 0;
        this.enabled = true;
        this.creationDateTimestamp = 0;
        this.deactivationDateTimestamp = 0;
    }

    @Ignore
    public Routine(@NonNull String name, @NonNull String description, int color, int numberOfDays, long startDateTimestamp, boolean enabled, long creationDateTimestamp, long deactivationDateTimestamp) {
        this.id = 0;
        this.name = name;
        this.description = description;
        this.color = color;
        this.numberOfDays = numberOfDays;
        this.startDateTimestamp = startDateTimestamp;
        this.enabled = enabled;
        this.creationDateTimestamp = creationDateTimestamp;
        this.deactivationDateTimestamp = deactivationDateTimestamp;
    }

    public Routine(long id, @NonNull String name, @NonNull String description, int color, int numberOfDays, long startDateTimestamp, boolean enabled, long creationDateTimestamp, long deactivationDateTimestamp) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.color = color;
        this.numberOfDays = numberOfDays;
        this.startDateTimestamp = startDateTimestamp;
        this.enabled = enabled;
        this.creationDateTimestamp = creationDateTimestamp;
        this.deactivationDateTimestamp = deactivationDateTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Routine routine = (Routine) o;
        return id == routine.id &&
                color == routine.color &&
                numberOfDays == routine.numberOfDays &&
                startDateTimestamp == routine.startDateTimestamp &&
                enabled == routine.enabled &&
                creationDateTimestamp == routine.creationDateTimestamp &&
                deactivationDateTimestamp == routine.deactivationDateTimestamp &&
                Objects.equals(name, routine.name) &&
                Objects.equals(description, routine.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, color, numberOfDays, startDateTimestamp, enabled, creationDateTimestamp, deactivationDateTimestamp);
    }

    @Override
    public String toString() {
        return "Routine{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", color=" + color +
                ", numberOfDays=" + numberOfDays +
                ", startDateTimestamp=" + startDateTimestamp +
                ", enabled=" + enabled +
                ", creationDateTimestamp=" + creationDateTimestamp +
                ", deactivationDateTimestamp=" + deactivationDateTimestamp +
                '}';
    }
}
