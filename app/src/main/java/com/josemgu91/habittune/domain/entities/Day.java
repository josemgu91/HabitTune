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

import com.josemgu91.habittune.domain.DomainException;

import java.util.Objects;

public final class Day {

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
