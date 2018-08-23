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

package com.josemgu91.habittune.adapter.ui;

import android.support.annotation.NonNull;

import com.josemgu91.habittune.domain.usecases.UseCaseOutput;

import java.util.concurrent.Executor;

public class UseCaseOutputExecutorWrapper<Output> implements UseCaseOutput<Output> {

    private final UseCaseOutput<Output> useCaseOutput;
    private final Executor viewExecutor;

    public UseCaseOutputExecutorWrapper(@NonNull final UseCaseOutput<Output> useCaseOutput, @NonNull final Executor viewExecutor) {
        this.useCaseOutput = useCaseOutput;
        this.viewExecutor = viewExecutor;
    }

    @Override
    public void onSuccess(@NonNull Output output) {
        viewExecutor.execute(() -> useCaseOutput.onSuccess(output));
    }

    @Override
    public void inProgress() {
        viewExecutor.execute(useCaseOutput::inProgress);
    }

    @Override
    public void onError() {
        viewExecutor.execute(useCaseOutput::onError);
    }
}
