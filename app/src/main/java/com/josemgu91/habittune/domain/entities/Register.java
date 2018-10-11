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

import com.josemgu91.habittune.domain.DomainException;

import java.util.Objects;

public class Register {

    @NonNull
    private final String id;
    private final int daySinceRoutineStartDate;
    @NonNull
    private final Time startTime;
    @Nullable
    private final Time endTime;

    public Register(@NonNull String id, int daySinceRoutineStartDate, @NonNull Time startTime, @Nullable Time endTime) {
        this.id = id;
        this.daySinceRoutineStartDate = daySinceRoutineStartDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Register(int daySinceRoutineStartDate, @NonNull Time startTime, @Nullable Time endTime) {
        this.id = "";
        this.daySinceRoutineStartDate = daySinceRoutineStartDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Register(@NonNull String id) {
        try {
            this.id = id;
            this.daySinceRoutineStartDate = 0;
            this.startTime = new Time(0);
            this.endTime = null;
        } catch (DomainException e) {
            throw new RuntimeException("Invalid Register entity. This is a default constructor, so this shouldn't occur!");
        }
    }

    @NonNull
    public String getId() {
        return id;
    }

    public int getDaySinceRoutineStartDate() {
        return daySinceRoutineStartDate;
    }

    @NonNull
    public Time getStartTime() {
        return startTime;
    }

    @Nullable
    public Time getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Register register = (Register) o;
        return daySinceRoutineStartDate == register.daySinceRoutineStartDate &&
                Objects.equals(id, register.id) &&
                Objects.equals(startTime, register.startTime) &&
                Objects.equals(endTime, register.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, daySinceRoutineStartDate, startTime, endTime);
    }

    @Override
    public String toString() {
        return "Register{" +
                "id='" + id + '\'' +
                ", daySinceRoutineStartDate=" + daySinceRoutineStartDate +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
