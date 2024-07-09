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

package com.josemgu91.habittune.android.ui.schedule;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.josemgu91.habittune.android.ui.Response;
import com.josemgu91.habittune.domain.entities.Time;
import com.josemgu91.habittune.domain.usecases.DeleteAssistance;
import com.josemgu91.habittune.domain.usecases.GetRoutineEntriesByDate;
import com.josemgu91.habittune.domain.usecases.RegisterAssistance;
import com.josemgu91.habittune.domain.usecases.common.UseCaseOutput;

import java.util.Date;
import java.util.List;

public class ViewModelSchedule extends ViewModel {

    private final GetRoutineEntriesByDate getRoutineEntriesByDate;
    private final RegisterAssistance registerAssistance;
    private final DeleteAssistance deleteAssistance;
    private final MutableLiveData<Response<LiveData<List<GetRoutineEntriesByDate.Output>>, Void>> getRoutineEntriesByDateResponse;

    public ViewModelSchedule(final GetRoutineEntriesByDate getRoutineEntriesByDate, final RegisterAssistance registerAssistance, final DeleteAssistance deleteAssistance) {
        this.getRoutineEntriesByDate = getRoutineEntriesByDate;
        this.registerAssistance = registerAssistance;
        this.deleteAssistance = deleteAssistance;
        getRoutineEntriesByDateResponse = new MutableLiveData<>();
    }

    public void fetchRoutines(final Date date) {
        getRoutineEntriesByDate.execute(new GetRoutineEntriesByDate.Input(date), new UseCaseOutput<LiveData<List<GetRoutineEntriesByDate.Output>>>() {
            @Override
            public void onSuccess(@Nullable LiveData<List<GetRoutineEntriesByDate.Output>> listLiveData) {
                getRoutineEntriesByDateResponse.setValue(new Response<>(Response.Status.SUCCESS, listLiveData, null));
            }

            @Override
            public void inProgress() {
                getRoutineEntriesByDateResponse.setValue(new Response<>(Response.Status.LOADING, null, null));
            }

            @Override
            public void onError() {
                getRoutineEntriesByDateResponse.setValue(new Response<>(Response.Status.ERROR, null, null));
            }
        });
    }

    public void registerAssistance(@NonNull final String routineEntryId, final int cycleNumber, @NonNull final Time startHour, @Nullable final Time endHour) {
        registerAssistance.execute(new RegisterAssistance.Input(routineEntryId, cycleNumber, startHour, endHour), new UseCaseOutput<Void>() {
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

    public void deleteAssistance(@NonNull final String routineEntryId, final int cycleNumber) {
        deleteAssistance.execute(new DeleteAssistance.Input(routineEntryId, cycleNumber), new UseCaseOutput<Void>() {
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

    public LiveData<Response<LiveData<List<GetRoutineEntriesByDate.Output>>, Void>> getGetRoutineEntriesByDateResponse() {
        return getRoutineEntriesByDateResponse;
    }
}
