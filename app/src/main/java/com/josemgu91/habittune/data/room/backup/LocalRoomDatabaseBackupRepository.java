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

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;

import com.josemgu91.habittune.data.file.ContentResolverFile;
import com.josemgu91.habittune.data.room.LocalRoomDatabase;
import com.josemgu91.habittune.domain.datagateways.BackupDataGateway;
import com.josemgu91.habittune.domain.datagateways.DataGatewayException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LocalRoomDatabaseBackupRepository implements BackupDataGateway {

    private final JsonBuilder jsonBuilder;
    private final DatabaseImporter databaseImporter;
    private final LocalRoomDatabase localRoomDatabase;
    private final ContentResolverFile contentResolverFile;

    private final static String CHARSET = "UTF-8";

    public LocalRoomDatabaseBackupRepository(final LocalRoomDatabase localRoomDatabase, final Context context) {
        this.jsonBuilder = new JsonBuilder(localRoomDatabase);
        this.databaseImporter = new DatabaseImporter(localRoomDatabase);
        this.localRoomDatabase = localRoomDatabase;
        this.contentResolverFile = new ContentResolverFile(context, CHARSET);
    }

    @Override
    public void importFrom(@NonNull String fileUriString) throws DataGatewayException {
        try {
            final Uri fileUri = Uri.parse(fileUriString);
            localRoomDatabase.clearAllTables();
            final String jsonString = contentResolverFile.readFileAsString(fileUri);
            final boolean success = databaseImporter.rebuildDatabase(new JSONObject(jsonString));
            if (!success) {
                throw new DataGatewayException("Can't build database!");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            throw new DataGatewayException(e.getMessage());
        }
    }

    @Override
    public void exportTo(@NonNull String fileUriString) throws DataGatewayException {
        try {
            final Uri fileUri = Uri.parse(fileUriString);
            final JSONObject jsonObject = jsonBuilder.buildJsonObject();
            contentResolverFile.writeStringToFile(fileUri, jsonObject.toString());
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            throw new DataGatewayException(e.getMessage());
        }
    }
}
