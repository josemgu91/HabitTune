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
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.FragmentInteractionListener;
import com.josemgu91.habittune.android.ui.BaseFragment;
import com.josemgu91.habittune.databinding.FragmentActivitiesBinding;
import com.josemgu91.habittune.domain.usecases.GetActivities;

import java.util.ArrayList;
import java.util.List;

public class FragmentActivities extends BaseFragment {

    private ViewModelActivities viewModelActivities;

    private FragmentActivitiesBinding fragmentActivitiesBinding;

    private RecyclerViewAdapterActivities recyclerViewAdapterActivities;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentActivitiesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_activities, container, false);
        final View view = fragmentActivitiesBinding.getRoot();
        fragmentActivitiesBinding.floatingActionButtonAdd.setOnClickListener((v) -> onFloatingActionButtonClick());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final RecyclerView recyclerViewActivities = fragmentActivitiesBinding.recyclerView;
        recyclerViewAdapterActivities = new RecyclerViewAdapterActivities(getContext(), LayoutInflater.from(getContext()));
        fragmentActivitiesBinding.setShowProgress(true);
        viewModelActivities.getResponse().observe(this, response -> {
            switch (response.status) {
                case LOADING:
                    fragmentActivitiesBinding.setShowProgress(true);
                    break;
                case ERROR:
                    fragmentActivitiesBinding.setShowProgress(false);
                    break;
                case SUCCESS:
                    fragmentActivitiesBinding.setShowProgress(false);
                    response.successData.observe(this, this::showActivities);
                    break;
            }
        });
        recyclerViewAdapterActivities.setOnActivitySelectedListener(activityName -> Toast.makeText(getContext(), activityName, Toast.LENGTH_SHORT).show());
        recyclerViewAdapterActivities.setOnMultiSelectionModeListener(new RecyclerViewAdapterActivities.OnMultiSelectionModeListener() {

            private ActionMode actionMode;

            @Override
            public void onMultiSelectionModeEnabled() {
                actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        mode.getMenuInflater().inflate(R.menu.list_multiple_selection, menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.actionDeleteItems:
                                Toast.makeText(getContext(), "Delete Items", Toast.LENGTH_SHORT).show();
                                return true;
                            default:
                                return false;
                        }
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        recyclerViewAdapterActivities.setMultiSelectionMode(false);
                    }
                });
            }

            @Override
            public void onMultiSelectionModeDisabled() {
                if (actionMode != null) {
                    actionMode.finish();
                }
            }

            @Override
            public void onItemSelected() {
                actionMode.setTitle(String.valueOf(recyclerViewAdapterActivities.getSelectedActivities().size()));
            }
        });
        recyclerViewActivities.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewActivities.setAdapter(recyclerViewAdapterActivities);
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentInteractionListener.updateToolbar(getString(R.string.activities_title), FragmentInteractionListener.IC_NAVIGATION_HAMBURGUER);
        fragmentInteractionListener.updateNavigationDrawer(true);
    }

    private void showActivities(List<GetActivities.Output> outputs) {
        final List<String> activities = new ArrayList<>();
        for (final GetActivities.Output output : outputs) {
            activities.add(output.getName());
        }
        recyclerViewAdapterActivities.setActivities(activities);
    }

    private void onFloatingActionButtonClick() {

        fragmentInteractionListener.navigateToFragmentNewActivity();
    }

}
