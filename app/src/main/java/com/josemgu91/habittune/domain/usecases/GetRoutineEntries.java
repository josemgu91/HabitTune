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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.josemgu91.habittune.domain.datagateways.RoutineDataGateway;
import com.josemgu91.habittune.domain.usecases.common.AbstractUseCase;
import com.josemgu91.habittune.domain.usecases.common.UseCaseOutput;

import java.util.concurrent.Executor;

public class GetRoutineEntries extends AbstractUseCase<GetRoutineEntries.Input, GetRoutineEntries.Output> {

    private final RoutineDataGateway routineDataGateway;

    public GetRoutineEntries(@NonNull Executor outputExecutor, @NonNull Executor useCaseExecutor, RoutineDataGateway routineDataGateway) {
        super(outputExecutor, useCaseExecutor);
        this.routineDataGateway = routineDataGateway;
    }

    @Override
    protected void executeUseCase(@Nullable Input input, @NonNull UseCaseOutput<Output> output) {

    }

    public static final class Input {

    }

    public static final class Output {

    }
}
