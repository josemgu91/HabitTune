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
import com.josemgu91.habittune.domain.entities.Routine;
import com.josemgu91.habittune.domain.entities.RoutineEntry;
import com.josemgu91.habittune.domain.usecases.common.AbstractUseCase;
import com.josemgu91.habittune.domain.usecases.common.UseCaseOutput;
import com.josemgu91.habittune.domain.util.Function;
import com.josemgu91.habittune.domain.util.ListMapper;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

public class GetRoutines extends AbstractUseCase<GetRoutines.Input, LiveData<List<GetRoutines.Output>>> {

    private final RoutineDataGateway routineDataGateway;
    private final Function<List<Routine>, List<Output>> listMapper;

    public GetRoutines(@NonNull Executor outputExecutor, @NonNull Executor useCaseExecutor, RoutineDataGateway routineDataGateway) {
        super(outputExecutor, useCaseExecutor);
        this.routineDataGateway = routineDataGateway;
        this.listMapper = new ListMapper<>(routine -> new Output(
                routine.getId(),
                routine.getName(),
                routine.getDescription(),
                routine.getColor(),
                routine.getNumberOfDays(),
                routine.getRoutineEntries() != null ?
                        new ListMapper<>((Function<RoutineEntry, GetRoutineEntries.Output>) input -> new GetRoutineEntries.Output(
                                input.getId(),
                                input.getDay().getDay(),
                                input.getStartTime().getTime(),
                                input.getEndTime().getTime(),
                                new GetRoutineEntries.Output.Activity(
                                        input.getActivity().getId(),
                                        input.getActivity().getName(),
                                        input.getActivity().getDescription(),
                                        input.getActivity().getColor()
                                )
                        )).apply(routine.getRoutineEntries()) :
                        null
        ));
    }

    @Override
    protected void executeUseCase(@Nullable Input input, @NonNull UseCaseOutput<LiveData<List<Output>>> output) {
        output.inProgress();
        try {
            final LiveData<List<Routine>> result = routineDataGateway.subscribeToAllRoutines(input.type == Input.WITH_ROUTINE_ENTRIES);
            final LiveData<List<Output>> outputLiveData = Transformations.map(result, listMapper::apply);
            output.onSuccess(outputLiveData);
        } catch (DataGatewayException e) {
            e.printStackTrace();
            output.onError();
        }
    }

    public static final class Input {

        public final static int WITH_ROUTINE_ENTRIES = 1;
        public final static int WITHOUT_ROUTINE_ENTRIES = 2;

        private final int type;

        public Input(int type) {
            this.type = type;
        }
    }

    public static final class Output {

        private final String id;
        private final String name;
        private final String description;
        private final int color;
        private final int numberOfDays;
        private final List<GetRoutineEntries.Output> routineEntries;

        public Output(String id, String name, String description, int color, int numberOfDays, List<GetRoutineEntries.Output> routineEntries) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.color = color;
            this.numberOfDays = numberOfDays;
            this.routineEntries = routineEntries;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public int getColor() {
            return color;
        }

        public int getNumberOfDays() {
            return numberOfDays;
        }

        public List<GetRoutineEntries.Output> getRoutineEntries() {
            return routineEntries;
        }

        @Override
        public String toString() {
            return "Output{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", color=" + color +
                    ", numberOfDays=" + numberOfDays +
                    ", routineEntries=" + routineEntries +
                    '}';
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
                    Objects.equals(description, output.description) &&
                    Objects.equals(routineEntries, output.routineEntries);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, description, color, numberOfDays, routineEntries);
        }
    }

}
