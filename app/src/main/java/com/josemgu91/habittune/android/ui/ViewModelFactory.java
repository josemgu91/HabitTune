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

package com.josemgu91.habittune.android.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.josemgu91.habittune.android.ui.activities.ViewModelActivities;
import com.josemgu91.habittune.android.ui.activity_create_update.ViewModelNewActivity;
import com.josemgu91.habittune.android.ui.activity_selection.ViewModelActivitySelection;
import com.josemgu91.habittune.android.ui.new_routine.ViewModelNewRoutine;
import com.josemgu91.habittune.android.ui.routine_editor.ViewModelRoutineDay;
import com.josemgu91.habittune.android.ui.routine_editor.ViewModelRoutineEditor;
import com.josemgu91.habittune.android.ui.routine_entry_add.ViewModelRoutineAddActivity;
import com.josemgu91.habittune.android.ui.routine_update.ViewModelUpdateRoutine;
import com.josemgu91.habittune.android.ui.routines.ViewModelRoutines;
import com.josemgu91.habittune.android.ui.schedule.ViewModelSchedule;
import com.josemgu91.habittune.android.ui.tag_editor.ViewModelTagEditor;
import com.josemgu91.habittune.android.usecases.UseCaseFactory;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final UseCaseFactory useCaseFactory;

    public ViewModelFactory(UseCaseFactory useCaseFactory) {
        this.useCaseFactory = useCaseFactory;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ViewModelActivities.class)) {
            return (T) new ViewModelActivities(useCaseFactory.createGetActivities(), useCaseFactory.createDeleteActivity());
        }
        if (modelClass.isAssignableFrom(ViewModelNewActivity.class)) {
            return (T) new ViewModelNewActivity(useCaseFactory.createCreateActivity(), useCaseFactory.createGetTags(), useCaseFactory.createUpdateActivity(), useCaseFactory.createGetActivity());
        }
        if (modelClass.isAssignableFrom(ViewModelTagEditor.class)) {
            return (T) new ViewModelTagEditor(useCaseFactory.createGetTags(), useCaseFactory.createCreateTag(), useCaseFactory.createDeleteTag(), useCaseFactory.createUpdateTag());
        }
        if (modelClass.isAssignableFrom(ViewModelRoutines.class)) {
            return (T) new ViewModelRoutines(useCaseFactory.createGetRoutines(), useCaseFactory.createDeleteRoutine());
        }
        if (modelClass.isAssignableFrom(ViewModelNewRoutine.class)) {
            return (T) new ViewModelNewRoutine(useCaseFactory.createCreateRoutine());
        }
        if (modelClass.isAssignableFrom(ViewModelRoutineEditor.class)) {
            return (T) new ViewModelRoutineEditor(useCaseFactory.createGetRoutine());
        }
        if (modelClass.isAssignableFrom(ViewModelRoutineDay.class)) {
            return (T) new ViewModelRoutineDay(useCaseFactory.createGetRoutineEntries(), useCaseFactory.createDeleteRoutineEntry());
        }
        if (modelClass.isAssignableFrom(ViewModelRoutineAddActivity.class)) {
            return (T) new ViewModelRoutineAddActivity(useCaseFactory.createCreateRoutineEntry(), useCaseFactory.createGetActivity(), useCaseFactory.createGetRoutineEntry());
        }
        if (modelClass.isAssignableFrom(ViewModelActivitySelection.class)) {
            return (T) new ViewModelActivitySelection(useCaseFactory.createGetActivities());
        }
        if (modelClass.isAssignableFrom(ViewModelUpdateRoutine.class)) {
            return (T) new ViewModelUpdateRoutine(useCaseFactory.createGetRoutine(), useCaseFactory.createUpdateRoutine());
        }
        if (modelClass.isAssignableFrom(ViewModelSchedule.class)) {
            return (T) new ViewModelSchedule(useCaseFactory.createGetRoutines());
        }
        try {
            return modelClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unknown ViewModel class");
        }
    }
}
