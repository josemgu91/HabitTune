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
import com.josemgu91.habittune.domain.usecases.GetActivities;

import java.util.ArrayList;
import java.util.List;

public class FragmentActivities extends FragmentList<ActivityItem> {

    private static final String SAVED_INSTANCE_STATE_ITEM_TO_DELETE_ID = "activityId";
    private static final String SAVED_INSTANCE_STATE_ITEM_TO_DELETE_NAME = "activityName";

    private ViewModelActivities viewModelActivities;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        viewModelActivities = ViewModelProviders.of(this, viewModelFactory).get(ViewModelActivities.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModelActivities.fetchActivities();
    }

    @Override
    protected void saveItemToDeleteState(Bundle outState) {
        outState.putString(SAVED_INSTANCE_STATE_ITEM_TO_DELETE_ID, itemToDelete.getId());
        outState.putString(SAVED_INSTANCE_STATE_ITEM_TO_DELETE_NAME, itemToDelete.getName());
    }

    @Override
    protected ActivityItem restoreItemToDeleteState(Bundle savedInstanceState) {
        if (!savedInstanceState.containsKey(SAVED_INSTANCE_STATE_ITEM_TO_DELETE_ID)) {
            return null;
        }
        final String id = savedInstanceState.getString(SAVED_INSTANCE_STATE_ITEM_TO_DELETE_ID);
        final String name = savedInstanceState.getString(SAVED_INSTANCE_STATE_ITEM_TO_DELETE_NAME);
        return new ActivityItem(id, name);
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

    }

    @Override
    protected void onDelete(ActivityItem itemToDelete) {
        viewModelActivities.deleteActivity(itemToDelete.getId());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fragmentListBinding.floatingActionButtonAdd.setOnClickListener(v -> fragmentInteractionListener.navigateToFragmentNewActivity());
        fragmentListBinding.setShowProgress(true);
        viewModelActivities.getGetActivitiesResponse().observe(this, response -> {
            switch (response.status) {
                case LOADING:
                    fragmentListBinding.setShowProgress(true);
                    break;
                case ERROR:
                    fragmentListBinding.setShowProgress(false);
                    break;
                case SUCCESS:
                    fragmentListBinding.setShowProgress(false);
                    response.successData.observe(this, this::updateActivities);
                    break;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentInteractionListener.updateToolbar(getString(R.string.activities_title), FragmentInteractionListener.IC_NAVIGATION_HAMBURGUER);
        fragmentInteractionListener.updateNavigationDrawer(true);
    }

    private void updateActivities(List<GetActivities.Output> outputs) {
        final List<ActivityItem> activities = new ArrayList<>();
        for (final GetActivities.Output output : outputs) {
            activities.add(new ActivityItem(output.getId(), output.getName()));
        }
        recyclerViewFlexibleAdapter.updateDataSet(activities);
    }
}
