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

package com.josemgu91.habittune.domain.usecases.common;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.josemgu91.habittune.domain.datagateways.DataGatewayException;
import com.josemgu91.habittune.domain.datagateways.RoutineDataGateway;
import com.josemgu91.habittune.domain.entities.Routine;
import com.josemgu91.habittune.domain.util.Function;

import java.util.Objects;
import java.util.concurrent.Executor;

public class GetRoutine extends AbstractUseCase<GetRoutine.Input, GetRoutine.Output> {

    private final RoutineDataGateway routineDataGateway;
    private final RoutineMapper routineMapper;

    public GetRoutine(@NonNull Executor outputExecutor, @NonNull Executor useCaseExecutor, RoutineDataGateway routineDataGateway) {
        super(outputExecutor, useCaseExecutor);
        this.routineDataGateway = routineDataGateway;
        this.routineMapper = new RoutineMapper();
    }

    @Override
    protected void executeUseCase(@Nullable Input input, @NonNull UseCaseOutput<Output> output) {
        output.inProgress();
        try {
            final Routine result = routineDataGateway.getRoutineById(input.id);
            output.onSuccess(routineMapper.apply(result));
        } catch (DataGatewayException e) {
            e.printStackTrace();
            output.onError();
        }
    }

    private static class RoutineMapper implements Function<Routine, Output> {

        @Override
        public Output apply(Routine input) {
            return new Output(
                    input.getId(),
                    input.getName(),
                    input.getDescription(),
                    input.getColor(),
                    input.getNumberOfDays()
            );
        }
    }

    public final static class Input {

        private final String id;

        public Input(String id) {
            this.id = id;
        }
    }

    public final static class Output {

        @NonNull
        private final String id;
        @NonNull
        private final String name;
        @NonNull
        private final String description;
        private final int color;
        private final int numberOfDays;

        public Output(@NonNull String id, @NonNull String name, @NonNull String description, int color, int numberOfDays) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.color = color;
            this.numberOfDays = numberOfDays;
        }

        @NonNull
        public String getId() {
            return id;
        }

        @NonNull
        public String getName() {
            return name;
        }

        @NonNull
        public String getDescription() {
            return description;
        }

        public int getColor() {
            return color;
        }

        public int getNumberOfDays() {
            return numberOfDays;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Output output = (Output) o;
            return color == output.color &&
                    numberOfDays == output.numberOfDays &&
                    Objects.equals(id, output.id) &&
                    Objects.equals(name, output.name) &&
                    Objects.equals(description, output.description);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, description, color, numberOfDays);
        }

        @Override
        public String toString() {
            return "Output{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", color=" + color +
                    ", numberOfDays=" + numberOfDays +
                    '}';
        }
    }
}
