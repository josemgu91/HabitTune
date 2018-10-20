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

import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ACTIVITIES;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ACTIVITY_COLOR;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ACTIVITY_DESCRIPTION;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ACTIVITY_ID;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ACTIVITY_NAME;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ACTIVITY_TAGS_IDS;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ASSISTANCE_REGISTERS;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ASSISTANCE_REGISTER_CYCLE_NUMBER;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ASSISTANCE_REGISTER_END_TIME;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ASSISTANCE_REGISTER_ID;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ASSISTANCE_REGISTER_START_TIME;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ROUTINES;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ROUTINE_COLOR;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ROUTINE_CREATION_DATE;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ROUTINE_DEACTIVATION_DATE;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ROUTINE_DESCRIPTION;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ROUTINE_ENABLED;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ROUTINE_ENTRIES;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ROUTINE_ENTRY_ACTIVITY_ID;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ROUTINE_ENTRY_ASSISTANCE_REGISTERS_IDS;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ROUTINE_ENTRY_CREATION_DATE;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ROUTINE_ENTRY_DAY;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ROUTINE_ENTRY_DEACTIVATION_DATE;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ROUTINE_ENTRY_ENABLED;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ROUTINE_ENTRY_END_TIME;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ROUTINE_ENTRY_ID;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ROUTINE_ENTRY_START_TIME;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ROUTINE_ID;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ROUTINE_NAME;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ROUTINE_NUMBER_OF_DAYS;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ROUTINE_ROUTINE_ENTRIES_IDS;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.ROUTINE_START_DATE;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.TAGS;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.TAG_ID;
import static com.josemgu91.habittune.data.room.backup.JsonExportFormatContract.TAG_NAME;

public class JsonBuilder {

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
