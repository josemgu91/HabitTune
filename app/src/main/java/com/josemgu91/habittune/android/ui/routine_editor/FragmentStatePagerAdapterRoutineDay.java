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

package com.josemgu91.habittune.android.ui.routine_editor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class FragmentStatePagerAdapterRoutineDay extends FragmentStatePagerAdapter {

    private int numberOfDays;
    @NonNull
    private String routineId;

    public void updateNumberOfDays(final int numberOfDays) {
        this.numberOfDays = numberOfDays;
        notifyDataSetChanged();
    }

    public FragmentStatePagerAdapterRoutineDay(FragmentManager fm, @NonNull final String routineId) {
        super(fm);
        this.routineId = routineId;
        this.numberOfDays = 0;
    }

    @Override
    public Fragment getItem(int position) {
        return FragmentRoutineDay.newInstance(routineId, position);
    }

    @Override
    public int getCount() {
        return numberOfDays;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return "Day " + (position + 1);
    }
}
