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

import com.josemgu91.habittune.data.room.LocalRoomDatabase;
import com.josemgu91.habittune.data.room.model.Activity;
import com.josemgu91.habittune.data.room.model.AssistanceRegister;
import com.josemgu91.habittune.data.room.model.Routine;
import com.josemgu91.habittune.data.room.model.RoutineActivityJoin;
import com.josemgu91.habittune.data.room.model.Tag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class JsonBuilder {

    private final static String ROUTINES = "routines";
    private final static String ACTIVITIES = "activities";
    private final static String TAGS = "tags";
    private final static String ROUTINE_ENTRIES = "routineEntries";
    private final static String ASSISTANCE_REGISTERS = "assistanceRegisters";

    private final static String ROUTINE_ID = "id";
    private final static String ROUTINE_NAME = "name";
    private final static String ROUTINE_DESCRIPTION = "description";
    private final static String ROUTINE_COLOR = "color";
    private final static String ROUTINE_NUMBER_OF_DAYS = "numberOfDays";
    private final static String ROUTINE_START_DATE = "startDate";
    private final static String ROUTINE_ROUTINE_ENTRIES_IDS = "routineEntriesIds";
    private final static String ROUTINE_ENABLED = "enabled";
    private final static String ROUTINE_CREATION_DATE = "creationDate";
    private final static String ROUTINE_DEACTIVATION_DATE = "deactivationDate";

    private final static String ACTIVITY_ID = "id";
    private final static String ACTIVITY_NAME = "name";
    private final static String ACTIVITY_DESCRIPTION = "description";
    private final static String ACTIVITY_COLOR = "color";
    private final static String ACTIVITY_TAGS_IDS = "tagsIds";

    private final static String TAG_ID = "id";
    private final static String TAG_NAME = "name";

    private final static String ROUTINE_ENTRY_ID = "id";
    private final static String ROUTINE_ENTRY_DAY = "day";
    private final static String ROUTINE_ENTRY_START_TIME = "startTime";
    private final static String ROUTINE_ENTRY_END_TIME = "endTime";
    private final static String ROUTINE_ENTRY_ACTIVITY_ID = "activityId";
    private final static String ROUTINE_ENTRY_ENABLED = "enabled";
    private final static String ROUTINE_ENTRY_CREATION_DATE = "creationDate";
    private final static String ROUTINE_ENTRY_DEACTIVATION_DATE = "deactivationDate";
    private final static String ROUTINE_ENTRY_ASSISTANCE_REGISTERS_IDS = "assistanceRegistersIds";

    private final static String ASSISTANCE_REGISTER_ID = "id";
    private final static String ASSISTANCE_REGISTER_CYCLE_NUMBER = "cycleNumber";
    private final static String ASSISTANCE_REGISTER_START_TIME = "startTime";
    private final static String ASSISTANCE_REGISTER_END_TIME = "endTime";

    private final LocalRoomDatabase localRoomDatabase;

    public JsonBuilder(LocalRoomDatabase localRoomDatabase) {
        this.localRoomDatabase = localRoomDatabase;
    }

    public JSONObject buildJsonObject() throws JSONException {
        final JSONObject exportJsonObject = new JSONObject();
        //Activities
        final JSONArray activitiesJsonArray = new JSONArray();
        final List<Activity> activityRoomModels = localRoomDatabase.getActivityDao().getAllActivities();
        for (final Activity activityRoomModel : activityRoomModels) {
            final List<Tag> tagRoomModels = localRoomDatabase.getActivityTagJoinDao().getAllTagsByActivityId(activityRoomModel.id);
            activitiesJsonArray.put(parseToJson(activityRoomModel, tagRoomModels));
        }
        exportJsonObject.put(ACTIVITIES, activitiesJsonArray);
        //Tags
        final JSONArray tagsJsonArray = new JSONArray();
        final List<Tag> tagRoomModels = localRoomDatabase.getTagDao().getAllTags();
        for (final Tag tagRoomModel : tagRoomModels) {
            tagsJsonArray.put(parseToJson(tagRoomModel));
        }
        exportJsonObject.put(TAGS, tagsJsonArray);
        //Routines
        final JSONArray routinesJsonArray = new JSONArray();
        final List<Routine> routineRoomModels = localRoomDatabase.getRoutineDao().getAllRoutines();
        for (final Routine routineRoomModel : routineRoomModels) {
            final List<RoutineActivityJoin> routineActivityJoinRoomModels = localRoomDatabase.getRoutineActivityJoinDao().getAllRoutineActivityJoinsByRoutineId(routineRoomModel.id);
            routinesJsonArray.put(parseToJson(routineRoomModel, routineActivityJoinRoomModels));
        }
        exportJsonObject.put(ROUTINES, routinesJsonArray);
        //Routine Entries
        final JSONArray routineEntriesJsonArray = new JSONArray();
        final List<RoutineActivityJoin> routineActivityJoinRoomModels = localRoomDatabase.getRoutineActivityJoinDao().getAllRoutineActivityJoins();
        for (final RoutineActivityJoin routineActivityJoinRoomModel : routineActivityJoinRoomModels) {
            final List<AssistanceRegister> assistanceRegisterRoomModels = localRoomDatabase.getAssistanceRegisterDao().getAssistanceRegistersByRoutineActivityJoinId(routineActivityJoinRoomModel.id);
            routineEntriesJsonArray.put(parseToJson(routineActivityJoinRoomModel, assistanceRegisterRoomModels));
        }
        exportJsonObject.put(ROUTINE_ENTRIES, routineEntriesJsonArray);
        //Assistance Registers
        final JSONArray assistanceRegistersJsonArray = new JSONArray();
        final List<AssistanceRegister> assistanceRegisterRoomModels = localRoomDatabase.getAssistanceRegisterDao().getAllAssistanceRegisters();
        for (final AssistanceRegister assistanceRegisterRoomModel : assistanceRegisterRoomModels) {
            assistanceRegistersJsonArray.put(parseToJson(assistanceRegisterRoomModel));
        }
        exportJsonObject.put(ASSISTANCE_REGISTERS, assistanceRegistersJsonArray);

        return exportJsonObject;
    }

    private JSONObject parseToJson(final Activity activityRoomModel, final List<Tag> tagRoomModels) throws JSONException {
        final JSONArray tagsJsonArray = new JSONArray();
        final JSONObject activityJsonObject = new JSONObject();
        activityJsonObject
                .put(ACTIVITY_ID, activityRoomModel.id)
                .put(ACTIVITY_NAME, activityRoomModel.name)
                .put(ACTIVITY_DESCRIPTION, activityRoomModel.description)
                .put(ACTIVITY_COLOR, activityRoomModel.color)
                .put(ACTIVITY_TAGS_IDS, tagsJsonArray);
        for (final Tag tagRoomModel : tagRoomModels) {
            tagsJsonArray.put(tagRoomModel.id);
        }
        return activityJsonObject;
    }

    private JSONObject parseToJson(final Tag tagRoomModel) throws JSONException {
        final JSONObject tagJsonObject = new JSONObject();
        tagJsonObject
                .put(TAG_ID, tagRoomModel.id)
                .put(TAG_NAME, tagRoomModel.name);
        return tagJsonObject;
    }

    private JSONObject parseToJson(final Routine routineRoomModel, final List<RoutineActivityJoin> routineActivityJoinRoomModels) throws JSONException {
        final JSONArray routineEntriesJsonArray = new JSONArray();
        final JSONObject routineJsonObject = new JSONObject();
        routineJsonObject
                .put(ROUTINE_ID, routineRoomModel.id)
                .put(ROUTINE_NAME, routineRoomModel.name)
                .put(ROUTINE_DESCRIPTION, routineRoomModel.description)
                .put(ROUTINE_COLOR, routineRoomModel.color)
                .put(ROUTINE_NUMBER_OF_DAYS, routineRoomModel.numberOfDays)
                .put(ROUTINE_START_DATE, routineRoomModel.startDateTimestamp)
                .put(ROUTINE_ROUTINE_ENTRIES_IDS, routineEntriesJsonArray)
                .put(ROUTINE_ENABLED, routineRoomModel.enabled)
                .put(ROUTINE_CREATION_DATE, routineRoomModel.creationDateTimestamp)
                .put(ROUTINE_DEACTIVATION_DATE, routineRoomModel.deactivationDateTimestamp);
        for (final RoutineActivityJoin routineActivityJoinRoomModel : routineActivityJoinRoomModels) {
            routineEntriesJsonArray.put(routineActivityJoinRoomModel.id);
        }
        return routineJsonObject;
    }

    private JSONObject parseToJson(final RoutineActivityJoin routineActivityJoinRoomModel, final List<AssistanceRegister> assistanceRegisterRoomModels) throws JSONException {
        final JSONArray assistanceRegistersJsonArray = new JSONArray();
        final JSONObject routineEntryJsonObject = new JSONObject();
        routineEntryJsonObject
                .put(ROUTINE_ENTRY_ID, routineActivityJoinRoomModel.id)
                .put(ROUTINE_ENTRY_DAY, routineActivityJoinRoomModel.day)
                .put(ROUTINE_ENTRY_START_TIME, routineActivityJoinRoomModel.startTime)
                .put(ROUTINE_ENTRY_END_TIME, routineActivityJoinRoomModel.endTime)
                .put(ROUTINE_ENTRY_ACTIVITY_ID, routineActivityJoinRoomModel.activityId)
                .put(ROUTINE_ENTRY_ENABLED, routineActivityJoinRoomModel.enabled)
                .put(ROUTINE_ENTRY_CREATION_DATE, routineActivityJoinRoomModel.creationDateTimestamp)
                .put(ROUTINE_ENTRY_DEACTIVATION_DATE, routineActivityJoinRoomModel.deactivationDateTimestamp)
                .put(ROUTINE_ENTRY_ASSISTANCE_REGISTERS_IDS, assistanceRegistersJsonArray);
        for (final AssistanceRegister assistanceRegisterRoomModel : assistanceRegisterRoomModels) {
            assistanceRegistersJsonArray.put(assistanceRegisterRoomModel.id);
        }
        return routineEntryJsonObject;
    }

    private JSONObject parseToJson(final AssistanceRegister assistanceRegister) throws JSONException {
        final JSONObject assistanceRegisterJsonObject = new JSONObject();
        assistanceRegisterJsonObject
                .put(ASSISTANCE_REGISTER_ID, assistanceRegister.id)
                .put(ASSISTANCE_REGISTER_CYCLE_NUMBER, assistanceRegister.cycleNumber)
                .put(ASSISTANCE_REGISTER_START_TIME, assistanceRegister.startTime)
                .put(ASSISTANCE_REGISTER_END_TIME, assistanceRegister.endTime);
        return assistanceRegisterJsonObject;
    }

}
