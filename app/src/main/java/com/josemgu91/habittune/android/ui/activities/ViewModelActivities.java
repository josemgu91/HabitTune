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

package com.josemgu91.habittune.android.ui.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import com.josemgu91.habittune.android.ui.Response;
import com.josemgu91.habittune.domain.usecases.DeleteActivity;
import com.josemgu91.habittune.domain.usecases.GetActivities;
import com.josemgu91.habittune.domain.usecases.GetActivity;
import com.josemgu91.habittune.domain.usecases.common.UseCaseOutput;

import java.util.List;

public class ViewModelActivities extends ViewModel {

    private final GetActivities getActivities;
    private final DeleteActivity deleteActivity;

    private final MutableLiveData<Response<LiveData<List<GetActivity.Output>>, Void>> getActivitiesResponse;

    public ViewModelActivities(final GetActivities getActivities, final DeleteActivity deleteActivity) {
        this.getActivities = getActivities;
        this.deleteActivity = deleteActivity;
        this.getActivitiesResponse = new MutableLiveData<>();
    }

    public void deleteActivity(final String activityId) {
        deleteActivity.execute(new DeleteActivity.Input(activityId), new UseCaseOutput<Void>() {
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

    public void fetchActivities() {
        getActivities.execute(null, new UseCaseOutput<LiveData<List<GetActivity.Output>>>() {
            @Override
            public void onSuccess(@Nullable LiveData<List<GetActivity.Output>> listLiveData) {
                getActivitiesResponse.setValue(new Response<>(Response.Status.SUCCESS, listLiveData, null));
            }

            @Override
            public void inProgress() {
                getActivitiesResponse.setValue(new Response<>(Response.Status.LOADING, null, null));
            }

            @Override
            public void onError() {
                getActivitiesResponse.setValue(new Response<>(Response.Status.ERROR, null, null));
            }
        });
    }

    public MutableLiveData<Response<LiveData<List<GetActivity.Output>>, Void>> getGetActivitiesResponse() {
        return getActivitiesResponse;
    }
}
