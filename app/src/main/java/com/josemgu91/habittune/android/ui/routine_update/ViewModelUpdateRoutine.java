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

package com.josemgu91.habittune.android.ui.routine_update;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.josemgu91.habittune.android.ui.Response;
import com.josemgu91.habittune.domain.usecases.UpdateRoutine;
import com.josemgu91.habittune.domain.usecases.GetRoutine;
import com.josemgu91.habittune.domain.usecases.common.UseCaseOutput;

public class ViewModelUpdateRoutine extends ViewModel {

    private final GetRoutine getRoutine;
    private final UpdateRoutine updateRoutine;

    private final MutableLiveData<Response<GetRoutine.Output, Void>> getRoutineResponse;

    public ViewModelUpdateRoutine(GetRoutine getRoutine, UpdateRoutine updateRoutine) {
        this.getRoutine = getRoutine;
        this.updateRoutine = updateRoutine;
        getRoutineResponse = new MutableLiveData<>();
    }

    public void fetchRoutine(@NonNull final String id) {
        getRoutine.execute(new GetRoutine.Input(id), new UseCaseOutput<GetRoutine.Output>() {
            @Override
            public void onSuccess(@Nullable GetRoutine.Output output) {
                getRoutineResponse.postValue(new Response<>(Response.Status.SUCCESS, output, null));
            }

            @Override
            public void inProgress() {
                getRoutineResponse.postValue(new Response<>(Response.Status.LOADING, null, null));
            }

            @Override
            public void onError() {
                getRoutineResponse.postValue(new Response<>(Response.Status.ERROR, null, null));
            }
        });
    }

    public void updateRoutine(@NonNull final String id, @NonNull final String name, @NonNull final String description, final int color) {
        updateRoutine.execute(new UpdateRoutine.Input(id, name, description, color), new UseCaseOutput<Void>() {
            @Override
            public void onSuccess(@Nullable Void aVoid) {
            }

            @Override
            public void inProgress() {
            }

            @Override
            public void onError() {
            }
        });
    }

    public LiveData<Response<GetRoutine.Output, Void>> getGetRoutineResponse() {
        return getRoutineResponse;
    }
}
