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
import androidx.room.ForeignKey;
import androidx.room.Index;

import java.util.Objects;

@Entity(tableName = "activityTagJoins",
        primaryKeys = {"activityId", "tagId"},
        indices = {@Index(value = {"tagId", "activityId"})},
        foreignKeys = {
                @ForeignKey(entity = Activity.class,
                        parentColumns = "id",
                        childColumns = "activityId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Tag.class,
                        parentColumns = "id",
                        childColumns = "tagId",
                        onDelete = ForeignKey.CASCADE)
        })
public class ActivityTagJoin {

    public final long activityId;
    public final long tagId;

    public ActivityTagJoin(long activityId, long tagId) {
        this.activityId = activityId;
        this.tagId = tagId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivityTagJoin that = (ActivityTagJoin) o;
        return activityId == that.activityId &&
                tagId == that.tagId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(activityId, tagId);
    }

    @Override
    public String toString() {
        return "ActivityTagJoin{" +
                "activityId=" + activityId +
                ", tagId=" + tagId +
                '}';
    }
}
