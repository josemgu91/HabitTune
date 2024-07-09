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

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

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
    private static final String SAVED_INSTANCE_STATE_ITEM_TO_DELETE_COLOR = "routineColor";

    private ViewModelRoutines viewModelRoutines;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        viewModelRoutines = new ViewModelProvider(this, viewModelFactory).get(ViewModelRoutines.class);
    }

    @Override
    protected void saveItemToDeleteState(Bundle outState) {
        outState.putString(SAVED_INSTANCE_STATE_ITEM_TO_DELETE_ID, itemToDelete.getId());
        outState.putString(SAVED_INSTANCE_STATE_ITEM_TO_DELETE_NAME, itemToDelete.getName());
        outState.putInt(SAVED_INSTANCE_STATE_ITEM_TO_DELETE_COLOR, itemToDelete.getColor());
    }

    @Override
    protected RoutineItem restoreItemToDeleteState(Bundle savedInstanceState) {
        if (!savedInstanceState.containsKey(SAVED_INSTANCE_STATE_ITEM_TO_DELETE_ID)) {
            return null;
        }
        final String id = savedInstanceState.getString(SAVED_INSTANCE_STATE_ITEM_TO_DELETE_ID);
        final String name = savedInstanceState.getString(SAVED_INSTANCE_STATE_ITEM_TO_DELETE_NAME);
        final int color = savedInstanceState.getInt(SAVED_INSTANCE_STATE_ITEM_TO_DELETE_COLOR);
        return new RoutineItem(id, name, color);
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
        fragmentInteractionListener.navigateToFragmentRoutineEditor(item.getId());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModelRoutines.fetchRoutines();
        fragmentListBinding.floatingActionButtonAdd.setOnClickListener(v -> fragmentInteractionListener.navigateToFragmentNewRoutine());
        fragmentListBinding.setShowProgress(true);
        viewModelRoutines.getGetRoutinesResponse().observe(getViewLifecycleOwner(), response -> {
            switch (response.status) {
                case LOADING:
                    fragmentListBinding.setShowProgress(true);
                    break;
                case ERROR:
                    fragmentListBinding.setShowProgress(false);
                    break;
                case SUCCESS:
                    fragmentListBinding.setShowProgress(false);
                    response.successData.observe(getViewLifecycleOwner(), this::updateRoutines);
                    break;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        fragmentInteractionListener.updateToolbar(getString(R.string.routines_title), FragmentInteractionListener.IC_NAVIGATION_HAMBURGUER);
        fragmentInteractionListener.updateNavigationDrawer(true);
    }

    private void updateRoutines(List<GetRoutines.Output> outputs) {
        if (outputs.size() == 0) {
            fragmentListBinding.setShowWarning(true);
            fragmentListBinding.textViewWarning.setText(R.string.routines_empty);
            return;
        }
        final List<RoutineItem> routineItems = new ArrayList<>();
        for (final GetRoutines.Output output : outputs) {
            routineItems.add(new RoutineItem(output.getId(), output.getName(), output.getColor()));
        }
        recyclerViewFlexibleAdapter.updateDataSet(routineItems);
    }
}
