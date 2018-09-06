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

package com.josemgu91.habittune.android.ui.new_activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.Application;
import com.josemgu91.habittune.android.FragmentInteractionListener;
import com.josemgu91.habittune.android.ui.ViewModelFactory;
import com.josemgu91.habittune.databinding.FragmentNewActivityBinding;
import com.josemgu91.habittune.domain.usecases.CreateActivity;

import java.util.ArrayList;

public class FragmentNewActivity extends Fragment implements ColorPickerDialogListener {

    private ViewModelNewActivity viewModelNewActivity;
    private FragmentNewActivityBinding fragmentNewActivityBinding;
    private FragmentInteractionListener fragmentInteractionListener;
    private ColorPickerDialog colorPickerDialog;

    private final static String FRAGMENT_TAG_COLOR_PICKER = "colorPickerDialog";
    private final static String SAVED_INSTANCE_STATE_KEY_COLOR = "color";

    @ColorInt
    private int defaultColor;
    @ColorInt
    private int selectedColor;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentInteractionListener = (FragmentInteractionListener) getActivity();
        defaultColor = ContextCompat.getColor(context, R.color.secondary);
        final ViewModelFactory viewModelFactory = ((Application) context.getApplicationContext()).getViewModelFactory();
        viewModelNewActivity = ViewModelProviders.of(this, viewModelFactory).get(ViewModelNewActivity.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState == null) {
            selectedColor = defaultColor;
        } else {
            selectedColor = savedInstanceState.getInt(SAVED_INSTANCE_STATE_KEY_COLOR);
        }
        colorPickerDialog = (ColorPickerDialog) getActivity().getFragmentManager().findFragmentByTag(FRAGMENT_TAG_COLOR_PICKER);
        if (colorPickerDialog == null) {
            colorPickerDialog = ColorPickerDialog.newBuilder().setColor(selectedColor).create();
        }
        colorPickerDialog.setColorPickerDialogListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_new_activity, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionCreateActivity) {
            createActivity();
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentNewActivityBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_activity, container, false);
        fragmentNewActivityBinding.viewColor.setBackgroundColor(selectedColor);
        //TODO: Add the selected tags.
        fragmentNewActivityBinding.editTextActivityTags.setOnClickListener(v -> fragmentInteractionListener.navigateToFragmentTagEditor(new ArrayList<>()));
        return fragmentNewActivityBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fragmentNewActivityBinding.viewColor.setOnClickListener(v -> colorPickerDialog.show(getActivity().getFragmentManager(), FRAGMENT_TAG_COLOR_PICKER));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (fragmentInteractionListener != null) {
            fragmentInteractionListener.updateToolbar(getString(R.string.new_activity_title), FragmentInteractionListener.IC_NAVIGATION_CLOSE);
            fragmentInteractionListener.updateNavigationDrawer(false);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_INSTANCE_STATE_KEY_COLOR, selectedColor);
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        selectedColor = color;
        fragmentNewActivityBinding.viewColor.setBackgroundColor(selectedColor);
        colorPickerDialog = ColorPickerDialog.newBuilder().setColor(selectedColor).create();
        colorPickerDialog.setColorPickerDialogListener(this);
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    private void createActivity() {
        final String activityName = fragmentNewActivityBinding.editTextActivityName.getText().toString();
        final String activityDescription = fragmentNewActivityBinding.editTextActivityDescription.getText().toString();
        final int activityColor = selectedColor;
        final CreateActivity.Input input = new CreateActivity.Input(
                activityName,
                activityDescription,
                activityColor
        );
        viewModelNewActivity.createActivity(input);
        getActivity().onBackPressed();
    }
}
