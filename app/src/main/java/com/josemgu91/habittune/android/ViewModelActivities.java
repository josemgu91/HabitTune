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

package com.josemgu91.habittune.android;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.josemgu91.habittune.adapter.ui.UseCaseExecutorWrapper;
import com.josemgu91.habittune.adapter.ui.UseCaseOutputExecutorWrapper;
import com.josemgu91.habittune.android.executors.DefaultThreadPoolExecutor;
import com.josemgu91.habittune.android.executors.UiThreadExecutor;
import com.josemgu91.habittune.domain.usecases.GetActivities;
import com.josemgu91.habittune.domain.usecases.UseCase;
import com.josemgu91.habittune.domain.usecases.UseCaseOutput;

import java.util.List;

public class ViewModelActivities extends AndroidViewModel {

    private final UseCase<Void> getActivities;

    private final MutableLiveData<Boolean> isInProgress;
    private final MutableLiveData<Boolean> hasError;
    private LiveData<List<GetActivities.Output>> activities;

    public ViewModelActivities(final Application application) {
        super(application);
        isInProgress = new MutableLiveData<>();
        hasError = new MutableLiveData<>();
        activities = new MutableLiveData<>();
        this.getActivities = new UseCaseExecutorWrapper<>(
                new DefaultThreadPoolExecutor(),
                new GetActivities(new UseCaseOutputExecutorWrapper<>(
                        new UseCaseOutput<LiveData<List<GetActivities.Output>>>() {
                            @Override
                            public void showResult(@NonNull LiveData<List<GetActivities.Output>> listLiveData) {
                                isInProgress.setValue(false);
                                hasError.setValue(false);
                                activities = listLiveData;
                            }

                            @Override
                            public void showInProgress() {
                                isInProgress.setValue(true);
                                hasError.setValue(false);
                            }

                            @Override
                            public void showError() {
                                isInProgress.setValue(false);
                                hasError.setValue(true);
                            }
                        }, new UiThreadExecutor()),
                        ((com.josemgu91.habittune.android.Application) application).getRoomRepository()
                )
        );
    }

    public void fetchActivities() {
        getActivities.execute(null);
    }

    public LiveData<List<GetActivities.Output>> getActivities() {
        return activities;
    }
}
