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
import androidx.room.Update;

import com.josemgu91.habittune.data.room.model.Activity;

import java.util.List;

@Dao
public interface ActivityDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertActivity(final Activity activity);

    @Query("SELECT * FROM activities ORDER BY name ASC")
    LiveData<List<Activity>> subscribeToAllActivities();

    @Query("SELECT * FROM activities ORDER BY name ASC")
    List<Activity> getAllActivities();

    @Query("SELECT * FROM activities WHERE id = :activityId")
    LiveData<Activity> subscribeToActivityById(final long activityId);

    @Query("SELECT * FROM activities WHERE id = :activityId")
    Activity getActivityById(final long activityId);

    @Update(onConflict = OnConflictStrategy.ABORT)
    int updateActivity(final Activity activity);

    @Delete
    int deleteActivity(final Activity activity);
}
