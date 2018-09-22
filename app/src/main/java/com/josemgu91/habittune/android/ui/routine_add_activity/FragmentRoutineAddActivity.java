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
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IdRes;
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
import com.josemgu91.habittune.android.ui.activity_selection.SharedViewModelActivitySelection;
import com.josemgu91.habittune.databinding.FragmentRoutineAddActivityBinding;

import java.text.DateFormat;
import java.util.Calendar;

public class FragmentRoutineAddActivity extends BaseFragment implements TimePickerDialog.OnTimeSetListener {

    private final static String FRAGMENT_TAG_TIME_PICKER_DIALOG = "timePickerDialog";

    private final static String SAVED_INSTANCE_STATE_KEY_VIEW_THAT_STARTED_TIME_PICKER = "viewThatStartedTimePicker";
    private final static String SAVED_INSTANCE_STATE_START_HOUR = "startHour";
    private final static String SAVED_INSTANCE_STATE_END_HOUR = "endHour";
    private final static String SAVED_INSTANCE_STATE_SELECTED_ACTIVITY_ID = "selectedActivityId";

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
        viewModelRoutineAddActivity = ViewModelProviders.of(this, viewModelFactory).get(ViewModelRoutineAddActivity.class);
        sharedViewModelActivitySelection = ViewModelProviders.of(getActivity(), viewModelFactory).get(SharedViewModelActivitySelection.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState == null) {
            startHour = new Hour(0, 0);
            endHour = new Hour(0, 0);
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
        fragmentRoutineAddActivityBinding.textViewSelectAnActivity.setOnClickListener(v -> fragmentInteractionListener.navigateToActivitySelection());
        fragmentRoutineAddActivityBinding.textViewStartHour.setOnClickListener(v -> showTimePicker(v.getId()));
        fragmentRoutineAddActivityBinding.textViewEndHour.setOnClickListener(v -> showTimePicker(v.getId()));
        fragmentRoutineAddActivityBinding.textViewStartHour.setText(getFormattedHour(startHour.hourOfDay, startHour.minute));
        fragmentRoutineAddActivityBinding.textViewEndHour.setText(getFormattedHour(endHour.hourOfDay, endHour.minute));
        return fragmentRoutineAddActivityBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        fragmentInteractionListener.updateToolbar(getString(R.string.routine_add_activity_title), FragmentInteractionListener.IC_NAVIGATION_CLOSE);
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
            viewModelRoutineAddActivity.getActivity(selectedActivityId);
            viewModelRoutineAddActivity.getGetActivityResponse().observe(this, response -> {
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

    private void showTimePicker(@IdRes final int viewThatStartedTimePicker) {
        this.viewThatStartedTimePicker = viewThatStartedTimePicker;
        timePickerDialog = new TimePickerDialog();
        timePickerDialog.setOnTimeSetListener(this);
        timePickerDialog.show(getFragmentManager(), FRAGMENT_TAG_TIME_PICKER_DIALOG);
    }

    @Override
    public void onTimeSet(final int hourOfDay, final int minute) {
        final String formattedHour = getFormattedHour(hourOfDay, minute);
        final Hour hour = new Hour(hourOfDay, minute);
        switch (viewThatStartedTimePicker) {
            case R.id.textViewStartHour:
                startHour = hour;
                fragmentRoutineAddActivityBinding.textViewStartHour.setText(formattedHour);
                break;
            case R.id.textViewEndHour:
                endHour = hour;
                fragmentRoutineAddActivityBinding.textViewEndHour.setText(formattedHour);
                break;
        }
    }

    private String getFormattedHour(final int hourOfDay, final int minute) {
        final DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        return dateFormat.format(calendar.getTime());
    }

    private final static class Hour implements Parcelable {

        private final int hourOfDay;
        private final int minute;

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
    }
}
