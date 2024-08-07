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
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "routineActivityJoins",
        indices = {
                @Index(value = {"activityId", "routineId", "day", "startTime", "endTime"}, unique = true),
                @Index(value = {"routineId", "day", "startTime"})
        },
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

    @PrimaryKey(autoGenerate = true)
    public final long id;
    public final long routineId;
    public final long activityId;
    public final int day;
    public final int startTime;
    public final int endTime;
    public final boolean enabled;
    public final long creationDateTimestamp;
    public final long deactivationDateTimestamp;

    @Ignore
    public RoutineActivityJoin(long id) {
        this.id = id;
        this.routineId = 0;
        this.activityId = 0;
        this.day = 0;
        this.startTime = 0;
        this.endTime = 0;
        this.enabled = true;
        this.creationDateTimestamp = 0;
        this.deactivationDateTimestamp = 0;
    }

    @Ignore
    public RoutineActivityJoin(long routineId, long activityId, int day, int startTime, int endTime, boolean enabled, long creationDateTimestamp, long deactivationDateTimestamp) {
        this.id = 0;
        this.routineId = routineId;
        this.activityId = activityId;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.enabled = enabled;
        this.creationDateTimestamp = creationDateTimestamp;
        this.deactivationDateTimestamp = deactivationDateTimestamp;
    }

    public RoutineActivityJoin(long id, long routineId, long activityId, int day, int startTime, int endTime, boolean enabled, long creationDateTimestamp, long deactivationDateTimestamp) {
        this.id = id;
        this.routineId = routineId;
        this.activityId = activityId;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.enabled = enabled;
        this.creationDateTimestamp = creationDateTimestamp;
        this.deactivationDateTimestamp = deactivationDateTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoutineActivityJoin that = (RoutineActivityJoin) o;
        return id == that.id &&
                routineId == that.routineId &&
                activityId == that.activityId &&
                day == that.day &&
                startTime == that.startTime &&
                endTime == that.endTime &&
                enabled == that.enabled &&
                creationDateTimestamp == that.creationDateTimestamp &&
                deactivationDateTimestamp == that.deactivationDateTimestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, routineId, activityId, day, startTime, endTime, enabled, creationDateTimestamp, deactivationDateTimestamp);
    }

    @Override
    public String toString() {
        return "RoutineActivityJoin{" +
                "id=" + id +
                ", routineId=" + routineId +
                ", activityId=" + activityId +
                ", day=" + day +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", enabled=" + enabled +
                ", creationDateTimestamp=" + creationDateTimestamp +
                ", deactivationDateTimestamp=" + deactivationDateTimestamp +
                '}';
    }
}
