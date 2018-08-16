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

package com.josemgu91.habittune.data.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.josemgu91.habittune.data.room.dao.ActivityDao;
import com.josemgu91.habittune.data.room.dao.ActivityTagJoinDao;
import com.josemgu91.habittune.data.room.dao.TagDao;
import com.josemgu91.habittune.data.room.model.Activity;
import com.josemgu91.habittune.data.room.model.ActivityTagJoin;
import com.josemgu91.habittune.data.room.model.Tag;

/*TODO: Set exportSchema directory when the final version releases.*/
@Database(entities = {Activity.class, ActivityTagJoin.class, Tag.class}, version = 1, exportSchema = false)
public abstract class LocalRoomDatabase extends RoomDatabase {

    public abstract ActivityDao getActivityDao();

    public abstract TagDao getTagDao();

    public abstract ActivityTagJoinDao getActivityTagJoinDao();
}
