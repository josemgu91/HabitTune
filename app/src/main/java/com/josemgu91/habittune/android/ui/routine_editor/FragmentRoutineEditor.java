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
import com.josemgu91.habittune.android.FragmentInteractionListener;
import com.josemgu91.habittune.android.ui.BaseFragment;
import com.josemgu91.habittune.databinding.FragmentRoutineEditorBinding;
import com.josemgu91.habittune.domain.usecases.GetRoutineEntries;
import com.josemgu91.habittune.domain.usecases.common.GetRoutine;

import java.util.List;

public class FragmentRoutineEditor extends BaseFragment {

    private final static String ARG_ROUTINE_ID = "routineId";

    private FragmentStatePagerAdapterRoutineDay fragmentStatePagerAdapterRoutineDay;

    public static FragmentRoutineEditor newInstance(final String routineId) {
        Bundle args = new Bundle();
        args.putString(ARG_ROUTINE_ID, routineId);
        FragmentRoutineEditor fragment = new FragmentRoutineEditor();
        fragment.setArguments(args);
        return fragment;
    }

    private String routineId;
    private ViewModelRoutineEditor viewModelRoutineEditor;
    private FragmentRoutineEditorBinding fragmentRoutineEditorBinding;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        viewModelRoutineEditor = ViewModelProviders.of(this, viewModelFactory).get(ViewModelRoutineEditor.class);
        routineId = getArguments().getString(ARG_ROUTINE_ID);
    }

    @Override
    public void onStart() {
        super.onStart();
        fragmentInteractionListener.updateToolbar(getString(R.string.routine_editor_title), FragmentInteractionListener.IC_NAVIGATION_UP);
        fragmentInteractionListener.updateNavigationDrawer(false);
        viewModelRoutineEditor.fetchRoutine(routineId);
        viewModelRoutineEditor.getGetRoutineResponse().observe(getViewLifecycleOwner(), response -> {
            switch (response.status) {
                case LOADING:
                    break;
                case ERROR:
                    break;
                case SUCCESS:
                    updateRoutine(response.successData);
                    break;
            }
        });
        viewModelRoutineEditor.fetchRoutineEntries(routineId);
        viewModelRoutineEditor.getGetRoutineEntriesResponse().observe(getViewLifecycleOwner(), response -> {
            switch (response.status) {
                case LOADING:
                    break;
                case ERROR:
                    break;
                case SUCCESS:
                    response.successData.observe(getViewLifecycleOwner(), this::updateRoutineEntries);
                    break;
            }
        });
    }

    private void updateRoutine(GetRoutine.Output routine) {
        fragmentStatePagerAdapterRoutineDay.updateNumberOfDays(routine.getNumberOfDays());
    }

    private void updateRoutineEntries(List<GetRoutineEntries.Output> routineEntries) {

    }

    @Override
    protected ToolbarOptions createToolbarOptions() {
        return new ToolbarOptions(R.id.toolbar);
    }

    @NonNull
    @Override
    public View createView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentRoutineEditorBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_routine_editor, container, false);
        fragmentStatePagerAdapterRoutineDay = new FragmentStatePagerAdapterRoutineDay(getChildFragmentManager());
        fragmentRoutineEditorBinding.viewPager.setAdapter(fragmentStatePagerAdapterRoutineDay);
        fragmentRoutineEditorBinding.tabLayout.setupWithViewPager(fragmentRoutineEditorBinding.viewPager);
        fragmentRoutineEditorBinding.floatingActionButtonAdd.setOnClickListener(
                v -> fragmentInteractionListener.navigateToRoutineAddActivity(routineId, getCurrentRoutineDay())
        );
        return fragmentRoutineEditorBinding.getRoot();
    }

    private int getCurrentRoutineDay() {
        return fragmentRoutineEditorBinding.viewPager.getCurrentItem();
    }
}
