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

package com.josemgu91.habittune.domain.usecases.common;

import java.util.Date;

public class ScheduleCalculator {

    private final static int MILLISECONDS_IN_A_DAY = 3600 * 24 * 1000;

    public boolean isInRoutineEntryDay(final Date date, final Date routineStartDate, final int routineNumberOfDays, final int routineEntryDay) {
        final int routineDayNumber = calculateRoutineDayNumber(date, routineStartDate, routineNumberOfDays);
        return routineDayNumber == routineEntryDay;
    }

    public int getRoutineEntryCycleNumber(final Date date, final Date routineStartDate, final int routineNumberOfDays) {
        final int daysSinceStartDate = calculateDaysBetween(routineStartDate, date);
        return daysSinceStartDate / routineNumberOfDays;
    }

    private int calculateRoutineDayNumber(final Date date, final Date routineStartDate, final int routineNumberOfDays) {
        final int daysSinceStartDate = calculateDaysBetween(routineStartDate, date);
        return daysSinceStartDate % routineNumberOfDays;
    }

    private int calculateDaysBetween(final Date start, final Date end) {
        return (int) Math.round((end.getTime() - start.getTime()) / (double) MILLISECONDS_IN_A_DAY);
    }

}
