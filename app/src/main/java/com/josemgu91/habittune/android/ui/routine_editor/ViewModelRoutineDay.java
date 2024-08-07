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

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.josemgu91.habittune.android.ui.Response;
import com.josemgu91.habittune.domain.usecases.DeleteRoutineEntry;
import com.josemgu91.habittune.domain.usecases.GetRoutineEntries;
import com.josemgu91.habittune.domain.usecases.common.UseCaseOutput;

import java.util.List;

public class ViewModelRoutineDay extends ViewModel {

    private final GetRoutineEntries getRoutineEntries;
    private final DeleteRoutineEntry deleteRoutineEntry;

    private final MutableLiveData<Response<LiveData<List<GetRoutineEntries.Output>>, Void>> getRoutineEntriesResponse;

    public ViewModelRoutineDay(@NonNull final GetRoutineEntries getRoutineEntries, @NonNull final DeleteRoutineEntry deleteRoutineEntry) {
        this.getRoutineEntries = getRoutineEntries;
        this.deleteRoutineEntry = deleteRoutineEntry;
        getRoutineEntriesResponse = new MutableLiveData<>();
    }

    public void fetchRoutineEntries(final String routineId, final int day) {
        getRoutineEntries.execute(new GetRoutineEntries.Input(routineId, day), new UseCaseOutput<LiveData<List<GetRoutineEntries.Output>>>() {
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

    public void deleteRoutineEntry(final String id) {
        deleteRoutineEntry.execute(new DeleteRoutineEntry.Input(id), new UseCaseOutput<Void>() {
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

    public LiveData<Response<LiveData<List<GetRoutineEntries.Output>>, Void>> getGetRoutineEntriesResponse() {
        return getRoutineEntriesResponse;
    }
}
