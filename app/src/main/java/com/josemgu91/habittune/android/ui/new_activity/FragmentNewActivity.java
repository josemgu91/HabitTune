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

import android.arch.lifecycle.ViewModelProvider;
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

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.Application;
import com.josemgu91.habittune.android.FragmentInteractionListener;
import com.josemgu91.habittune.android.ui.BaseFragment;
import com.josemgu91.habittune.android.ui.Response;
import com.josemgu91.habittune.android.ui.tag_editor.SharedViewModelTagEditor;
import com.josemgu91.habittune.databinding.FragmentNewActivityBinding;
import com.josemgu91.habittune.domain.usecases.CreateActivity;
import com.josemgu91.habittune.domain.usecases.GetTags;

import java.util.ArrayList;
import java.util.List;

public class FragmentNewActivity extends BaseFragment implements ColorPickerDialogListener {

    private ViewModelNewActivity viewModelNewActivity;
    private FragmentNewActivityBinding fragmentNewActivityBinding;
    private ColorPickerDialog colorPickerDialog;

    private final static String FRAGMENT_TAG_COLOR_PICKER = "colorPickerDialog";

    @ColorInt
    private int defaultColor;

    private SharedViewModelTagEditor sharedViewModelTagEditor;

    private List<GetTags.Output> tags;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        defaultColor = ContextCompat.getColor(context, R.color.secondary);
        final ViewModelProvider.Factory viewModelFactory = ((Application) context.getApplicationContext()).getViewModelFactory();
        viewModelNewActivity = ViewModelProviders.of(this, viewModelFactory).get(ViewModelNewActivity.class);
        sharedViewModelTagEditor = ViewModelProviders.of(getActivity(), viewModelFactory).get(SharedViewModelTagEditor.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState == null) {
            viewModelNewActivity.getSelectedColor().setValue(defaultColor);
        } else {
            onRestoreInstanceState(savedInstanceState);
        }
        colorPickerDialog = (ColorPickerDialog) getActivity().getFragmentManager().findFragmentByTag(FRAGMENT_TAG_COLOR_PICKER);
        if (colorPickerDialog == null) {
            colorPickerDialog = ColorPickerDialog.newBuilder().create();
        }
        colorPickerDialog.setColorPickerDialogListener(this);
    }

    public void onRestoreInstanceState(final Bundle savedInstanceState) {
        viewModelNewActivity.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        viewModelNewActivity.onSaveInstanceState(outState);
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
        fragmentNewActivityBinding.textViewActivityTags.setOnClickListener(v -> goToTagEditor());
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
        if (sharedViewModelTagEditor.getSelectedTagIds() != null) {
            viewModelNewActivity.getTags(sharedViewModelTagEditor.getSelectedTagIds());
            sharedViewModelTagEditor.clear();
        }
        viewModelNewActivity.getGetTagsResponse().observe(this, response -> {
            if (response.status == Response.Status.SUCCESS) {
                response.successData.observe(this, outputs -> {
                    tags = outputs;
                    final List<String> tagNames = new ArrayList<>();
                    for (final GetTags.Output output : outputs) {
                        tagNames.add(output.getName());
                    }
                    showTagNames(tagNames);
                });
            }
        });
        viewModelNewActivity.getSelectedColor().observe(this, color -> {
            fragmentNewActivityBinding.viewColor.setBackgroundColor(color);
            colorPickerDialog = ColorPickerDialog.newBuilder().setColor(color).create();
            colorPickerDialog.setColorPickerDialogListener(this);
        });
        fragmentInteractionListener.updateToolbar(getString(R.string.new_activity_title), FragmentInteractionListener.IC_NAVIGATION_CLOSE);
        fragmentInteractionListener.updateNavigationDrawer(false);
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        viewModelNewActivity.getSelectedColor().setValue(color);
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    private void showTagNames(final List<String> tagNames) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final String tagName : tagNames) {
            if (stringBuilder.length() == 0) {
                stringBuilder.append(tagName);
            } else {
                stringBuilder.append(", ").append(tagName);
            }
        }
        fragmentNewActivityBinding.textViewActivityTags.setText(stringBuilder.toString());
    }

    private void goToTagEditor() {
        if (tags != null) {
            final List<String> selectedTagIds = new ArrayList<>();
            for (final GetTags.Output tag : tags) {
                selectedTagIds.add(tag.getId());
            }
            sharedViewModelTagEditor.setSelectedTagIds(selectedTagIds);
        }
        fragmentInteractionListener.navigateToFragmentTagEditor();
    }

    private void createActivity() {
        final String activityName = fragmentNewActivityBinding.editTextActivityName.getText().toString();
        final String activityDescription = fragmentNewActivityBinding.editTextActivityDescription.getText().toString();
        final int activityColor = viewModelNewActivity.getSelectedColor().getValue();
        final List<String> tagIds = new ArrayList<>();
        if (tags != null) {
            for (final GetTags.Output tag : tags) {
                tagIds.add(tag.getId());
            }
        }
        final CreateActivity.Input input = new CreateActivity.Input(
                activityName,
                activityDescription,
                activityColor,
                tagIds
        );
        viewModelNewActivity.createActivity(input);
        getActivity().onBackPressed();
    }
}
