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

import java.util.Objects;

@Entity(tableName = "routineActivityJoins",
        primaryKeys = {"routineId", "activityId"},
        indices = {@Index(value = {"activityId", "routineId"})},
        foreignKeys = {
                @ForeignKey(entity = Routine.class,
                        parentColumns = "id",
                        childColumns = "routineId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Activity.class,
                        parentColumns = "id",
                        childColumns = "activityId",
                        onDelete = ForeignKey.CASCADE)
        })
public class RoutineActivityJoin {

    public final long routineId;
    public final long activityId;

    public RoutineActivityJoin(long routineId, long activityId) {
        this.routineId = routineId;
        this.activityId = activityId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoutineActivityJoin that = (RoutineActivityJoin) o;
        return routineId == that.routineId &&
                activityId == that.activityId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(routineId, activityId);
    }

    @Override
    public String toString() {
        return "RoutineActivityJoin{" +
                "routineId=" + routineId +
                ", activityId=" + activityId +
                '}';
    }
}
