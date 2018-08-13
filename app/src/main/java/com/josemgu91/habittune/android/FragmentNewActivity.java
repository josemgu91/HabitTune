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

package com.josemgu91.habittune.android;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.josemgu91.habittune.R;
import com.josemgu91.habittune.databinding.FragmentNewActivityBinding;

public class FragmentNewActivity extends Fragment implements ColorPickerDialogListener {

    private FragmentInteractionListener fragmentInteractionListener;

    private FragmentNewActivityBinding fragmentNewActivityBinding;

    private ColorPickerDialog colorPickerDialog;

    private final static String FRAGMENT_TAG_COLOR_PICKER = "color-picker-dialog";

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
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentNewActivityBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_activity, container, false);
        fragmentNewActivityBinding.viewColor.setBackgroundColor(selectedColor);
        return fragmentNewActivityBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        fragmentNewActivityBinding.viewColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorPickerDialog.show(getActivity().getFragmentManager(), FRAGMENT_TAG_COLOR_PICKER);
            }
        });
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
}
