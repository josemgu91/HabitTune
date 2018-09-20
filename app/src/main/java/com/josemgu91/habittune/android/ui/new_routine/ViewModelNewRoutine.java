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

package com.josemgu91.habittune.android.ui.new_routine;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import com.josemgu91.habittune.android.ui.Response;
import com.josemgu91.habittune.domain.usecases.CreateRoutine;
import com.josemgu91.habittune.domain.usecases.common.UseCaseOutput;

public class ViewModelNewRoutine extends ViewModel {

    private CreateRoutine createRoutine;

    private final MutableLiveData<Response<Void, Void>> createRoutineResponse;

    public ViewModelNewRoutine(CreateRoutine createRoutine) {
        this.createRoutine = createRoutine;
        this.createRoutineResponse = new MutableLiveData<>();
    }

    public void createRoutine(final CreateRoutine.Input routine) {
        createRoutine.execute(routine, new UseCaseOutput<Void>() {
            @Override
            public void onSuccess(@Nullable Void aVoid) {
                createRoutineResponse.setValue(new Response<>(Response.Status.SUCCESS, null, null));
            }

            @Override
            public void inProgress() {
                createRoutineResponse.setValue(new Response<>(Response.Status.LOADING, null, null));
            }

            @Override
            public void onError() {
                createRoutineResponse.setValue(new Response<>(Response.Status.ERROR, null, null));
            }
        });
    }
}