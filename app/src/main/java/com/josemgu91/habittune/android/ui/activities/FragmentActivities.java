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

package com.josemgu91.habittune.android.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.FragmentInteractionListener;
import com.josemgu91.habittune.android.ui.common.ConfirmationDialog;
import com.josemgu91.habittune.android.ui.common.FragmentList;
import com.josemgu91.habittune.domain.usecases.GetActivity;

import java.util.ArrayList;
import java.util.List;

public class FragmentActivities extends FragmentList<ActivityItem> {

    private static final String SAVED_INSTANCE_STATE_ITEM_TO_DELETE_ID = "activityId";
    private static final String SAVED_INSTANCE_STATE_ITEM_TO_DELETE_NAME = "activityName";
    private static final String SAVED_INSTANCE_STATE_ITEM_TO_DELETE_COLOR = "activityColor";

    private ViewModelActivities viewModelActivities;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        viewModelActivities = ViewModelProviders.of(this, viewModelFactory).get(ViewModelActivities.class);
    }

    @Override
    protected void saveItemToDeleteState(Bundle outState) {
        outState.putString(SAVED_INSTANCE_STATE_ITEM_TO_DELETE_ID, itemToDelete.getId());
        outState.putString(SAVED_INSTANCE_STATE_ITEM_TO_DELETE_NAME, itemToDelete.getName());
        outState.putInt(SAVED_INSTANCE_STATE_ITEM_TO_DELETE_COLOR, itemToDelete.getColor());
    }

    @Override
    protected ActivityItem restoreItemToDeleteState(Bundle savedInstanceState) {
        if (!savedInstanceState.containsKey(SAVED_INSTANCE_STATE_ITEM_TO_DELETE_ID)) {
            return null;
        }
        final String id = savedInstanceState.getString(SAVED_INSTANCE_STATE_ITEM_TO_DELETE_ID);
        final String name = savedInstanceState.getString(SAVED_INSTANCE_STATE_ITEM_TO_DELETE_NAME);
        final int color = savedInstanceState.getInt(SAVED_INSTANCE_STATE_ITEM_TO_DELETE_COLOR);
        return new ActivityItem(id, name, color);
    }

    @Override
    protected ConfirmationDialog createDeletionConfirmationDialog() {
        return ConfirmationDialog.newInstance(
                R.string.activities_delete_dialog_title,
                R.string.activities_delete_dialog_content,
                R.string.action_delete,
                R.string.action_cancel
        );
    }

    @Override
    protected void onItemSelected(ActivityItem item) {
        fragmentInteractionListener.navigateToFragmentUpdateActivity(item.getId());
    }

    @Override
    protected void onDelete(ActivityItem itemToDelete) {
        viewModelActivities.deleteActivity(itemToDelete.getId());
        fragmentInteractionListener.updateWidgets();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModelActivities.fetchActivities();
        fragmentListBinding.floatingActionButtonAdd.setOnClickListener(v -> fragmentInteractionListener.navigateToFragmentNewActivity());
        fragmentListBinding.setShowProgress(true);
        viewModelActivities.getGetActivitiesResponse().observe(getViewLifecycleOwner(), response -> {
            switch (response.status) {
                case LOADING:
                    fragmentListBinding.setShowProgress(true);
                    break;
                case ERROR:
                    fragmentListBinding.setShowProgress(false);
                    break;
                case SUCCESS:
                    fragmentListBinding.setShowProgress(false);
                    response.successData.observe(getViewLifecycleOwner(), this::updateActivities);
                    break;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        fragmentInteractionListener.updateToolbar(getString(R.string.activities_title), FragmentInteractionListener.IC_NAVIGATION_HAMBURGUER);
        fragmentInteractionListener.updateNavigationDrawer(true);
    }

    private void updateActivities(List<GetActivity.Output> outputs) {
        if (outputs.size() == 0) {
            fragmentListBinding.setShowWarning(true);
            fragmentListBinding.textViewWarning.setText(R.string.activities_empty);
            return;
        }
        fragmentListBinding.setShowWarning(false);
        final List<ActivityItem> activities = new ArrayList<>();
        for (final GetActivity.Output output : outputs) {
            activities.add(new ActivityItem(output.getId(), output.getName(), output.getColor()));
        }
        recyclerViewFlexibleAdapter.updateDataSet(activities);
    }
}
