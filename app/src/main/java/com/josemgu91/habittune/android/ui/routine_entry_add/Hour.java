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

package com.josemgu91.habittune.android.ui.routine_entry_add;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.util.Calendar;

public class Hour implements Parcelable {

    protected final int hourOfDay;
    protected final int minute;

    public static Hour currentHour() {
        final Calendar calendar = Calendar.getInstance();
        final int currentHourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        final int currentMinute = calendar.get(Calendar.MINUTE);
        return new Hour(currentHourOfDay, currentMinute);
    }

    public Hour(int timeInSeconds) {
        this.hourOfDay = timeInSeconds / 3600;
        this.minute = (timeInSeconds % 3600) / 60;
    }

    public Hour(int hourOfDay, int minute) {
        this.hourOfDay = hourOfDay;
        this.minute = minute;
    }

    protected Hour(Parcel in) {
        hourOfDay = in.readInt();
        minute = in.readInt();
    }

    public static final Creator<Hour> CREATOR = new Creator<Hour>() {
        @Override
        public Hour createFromParcel(Parcel in) {
            return new Hour(in);
        }

        @Override
        public Hour[] newArray(int size) {
            return new Hour[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(hourOfDay);
        dest.writeInt(minute);
    }

    public int inSeconds() {
        return (hourOfDay * 3600) + (minute * 60);
    }

    //TODO: Reuse.
    public String format() {
        final DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        return dateFormat.format(calendar.getTime());
    }
}
