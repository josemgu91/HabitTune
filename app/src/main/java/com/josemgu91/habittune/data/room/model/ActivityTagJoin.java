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
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

import java.util.Objects;

@Entity(tableName = "activityTagJoins",
        primaryKeys = {"activityName", "tagName"},
        indices = {@Index(value = {"tagName", "activityName"})},
        foreignKeys = {
                @ForeignKey(entity = Activity.class,
                        parentColumns = "name",
                        childColumns = "activityName"),
                @ForeignKey(entity = Tag.class,
                        parentColumns = "name",
                        childColumns = "tagName")
        })
public class ActivityTagJoin {

    @NonNull
    public final String activityName;
    @NonNull
    public final String tagName;

    public ActivityTagJoin(@NonNull final String activityName, @NonNull final String tagName) {
        this.activityName = activityName;
        this.tagName = tagName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivityTagJoin that = (ActivityTagJoin) o;
        return Objects.equals(activityName, that.activityName) &&
                Objects.equals(tagName, that.tagName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activityName, tagName);
    }

    @Override
    public String toString() {
        return "ActivityTagJoin{" +
                "activityName='" + activityName + '\'' +
                ", tagName='" + tagName + '\'' +
                '}';
    }
}
