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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;

public class Activity {

    @NonNull
    private final String id;
    @NonNull
    private final String name;
    @NonNull
    private final String description;
    private final int color;
    @Nullable
    private final List<Tag> tags;

    public Activity(@NonNull String id, @NonNull String name, @NonNull String description, int color, @Nullable List<Tag> tags) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.color = color;
        this.tags = tags;
    }

    public Activity(@NonNull String name, @NonNull String description, int color, @Nullable List<Tag> tags) {
        this.id = "";
        this.name = name;
        this.description = description;
        this.color = color;
        this.tags = tags;
    }

    public Activity(@NonNull String id) {
        this.id = id;
        this.name = "";
        this.description = "";
        this.color = 0;
        this.tags = null;
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

    @Nullable
    public List<Tag> getTags() {
        return tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return color == activity.color &&
                Objects.equals(id, activity.id) &&
                Objects.equals(name, activity.name) &&
                Objects.equals(description, activity.description) &&
                Objects.equals(tags, activity.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, color, tags);
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", color=" + color +
                ", tags=" + tags +
                '}';
    }
}
