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

package com.josemgu91.habittune.android.ui.statistics;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.FragmentInteractionListener;
import com.josemgu91.habittune.android.ui.BaseFragment;
import com.josemgu91.habittune.android.ui.activities.ActivityItem;
import com.josemgu91.habittune.android.ui.activity_selection.ViewModelActivitySelection;
import com.josemgu91.habittune.databinding.FragmentActivitySelectionBinding;
import com.josemgu91.habittune.domain.usecases.GetActivity;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;

public class FragmentActivitySelection extends BaseFragment {

    private FragmentActivitySelectionBinding fragmentActivitySelectionBinding;

    private FlexibleAdapter<ActivityItem> flexibleAdapterActivities;
    private ViewModelActivitySelection viewModelActivitySelection;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        viewModelActivitySelection = ViewModelProviders.of(this, viewModelFactory).get(ViewModelActivitySelection.class);
    }

    @NonNull
    @Override
    protected View createView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentActivitySelectionBinding = FragmentActivitySelectionBinding.inflate(inflater, container, false);
        flexibleAdapterActivities = new FlexibleAdapter<>(null, (FlexibleAdapter.OnItemClickListener) (view, position) -> {
            final ActivityItem activityItem = flexibleAdapterActivities.getItem(position);
            onActivitySelected(activityItem);
            return true;
        }, true);
        fragmentActivitySelectionBinding.recyclerView.setAdapter(flexibleAdapterActivities);
        fragmentActivitySelectionBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return fragmentActivitySelectionBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModelActivitySelection.fetchActivities();
        fragmentActivitySelectionBinding.setShowProgress(true);
        viewModelActivitySelection.getGetActivitiesResponse().observe(getViewLifecycleOwner(), response -> {
            switch (response.status) {
                case LOADING:
                    fragmentActivitySelectionBinding.setShowProgress(true);
                    break;
                case ERROR:
                    fragmentActivitySelectionBinding.setShowProgress(false);
                    break;
                case SUCCESS:
                    fragmentActivitySelectionBinding.setShowProgress(false);
                    response.successData.observe(getViewLifecycleOwner(), this::updateActivities);
                    break;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        fragmentInteractionListener.updateToolbar(getString(R.string.statistics_title), FragmentInteractionListener.IC_NAVIGATION_HAMBURGUER);
        fragmentInteractionListener.updateNavigationDrawer(true);
    }

    @Override
    protected ToolbarOptions createToolbarOptions() {
        return new ToolbarOptions(true);
    }

    private void updateActivities(List<GetActivity.Output> outputs) {
        final List<ActivityItem> activities = new ArrayList<>();
        for (final GetActivity.Output output : outputs) {
            activities.add(new ActivityItem(output.getId(), output.getName()));
        }
        flexibleAdapterActivities.updateDataSet(activities);
    }

    private void onActivitySelected(final ActivityItem activityItem) {
        fragmentInteractionListener.navigateToFragmentStatistics(activityItem.getId());
    }
}