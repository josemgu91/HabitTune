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

package com.josemgu91.habittune.android.ui.routines;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.FragmentInteractionListener;
import com.josemgu91.habittune.android.ui.BaseFragment;
import com.josemgu91.habittune.databinding.FragmentRoutinesBinding;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.helpers.ItemTouchHelperCallback;
import eu.davidea.flexibleadapter.items.IFlexible;

public class FragmentRoutines extends BaseFragment {

    private ViewModelRoutines viewModelRoutines;

    private FragmentRoutinesBinding fragmentRoutinesBinding;

    private RecyclerViewAdapterRoutines recyclerViewAdapterRoutines;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        viewModelRoutines = ViewModelProviders.of(this, viewModelFactory).get(ViewModelRoutines.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentRoutinesBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_routines, container, false);
        recyclerViewAdapterRoutines = new RecyclerViewAdapterRoutines(null, null, true);
        fragmentRoutinesBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentRoutinesBinding.recyclerView.setAdapter(recyclerViewAdapterRoutines);
        recyclerViewAdapterRoutines.setSwipeEnabled(true);
        final ItemTouchHelperCallback itemTouchHelperCallback = recyclerViewAdapterRoutines.getItemTouchHelperCallback();
        itemTouchHelperCallback.setSwipeFlags(ItemTouchHelper.RIGHT);
        return fragmentRoutinesBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
        fragmentRoutinesBinding.setShowProgress(false);
        recyclerViewAdapterRoutines.updateDataSet(generateTestData(100));
    }

    private void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        recyclerViewAdapterRoutines.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        recyclerViewAdapterRoutines.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentInteractionListener.updateToolbar(getString(R.string.routines_title), FragmentInteractionListener.IC_NAVIGATION_HAMBURGUER);
        fragmentInteractionListener.updateNavigationDrawer(true);
    }

    private List<IFlexible> generateTestData(final int size) {
        final List<IFlexible> routineItems = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            routineItems.add(new RoutineItem(
                    String.valueOf(i),
                    "Routine item " + i
            ));
        }
        return routineItems;
    }
}
