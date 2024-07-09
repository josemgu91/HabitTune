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

package com.josemgu91.habittune.data.room.csv;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;

import com.josemgu91.habittune.data.file.ContentResolverFile;
import com.josemgu91.habittune.data.room.LocalRoomDatabase;
import com.josemgu91.habittune.data.room.custom_responses.RoutineEntry;
import com.josemgu91.habittune.domain.datagateways.DataGatewayException;
import com.josemgu91.habittune.domain.datagateways.ExportDataGateway;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.siegmar.fastcsv.writer.CsvWriter;

public class CsvExporter implements ExportDataGateway {

    private final LocalRoomDatabase localRoomDatabase;
    private final ContentResolverFile contentResolverFile;

    private final static String CHARSET = "UTF-8";

    public CsvExporter(LocalRoomDatabase localRoomDatabase, Context context) {
        this.localRoomDatabase = localRoomDatabase;
        this.contentResolverFile = new ContentResolverFile(context, CHARSET);
    }

    @Override
    public void exportToCsv(@NonNull String activityId, @NonNull String fileUriString) throws DataGatewayException {
        try {
            final Collection<String[]> csvData = new ArrayList<>();
            final List<RoutineEntry> routineEntries = localRoomDatabase.getStatisticsDao().getRoutineActivityJoinsWithRoutineInfoByActivityId(
                    Long.valueOf(activityId)
            );
            csvData.add(new String[]{
                    "routineActivityJoinId",
                    "routineActivityJoinCreationDateTimestamp",
                    "routineActivityJoinEntryDay",
                    "activityId",
                    "routineId",
                    "routineStartDateTimestamp",
                    "routineNumberOfDays"
            });
            for (final RoutineEntry routineEntry : routineEntries) {
                csvData.add(new String[]{
                        String.valueOf(routineEntry.routineActivityJoinId),
                        String.valueOf(routineEntry.routineActivityJoinCreationDateTimestamp),
                        String.valueOf(routineEntry.routineActivityJoinEntryDay),
                        String.valueOf(routineEntry.activityId),
                        String.valueOf(routineEntry.routineId),
                        String.valueOf(routineEntry.routineStartDateTimestamp),
                        String.valueOf(routineEntry.routineNumberOfDays)
                });
            }
            final Uri uriFile = Uri.parse(fileUriString);
            final Writer writer = contentResolverFile.getWriter(uriFile);
            final CsvWriter csvWriter = new CsvWriter();
            csvWriter.write(writer, csvData);
        } catch (IOException e) {
            e.printStackTrace();
            throw new DataGatewayException(e.getMessage());
        }
    }
}
