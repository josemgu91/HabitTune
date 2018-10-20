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

import android.support.v4.util.LongSparseArray;

import com.josemgu91.habittune.data.room.LocalRoomDatabase;
import com.josemgu91.habittune.data.room.model.Activity;
import com.josemgu91.habittune.data.room.model.ActivityTagJoin;
import com.josemgu91.habittune.data.room.model.AssistanceRegister;
import com.josemgu91.habittune.data.room.model.Routine;
import com.josemgu91.habittune.data.room.model.RoutineActivityJoin;
import com.josemgu91.habittune.data.room.model.Tag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class DatabaseImporter {

    private final LocalRoomDatabase localRoomDatabase;

    private final LongSparseArray<Long> activitiesIds;
    private final LongSparseArray<Long> tagsIds;
    private final LongSparseArray<Long> routinesIds;
    private final LongSparseArray<Long> routineEntriesIds;
    private final LongSparseArray<Long> assistanceRegistersIds;

    public DatabaseImporter(LocalRoomDatabase localRoomDatabase) {
        this.localRoomDatabase = localRoomDatabase;
        activitiesIds = new LongSparseArray<>();
        tagsIds = new LongSparseArray<>();
        routinesIds = new LongSparseArray<>();
        routineEntriesIds = new LongSparseArray<>();
        assistanceRegistersIds = new LongSparseArray<>();
    }

    public boolean rebuildDatabase(final JSONObject exportedJsonObject) throws JSONException {
        if (!hasValidFormat(exportedJsonObject)) {
            return false;
        }
        //Activities
        final JSONArray activitiesJsonArray = exportedJsonObject.getJSONArray(ACTIVITIES);
        for (int i = 0; i < activitiesJsonArray.length(); i++) {
            final JSONObject activityJsonObject = activitiesJsonArray.getJSONObject(i);
            final long jsonActivityId = activityJsonObject.getLong(ACTIVITY_ID);
            final long roomActivityId = localRoomDatabase.getActivityDao().insertActivity(parseActivityFromJson(activityJsonObject));
            activitiesIds.append(jsonActivityId, roomActivityId);
        }
        //Tags
        final JSONArray tagsJsonArray = exportedJsonObject.getJSONArray(TAGS);
        for (int i = 0; i < tagsJsonArray.length(); i++) {
            final JSONObject tagJsonObject = tagsJsonArray.getJSONObject(i);
            final long jsonTagId = tagJsonObject.getLong(TAG_ID);
            final long roomTagId = localRoomDatabase.getTagDao().insertTag(parseTagFromJson(tagJsonObject));
            tagsIds.append(jsonTagId, roomTagId);
        }
        //Routines
        final JSONArray routinesJsonArray = exportedJsonObject.getJSONArray(ROUTINES);
        for (int i = 0; i < routinesJsonArray.length(); i++) {
            final JSONObject routineJsonObject = routinesJsonArray.getJSONObject(i);
            final long jsonRoutineId = routineJsonObject.getLong(ROUTINE_ID);
            final long roomJsonId = localRoomDatabase.getRoutineDao().insertRoutine(parseRoutineFromJson(routineJsonObject));
            routinesIds.append(jsonRoutineId, roomJsonId);
        }
        //Activity Tags
        for (int i = 0; i < activitiesJsonArray.length(); i++) {
            final JSONObject activityJsonObject = activitiesJsonArray.getJSONObject(i);
            final long jsonActivityId = activityJsonObject.getLong(ACTIVITY_ID);
            final long roomActivityId = activitiesIds.get(jsonActivityId);
            final JSONArray activityTagsJsonArray = activityJsonObject.getJSONArray(ACTIVITY_TAGS_IDS);
            for (int j = 0; i < activityTagsJsonArray.length(); j++) {
                final long jsonTagId = activityTagsJsonArray.getLong(i);
                final long roomTagId = tagsIds.get(jsonTagId);
                localRoomDatabase.getActivityTagJoinDao().insertActivityTagJoin(parseActivityTagJoin(roomActivityId, roomTagId));
            }
        }
        //Routine Entries
        final JSONArray routineEntriesJsonArray = exportedJsonObject.getJSONArray(ROUTINE_ENTRIES);
        for (int i = 0; i < routineEntriesJsonArray.length(); i++) {
            final JSONObject routineEntryJsonObject = routineEntriesJsonArray.getJSONObject(i);
            final long jsonActivityId = routineEntryJsonObject.getLong(ROUTINE_ENTRY_ACTIVITY_ID);
            final long jsonRoutineEntryId = routineEntryJsonObject.getLong(ROUTINE_ENTRY_ID);
            final long roomRoutineActivityJoinId = localRoomDatabase.getRoutineActivityJoinDao().insertRoutineActivityJoin(
                    parseRoutineActivityJoin(
                            routineEntryJsonObject,
                            getRoomRoutineId(jsonRoutineEntryId, routinesJsonArray),
                            activitiesIds.get(jsonActivityId)
                    )
            );
            routineEntriesIds.append(jsonRoutineEntryId, roomRoutineActivityJoinId);
        }
        //Assistance Registers
        final JSONArray assistanceRegistersJsonArray = exportedJsonObject.getJSONArray(ASSISTANCE_REGISTERS);
        for (int i = 0; i < assistanceRegistersJsonArray.length(); i++) {
            final JSONObject assistanceRegisterJsonObject = assistanceRegistersJsonArray.getJSONObject(i);
            final long jsonAssistanceRegisterId = assistanceRegisterJsonObject.getLong(ASSISTANCE_REGISTER_ID);
            final long roomAssistanceRegisterId = localRoomDatabase.getAssistanceRegisterDao().insertAssistanceRegister(
                    parseAssistanceRegisterFromJson(
                            assistanceRegisterJsonObject,
                            getRoomRoutineActivityJoinId(jsonAssistanceRegisterId, routineEntriesJsonArray)
                    )
            );
            assistanceRegistersIds.append(jsonAssistanceRegisterId, roomAssistanceRegisterId);
        }

        return true;
    }

    //TODO: Implement exported JSON structure validation.
    private boolean hasValidFormat(final JSONObject jsonObject) {
        return true;
    }

    private long getRoomRoutineActivityJoinId(final long jsonAssistanceRegisterIdToSearch, final JSONArray routineEntriesJsonArray) throws JSONException {
        for (int i = 0; i < routineEntriesJsonArray.length(); i++) {
            final JSONObject routineEntryJsonObject = routineEntriesJsonArray.getJSONObject(i);
            final JSONArray assistanceRegistersIdsJsonArray = routineEntryJsonObject.getJSONArray(ROUTINE_ENTRY_ASSISTANCE_REGISTERS_IDS);
            for (int j = 0; j < assistanceRegistersIdsJsonArray.length(); j++) {
                final long jsonAssistanceRegisterId = assistanceRegistersIdsJsonArray.getLong(j);
                if (jsonAssistanceRegisterId == jsonAssistanceRegisterIdToSearch) {
                    final long jsonRoutineEntryId = routineEntryJsonObject.getLong(ROUTINE_ENTRY_ID);
                    final long roomRoutineActivityJoinId = routineEntriesIds.get(jsonRoutineEntryId);
                    return roomRoutineActivityJoinId;
                }
            }
        }
        throw new RuntimeException("Couldn't find the Room's RoutineActivityJoin ID!");
    }

    private long getRoomRoutineId(final long jsonRoutineEntryIdToSearch, final JSONArray routinesJsonArray) throws JSONException {
        for (int i = 0; i < routinesJsonArray.length(); i++) {
            final JSONObject routineJsonObject = routinesJsonArray.getJSONObject(i);
            final JSONArray routineEntriesIdsJsonArray = routineJsonObject.getJSONArray(ROUTINE_ROUTINE_ENTRIES_IDS);
            for (int j = 0; j < routineEntriesIdsJsonArray.length(); j++) {
                final long jsonRoutineEntryId = routineEntriesIdsJsonArray.getLong(j);
                if (jsonRoutineEntryId == jsonRoutineEntryIdToSearch) {
                    final long jsonRoutineId = routineJsonObject.getLong(ROUTINE_ID);
                    final long roomRoutineId = routinesIds.get(jsonRoutineId);
                    return roomRoutineId;
                }
            }
        }
        throw new RuntimeException("Couldn't find the Room's Routine ID!");
    }

    private ActivityTagJoin parseActivityTagJoin(final long roomActivityId, final long roomTagId) {
        return new ActivityTagJoin(
                roomActivityId,
                roomTagId
        );
    }

    private RoutineActivityJoin parseRoutineActivityJoin(final JSONObject routineActivityJoinJsonObject, final long roomRoutineId, final long roomActivityId) throws JSONException {
        return new RoutineActivityJoin(
                roomRoutineId,
                roomActivityId,
                routineActivityJoinJsonObject.getInt(ROUTINE_ENTRY_DAY),
                routineActivityJoinJsonObject.getInt(ROUTINE_ENTRY_START_TIME),
                routineActivityJoinJsonObject.getInt(ROUTINE_ENTRY_END_TIME),
                routineActivityJoinJsonObject.getBoolean(ROUTINE_ENTRY_ENABLED),
                routineActivityJoinJsonObject.getLong(ROUTINE_ENTRY_CREATION_DATE),
                routineActivityJoinJsonObject.getLong(ROUTINE_ENTRY_DEACTIVATION_DATE)
        );
    }

    private Routine parseRoutineFromJson(final JSONObject routineJsonObject) throws JSONException {
        return new Routine(
                routineJsonObject.getString(ROUTINE_NAME),
                routineJsonObject.getString(ROUTINE_DESCRIPTION),
                routineJsonObject.getInt(ROUTINE_COLOR),
                routineJsonObject.getInt(ROUTINE_NUMBER_OF_DAYS),
                routineJsonObject.getLong(ROUTINE_START_DATE),
                routineJsonObject.getBoolean(ROUTINE_ENABLED),
                routineJsonObject.getLong(ROUTINE_CREATION_DATE),
                routineJsonObject.getLong(ROUTINE_DEACTIVATION_DATE)
        );
    }

    private AssistanceRegister parseAssistanceRegisterFromJson(final JSONObject assistanceRegisterJsonObject, final long roomRoutineActivityJoinId) throws JSONException {
        return new AssistanceRegister(
                assistanceRegisterJsonObject.getInt(ASSISTANCE_REGISTER_CYCLE_NUMBER),
                assistanceRegisterJsonObject.getInt(ASSISTANCE_REGISTER_START_TIME),
                assistanceRegisterJsonObject.getInt(ASSISTANCE_REGISTER_END_TIME),
                roomRoutineActivityJoinId
        );
    }

    private Tag parseTagFromJson(final JSONObject tagJsonObject) throws JSONException {
        return new Tag(
                tagJsonObject.getString(TAG_NAME)
        );
    }

    private Activity parseActivityFromJson(final JSONObject activityJsonObject) throws JSONException {
        return new Activity(
                activityJsonObject.getString(ACTIVITY_NAME),
                activityJsonObject.getString(ACTIVITY_DESCRIPTION),
                activityJsonObject.getInt(ACTIVITY_COLOR)
        );
    }
}
