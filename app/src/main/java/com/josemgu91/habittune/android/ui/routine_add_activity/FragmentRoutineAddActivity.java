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

package com.josemgu91.habittune.android.ui.routine_add_activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.FragmentInteractionListener;
import com.josemgu91.habittune.android.ui.BaseFragment;
import com.josemgu91.habittune.databinding.FragmentRoutineAddActivityBinding;

public class FragmentRoutineAddActivity extends BaseFragment {

    private final static String FRAGMENT_TAG_TIME_PICKER_DIALOG = "timePickerDialog";

    private FragmentRoutineAddActivityBinding fragmentRoutineAddActivityBinding;
    private ViewModelRoutineAddActivity viewModelRoutineAddActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        viewModelRoutineAddActivity = ViewModelProviders.of(this, viewModelFactory).get(ViewModelRoutineAddActivity.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @NonNull
    @Override
    protected View createView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentRoutineAddActivityBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_routine_add_activity, container, false);
        fragmentRoutineAddActivityBinding.textViewSelectAnActivity.setOnClickListener(v -> fragmentInteractionListener.navigateToActivitySelection());
        fragmentRoutineAddActivityBinding.textViewStartHour.setOnClickListener(v -> showTimePicker());
        fragmentRoutineAddActivityBinding.textViewEndHour.setOnClickListener(v -> showTimePicker());
        return fragmentRoutineAddActivityBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        fragmentInteractionListener.updateToolbar(getString(R.string.routine_add_activity_title), FragmentInteractionListener.IC_NAVIGATION_CLOSE);
        fragmentInteractionListener.updateNavigationDrawer(false);
    }

    @Override
    protected ToolbarOptions createToolbarOptions() {
        return new ToolbarOptions(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_routine_add_activity, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionAssignActivity) {
            fragmentInteractionListener.finishFragment();
            return true;
        }
        return false;
    }

    private void showTimePicker() {
        final TimePickerDialog timePickerDialog = new TimePickerDialog();
        timePickerDialog.show(getFragmentManager(), FRAGMENT_TAG_TIME_PICKER_DIALOG);
    }
}
