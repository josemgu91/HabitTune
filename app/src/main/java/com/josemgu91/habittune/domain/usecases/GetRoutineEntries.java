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

import com.josemgu91.habittune.domain.datagateways.DataGatewayException;
import com.josemgu91.habittune.domain.datagateways.RoutineEntryDataGateway;
import com.josemgu91.habittune.domain.entities.RoutineEntry;
import com.josemgu91.habittune.domain.usecases.common.AbstractUseCase;
import com.josemgu91.habittune.domain.usecases.common.UseCaseOutput;
import com.josemgu91.habittune.domain.util.Function;
import com.josemgu91.habittune.domain.util.ListMapper;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

public class GetRoutineEntries extends AbstractUseCase<GetRoutineEntries.Input, LiveData<List<GetRoutineEntries.Output>>> {

    private final RoutineEntryDataGateway routineEntryDataGateway;
    private final Function<List<RoutineEntry>, List<Output>> listMapper;

    public GetRoutineEntries(@NonNull Executor outputExecutor, @NonNull Executor useCaseExecutor, RoutineEntryDataGateway routineEntryDataGateway) {
        super(outputExecutor, useCaseExecutor);
        this.routineEntryDataGateway = routineEntryDataGateway;
        this.listMapper = new ListMapper<>(new RoutineEntryMapper());
    }

    @Override
    protected void executeUseCase(@Nullable Input input, @NonNull UseCaseOutput<LiveData<List<Output>>> output) {
        output.inProgress();
        try {
            final LiveData<List<RoutineEntry>> result;
            if (input.dayNumber == Input.ALL_DAYS) {
                result = routineEntryDataGateway.subscribeToAllRoutineEntriesByRoutineId(input.routineId);
            } else {
                result = routineEntryDataGateway.subscribeToAllRoutineEntriesByRoutineIdAndDay(input.routineId, input.dayNumber);
            }
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
            return new Output(
                    input.getId(),
                    input.getDay().getDay(),
                    input.getStartTime().getTime(),
                    input.getEndTime().getTime(),
                    new Output.Activity(
                            input.getActivity().getId(),
                            input.getActivity().getName(),
                            input.getActivity().getDescription(),
                            input.getActivity().getColor()
                    )
            );
        }
    }

    public static final class Input {

        public final static int ALL_DAYS = -1;

        private final String routineId;
        private final int dayNumber;

        public Input(String routineId, int dayNumber) {
            this.routineId = routineId;
            this.dayNumber = dayNumber;
        }

        public Input(String routineId) {
            this.routineId = routineId;
            this.dayNumber = ALL_DAYS;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Input input = (Input) o;
            return dayNumber == input.dayNumber &&
                    Objects.equals(routineId, input.routineId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(routineId, dayNumber);
        }

        @Override
        public String toString() {
            return "Input{" +
                    "routineId='" + routineId + '\'' +
                    ", dayNumber=" + dayNumber +
                    '}';
        }
    }

    public static final class Output {

        @NonNull
        private final String id;
        private final int day;
        private final int startTime;
        private final int endTime;
        @NonNull
        private final Activity activity;

        public Output(@NonNull String id, int day, int startTime, int endTime, @NonNull Activity activity) {
            this.id = id;
            this.day = day;
            this.startTime = startTime;
            this.endTime = endTime;
            this.activity = activity;
        }

        @NonNull
        public String getId() {
            return id;
        }

        public int getDay() {
            return day;
        }

        public int getStartTime() {
            return startTime;
        }

        public int getEndTime() {
            return endTime;
        }

        @NonNull
        public Activity getActivity() {
            return activity;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Output output = (Output) o;
            return day == output.day &&
                    startTime == output.startTime &&
                    endTime == output.endTime &&
                    Objects.equals(id, output.id) &&
                    Objects.equals(activity, output.activity);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, day, startTime, endTime, activity);
        }

        @Override
        public String toString() {
            return "Output{" +
                    "id='" + id + '\'' +
                    ", day=" + day +
                    ", startTime=" + startTime +
                    ", endTime=" + endTime +
                    ", activity=" + activity +
                    '}';
        }

        public final static class Activity {
            @NonNull
            private final String id;
            @NonNull
            private final String name;
            @NonNull
            private final String description;
            private final int color;

            public Activity(@NonNull String id, @NonNull String name, @NonNull String description, int color) {
                this.id = id;
                this.name = name;
                this.description = description;
                this.color = color;
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

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Activity activity = (Activity) o;
                return color == activity.color &&
                        Objects.equals(id, activity.id) &&
                        Objects.equals(name, activity.name) &&
                        Objects.equals(description, activity.description);
            }

            @Override
            public int hashCode() {
                return Objects.hash(id, name, description, color);
            }

            @Override
            public String toString() {
                return "Activity{" +
                        "id='" + id + '\'' +
                        ", name='" + name + '\'' +
                        ", description='" + description + '\'' +
                        ", color=" + color +
                        '}';
            }
        }

    }
}
