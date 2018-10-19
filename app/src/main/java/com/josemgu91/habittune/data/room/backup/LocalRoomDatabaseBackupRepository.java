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
import android.support.annotation.NonNull;

import com.josemgu91.habittune.data.room.LocalRoomDatabase;
import com.josemgu91.habittune.domain.datagateways.BackupDataGateway;
import com.josemgu91.habittune.domain.datagateways.DataGatewayException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class LocalRoomDatabaseBackupRepository implements BackupDataGateway {

    private final JsonBuilder jsonBuilder;
    private final Context context;

    private final static String CHARSET = "UTF-8";

    public LocalRoomDatabaseBackupRepository(final LocalRoomDatabase localRoomDatabase, final Context context) {
        this.jsonBuilder = new JsonBuilder(localRoomDatabase);
        this.context = context;
    }

    @Override
    public void importFrom(@NonNull String fileUriString) throws DataGatewayException {
        final Uri fileUri = Uri.parse(fileUriString);
    }

    @Override
    public void exportTo(@NonNull String fileUriString) throws DataGatewayException {
        try {
            final Uri fileUri = Uri.parse(fileUriString);
            final JSONObject jsonObject = jsonBuilder.buildJsonObject();
            final OutputStream outputStream = context.getContentResolver().openOutputStream(fileUri);
            if (outputStream == null) {
                throw new DataGatewayException("Can't open output stream !");
            }
            final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, CHARSET);
            outputStreamWriter.write(jsonObject.toString());
            outputStreamWriter.close();
            outputStream.close();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            throw new DataGatewayException(e.getMessage());
        }
    }
}
