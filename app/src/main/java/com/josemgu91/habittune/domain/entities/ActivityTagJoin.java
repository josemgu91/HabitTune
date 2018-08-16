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

package com.josemgu91.habittune.domain.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

@Entity(tableName = "activityTagJoins",
        primaryKeys = {"activityName", "tagName"},
        foreignKeys = {
                @ForeignKey(entity = Activity.class,
                        parentColumns = "name",
                        childColumns = "activityName"),
                @ForeignKey(entity = Tag.class,
                        parentColumns = "name",
                        childColumns = "tagName")
        })
public class ActivityTagJoin {

    public final String activityName;
    public final String tagName;

    public ActivityTagJoin(String activityName, String tagName) {
        this.activityName = activityName;
        this.tagName = tagName;
    }
}
