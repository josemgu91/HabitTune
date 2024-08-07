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

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.annotation.Nullable;

import com.josemgu91.habittune.android.ui.Response;
import com.josemgu91.habittune.domain.usecases.CreateRoutine;
import com.josemgu91.habittune.domain.usecases.common.UseCaseOutput;

public class ViewModelNewRoutine extends ViewModel {

    private CreateRoutine createRoutine;

    private final MutableLiveData<Response<CreateRoutine.Output, Void>> createRoutineResponse;

    public ViewModelNewRoutine(CreateRoutine createRoutine) {
        this.createRoutine = createRoutine;
        this.createRoutineResponse = new MutableLiveData<>();
    }

    public void createRoutine(final CreateRoutine.Input routine) {
        createRoutine.execute(routine, new UseCaseOutput<CreateRoutine.Output>() {
            @Override
            public void onSuccess(@Nullable CreateRoutine.Output output) {
                createRoutineResponse.setValue(new Response<>(Response.Status.SUCCESS, output, null));
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

    public LiveData<Response<CreateRoutine.Output, Void>> getCreateRoutineResponse() {
        return createRoutineResponse;
    }
}
