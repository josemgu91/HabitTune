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

import java.util.concurrent.Executor;

public abstract class AbstractUseCase<Input, Output> implements UseCase<Input, Output> {

    @NonNull
    private final Executor useCaseExecutor;
    @NonNull
    private final Executor outputExecutor;

    public AbstractUseCase(@NonNull final Executor outputExecutor, @NonNull final Executor useCaseExecutor) {
        this.useCaseExecutor = useCaseExecutor;
        this.outputExecutor = outputExecutor;
    }

    @Override
    public void execute(@Nullable final Input input, @NonNull final UseCaseOutput<Output> output) {
        useCaseExecutor.execute(() -> executeUseCase(
                input,
                new DefaultUseCaseOutputExecutor<>(outputExecutor, output)
        ));
    }

    protected abstract void executeUseCase(@Nullable final Input input, @NonNull final UseCaseOutput<Output> output);

    private static class DefaultUseCaseOutputExecutor<Output> implements UseCaseOutput<Output> {

        @NonNull
        private final Executor outputExecutor;
        @NonNull
        private final UseCaseOutput<Output> useCaseOutput;

        public DefaultUseCaseOutputExecutor(@NonNull final Executor outputExecutor, @NonNull final UseCaseOutput<Output> useCaseOutput) {
            this.outputExecutor = outputExecutor;
            this.useCaseOutput = useCaseOutput;
        }

        @Override
        public void onSuccess(@Nullable Output output) {
            outputExecutor.execute(() -> useCaseOutput.onSuccess(output));
        }

        @Override
        public void inProgress() {
            outputExecutor.execute(useCaseOutput::inProgress);
        }

        @Override
        public void onError() {
            outputExecutor.execute(useCaseOutput::onError);
        }
    }

}
