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
        tableName = "activities",
        indices = @Index(
                value = "name",
                unique = true
        )
)
public class Activity {

    @PrimaryKey(autoGenerate = true)
    public final long id;
    @NonNull
    public final String name;
    @NonNull
    public final String description;
    public final int color;

    @Ignore
    public Activity(long id) {
        this.id = id;
        this.name = "";
        this.description = "";
        this.color = 0;
    }

    @Ignore
    public Activity(@NonNull String name, @NonNull String description, int color) {
        this.id = 0;
        this.name = name;
        this.description = description;
        this.color = color;
    }

    public Activity(long id, @NonNull String name, @NonNull String description, int color) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return id == activity.id &&
                color == activity.color &&
                Objects.equals(name, activity.name) &&
                Objects.equals(description, activity.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, color);
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", color=" + color +
                '}';
    }
}
