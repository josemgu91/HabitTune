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

package com.josemgu91.habittune.android.ui.common;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFormatter {

    public String formatHour(final int hourInSeconds) {
        final DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        final Calendar calendar = Calendar.getInstance();
        final int hours = hourInSeconds / 3600;
        final int minutes = (hourInSeconds % 3600) / 60;
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        return dateFormat.format(calendar.getTime());
    }

    public String formatDate(final Date date) {
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return dateFormat.format(calendar.getTime());
    }

}
