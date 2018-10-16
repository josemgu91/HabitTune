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

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import com.josemgu91.habittune.android.ui.Response;
import com.josemgu91.habittune.domain.usecases.GetRoutineEntriesByDate;
import com.josemgu91.habittune.domain.usecases.common.UseCaseOutput;

import java.util.Date;
import java.util.List;

public class ViewModelSchedule extends ViewModel {

    private final GetRoutineEntriesByDate getRoutineEntriesByDate;
    private final MutableLiveData<Response<LiveData<List<GetRoutineEntriesByDate.Output>>, Void>> getRoutineEntriesByDateResponse;

    public ViewModelSchedule(GetRoutineEntriesByDate getRoutineEntriesByDate) {
        this.getRoutineEntriesByDate = getRoutineEntriesByDate;
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

    public LiveData<Response<LiveData<List<GetRoutineEntriesByDate.Output>>, Void>> getGetRoutineEntriesByDateResponse() {
        return getRoutineEntriesByDateResponse;
    }
}
