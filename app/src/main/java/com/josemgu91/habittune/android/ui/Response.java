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

package com.josemgu91.habittune.android.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Response<SuccessData, ErrorData> {

    @NonNull
    public final Status status;
    @Nullable
    public final SuccessData successData;
    @Nullable
    public final ErrorData errorData;

    public Response(@NonNull final Status status, @Nullable final SuccessData successData, @Nullable final ErrorData errorData) {
        this.status = status;
        this.successData = successData;
        this.errorData = errorData;
    }

    public enum Status {
        LOADING,
        ERROR,
        SUCCESS
    }

}
