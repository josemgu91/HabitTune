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

package com.josemgu91.habittune.android.ui.new_routine;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.FragmentInteractionListener;
import com.josemgu91.habittune.android.ui.BaseFragment;
import com.josemgu91.habittune.databinding.FragmentNewRoutineBinding;
import com.josemgu91.habittune.domain.usecases.CreateRoutine;

public class FragmentNewRoutine extends BaseFragment implements ColorPickerDialogListener {

    private FragmentNewRoutineBinding fragmentNewRoutineBinding;
    private ViewModelNewRoutine viewModelNewRoutine;
    private ColorPickerDialog colorPickerDialog;

    private final static String FRAGMENT_TAG_COLOR_PICKER = "colorPickerDialog";

    private final static String SAVED_INSTANCE_STATE_KEY_SELECTED_COLOR = "selectedColor";
    private final static String SAVED_INSTANCE_STATE_KEY_NUMBER_OF_DAYS = "numberOfDays";

    @ColorInt
    private int defaultColor;
    @ColorInt
    private int selectedColor;
    private int numberOfDays;

    private final static int MAX_NUMBER_OF_DAYS = 7;
    private final static int DEFAULT_NUMBER_OF_DAYS = 1;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        defaultColor = ContextCompat.getColor(context, R.color.secondary);
        viewModelNewRoutine = ViewModelProviders.of(this, viewModelFactory).get(ViewModelNewRoutine.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState == null) {
            selectedColor = defaultColor;
            numberOfDays = DEFAULT_NUMBER_OF_DAYS;
        } else {
            numberOfDays = savedInstanceState.getInt(SAVED_INSTANCE_STATE_KEY_NUMBER_OF_DAYS);
            selectedColor = savedInstanceState.getInt(SAVED_INSTANCE_STATE_KEY_SELECTED_COLOR);
        }
        colorPickerDialog = (ColorPickerDialog) getActivity().getFragmentManager().findFragmentByTag(FRAGMENT_TAG_COLOR_PICKER);
        if (colorPickerDialog != null) {
            colorPickerDialog.setColorPickerDialogListener(this);
        }
    }

    @Override
    protected ToolbarOptions createToolbarOptions() {
        return new ToolbarOptions(true);
    }

    @NonNull
    @Override
    public View createView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentNewRoutineBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_routine, container, false);
        fragmentNewRoutineBinding.viewColor.setOnClickListener(v -> showColorPicker());
        fragmentNewRoutineBinding.seekBarNumberOfDays.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                final int numberOfDays = progress + 1;
                fragmentNewRoutineBinding.textViewNumberOfDays.setText(getString(R.string.new_routine_number_of_days, String.valueOf(numberOfDays)));
                FragmentNewRoutine.this.numberOfDays = numberOfDays;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        fragmentNewRoutineBinding.seekBarNumberOfDays.setMax(MAX_NUMBER_OF_DAYS - 1);
        if (savedInstanceState == null) {
            fragmentNewRoutineBinding.seekBarNumberOfDays.setProgress(DEFAULT_NUMBER_OF_DAYS - 1);
            fragmentNewRoutineBinding.textViewNumberOfDays.setText(getString(R.string.new_routine_number_of_days, String.valueOf(DEFAULT_NUMBER_OF_DAYS)));
        }
        fragmentNewRoutineBinding.viewColor.setBackgroundColor(selectedColor);
        return fragmentNewRoutineBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentInteractionListener.updateToolbar(getString(R.string.new_routine_title), FragmentInteractionListener.IC_NAVIGATION_CLOSE);
        fragmentInteractionListener.updateNavigationDrawer(false);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_INSTANCE_STATE_KEY_SELECTED_COLOR, selectedColor);
        outState.putInt(SAVED_INSTANCE_STATE_KEY_NUMBER_OF_DAYS, numberOfDays);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_new_routine, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionCreateRoutine) {
            createRoutine();
            return true;
        }
        return false;
    }

    private void createRoutine() {
        viewModelNewRoutine.createRoutine(new CreateRoutine.Input(
                fragmentNewRoutineBinding.editTextRoutineName.getText().toString(),
                fragmentNewRoutineBinding.editTextRoutineDescription.getText().toString(),
                selectedColor,
                numberOfDays
        ));
        fragmentInteractionListener.finishFragment();
        fragmentInteractionListener.navigateToFragmentRoutineEditor();
    }

    private void showColorPicker() {
        colorPickerDialog = ColorPickerDialog.newBuilder().setColor(selectedColor).create();
        colorPickerDialog.setColorPickerDialogListener(this);
        colorPickerDialog.show(getActivity().getFragmentManager(), FRAGMENT_TAG_COLOR_PICKER);
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        selectedColor = color;
        fragmentNewRoutineBinding.viewColor.setBackgroundColor(color);
        colorPickerDialog = ColorPickerDialog.newBuilder().setColor(color).create();
        colorPickerDialog.setColorPickerDialogListener(this);
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }
}
