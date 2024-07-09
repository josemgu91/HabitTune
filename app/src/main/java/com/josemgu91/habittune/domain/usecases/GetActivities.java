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

package com.josemgu91.habittune.domain.usecases;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.josemgu91.habittune.domain.datagateways.ActivityDataGateway;
import com.josemgu91.habittune.domain.datagateways.DataGatewayException;
import com.josemgu91.habittune.domain.entities.Activity;
import com.josemgu91.habittune.domain.usecases.common.AbstractUseCase;
import com.josemgu91.habittune.domain.usecases.common.UseCaseOutput;
import com.josemgu91.habittune.domain.util.Function;
import com.josemgu91.habittune.domain.util.ListMapper;

import java.util.List;
import java.util.concurrent.Executor;

public class GetActivities extends AbstractUseCase<Void, LiveData<List<GetActivity.Output>>> {

    private final ActivityDataGateway activityDataGateway;
    private final Function<List<Activity>, List<GetActivity.Output>> listMapper;

    public GetActivities(@NonNull Executor outputExecutor, @NonNull Executor useCaseExecutor, @NonNull ActivityDataGateway activityDataGateway) {
        super(outputExecutor, useCaseExecutor);
        this.activityDataGateway = activityDataGateway;
        this.listMapper = new ListMapper<>(new GetActivity.ActivityMapper());
    }

    @Override
    protected void executeUseCase(@Nullable Void aVoid, @NonNull UseCaseOutput<LiveData<List<GetActivity.Output>>> output) {
        output.inProgress();
        try {
            final LiveData<List<Activity>> result = activityDataGateway.subscribeToAllActivities();
            final LiveData<List<GetActivity.Output>> outputLiveData = Transformations.map(result, listMapper::apply);
            output.onSuccess(outputLiveData);
        } catch (DataGatewayException e) {
            e.printStackTrace();
            output.onError();
        }
    }

}
