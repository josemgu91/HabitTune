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

import com.josemgu91.habittune.domain.DomainException;

import java.util.Objects;

public class RoutineEntry {

    @NonNull
    private final String id;
    @NonNull
    private final Day day;
    @NonNull
    private final Time startTime;
    @NonNull
    private final Time endTime;
    @NonNull
    private final Activity activity;

    public RoutineEntry(@NonNull final String id, @NonNull final Day day, @NonNull final Time startTime, @NonNull final Time endTime, @NonNull final Activity activity) {
        this.id = id;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.activity = activity;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public Day getDay() {
        return day;
    }

    @NonNull
    public Time getStartTime() {
        return startTime;
    }

    @NonNull
    public Time getEndTime() {
        return endTime;
    }

    @NonNull
    public Activity getActivity() {
        return activity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoutineEntry that = (RoutineEntry) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(day, that.day) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime) &&
                Objects.equals(activity, that.activity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, day, startTime, endTime, activity);
    }

    @Override
    public String toString() {
        return "RoutineEntry{" +
                "id='" + id + '\'' +
                ", day=" + day +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", activity=" + activity +
                '}';
    }

    public static final class Day {

        private final int day;

        public Day(int day) throws DomainException {
            if (day < 0) {
                throw new DomainException("Invalid day!");
            }
            this.day = day;
        }

        public int getDay() {
            return day;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Day day1 = (Day) o;
            return day == day1.day;
        }

        @Override
        public int hashCode() {
            return Objects.hash(day);
        }

        @Override
        public String toString() {
            return "Day{" +
                    "day=" + day +
                    '}';
        }
    }

    public static final class Time {

        private final int time;

        public Time(int time) throws DomainException {
            if (time < 0 || time > 86400) {
                throw new DomainException("Invalid time. Time must be between 0 and 86400.");
            }
            this.time = time;
        }

        public int getTime() {
            return time;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Time time1 = (Time) o;
            return time == time1.time;
        }

        @Override
        public int hashCode() {
            return Objects.hash(time);
        }

        @Override
        public String toString() {
            return "Time{" +
                    "time=" + time +
                    '}';
        }
    }

}
