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

import java.util.Date;
import java.util.Objects;

public class AssistanceRegister {

    @NonNull
    private final String id;
    @NonNull
    private final Date startDate;
    @NonNull
    private final Date endDate;

    public AssistanceRegister(@NonNull String id, @NonNull Date startDate, @NonNull Date endDate) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public AssistanceRegister(@NonNull Date startDate, @NonNull Date endDate) {
        this.id = "";
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public AssistanceRegister(@NonNull String id) {
        this.id = id;
        this.startDate = new Date();
        this.endDate = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssistanceRegister that = (AssistanceRegister) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startDate, endDate);
    }

    @Override
    public String toString() {
        return "AssistanceRegister{" +
                "id='" + id + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
