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

import com.josemgu91.habittune.domain.datagateways.DataGatewayException;
import com.josemgu91.habittune.domain.datagateways.RoutineDataGateway;
import com.josemgu91.habittune.domain.entities.Routine;
import com.josemgu91.habittune.domain.usecases.common.AbstractUseCase;
import com.josemgu91.habittune.domain.usecases.common.UseCaseOutput;

import java.util.Objects;
import java.util.concurrent.Executor;

public class CreateRoutine extends AbstractUseCase<CreateRoutine.Input, CreateRoutine.Output> {

    private final RoutineDataGateway routineDataGateway;

    public CreateRoutine(@NonNull Executor outputExecutor, @NonNull Executor useCaseExecutor, RoutineDataGateway routineDataGateway) {
        super(outputExecutor, useCaseExecutor);
        this.routineDataGateway = routineDataGateway;
    }

    @Override
    protected void executeUseCase(@Nullable Input input, @NonNull UseCaseOutput<Output> output) {
        output.inProgress();
        try {
            final Routine routineCreated = routineDataGateway.createRoutine(new Routine(
                    input.name,
                    input.description,
                    input.color,
                    input.numberOfDays
            ));
            if (routineCreated != null) {
                output.onSuccess(new Output(routineCreated.getId()));
            } else {
                output.onError();
            }
        } catch (DataGatewayException e) {
            e.printStackTrace();
            output.onError();
        }
    }

    public final static class Input {
        private final String name;
        private final String description;
        private final int color;
        private final int numberOfDays;

        public Input(String name, String description, int color, int numberOfDays) {
            this.name = name;
            this.description = description;
            this.color = color;
            this.numberOfDays = numberOfDays;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Input input = (Input) o;
            return color == input.color &&
                    numberOfDays == input.numberOfDays &&
                    Objects.equals(name, input.name) &&
                    Objects.equals(description, input.description);
        }

        @Override
        public int hashCode() {

            return Objects.hash(name, description, color, numberOfDays);
        }

        @Override
        public String toString() {
            return "Input{" +
                    "name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", color=" + color +
                    ", numberOfDays=" + numberOfDays +
                    '}';
        }
    }

    public static final class Output {

        @NonNull
        private final String id;

        public Output(@NonNull String id) {
            this.id = id;
        }

        @NonNull
        public String getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Output output = (Output) o;
            return Objects.equals(id, output.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return "Output{" +
                    "id='" + id + '\'' +
                    '}';
        }
    }
}
