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

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.Nullable;
import android.util.Log;

import com.josemgu91.habittune.android.usecases.DefaultUseCaseFactory;
import com.josemgu91.habittune.domain.usecases.CreateActivity;
import com.josemgu91.habittune.domain.usecases.GetActivities;
import com.josemgu91.habittune.domain.usecases.UseCase;
import com.josemgu91.habittune.domain.usecases.UseCaseOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ViewModelActivities extends AndroidViewModel {

    private final UseCase<Void> getActivities;
    private final UseCase<CreateActivity.Input> createActivity;

    private final MutableLiveData<Boolean> isInProgress;
    private final MutableLiveData<Boolean> hasError;
    private LiveData<List<GetActivities.Output>> activities;

    public ViewModelActivities(final Application application) {
        super(application);
        isInProgress = new MutableLiveData<>();
        hasError = new MutableLiveData<>();
        //activities = new MutableLiveData<>();
        final com.josemgu91.habittune.android.Application habitTuneApplication = ((com.josemgu91.habittune.android.Application) application);
        final DefaultUseCaseFactory defaultUseCaseFactory = new DefaultUseCaseFactory(
                habitTuneApplication.getUiThreadExecutor(),
                habitTuneApplication.getDefaultThreadPoolExecutor(),
                habitTuneApplication.getRoomRepository()
        );
        this.getActivities = defaultUseCaseFactory.createGetActivities(
                new UseCaseOutput<LiveData<List<GetActivities.Output>>>() {
                    @Override
                    public void onSuccess(@Nullable LiveData<List<GetActivities.Output>> listLiveData) {
                        Log.d("ViewModelActivities", "onSuccess");
                        activities = listLiveData;
                        isInProgress.setValue(false);
                        hasError.setValue(false);
                    }

                    @Override
                    public void inProgress() {
                        Log.d("ViewModelActivities", "inProgress");
                        isInProgress.setValue(true);
                        hasError.setValue(false);
                    }

                    @Override
                    public void onError() {
                        Log.d("ViewModelActivities", "onError");
                        isInProgress.setValue(false);
                        hasError.setValue(true);
                    }
                }
        );
        this.createActivity = defaultUseCaseFactory.createCreateActivity(
                new UseCaseOutput<Void>() {
                    @Override
                    public void onSuccess(@Nullable Void aVoid) {

                    }

                    @Override
                    public void inProgress() {

                    }

                    @Override
                    public void onError() {

                    }
                }
        );
    }

    public void fetchActivities() {
        getActivities.execute(null);
    }

    public void addTestActivity() {
        createActivity.execute(new CreateActivity.Input(
                "Test Activity " + new Random().nextInt(),
                "This is the desription",
                0xFF00FFFF,
                new ArrayList<>()
        ));
    }

    public LiveData<List<GetActivities.Output>> getActivities() {
        return activities;
    }

    public MutableLiveData<Boolean> getIsInProgress() {
        return isInProgress;
    }

    public MutableLiveData<Boolean> getHasError() {
        return hasError;
    }
}
