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

package com.josemgu91.habittune.android.ui.new_activity;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import com.josemgu91.habittune.android.ui.Response;
import com.josemgu91.habittune.domain.usecases.CreateActivity;
import com.josemgu91.habittune.domain.usecases.common.UseCaseOutput;

public class ViewModelNewActivity extends ViewModel {

    private final CreateActivity createActivity;

    private final MutableLiveData<Response<Void, Void>> response;

    public ViewModelNewActivity(final CreateActivity createActivity) {
        this.createActivity = createActivity;
        this.response = new MutableLiveData<>();
    }

    public void createActivity(final CreateActivity.Input activity) {
        createActivity.execute(activity, new UseCaseOutput<Void>() {
            @Override
            public void onSuccess(@Nullable Void aVoid) {
                response.setValue(new Response<>(Response.Status.SUCCESS, null, null));
            }

            @Override
            public void inProgress() {
                response.setValue(new Response<>(Response.Status.LOADING, null, null));
            }

            @Override
            public void onError() {
                response.setValue(new Response<>(Response.Status.ERROR, null, null));
            }
        });
    }

    public MutableLiveData<Response<Void, Void>> getResponse() {
        return response;
    }
}
