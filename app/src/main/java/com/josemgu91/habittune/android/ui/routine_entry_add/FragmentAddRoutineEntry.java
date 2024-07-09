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

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.FragmentInteractionListener;
import com.josemgu91.habittune.android.ui.BaseFragment;
import com.josemgu91.habittune.android.ui.activity_selection.SharedViewModelActivitySelection;
import com.josemgu91.habittune.databinding.FragmentRoutineAddActivityBinding;

public class FragmentAddRoutineEntry extends BaseFragment implements TimePickerDialog.OnTimeSetListener {

    private final static String ARG_ROUTINE_ID = "routineId";
    private final static String ARG_ROUTINE_DAY = "routineDay";

    private final static String FRAGMENT_TAG_TIME_PICKER_DIALOG = "timePickerDialog";

    private final static String SAVED_INSTANCE_STATE_KEY_VIEW_THAT_STARTED_TIME_PICKER = "viewThatStartedTimePicker";
    private final static String SAVED_INSTANCE_STATE_START_HOUR = "startHour";
    private final static String SAVED_INSTANCE_STATE_END_HOUR = "endHour";
    private final static String SAVED_INSTANCE_STATE_SELECTED_ACTIVITY_ID = "selectedActivityId";

    public static FragmentAddRoutineEntry newInstance(@NonNull final String routineId, final int routineDay) {
        final Bundle args = new Bundle();
        args.putString(ARG_ROUTINE_ID, routineId);
        args.putInt(ARG_ROUTINE_DAY, routineDay);
        FragmentAddRoutineEntry fragment = new FragmentAddRoutineEntry();
        fragment.setArguments(args);
        return fragment;
    }

    private String routineId;
    private int routineDay;

    @IdRes
    private int viewThatStartedTimePicker;

    private FragmentRoutineAddActivityBinding fragmentRoutineAddActivityBinding;
    private ViewModelRoutineAddActivity viewModelRoutineAddActivity;

    private TimePickerDialog timePickerDialog;

    private Hour startHour;
    private Hour endHour;

    private String selectedActivityId;

    private SharedViewModelActivitySelection sharedViewModelActivitySelection;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        viewModelRoutineAddActivity = new ViewModelProvider(this, viewModelFactory).get(ViewModelRoutineAddActivity.class);
        sharedViewModelActivitySelection = new ViewModelProvider(getActivity(), viewModelFactory).get(SharedViewModelActivitySelection.class);
        final Bundle arguments = getArguments();
        routineId = arguments.getString(ARG_ROUTINE_ID);
        routineDay = arguments.getInt(ARG_ROUTINE_DAY);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState == null) {
            startHour = Hour.currentHour();
            endHour = new Hour((startHour.hourOfDay + 1) % 24, startHour.minute);
            return;
        }
        selectedActivityId = savedInstanceState.getString(SAVED_INSTANCE_STATE_SELECTED_ACTIVITY_ID);
        startHour = savedInstanceState.getParcelable(SAVED_INSTANCE_STATE_START_HOUR);
        endHour = savedInstanceState.getParcelable(SAVED_INSTANCE_STATE_END_HOUR);
        timePickerDialog = (TimePickerDialog) getFragmentManager().findFragmentByTag(FRAGMENT_TAG_TIME_PICKER_DIALOG);
        if (timePickerDialog != null) {
            viewThatStartedTimePicker = savedInstanceState.getInt(SAVED_INSTANCE_STATE_KEY_VIEW_THAT_STARTED_TIME_PICKER);
            timePickerDialog.setOnTimeSetListener(this);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_INSTANCE_STATE_KEY_VIEW_THAT_STARTED_TIME_PICKER, viewThatStartedTimePicker);
        outState.putParcelable(SAVED_INSTANCE_STATE_START_HOUR, startHour);
        outState.putParcelable(SAVED_INSTANCE_STATE_END_HOUR, endHour);
        outState.putString(SAVED_INSTANCE_STATE_SELECTED_ACTIVITY_ID, selectedActivityId);
    }

    @NonNull
    @Override
    protected View createView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentRoutineAddActivityBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_routine_add_activity, container, false);
        fragmentRoutineAddActivityBinding.textViewSelectAnActivity.setOnClickListener(
                v -> fragmentInteractionListener.navigateToFragmentActivitySelection()
        );
        fragmentRoutineAddActivityBinding.textViewStartHour.setOnClickListener(
                v -> showTimePicker(v.getId(), startHour)
        );
        fragmentRoutineAddActivityBinding.textViewEndHour.setOnClickListener(
                v -> showTimePicker(v.getId(), endHour)
        );
        fragmentRoutineAddActivityBinding.textViewStartHour.setText(startHour.format());
        fragmentRoutineAddActivityBinding.textViewEndHour.setText(endHour.format());
        return fragmentRoutineAddActivityBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        fragmentInteractionListener.updateToolbar(getString(R.string.add_routine_entry_title), FragmentInteractionListener.IC_NAVIGATION_CLOSE);
        fragmentInteractionListener.updateNavigationDrawer(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        final String newSelectedActivityId = sharedViewModelActivitySelection.getSelectedActivityId();
        if (newSelectedActivityId != null) {
            selectedActivityId = newSelectedActivityId;
            sharedViewModelActivitySelection.clear();
        }
        if (selectedActivityId != null) {
            fragmentRoutineAddActivityBinding.textViewSelectAnActivity.setText(selectedActivityId);
            viewModelRoutineAddActivity.fetchActivity(selectedActivityId);
            viewModelRoutineAddActivity.getGetActivityResponse().observe(getViewLifecycleOwner(), response -> {
                switch (response.status) {
                    case SUCCESS:
                        fragmentRoutineAddActivityBinding.textViewSelectAnActivity.setText(response.successData.getName());
                        break;
                }
            });
        }
    }

    @Override
    protected ToolbarOptions createToolbarOptions() {
        return new ToolbarOptions(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_add_routine_entry, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionAddRoutineEntry) {
            if (selectedActivityId == null) {
                fragmentRoutineAddActivityBinding.textViewSelectAnActivity.setError(
                        getString(R.string.add_routine_error_no_activity)
                );
                return true;
            }
            fragmentRoutineAddActivityBinding.textViewSelectAnActivity.setError(null);
            createRoutineEntry();
            fragmentInteractionListener.finishFragment();
            return true;
        }
        return false;
    }

    private void createRoutineEntry() {
        viewModelRoutineAddActivity.createRoutineEntry(
                routineId,
                selectedActivityId,
                routineDay,
                startHour.inSeconds(),
                endHour.inSeconds()
        );
    }

    private void showTimePicker(@IdRes final int viewThatStartedTimePicker, final Hour hour) {
        this.viewThatStartedTimePicker = viewThatStartedTimePicker;
        timePickerDialog = TimePickerDialog.newInstance(hour.hourOfDay, hour.minute);
        timePickerDialog.setOnTimeSetListener(this);
        timePickerDialog.show(getFragmentManager(), FRAGMENT_TAG_TIME_PICKER_DIALOG);
    }

    @Override
    public void onTimeSet(final int hourOfDay, final int minute) {
        final Hour hour = new Hour(hourOfDay, minute);
        if (viewThatStartedTimePicker == R.id.textViewStartHour) {
            startHour = hour;
            fragmentRoutineAddActivityBinding.textViewStartHour.setText(hour.format());
        } else if (viewThatStartedTimePicker == R.id.textViewEndHour) {
            endHour = hour;
            fragmentRoutineAddActivityBinding.textViewEndHour.setText(hour.format());
        }
    }

}
