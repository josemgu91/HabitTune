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

package com.josemgu91.habittune.android.ui.routine_editor;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import com.josemgu91.habittune.android.ui.Response;
import com.josemgu91.habittune.domain.usecases.GetRoutineEntries;
import com.josemgu91.habittune.domain.usecases.common.GetRoutine;
import com.josemgu91.habittune.domain.usecases.common.UseCaseOutput;

import java.util.List;

public class ViewModelRoutineEditor extends ViewModel {

    private final GetRoutineEntries getRoutineEntries;
    private final GetRoutine getRoutine;

    private final MutableLiveData<Response<LiveData<List<GetRoutineEntries.Output>>, Void>> getRoutineEntriesResponse;
    private final MutableLiveData<Response<GetRoutine.Output, Void>> getRoutineResponse;

    public ViewModelRoutineEditor(GetRoutine getRoutine, GetRoutineEntries getRoutineEntries) {
        this.getRoutineEntries = getRoutineEntries;
        this.getRoutine = getRoutine;
        getRoutineEntriesResponse = new MutableLiveData<>();
        getRoutineResponse = new MutableLiveData<>();
    }

    public void fetchRoutineEntries(final String routineId) {
        getRoutineEntries.execute(new GetRoutineEntries.Input(routineId), new UseCaseOutput<LiveData<List<GetRoutineEntries.Output>>>() {
            @Override
            public void onSuccess(@Nullable LiveData<List<GetRoutineEntries.Output>> listLiveData) {
                getRoutineEntriesResponse.setValue(new Response<>(Response.Status.SUCCESS, listLiveData, null));
            }

            @Override
            public void inProgress() {
                getRoutineEntriesResponse.setValue(new Response<>(Response.Status.LOADING, null, null));
            }

            @Override
            public void onError() {
                getRoutineEntriesResponse.setValue(new Response<>(Response.Status.ERROR, null, null));
            }
        });
    }

    public void fetchRoutine(final String routineId) {
        getRoutine.execute(new GetRoutine.Input(routineId), new UseCaseOutput<GetRoutine.Output>() {
            @Override
            public void onSuccess(@Nullable GetRoutine.Output output) {
                getRoutineResponse.setValue(new Response<>(Response.Status.SUCCESS, output, null));
            }

            @Override
            public void inProgress() {
                getRoutineResponse.setValue(new Response<>(Response.Status.LOADING, null, null));
            }

            @Override
            public void onError() {
                getRoutineResponse.setValue(new Response<>(Response.Status.ERROR, null, null));
            }
        });
    }

    public LiveData<Response<LiveData<List<GetRoutineEntries.Output>>, Void>> getGetRoutineEntriesResponse() {
        return getRoutineEntriesResponse;
    }

    public LiveData<Response<GetRoutine.Output, Void>> getGetRoutineResponse() {
        return getRoutineResponse;
    }
}
