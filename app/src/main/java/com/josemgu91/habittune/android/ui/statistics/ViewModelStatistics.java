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

package com.josemgu91.habittune.android.ui.statistics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.josemgu91.habittune.android.ui.Response;
import com.josemgu91.habittune.domain.usecases.CalculateAssistanceStatistics;
import com.josemgu91.habittune.domain.usecases.GetActivity;
import com.josemgu91.habittune.domain.usecases.common.UseCaseOutput;

public class ViewModelStatistics extends ViewModel {

    private final GetActivity getActivity;
    private final CalculateAssistanceStatistics calculateAssistanceStatistics;

    private final MutableLiveData<Response<GetActivity.Output, Void>> getActivityResponse;
    private final MutableLiveData<Response<CalculateAssistanceStatistics.Output, Void>> calculateAssistanceStatsResponse;

    public ViewModelStatistics(GetActivity getActivity, CalculateAssistanceStatistics calculateAssistanceStatistics) {
        this.getActivity = getActivity;
        this.calculateAssistanceStatistics = calculateAssistanceStatistics;
        getActivityResponse = new MutableLiveData<>();
        calculateAssistanceStatsResponse = new MutableLiveData<>();
    }

    public void fetchActivity(@NonNull final String activityId) {
        getActivity.execute(new GetActivity.Input(activityId), new UseCaseOutput<GetActivity.Output>() {
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

    public void calculateAssistanceStats(@NonNull final String activityId) {
        calculateAssistanceStatistics.execute(new CalculateAssistanceStatistics.Input(activityId), new UseCaseOutput<CalculateAssistanceStatistics.Output>() {
            @Override
            public void onSuccess(@Nullable CalculateAssistanceStatistics.Output output) {
                calculateAssistanceStatsResponse.setValue(new Response<>(Response.Status.SUCCESS, output, null));
            }

            @Override
            public void inProgress() {
                calculateAssistanceStatsResponse.setValue(new Response<>(Response.Status.LOADING, null, null));
            }

            @Override
            public void onError() {
                calculateAssistanceStatsResponse.setValue(new Response<>(Response.Status.ERROR, null, null));
            }
        });
    }

    public LiveData<Response<GetActivity.Output, Void>> getGetActivityResponse() {
        return getActivityResponse;
    }

    public LiveData<Response<CalculateAssistanceStatistics.Output, Void>> getCalculateAssistanceStatsResponse() {
        return calculateAssistanceStatsResponse;
    }
}
