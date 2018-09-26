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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.josemgu91.habittune.domain.usecases.GetRoutineEntries;

import java.util.ArrayList;
import java.util.List;

public class FragmentStatePagerAdapterRoutineDay extends FragmentStatePagerAdapter {

    private int numberOfDays;
    @NonNull
    private List<GetRoutineEntries.Output> routineEntries;

    public void updateNumberOfDays(final int numberOfDays) {
        this.numberOfDays = numberOfDays;
        notifyDataSetChanged();
    }

    public void updateRoutineEntries(@NonNull final List<GetRoutineEntries.Output> routineEntries) {
        this.routineEntries = routineEntries;
        notifyDataSetChanged();
    }

    @NonNull
    public List<GetRoutineEntries.Output> getRoutineEntries() {
        return routineEntries;
    }

    public FragmentStatePagerAdapterRoutineDay(FragmentManager fm) {
        super(fm);
        this.numberOfDays = 0;
        routineEntries = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return FragmentRoutineDay.newInstance("Day " + (position + 1));
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
