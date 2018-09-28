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
import com.josemgu91.habittune.android.ui.Response;
import com.josemgu91.habittune.android.ui.tag_editor.SharedViewModelTagEditor;
import com.josemgu91.habittune.databinding.FragmentNewActivityBinding;
import com.josemgu91.habittune.domain.usecases.GetActivity;
import com.josemgu91.habittune.domain.usecases.GetTags;
import com.josemgu91.habittune.domain.usecases.UpdateActivity;

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


    private final static String SAVED_INSTANCE_STATE_KEY_COLOR = "color";
    private final static String SAVED_INSTANCE_STATE_KEY_SELECTED_TAGS_IDS = "selectedTagsIds";

    private final static String SAVED_INSTANCE_STATE_KEY_ACTIVITY_TO_UPDATE_RETRIEVED = "activityToUpdateRetrieved";

    private boolean activityToUpdateRetrieved;

    private String activityId;

    private ViewModelNewActivity viewModelNewActivity;
    private FragmentNewActivityBinding fragmentNewActivityBinding;
    private ColorPickerDialog colorPickerDialog;

    private final static String FRAGMENT_TAG_COLOR_PICKER = "colorPickerDialog";

    private SharedViewModelTagEditor sharedViewModelTagEditor;

    private ArrayList<String> selectedTagsIds;

    private int selectedColor;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        viewModelNewActivity = ViewModelProviders.of(this, viewModelFactory).get(ViewModelNewActivity.class);
        sharedViewModelTagEditor = ViewModelProviders.of(getActivity(), viewModelFactory).get(SharedViewModelTagEditor.class);
        activityId = getArguments().getString(ARG_ACTIVITY_ID);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            activityToUpdateRetrieved = savedInstanceState.getBoolean(SAVED_INSTANCE_STATE_KEY_ACTIVITY_TO_UPDATE_RETRIEVED);
            selectedColor = savedInstanceState.getInt(SAVED_INSTANCE_STATE_KEY_COLOR);
            selectedTagsIds = savedInstanceState.getStringArrayList(SAVED_INSTANCE_STATE_KEY_SELECTED_TAGS_IDS);
            updateTagsNames(selectedTagsIds);
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
        outState.putBoolean(SAVED_INSTANCE_STATE_KEY_ACTIVITY_TO_UPDATE_RETRIEVED, activityToUpdateRetrieved);
        outState.putInt(SAVED_INSTANCE_STATE_KEY_COLOR, selectedColor);
        outState.putStringArrayList(SAVED_INSTANCE_STATE_KEY_SELECTED_TAGS_IDS, selectedTagsIds);
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
        fragmentNewActivityBinding.viewColor.setOnClickListener(v -> showColorPicker());
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
            selectedTagsIds = new ArrayList<>(sharedViewModelTagEditor.getSelectedTagIds());
            sharedViewModelTagEditor.clear();
            updateTagsNames(selectedTagsIds);
        }
        if (activityToUpdateRetrieved) {
            return;
        }
        viewModelNewActivity.getActivity(activityId);
        viewModelNewActivity.getGetActivityResponse().observe(getViewLifecycleOwner(), response -> {
            switch (response.status) {
                case LOADING:
                    break;
                case ERROR:
                    break;
                case SUCCESS:
                    onActivityToUpdateRetrieved(response.successData);
                    break;
            }
        });
    }

    private void onActivityToUpdateRetrieved(GetActivity.Output activityToUpdate) {
        final ArrayList<String> tagsIds = new ArrayList<>();
        for (final GetActivity.Output.Tag tag : activityToUpdate.getTags()) {
            tagsIds.add(tag.getId());
        }
        fragmentNewActivityBinding.editTextActivityName = activityToUpdate.getName();
        fragmentNewActivityBinding.editTextActivityDescription = activityToUpdate.getDescription();
        selectedTagsIds = tagsIds;
        updateColor(activityToUpdate.getColor());
        activityToUpdateRetrieved = true;
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
        fragmentNewActivityBinding.viewColor.setBackgroundColor(color);
        colorPickerDialog = ColorPickerDialog.newBuilder().setColor(color).create();
        colorPickerDialog.setColorPickerDialogListener(this);
    }

    private void updateTagsNames(final List<String> tagsIds) {
        viewModelNewActivity.getTags(tagsIds);
        viewModelNewActivity.getGetTagsResponse().observe(getViewLifecycleOwner(), response -> {
            if (response.status == Response.Status.SUCCESS) {
                response.successData.observe(getViewLifecycleOwner(), outputs -> {
                    final List<String> tagNames = new ArrayList<>();
                    for (final GetTags.Output output : outputs) {
                        tagNames.add(output.getName());
                    }
                    showTagNames(tagNames);
                });
            }
        });
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
        sharedViewModelTagEditor.setSelectedTagIds(selectedTagsIds);
        fragmentInteractionListener.navigateToFragmentTagEditor();
    }

    private void updateActivity() {
        final String activityName = fragmentNewActivityBinding.editTextActivityName.getText().toString();
        final String activityDescription = fragmentNewActivityBinding.editTextActivityDescription.getText().toString();
        final UpdateActivity.Input input = new UpdateActivity.Input(
                activityId,
                activityName,
                activityDescription,
                selectedColor,
                selectedTagsIds
        );
        viewModelNewActivity.updateActivity(input);
        fragmentInteractionListener.finishFragment();
    }

}
