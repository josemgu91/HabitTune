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

package com.josemgu91.habittune.android.ui.routine_update;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.FragmentInteractionListener;
import com.josemgu91.habittune.android.ui.BaseFragment;
import com.josemgu91.habittune.databinding.FragmentUpdateRoutineBinding;
import com.josemgu91.habittune.domain.usecases.GetRoutine;

public class FragmentUpdateRoutine extends BaseFragment implements ColorPickerDialogListener {

    private static final String ARG_ROUTINE_ID = "routineId";

    public static FragmentUpdateRoutine newInstance(@NonNull final String routineId) {
        final Bundle args = new Bundle();
        final FragmentUpdateRoutine fragment = new FragmentUpdateRoutine();
        args.putString(ARG_ROUTINE_ID, routineId);
        fragment.setArguments(args);
        return fragment;
    }

    private final static String SAVED_INSTANCE_STATE_KEY_COLOR = "color";

    private final static String SAVED_INSTANCE_STATE_KEY_ROUTINE_TO_UPDATE_RETRIEVED = "routineToUpdateRetrieved";

    private final static String FRAGMENT_TAG_COLOR_PICKER = "colorPickerDialog";

    private ViewModelUpdateRoutine viewModelUpdateRoutine;
    private FragmentUpdateRoutineBinding fragmentUpdateRoutineBinding;
    private ColorPickerDialog colorPickerDialog;

    private String routineId;

    private boolean routineToUpdateRetrieved;

    private int selectedColor;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        viewModelUpdateRoutine = ViewModelProviders.of(this, viewModelFactory).get(ViewModelUpdateRoutine.class);
        routineId = getArguments().getString(ARG_ROUTINE_ID);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            routineToUpdateRetrieved = savedInstanceState.getBoolean(SAVED_INSTANCE_STATE_KEY_ROUTINE_TO_UPDATE_RETRIEVED);
            selectedColor = savedInstanceState.getInt(SAVED_INSTANCE_STATE_KEY_COLOR);
        }
        colorPickerDialog = (ColorPickerDialog) getActivity().getFragmentManager().findFragmentByTag(FRAGMENT_TAG_COLOR_PICKER);
        if (colorPickerDialog == null) {
            colorPickerDialog = ColorPickerDialog.newBuilder().create();
        }
        colorPickerDialog.setColorPickerDialogListener(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_INSTANCE_STATE_KEY_ROUTINE_TO_UPDATE_RETRIEVED, routineToUpdateRetrieved);
        outState.putInt(SAVED_INSTANCE_STATE_KEY_COLOR, selectedColor);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_update_routine, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionUpdateRoutine) {
            updateRoutine();
            return true;
        }
        return false;
    }

    @NonNull
    @Override
    protected View createView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentUpdateRoutineBinding = FragmentUpdateRoutineBinding.inflate(inflater, container, false);
        fragmentUpdateRoutineBinding.viewColor.setOnClickListener(v -> showColorPicker());
        return fragmentUpdateRoutineBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        fragmentInteractionListener.updateToolbar(getString(R.string.update_routine_title), FragmentInteractionListener.IC_NAVIGATION_CLOSE);
        fragmentInteractionListener.updateNavigationDrawer(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (routineToUpdateRetrieved) {
            updateColor(selectedColor);
            return;
        }
        viewModelUpdateRoutine.fetchRoutine(routineId);
        viewModelUpdateRoutine.getGetRoutineResponse().observe(getViewLifecycleOwner(), response -> {
            switch (response.status) {
                case LOADING:
                    break;
                case ERROR:
                    break;
                case SUCCESS:
                    onRoutineToUpdateRetrieved(response.successData);
                    break;
            }
        });
    }

    private void onRoutineToUpdateRetrieved(final GetRoutine.Output routine) {
        fragmentUpdateRoutineBinding.editTextRoutineDescription.setText(routine.getDescription());
        fragmentUpdateRoutineBinding.editTextRoutineName.setText(routine.getName());
        updateColor(routine.getColor());
        routineToUpdateRetrieved = true;
    }

    @Override
    protected ToolbarOptions createToolbarOptions() {
        return new ToolbarOptions(true);
    }

    private void updateRoutine() {
        viewModelUpdateRoutine.updateRoutine(
                routineId,
                fragmentUpdateRoutineBinding.editTextRoutineName.getText().toString(),
                fragmentUpdateRoutineBinding.editTextRoutineDescription.getText().toString(),
                selectedColor
        );
        fragmentInteractionListener.finishFragment();
    }

    private void showColorPicker() {
        fragmentInteractionListener.hideSoftKeyboard();
        colorPickerDialog = ColorPickerDialog.newBuilder().setColor(selectedColor).create();
        colorPickerDialog.setColorPickerDialogListener(this);
        colorPickerDialog.show(getActivity().getFragmentManager(), FRAGMENT_TAG_COLOR_PICKER);
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        updateColor(color);
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    private void updateColor(int color) {
        selectedColor = color;
        fragmentUpdateRoutineBinding.viewColor.setBackgroundColor(color);
        colorPickerDialog = ColorPickerDialog.newBuilder().setColor(color).create();
        colorPickerDialog.setColorPickerDialogListener(this);
    }
}
