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

package com.josemgu91.habittune.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.josemgu91.habittune.data.model.Activity;
import com.josemgu91.habittune.data.model.ActivityTagJoin;
import com.josemgu91.habittune.data.model.Tag;

@Database(entities = {Activity.class, ActivityTagJoin.class, Tag.class}, version = 1)
public abstract class LocalRoomDatabase extends RoomDatabase {
}
