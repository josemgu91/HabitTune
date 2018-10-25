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

package com.josemgu91.habittune.data.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import com.josemgu91.habittune.data.room.custom_responses.RoutineEntry;

import java.util.List;

@Dao
public interface StatisticsDao {

    @NonNull
    @Query("SELECT \n" +
            "routineActivityJoins.id AS 'routineActivityJoinId',\n" +
            "routineActivityJoins.creationDateTimestamp AS 'routineActivityJoinCreationDateTimestamp',\n" +
            "routineActivityJoins.day AS 'routineActivityJoinEntryDay',\n" +
            "routineActivityJoins.activityId AS 'activityId',\n" +
            "routines.id AS 'routineId',\n" +
            "routines.startDateTimestamp AS 'routineStartDateTimestamp',\n" +
            "routines.numberOfDays AS 'routineNumberOfDays'\n" +
            "FROM routineActivityJoins\n" +
            "INNER JOIN routines WHERE activityId = :activityId AND routineActivityJoins.routineId = routines.id")
    List<RoutineEntry> getRoutineActivityJoinsWithRoutineInfoByActivityId(final long activityId);

    @Query("SELECT count(id) FROM assistanceRegisters WHERE routineActivityJoinId IN (SELECT id FROM routineActivityJoins WHERE activityId = :activityId) AND startTime != '-1' AND endTime != '-1'")
    int countCompletedAssistanceRegistersByActivityId(final long activityId);

}
