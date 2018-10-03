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

import com.josemgu91.habittune.data.room.model.Routine;

import java.util.List;

@Dao
public interface RoutineDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertRoutine(final Routine routine);

    @Query("SELECT * FROM routines WHERE id = :id")
    Routine getRoutineById(final String id);

    @Query("SELECT * FROM routines ORDER BY name ASC")
    LiveData<List<Routine>> subscribeToAllRoutines();

    @Delete
    int deleteRoutine(final Routine routine);

    @Query("UPDATE routines SET name = :name, description = :description, color = :color WHERE id = :routineId")
    int updateRoutine(final String routineId, final String name, final String description, final int color);

}
