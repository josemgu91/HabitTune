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

package com.josemgu91.habittune.android;

import android.support.annotation.ColorInt;

import com.josemgu91.habittune.data.room.LocalRoomDatabase;
import com.josemgu91.habittune.data.room.model.Activity;
import com.josemgu91.habittune.data.room.model.ActivityTagJoin;
import com.josemgu91.habittune.data.room.model.Routine;
import com.josemgu91.habittune.data.room.model.RoutineActivityJoin;
import com.josemgu91.habittune.data.room.model.Tag;

import java.util.Calendar;
import java.util.Date;

public class DefaultDatabasePopulator implements DatabasePopulator {

    private final LocalRoomDatabase localRoomDatabase;

    public DefaultDatabasePopulator(LocalRoomDatabase localRoomDatabase) {
        this.localRoomDatabase = localRoomDatabase;
    }

    @Override
    public void populate() {
        final Date currentDate = new Date();
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        final long mondayStartDateTimestamp = calendar.getTime().getTime();
        final long currentDateTimestamp = currentDate.getTime();

        @ColorInt final int red400 = 0xFFEF5350;
        @ColorInt final int pink400 = 0xFFEC407A;
        @ColorInt final int purple400 = 0xFFAB47BC;
        @ColorInt final int deepPurple400 = 0xFF7E57C2;
        @ColorInt final int indigo400 = 0xFF5C6BC0;
        @ColorInt final int blue400 = 0xFF42A5F5;
        @ColorInt final int lightBlue400 = 0xFF29B6F6;
        @ColorInt final int cyan400 = 0xFF26C6DA;
        @ColorInt final int teal400 = 0xFF26A69A;
        @ColorInt final int green400 = 0xFF66BB6A;
        @ColorInt final int lightGreen400 = 0xFF9CCC65;

        //Activities.
        final long breakfastActivityId = localRoomDatabase.getActivityDao().insertActivity(
                new Activity(
                        "Breakfast",
                        "Daily breakfast",
                        red400
                )
        );
        final long lunchActivityId = localRoomDatabase.getActivityDao().insertActivity(
                new Activity(
                        "Lunch",
                        "Daily lunch",
                        pink400
                )
        );
        final long dinnerActivityId = localRoomDatabase.getActivityDao().insertActivity(
                new Activity(
                        "Dinner",
                        "Daily dinner",
                        purple400
                )
        );
        final long sleepActivityId = localRoomDatabase.getActivityDao().insertActivity(
                new Activity(
                        "Sleep",
                        "Time to sleep!",
                        deepPurple400
                )
        );
        final long gymActivityId = localRoomDatabase.getActivityDao().insertActivity(
                new Activity(
                        "Gym session",
                        "Exercises session.",
                        blue400
                )
        );
        final long runningActivityId = localRoomDatabase.getActivityDao().insertActivity(
                new Activity(
                        "Running",
                        "Running session.",
                        lightBlue400
                )
        );
        final long workActivityId = localRoomDatabase.getActivityDao().insertActivity(
                new Activity(
                        "Work",
                        "Time to work!",
                        indigo400
                )
        );
        final long pianoActivityId = localRoomDatabase.getActivityDao().insertActivity(
                new Activity(
                        "Piano practice",
                        "Piano practice session.",
                        green400
                )
        );
        final long readingActivityId = localRoomDatabase.getActivityDao().insertActivity(
                new Activity(
                        "Reading",
                        "Reading session.",
                        lightGreen400
                )
        );

        //Tags.
        final long studyTagId = localRoomDatabase.getTagDao().insertTag(
                new Tag(
                        "Study"
                )
        );
        final long foodTagId = localRoomDatabase.getTagDao().insertTag(
                new Tag(
                        "Food"
                )
        );
        final long healthTagId = localRoomDatabase.getTagDao().insertTag(
                new Tag(
                        "Health"
                )
        );
        final long exerciseTagId = localRoomDatabase.getTagDao().insertTag(
                new Tag(
                        "Exercise"
                )
        );
        final long musicTagId = localRoomDatabase.getTagDao().insertTag(
                new Tag(
                        "Music"
                )
        );

        //ActivityTags
        localRoomDatabase.getActivityTagJoinDao().insertActivityTagJoin(
                new ActivityTagJoin(
                        breakfastActivityId,
                        foodTagId
                )
        );
        localRoomDatabase.getActivityTagJoinDao().insertActivityTagJoin(
                new ActivityTagJoin(
                        lunchActivityId,
                        foodTagId
                )
        );
        localRoomDatabase.getActivityTagJoinDao().insertActivityTagJoin(
                new ActivityTagJoin(
                        dinnerActivityId,
                        foodTagId
                )
        );
        localRoomDatabase.getActivityTagJoinDao().insertActivityTagJoin(
                new ActivityTagJoin(
                        sleepActivityId,
                        healthTagId
                )
        );
        localRoomDatabase.getActivityTagJoinDao().insertActivityTagJoin(
                new ActivityTagJoin(
                        gymActivityId,
                        healthTagId
                )
        );
        localRoomDatabase.getActivityTagJoinDao().insertActivityTagJoin(
                new ActivityTagJoin(
                        gymActivityId,
                        exerciseTagId
                )
        );
        localRoomDatabase.getActivityTagJoinDao().insertActivityTagJoin(
                new ActivityTagJoin(
                        runningActivityId,
                        healthTagId
                )
        );
        localRoomDatabase.getActivityTagJoinDao().insertActivityTagJoin(
                new ActivityTagJoin(
                        runningActivityId,
                        exerciseTagId
                )
        );
        localRoomDatabase.getActivityTagJoinDao().insertActivityTagJoin(
                new ActivityTagJoin(
                        pianoActivityId,
                        studyTagId
                )
        );
        localRoomDatabase.getActivityTagJoinDao().insertActivityTagJoin(
                new ActivityTagJoin(
                        pianoActivityId,
                        musicTagId
                )
        );
        localRoomDatabase.getActivityTagJoinDao().insertActivityTagJoin(
                new ActivityTagJoin(
                        readingActivityId,
                        studyTagId
                )
        );

        //Routines.
        final long dailyRoutineId = localRoomDatabase.getRoutineDao().insertRoutine(
                new Routine(
                        "Daily routine",
                        "This is the every day activities.",
                        cyan400,
                        1,
                        mondayStartDateTimestamp,
                        true,
                        currentDateTimestamp,
                        0
                )
        );
        final long studyRoutineId = localRoomDatabase.getRoutineDao().insertRoutine(
                new Routine(
                        "Practice",
                        "Study sessions.",
                        teal400,
                        6,
                        mondayStartDateTimestamp,
                        true,
                        currentDateTimestamp,
                        0
                )
        );
        final long workRoutineId = localRoomDatabase.getRoutineDao().insertRoutine(
                new Routine(
                        "Work",
                        "Work routine.",
                        red400,
                        7,
                        mondayStartDateTimestamp,
                        true,
                        currentDateTimestamp,
                        0
                )
        );
        final long exerciseRoutineId = localRoomDatabase.getRoutineDao().insertRoutine(
                new Routine(
                        "Exercise routine",
                        "Exercise routine.",
                        blue400,
                        7,
                        mondayStartDateTimestamp,
                        true,
                        currentDateTimestamp,
                        0
                )
        );

        //RoutineActivities
        localRoomDatabase.getRoutineActivityJoinDao().insertRoutineActivityJoin(
                new RoutineActivityJoin(
                        dailyRoutineId,
                        breakfastActivityId,
                        0,
                        generateTime(6, 25),
                        generateTime(6, 40),
                        true,
                        currentDateTimestamp,
                        0
                )
        );
        localRoomDatabase.getRoutineActivityJoinDao().insertRoutineActivityJoin(
                new RoutineActivityJoin(
                        dailyRoutineId,
                        lunchActivityId,
                        0,
                        generateTime(13, 0),
                        generateTime(13, 40),
                        true,
                        currentDateTimestamp,
                        0
                )
        );
        localRoomDatabase.getRoutineActivityJoinDao().insertRoutineActivityJoin(
                new RoutineActivityJoin(
                        dailyRoutineId,
                        dinnerActivityId,
                        0,
                        generateTime(17, 30),
                        generateTime(18, 0),
                        true,
                        currentDateTimestamp,
                        0
                )
        );
        localRoomDatabase.getRoutineActivityJoinDao().insertRoutineActivityJoin(
                new RoutineActivityJoin(
                        dailyRoutineId,
                        sleepActivityId,
                        0,
                        generateTime(22, 45),
                        generateTime(6, 15),
                        true,
                        currentDateTimestamp,
                        0
                )
        );

        localRoomDatabase.getRoutineActivityJoinDao().insertRoutineActivityJoin(
                new RoutineActivityJoin(
                        studyRoutineId,
                        pianoActivityId,
                        0,
                        generateTime(16, 30),
                        generateTime(17, 20),
                        true,
                        currentDateTimestamp,
                        0
                )
        );
        localRoomDatabase.getRoutineActivityJoinDao().insertRoutineActivityJoin(
                new RoutineActivityJoin(
                        studyRoutineId,
                        readingActivityId,
                        1,
                        generateTime(16, 30),
                        generateTime(17, 20),
                        true,
                        currentDateTimestamp,
                        0
                )
        );
        localRoomDatabase.getRoutineActivityJoinDao().insertRoutineActivityJoin(
                new RoutineActivityJoin(
                        studyRoutineId,
                        pianoActivityId,
                        2,
                        generateTime(16, 30),
                        generateTime(17, 20),
                        true,
                        currentDateTimestamp,
                        0
                )
        );
        localRoomDatabase.getRoutineActivityJoinDao().insertRoutineActivityJoin(
                new RoutineActivityJoin(
                        studyRoutineId,
                        readingActivityId,
                        3,
                        generateTime(16, 30),
                        generateTime(17, 20),
                        true,
                        currentDateTimestamp,
                        0
                )
        );
        localRoomDatabase.getRoutineActivityJoinDao().insertRoutineActivityJoin(
                new RoutineActivityJoin(
                        studyRoutineId,
                        pianoActivityId,
                        4,
                        generateTime(16, 30),
                        generateTime(17, 20),
                        true,
                        currentDateTimestamp,
                        0
                )
        );
        localRoomDatabase.getRoutineActivityJoinDao().insertRoutineActivityJoin(
                new RoutineActivityJoin(
                        studyRoutineId,
                        readingActivityId,
                        5,
                        generateTime(16, 30),
                        generateTime(17, 20),
                        true,
                        currentDateTimestamp,
                        0
                )
        );

        localRoomDatabase.getRoutineActivityJoinDao().insertRoutineActivityJoin(
                new RoutineActivityJoin(
                        workRoutineId,
                        workActivityId,
                        0,
                        generateTime(9, 0),
                        generateTime(16, 0),
                        true,
                        currentDateTimestamp,
                        0
                )
        );
        localRoomDatabase.getRoutineActivityJoinDao().insertRoutineActivityJoin(
                new RoutineActivityJoin(
                        workRoutineId,
                        workActivityId,
                        1,
                        generateTime(9, 0),
                        generateTime(16, 0),
                        true,
                        currentDateTimestamp,
                        0
                )
        );
        localRoomDatabase.getRoutineActivityJoinDao().insertRoutineActivityJoin(
                new RoutineActivityJoin(
                        workRoutineId,
                        workActivityId,
                        2,
                        generateTime(9, 0),
                        generateTime(16, 0),
                        true,
                        currentDateTimestamp,
                        0
                )
        );
        localRoomDatabase.getRoutineActivityJoinDao().insertRoutineActivityJoin(
                new RoutineActivityJoin(
                        workRoutineId,
                        workActivityId,
                        3,
                        generateTime(9, 0),
                        generateTime(16, 0),
                        true,
                        currentDateTimestamp,
                        0
                )
        );
        localRoomDatabase.getRoutineActivityJoinDao().insertRoutineActivityJoin(
                new RoutineActivityJoin(
                        workRoutineId,
                        workActivityId,
                        4,
                        generateTime(9, 0),
                        generateTime(16, 0),
                        true,
                        currentDateTimestamp,
                        0
                )
        );

        localRoomDatabase.getRoutineActivityJoinDao().insertRoutineActivityJoin(
                new RoutineActivityJoin(
                        exerciseRoutineId,
                        gymActivityId,
                        0,
                        generateTime(7, 0),
                        generateTime(8, 30),
                        true,
                        currentDateTimestamp,
                        0
                )
        );
        localRoomDatabase.getRoutineActivityJoinDao().insertRoutineActivityJoin(
                new RoutineActivityJoin(
                        exerciseRoutineId,
                        gymActivityId,
                        1,
                        generateTime(7, 0),
                        generateTime(8, 30),
                        true,
                        currentDateTimestamp,
                        0
                )
        );
        localRoomDatabase.getRoutineActivityJoinDao().insertRoutineActivityJoin(
                new RoutineActivityJoin(
                        exerciseRoutineId,
                        gymActivityId,
                        2,
                        generateTime(7, 0),
                        generateTime(8, 30),
                        true,
                        currentDateTimestamp,
                        0
                )
        );
        localRoomDatabase.getRoutineActivityJoinDao().insertRoutineActivityJoin(
                new RoutineActivityJoin(
                        exerciseRoutineId,
                        gymActivityId,
                        3,
                        generateTime(7, 0),
                        generateTime(8, 30),
                        true,
                        currentDateTimestamp,
                        0
                )
        );
        localRoomDatabase.getRoutineActivityJoinDao().insertRoutineActivityJoin(
                new RoutineActivityJoin(
                        exerciseRoutineId,
                        gymActivityId,
                        4,
                        generateTime(7, 0),
                        generateTime(8, 30),
                        true,
                        currentDateTimestamp,
                        0
                )
        );
        localRoomDatabase.getRoutineActivityJoinDao().insertRoutineActivityJoin(
                new RoutineActivityJoin(
                        exerciseRoutineId,
                        runningActivityId,
                        6,
                        generateTime(7, 0),
                        generateTime(9, 0),
                        true,
                        currentDateTimestamp,
                        0
                )
        );
    }

    private int generateTime(final int hourOfDay, final int minute) {
        return (hourOfDay * 3600) + (minute * 60);
    }
}
