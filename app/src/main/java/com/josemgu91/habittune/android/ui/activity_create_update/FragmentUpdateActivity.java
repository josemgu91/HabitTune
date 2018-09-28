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

package com.josemgu91.habittune.android.ui.activity_create_update;

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
import com.josemgu91.habittune.android.FragmentInteractionListener;
import com.josemgu91.habittune.android.ui.BaseFragment;
import com.josemgu91.habittune.android.ui.Response;
import com.josemgu91.habittune.android.ui.tag_editor.SharedViewModelTagEditor;
import com.josemgu91.habittune.databinding.FragmentNewActivityBinding;
import com.josemgu91.habittune.domain.usecases.CreateActivity;
import com.josemgu91.habittune.domain.usecases.GetTags;

import java.util.ArrayList;
import java.util.List;

public class FragmentUpdateActivity extends BaseFragment implements ColorPickerDialogListener {

    private static final String ARG_ACTIVITY_ID = "activityId";

    public static FragmentUpdateActivity newInstance(final String activityId) {
        final Bundle args = new Bundle();
        args.putString(ARG_ACTIVITY_ID, activityId);
        final FragmentUpdateActivity fragment = new FragmentUpdateActivity();
        fragment.setArguments(args);
        return fragment;
    }

    private final static String SAVED_INSTANCE_STATE_KEY_SELECTED_COLOR = "selectedColor";

    private String activityId;

    private ViewModelNewActivity viewModelNewActivity;
    private FragmentNewActivityBinding fragmentNewActivityBinding;
    private ColorPickerDialog colorPickerDialog;

    private final static String FRAGMENT_TAG_COLOR_PICKER = "colorPickerDialog";

    private int selectedColor;
    @ColorInt
    private int defaultColor;

    private SharedViewModelTagEditor sharedViewModelTagEditor;

    private List<GetTags.Output> tags;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        defaultColor = ContextCompat.getColor(context, R.color.secondary);
        viewModelNewActivity = ViewModelProviders.of(this, viewModelFactory).get(ViewModelNewActivity.class);
        sharedViewModelTagEditor = ViewModelProviders.of(getActivity(), viewModelFactory).get(SharedViewModelTagEditor.class);
        activityId = getArguments().getString(ARG_ACTIVITY_ID);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState == null) {
            selectedColor = defaultColor;
        } else {
            selectedColor = savedInstanceState.getInt(SAVED_INSTANCE_STATE_KEY_SELECTED_COLOR);
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
        outState.putInt(SAVED_INSTANCE_STATE_KEY_SELECTED_COLOR, selectedColor);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_update_activity, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionUpdateActivity) {
            updateActivity();
            return true;
        }
        return false;
    }

    @Override
    protected ToolbarOptions createToolbarOptions() {
        return new ToolbarOptions(true);
    }

    @NonNull
    @Override
    public View createView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentNewActivityBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_activity, container, false);
        fragmentNewActivityBinding.textViewActivityTags.setOnClickListener(v -> goToTagEditor());
        return fragmentNewActivityBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fragmentNewActivityBinding.viewColor.setOnClickListener(v -> {
            fragmentInteractionListener.hideSoftKeyboard();
            colorPickerDialog.show(getActivity().getFragmentManager(), FRAGMENT_TAG_COLOR_PICKER);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        fragmentInteractionListener.updateToolbar(getString(R.string.new_activity_title), FragmentInteractionListener.IC_NAVIGATION_CLOSE);
        fragmentInteractionListener.updateNavigationDrawer(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sharedViewModelTagEditor.getSelectedTagIds() != null) {
            viewModelNewActivity.getTags(sharedViewModelTagEditor.getSelectedTagIds());
            sharedViewModelTagEditor.clear();
        }
        viewModelNewActivity.getGetTagsResponse().observe(getViewLifecycleOwner(), response -> {
            if (response.status == Response.Status.SUCCESS) {
                response.successData.observe(getViewLifecycleOwner(), outputs -> {
                    tags = outputs;
                    final List<String> tagNames = new ArrayList<>();
                    for (final GetTags.Output output : outputs) {
                        tagNames.add(output.getName());
                    }
                    showTagNames(tagNames);
                });
            }
        });
        viewModelNewActivity.getSelectedColor().observe(getViewLifecycleOwner(), color -> {
            fragmentNewActivityBinding.viewColor.setBackgroundColor(color);
            colorPickerDialog = ColorPickerDialog.newBuilder().setColor(color).create();
            colorPickerDialog.setColorPickerDialogListener(this);
        });
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

    private void updateActivity() {

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
        fragmentInteractionListener.finishFragment();
    }
}
