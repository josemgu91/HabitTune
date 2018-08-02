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

package com.josemgu91.habittune;

import android.os.Parcel;
import android.support.v4.app.Fragment;

public class ScheduleKey extends BaseKey {
    @Override
    protected Fragment createFragment() {
        return new FragmentSchedule();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public static final Creator<ScheduleKey> CREATOR = new Creator<ScheduleKey>() {
        @Override
        public ScheduleKey createFromParcel(Parcel source) {
            return new ScheduleKey();
        }

        @Override
        public ScheduleKey[] newArray(int size) {
            return new ScheduleKey[size];
        }
    };
}
