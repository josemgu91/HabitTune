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

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.josemgu91.habittune.data.room.model.Activity;
import com.josemgu91.habittune.data.room.model.RoutineActivityJoin;

import java.util.List;

@Dao
public interface RoutineActivityJoinDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertRoutineActivityJoin(final RoutineActivityJoin routineActivityJoin);

    @Delete
    int deleteRoutineActivityJoin(final RoutineActivityJoin routineActivityJoin);

    @Query("SELECT activities.id AS `id`, activities.name AS `name`, activities.description AS `description`, activities.color AS `color` FROM activities INNER JOIN routineActivityJoins ON activities.id = routineActivityJoins.activityId WHERE routineActivityJoins.routineId = :routineId")
    LiveData<List<Activity>> subscribeToAllActivitiesByRoutineId(final long routineId);

}
