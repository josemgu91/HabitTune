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

    //Cycle numbers are relative to the Routine start date and NOT to the Routine Entry creation date.

    private final static int MILLISECONDS_IN_A_DAY = 3600 * 24 * 1000;

    public boolean isInRoutineEntryDay(final Date date, final Date routineStartDate, final int routineNumberOfDays, final int routineEntryDay) {
        final int routineDayNumber = calculateRoutineDayNumber(date.getTime(), routineStartDate.getTime(), routineNumberOfDays);
        return routineDayNumber == routineEntryDay;
    }

    public int computeRoutineEntryCycleNumber(final Date date, final Date routineStartDate, final int routineNumberOfDays) {
        final int daysSinceStartDate = calculateDaysBetween(routineStartDate.getTime(), date.getTime());
        return daysSinceStartDate / routineNumberOfDays;
    }

    //FIXME: This algorithms doesn't pass the tests!
    public int computeNumberOfRoutineEntryEventsBetweenDates(final Date fromDate, final Date toDate, final Date routineStartDate, final Date routineEntryCreationDate, final int routineNumberOfDays, final int routineEntryDay) {
        long fromDateTimestamp = fromDate.getTime();
        long toDateTimestamp = toDate.getTime();
        long routineStartDateTimestamp = routineStartDate.getTime();
        long routineEntryCreationDateTimestamp = routineEntryCreationDate.getTime();

        if (fromDateTimestamp > toDateTimestamp) {
            throw new IllegalArgumentException("fromDateTimestamp > toDateTimestamp");
        }
        if (routineEntryCreationDateTimestamp < routineStartDateTimestamp) {
            throw new IllegalArgumentException("routineEntryCreationDateTimestamp < routineStartDateTimestamp");
        }

        if (toDateTimestamp < routineEntryCreationDateTimestamp) {
            return 0;
        }
        if (fromDateTimestamp < routineEntryCreationDateTimestamp) {
            fromDateTimestamp = routineEntryCreationDateTimestamp;
        }
        final int daysBetweenFromDateAndToDate = calculateDaysBetween(fromDateTimestamp, toDateTimestamp);
        final int fromDateDayNumber = calculateRoutineDayNumber(fromDateTimestamp, routineStartDateTimestamp, routineNumberOfDays);
        return ((daysBetweenFromDateAndToDate + (fromDateDayNumber - routineEntryDay)) / routineNumberOfDays) + 1;
    }

    private int calculateRoutineDayNumber(final long dateTimestamp, final long routineStartDateTimestamp, final int routineNumberOfDays) {
        final int daysSinceStartDate = calculateDaysBetween(routineStartDateTimestamp, dateTimestamp);
        return daysSinceStartDate % routineNumberOfDays;
    }

    private int calculateDaysBetween(final long startTimestamp, final long endTimestamp) {
        return (int) Math.round((endTimestamp - startTimestamp) / (double) MILLISECONDS_IN_A_DAY);
    }

}
