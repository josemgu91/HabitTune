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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import com.josemgu91.habittune.databinding.FragmentActivitiesBinding;

import java.util.ArrayList;
import java.util.List;

public class FragmentActivities extends Fragment {

    private FragmentInteractionListener fragmentInteractionListener;

    private RecyclerView recyclerViewActivities;
    private FloatingActionButton floatingActionButtonAdd;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentInteractionListener = (FragmentInteractionListener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final FragmentActivitiesBinding fragmentActivitiesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_activities, container, false);
        final View view = fragmentActivitiesBinding.getRoot();
        recyclerViewActivities = fragmentActivitiesBinding.recyclerView;
        floatingActionButtonAdd = fragmentActivitiesBinding.floatingActionButtonAdd;
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        final RecyclerViewAdapterActivities recyclerViewAdapterActivities = new RecyclerViewAdapterActivities(getContext(), LayoutInflater.from(getContext()));
        recyclerViewAdapterActivities.setActivities(generateActivityTestData(20));
        recyclerViewAdapterActivities.setOnActivitySelectedListener(new RecyclerViewAdapterActivities.OnActivitySelectedListener() {
            @Override
            public void onActivitySelected(String activityName) {
                Toast.makeText(getContext(), activityName, Toast.LENGTH_SHORT).show();
            }
        });
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
        floatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentInteractionListener.navigateToFragmentNewActivity();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (fragmentInteractionListener != null) {
            fragmentInteractionListener.updateToolbar(getString(R.string.activities_title), FragmentInteractionListener.IC_NAVIGATION_HAMBURGUER);
            fragmentInteractionListener.updateNavigationDrawer(true);
        }
    }

    private List<String> generateActivityTestData(final int size) {
        final List<String> activityList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            activityList.add("Activity " + (i + 1));
        }
        return activityList;
    }
}
