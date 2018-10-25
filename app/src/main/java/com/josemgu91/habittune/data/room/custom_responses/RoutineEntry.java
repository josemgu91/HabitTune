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

package com.josemgu91.habittune.data.room.custom_responses;

public class RoutineEntry {

    public final long routineActivityJoinId;
    public final long routineActivityJoinCreationDateTimestamp;
    public final int routineActivityJoinEntryDay;
    public final long activityId;
    public final long routineId;
    public final long routineStartDateTimestamp;
    public final int routineNumberOfDays;

    public RoutineEntry(long routineActivityJoinId, long routineActivityJoinCreationDateTimestamp, int routineActivityJoinEntryDay, long activityId, long routineId, long routineStartDateTimestamp, int routineNumberOfDays) {
        this.routineActivityJoinId = routineActivityJoinId;
        this.routineActivityJoinCreationDateTimestamp = routineActivityJoinCreationDateTimestamp;
        this.routineActivityJoinEntryDay = routineActivityJoinEntryDay;
        this.activityId = activityId;
        this.routineId = routineId;
        this.routineStartDateTimestamp = routineStartDateTimestamp;
        this.routineNumberOfDays = routineNumberOfDays;
    }
}
