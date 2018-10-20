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

package com.josemgu91.habittune.data.room.backup;

public interface JsonExportFormatContract {

    public final static String ROUTINES = "routines";
    public final static String ACTIVITIES = "activities";
    public final static String TAGS = "tags";
    public final static String ROUTINE_ENTRIES = "routineEntries";
    public final static String ASSISTANCE_REGISTERS = "assistanceRegisters";

    public final static String ROUTINE_ID = "id";
    public final static String ROUTINE_NAME = "name";
    public final static String ROUTINE_DESCRIPTION = "description";
    public final static String ROUTINE_COLOR = "color";
    public final static String ROUTINE_NUMBER_OF_DAYS = "numberOfDays";
    public final static String ROUTINE_START_DATE = "startDate";
    public final static String ROUTINE_ROUTINE_ENTRIES_IDS = "routineEntriesIds";
    public final static String ROUTINE_ENABLED = "enabled";
    public final static String ROUTINE_CREATION_DATE = "creationDate";
    public final static String ROUTINE_DEACTIVATION_DATE = "deactivationDate";

    public final static String ACTIVITY_ID = "id";
    public final static String ACTIVITY_NAME = "name";
    public final static String ACTIVITY_DESCRIPTION = "description";
    public final static String ACTIVITY_COLOR = "color";
    public final static String ACTIVITY_TAGS_IDS = "tagsIds";

    public final static String TAG_ID = "id";
    public final static String TAG_NAME = "name";

    public final static String ROUTINE_ENTRY_ID = "id";
    public final static String ROUTINE_ENTRY_DAY = "day";
    public final static String ROUTINE_ENTRY_START_TIME = "startTime";
    public final static String ROUTINE_ENTRY_END_TIME = "endTime";
    public final static String ROUTINE_ENTRY_ACTIVITY_ID = "activityId";
    public final static String ROUTINE_ENTRY_ENABLED = "enabled";
    public final static String ROUTINE_ENTRY_CREATION_DATE = "creationDate";
    public final static String ROUTINE_ENTRY_DEACTIVATION_DATE = "deactivationDate";
    public final static String ROUTINE_ENTRY_ASSISTANCE_REGISTERS_IDS = "assistanceRegistersIds";

    public final static String ASSISTANCE_REGISTER_ID = "id";
    public final static String ASSISTANCE_REGISTER_CYCLE_NUMBER = "cycleNumber";
    public final static String ASSISTANCE_REGISTER_START_TIME = "startTime";
    public final static String ASSISTANCE_REGISTER_END_TIME = "endTime";
}
