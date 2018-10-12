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

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.josemgu91.habittune.domain.datagateways.DataGatewayException;
import com.josemgu91.habittune.domain.datagateways.RoutineDataGateway;
import com.josemgu91.habittune.domain.entities.RoutineEntry;
import com.josemgu91.habittune.domain.usecases.common.AbstractUseCase;
import com.josemgu91.habittune.domain.usecases.common.UseCaseOutput;
import com.josemgu91.habittune.domain.util.Function;
import com.josemgu91.habittune.domain.util.ListMapper;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

public class GetRoutineEntriesByDate extends AbstractUseCase<GetRoutineEntriesByDate.Input, LiveData<List<GetRoutineEntriesByDate.Output>>> {

    private final RoutineDataGateway routineDataGateway;
    private final Function<List<RoutineEntry>, List<Output>> listMapper;

    public GetRoutineEntriesByDate(@NonNull Executor outputExecutor, @NonNull Executor useCaseExecutor, RoutineDataGateway routineDataGateway) {
        super(outputExecutor, useCaseExecutor);
        this.routineDataGateway = routineDataGateway;
        this.listMapper = new ListMapper<>(new RoutineEntryMapper());
    }

    @Override
    protected void executeUseCase(@Nullable Input input, @NonNull UseCaseOutput<LiveData<List<Output>>> output) {
        output.inProgress();
        try {
            final LiveData<List<RoutineEntry>> result = routineDataGateway.subscribeToRoutineEntriesByDate(input.date);
            final LiveData<List<Output>> outputLiveData = Transformations.map(result, listMapper::apply);
            output.onSuccess(outputLiveData);
        } catch (DataGatewayException e) {
            e.printStackTrace();
            output.onError();
        }
    }

    private final class RoutineEntryMapper implements Function<RoutineEntry, Output> {

        @Override
        public Output apply(RoutineEntry input) {
            return new Output();
        }
    }

    public static final class Input {

        private final Date date;

        public Input(final Date date) {
            this.date = date;
        }


    }

    public static final class Output {

    }

}
