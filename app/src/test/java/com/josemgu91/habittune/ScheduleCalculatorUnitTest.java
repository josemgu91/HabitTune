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

package com.josemgu91.habittune;

import com.josemgu91.habittune.domain.usecases.common.ScheduleCalculator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

public class ScheduleCalculatorUnitTest {

    private ScheduleCalculator scheduleCalculator;

    @Before
    public void init() {
        scheduleCalculator = new ScheduleCalculator();
    }

    @Test
    public void computeNumberOfRoutineEntryEventsBetweenDates() {
        final Calendar calendar = Calendar.getInstance();
        Date fromDate;
        Date toDate;
        Date routineStartDate;
        Date routineEntryStartDate;
        int actualValue;

        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        fromDate = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, 10);
        toDate = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        routineStartDate = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, 5);
        routineEntryStartDate = calendar.getTime();
        actualValue = scheduleCalculator.computeNumberOfRoutineEntryEventsBetweenDates(
                fromDate,
                toDate,
                routineStartDate,
                routineEntryStartDate,
                3,
                2
        );
        Assert.assertEquals(2, actualValue);

        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        fromDate = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, 18);
        toDate = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        routineStartDate = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, 5);
        routineEntryStartDate = calendar.getTime();
        actualValue = scheduleCalculator.computeNumberOfRoutineEntryEventsBetweenDates(
                fromDate,
                toDate,
                routineStartDate,
                routineEntryStartDate,
                3,
                2
        );
        Assert.assertEquals(5, actualValue);

        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        fromDate = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, 12);
        toDate = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        routineStartDate = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, 6);
        routineEntryStartDate = calendar.getTime();
        actualValue = scheduleCalculator.computeNumberOfRoutineEntryEventsBetweenDates(
                fromDate,
                toDate,
                routineStartDate,
                routineEntryStartDate,
                3,
                1
        );
        Assert.assertEquals(2, actualValue);

        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 9);
        fromDate = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, 10);
        toDate = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        routineStartDate = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, 6);
        routineEntryStartDate = calendar.getTime();
        actualValue = scheduleCalculator.computeNumberOfRoutineEntryEventsBetweenDates(
                fromDate,
                toDate,
                routineStartDate,
                routineEntryStartDate,
                3,
                1
        );
        Assert.assertEquals(0, actualValue);
    }

}
