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

package com.josemgu91.habittune.android.ui.routine_entry_add;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.annotation.Nullable;

import com.josemgu91.habittune.android.ui.Response;
import com.josemgu91.habittune.domain.usecases.CreateRoutineEntry;
import com.josemgu91.habittune.domain.usecases.GetActivity;
import com.josemgu91.habittune.domain.usecases.GetRoutineEntry;
import com.josemgu91.habittune.domain.usecases.common.UseCaseOutput;

public class ViewModelRoutineAddActivity extends ViewModel {

    private final GetRoutineEntry getRoutineEntry;
    private final CreateRoutineEntry createRoutineEntry;
    private final GetActivity getActivity;

    private final MutableLiveData<Response<GetActivity.Output, Void>> getActivityResponse;
    private final MutableLiveData<Response<Void, Void>> createRoutineEntryResponse;
    private final MutableLiveData<Response<GetRoutineEntry.Output, Void>> getRoutineEntryResponse;

    public ViewModelRoutineAddActivity(final CreateRoutineEntry createRoutineEntry, final GetActivity getActivity, final GetRoutineEntry getRoutineEntry) {
        this.createRoutineEntry = createRoutineEntry;
        this.getActivity = getActivity;
        this.getRoutineEntry = getRoutineEntry;
        this.getActivityResponse = new MutableLiveData<>();
        this.createRoutineEntryResponse = new MutableLiveData<>();
        this.getRoutineEntryResponse = new MutableLiveData<>();
    }

    public void fetchRoutineEntry(final String id) {
        getRoutineEntry.execute(new GetRoutineEntry.Input(id), new UseCaseOutput<GetRoutineEntry.Output>() {
            @Override
            public void onSuccess(@Nullable GetRoutineEntry.Output output) {
                getRoutineEntryResponse.setValue(new Response<>(Response.Status.SUCCESS, output, null));
            }

            @Override
            public void inProgress() {
                getRoutineEntryResponse.setValue(new Response<>(Response.Status.LOADING, null, null));
            }

            @Override
            public void onError() {
                getRoutineEntryResponse.setValue(new Response<>(Response.Status.ERROR, null, null));
            }
        });
    }

    public void fetchActivity(final String id) {
        getActivity.execute(new GetActivity.Input(id), new UseCaseOutput<GetActivity.Output>() {
            @Override
            public void onSuccess(@Nullable GetActivity.Output output) {
                getActivityResponse.setValue(new Response<>(Response.Status.SUCCESS, output, null));
            }

            @Override
            public void inProgress() {
                getActivityResponse.setValue(new Response<>(Response.Status.LOADING, null, null));
            }

            @Override
            public void onError() {
                getActivityResponse.setValue(new Response<>(Response.Status.ERROR, null, null));
            }
        });
    }

    public void createRoutineEntry(final String routineId, final String activityId, int day, int startTime, int endTime) {
        createRoutineEntry.execute(new CreateRoutineEntry.Input(routineId, activityId, day, startTime, endTime), new UseCaseOutput<Void>() {
            @Override
            public void onSuccess(@Nullable Void aVoid) {
                createRoutineEntryResponse.setValue(new Response<>(Response.Status.SUCCESS, null, null));
            }

            @Override
            public void inProgress() {
                createRoutineEntryResponse.setValue(new Response<>(Response.Status.LOADING, null, null));
            }

            @Override
            public void onError() {
                createRoutineEntryResponse.setValue(new Response<>(Response.Status.ERROR, null, null));
            }
        });
    }

    public LiveData<Response<GetActivity.Output, Void>> getGetActivityResponse() {
        return getActivityResponse;
    }

    public LiveData<Response<Void, Void>> getCreateRoutineEntryResponse() {
        return createRoutineEntryResponse;
    }

    public LiveData<Response<GetRoutineEntry.Output, Void>> getGetRoutineEntryResponse() {
        return getRoutineEntryResponse;
    }
}
