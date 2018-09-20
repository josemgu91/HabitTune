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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.josemgu91.habittune.databinding.FragmentRoutineDayBinding;

public class FragmentRoutineDay extends Fragment {

    public static FragmentRoutineDay newInstance(final String text) {
        Bundle args = new Bundle();
        args.putString("text", text);
        FragmentRoutineDay fragment = new FragmentRoutineDay();
        fragment.setArguments(args);
        return fragment;
    }

    private FragmentRoutineDayBinding fragmentRoutineDayBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentRoutineDayBinding = FragmentRoutineDayBinding.inflate(inflater, container, false);
        fragmentRoutineDayBinding.textViewDay.setText(getArguments().getString("text"));
        return fragmentRoutineDayBinding.getRoot();
    }
}