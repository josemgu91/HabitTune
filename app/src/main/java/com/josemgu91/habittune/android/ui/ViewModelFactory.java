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
import com.josemgu91.habittune.android.ui.new_activity.ViewModelNewActivity;
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
            return (T) new ViewModelActivities(useCaseFactory.createGetActivities());
        }
        if (modelClass.isAssignableFrom(ViewModelNewActivity.class)) {
            return (T) new ViewModelNewActivity(useCaseFactory.createCreateActivity());
        }
        throw new RuntimeException("Unknown ViewModel class");
    }
}