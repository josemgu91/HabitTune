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

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.ui.BaseFragment;
import com.josemgu91.habittune.databinding.FragmentRoutineEditorBinding;

public class FragmentRoutineEditor extends BaseFragment {

    private ViewModelRoutineEditor viewModelRoutineEditor;
    private FragmentRoutineEditorBinding fragmentRoutineEditorBinding;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        viewModelRoutineEditor = ViewModelProviders.of(this).get(ViewModelRoutineEditor.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentRoutineEditorBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_routine_editor, container, false);
        fragmentRoutineEditorBinding.viewPager.setAdapter(new FragmentStatePagerAdapterRoutineDay(getChildFragmentManager()));
        fragmentRoutineEditorBinding.tabLayout.setupWithViewPager(fragmentRoutineEditorBinding.viewPager);
        return fragmentRoutineEditorBinding.getRoot();
    }
}
