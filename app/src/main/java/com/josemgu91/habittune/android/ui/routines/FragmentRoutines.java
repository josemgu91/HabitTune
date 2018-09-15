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
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.FragmentInteractionListener;
import com.josemgu91.habittune.android.ui.common.ConfirmationDialog;
import com.josemgu91.habittune.android.ui.common.FragmentList;
import com.josemgu91.habittune.domain.usecases.GetRoutines;

import java.util.ArrayList;
import java.util.List;

public class FragmentRoutines extends FragmentList<RoutineItem> {

    private static final String SAVED_INSTANCE_STATE_ITEM_TO_DELETE_ID = "routineId";
    private static final String SAVED_INSTANCE_STATE_ITEM_TO_DELETE_NAME = "routineName";

    private ViewModelRoutines viewModelRoutines;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        viewModelRoutines = ViewModelProviders.of(this, viewModelFactory).get(ViewModelRoutines.class);
    }

    @Override
    protected void saveItemToDeleteState(Bundle outState) {
        outState.putString(SAVED_INSTANCE_STATE_ITEM_TO_DELETE_ID, itemToDelete.getId());
        outState.putString(SAVED_INSTANCE_STATE_ITEM_TO_DELETE_NAME, itemToDelete.getName());
    }

    @Override
    protected RoutineItem restoreItemToDeleteState(Bundle savedInstanceState) {
        if (!savedInstanceState.containsKey(SAVED_INSTANCE_STATE_ITEM_TO_DELETE_ID)) {
            return null;
        }
        final String id = savedInstanceState.getString(SAVED_INSTANCE_STATE_ITEM_TO_DELETE_ID);
        final String name = savedInstanceState.getString(SAVED_INSTANCE_STATE_ITEM_TO_DELETE_NAME);
        return new RoutineItem(id, name);
    }

    @Override
    protected ConfirmationDialog createDeletionConfirmationDialog() {
        return ConfirmationDialog.newInstance(
                R.string.routines_delete_dialog_title,
                R.string.routines_delete_dialog_content,
                R.string.action_delete,
                R.string.action_cancel
        );
    }

    @Override
    protected void onDelete(RoutineItem itemToDelete) {
        viewModelRoutines.deleteRoutine(itemToDelete.getId());
    }

    @Override
    protected void onItemSelected(RoutineItem item) {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fragmentListBinding.floatingActionButtonAdd.setOnClickListener(null);
        fragmentListBinding.setShowProgress(true);
        viewModelRoutines.getGetRoutinesResponse().observe(this, response -> {
            switch (response.status) {
                case LOADING:
                    fragmentListBinding.setShowProgress(true);
                    break;
                case ERROR:
                    fragmentListBinding.setShowProgress(false);
                    break;
                case SUCCESS:
                    fragmentListBinding.setShowProgress(false);
                    response.successData.observe(this, this::updateRoutines);
                    break;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentInteractionListener.updateToolbar(getString(R.string.routines_title), FragmentInteractionListener.IC_NAVIGATION_HAMBURGUER);
        fragmentInteractionListener.updateNavigationDrawer(true);
    }

    private void updateRoutines(List<GetRoutines.Output> outputs) {
        final List<RoutineItem> routineItems = new ArrayList<>();
        for (final GetRoutines.Output output : outputs) {
            routineItems.add(new RoutineItem(output.getId(), output.getName()));
        }
        recyclerViewFlexibleAdapter.updateDataSet(routineItems);
    }
}
