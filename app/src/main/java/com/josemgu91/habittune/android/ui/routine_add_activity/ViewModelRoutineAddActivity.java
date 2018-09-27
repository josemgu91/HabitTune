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

package com.josemgu91.habittune.android.ui.routine_add_activity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import com.josemgu91.habittune.android.ui.Response;
import com.josemgu91.habittune.domain.usecases.CreateRoutineEntry;
import com.josemgu91.habittune.domain.usecases.GetActivity;
import com.josemgu91.habittune.domain.usecases.UpdateRoutineEntry;
import com.josemgu91.habittune.domain.usecases.common.UseCaseOutput;

public class ViewModelRoutineAddActivity extends ViewModel {

    private final CreateRoutineEntry createRoutineEntry;
    private final GetActivity getActivity;
    private final UpdateRoutineEntry updateRoutineEntry;

    private final MutableLiveData<Response<GetActivity.Output, Void>> getActivityResponse;
    private final MutableLiveData<Response<Void, Void>> createRoutineEntryResponse;
    private final MutableLiveData<Response<Void, Void>> updateRoutineEntryResponse;

    public ViewModelRoutineAddActivity(final CreateRoutineEntry createRoutineEntry, final GetActivity getActivity, final UpdateRoutineEntry updateRoutineEntry) {
        this.createRoutineEntry = createRoutineEntry;
        this.getActivity = getActivity;
        this.updateRoutineEntry = updateRoutineEntry;
        this.getActivityResponse = new MutableLiveData<>();
        this.createRoutineEntryResponse = new MutableLiveData<>();
        this.updateRoutineEntryResponse = new MutableLiveData<>();
    }

    public void getActivity(final String id) {
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

    public void setUpdateRoutineEntry(final String routineEntryId, final String routineId, final String activityId, int day, int startTime, int endTime) {
        updateRoutineEntry.execute(new UpdateRoutineEntry.Input(routineId, routineId, activityId, day, startTime, endTime), new UseCaseOutput<Void>() {
            @Override
            public void onSuccess(@Nullable Void aVoid) {
                updateRoutineEntryResponse.setValue(new Response<>(Response.Status.SUCCESS, null, null));
            }

            @Override
            public void inProgress() {
                updateRoutineEntryResponse.setValue(new Response<>(Response.Status.LOADING, null, null));
            }

            @Override
            public void onError() {
                updateRoutineEntryResponse.setValue(new Response<>(Response.Status.ERROR, null, null));
            }
        });
    }

    public LiveData<Response<GetActivity.Output, Void>> getGetActivityResponse() {
        return getActivityResponse;
    }

    public LiveData<Response<Void, Void>> getCreateRoutineEntryResponse() {
        return createRoutineEntryResponse;
    }

    public LiveData<Response<Void, Void>> getUpdateRoutineEntryResponse() {
        return updateRoutineEntryResponse;
    }
}
