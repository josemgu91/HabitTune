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
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Objects;

@Entity(
        tableName = "assistanceRegisters",
        indices = {
                @Index(
                        value = {"routineActivityJoinId", "cycleNumber"},
                        unique = true
                )
        },
        foreignKeys = {
                @ForeignKey(
                        entity = RoutineActivityJoin.class,
                        parentColumns = "id",
                        childColumns = "routineActivityJoinId",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class AssistanceRegister {

    @PrimaryKey(autoGenerate = true)
    public final long id;
    public final int cycleNumber;
    public final int startTime;
    public final int endTime;
    public final long routineActivityJoinId;

    @Ignore
    public AssistanceRegister(long id) {
        this.id = id;
        this.cycleNumber = 0;
        this.startTime = 0;
        this.endTime = 0;
        this.routineActivityJoinId = 0;
    }

    @Ignore
    public AssistanceRegister(int cycleNumber, int startTime, int endTime, long routineActivityJoinId) {
        this.id = 0;
        this.cycleNumber = cycleNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.routineActivityJoinId = routineActivityJoinId;
    }

    public AssistanceRegister(long id, int cycleNumber, int startTime, int endTime, long routineActivityJoinId) {
        this.id = id;
        this.cycleNumber = cycleNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.routineActivityJoinId = routineActivityJoinId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssistanceRegister that = (AssistanceRegister) o;
        return id == that.id &&
                cycleNumber == that.cycleNumber &&
                startTime == that.startTime &&
                endTime == that.endTime &&
                routineActivityJoinId == that.routineActivityJoinId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cycleNumber, startTime, endTime, routineActivityJoinId);
    }

    @Override
    public String toString() {
        return "AssistanceRegister{" +
                "id=" + id +
                ", cycleNumber=" + cycleNumber +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", routineActivityJoinId=" + routineActivityJoinId +
                '}';
    }
}
