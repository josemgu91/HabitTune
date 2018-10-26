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

package com.josemgu91.habittune.data.file;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Scanner;

public class ContentResolverFile {

    private final ContentResolver contentResolver;
    private final String charset;

    public ContentResolverFile(Context context, final String charset) {
        this.contentResolver = context.getContentResolver();
        this.charset = charset;
    }

    public final String readFileAsString(final Uri contentFileUri) throws IOException {
        final InputStream inputStream = contentResolver.openInputStream(contentFileUri);
        if (inputStream == null) {
            throw new IOException("Can't open input stream!");
        }
        final Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        if (!scanner.hasNext()) {
            throw new IOException("Empty file!");
        }
        return scanner.next();
    }

    public final boolean writeStringToFile(final Uri contentFileUri, final String stringToWrite) throws IOException {
        final OutputStream outputStream = contentResolver.openOutputStream(contentFileUri);
        if (outputStream == null) {
            throw new IOException("Can't open output stream!");
        }
        final Writer outputStreamWriter = getWriter(contentFileUri);
        outputStreamWriter.write(stringToWrite);
        outputStreamWriter.close();
        outputStream.close();
        return true;
    }

    public final Writer getWriter(final Uri contentFileUri) throws IOException {
        final OutputStream outputStream = contentResolver.openOutputStream(contentFileUri);
        if (outputStream == null) {
            throw new IOException("Can't open output stream!");
        }
        return new OutputStreamWriter(outputStream, charset);
    }

}
