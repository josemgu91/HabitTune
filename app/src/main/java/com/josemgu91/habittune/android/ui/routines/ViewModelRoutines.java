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

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import com.josemgu91.habittune.android.ui.Response;
import com.josemgu91.habittune.domain.usecases.DeleteRoutine;
import com.josemgu91.habittune.domain.usecases.GetRoutines;
import com.josemgu91.habittune.domain.usecases.common.UseCaseOutput;

import java.util.List;

public class ViewModelRoutines extends ViewModel {

    private final GetRoutines getRoutines;
    private final DeleteRoutine deleteRoutine;

    private final MutableLiveData<Response<LiveData<List<GetRoutines.Output>>, Void>> getRoutinesResponse;

    public ViewModelRoutines(GetRoutines getRoutines, DeleteRoutine deleteRoutine) {
        this.getRoutines = getRoutines;
        this.deleteRoutine = deleteRoutine;
        getRoutinesResponse = new MutableLiveData<>();
    }

    public void deleteRoutine(final String routineId) {
        deleteRoutine.execute(new DeleteRoutine.Input(routineId), new UseCaseOutput<Void>() {
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

    public void fetchRoutines() {
        getRoutines.execute(new GetRoutines.Input(GetRoutines.Input.WITHOUT_ROUTINE_ENTRIES), new UseCaseOutput<LiveData<List<GetRoutines.Output>>>() {
            @Override
            public void onSuccess(@Nullable LiveData<List<GetRoutines.Output>> listLiveData) {
                getRoutinesResponse.setValue(new Response<>(Response.Status.SUCCESS, listLiveData, null));
            }

            @Override
            public void inProgress() {
                getRoutinesResponse.setValue(new Response<>(Response.Status.LOADING, null, null));
            }

            @Override
            public void onError() {
                getRoutinesResponse.setValue(new Response<>(Response.Status.ERROR, null, null));
            }
        });
    }

    public MutableLiveData<Response<LiveData<List<GetRoutines.Output>>, Void>> getGetRoutinesResponse() {
        return getRoutinesResponse;
    }
}
