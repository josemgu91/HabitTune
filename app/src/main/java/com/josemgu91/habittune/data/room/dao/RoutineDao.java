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

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

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

    @Query("SELECT * FROM routines ORDER BY name ASC")
    List<Routine> getAllRoutines();

    @Delete
    int deleteRoutine(final Routine routine);

    @Query("UPDATE routines SET name = :name, description = :description, color = :color WHERE id = :routineId")
    int updateRoutine(final String routineId, final String name, final String description, final int color);

}
