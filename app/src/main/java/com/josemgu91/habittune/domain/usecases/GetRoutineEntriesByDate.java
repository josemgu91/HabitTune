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
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.josemgu91.habittune.domain.datagateways.DataGatewayException;
import com.josemgu91.habittune.domain.datagateways.RoutineDataGateway;
import com.josemgu91.habittune.domain.datagateways.RoutineEntryDataGateway;
import com.josemgu91.habittune.domain.entities.Routine;
import com.josemgu91.habittune.domain.entities.RoutineEntry;
import com.josemgu91.habittune.domain.usecases.common.AbstractUseCase;
import com.josemgu91.habittune.domain.usecases.common.ScheduleCalculator;
import com.josemgu91.habittune.domain.usecases.common.UseCaseOutput;
import com.josemgu91.habittune.domain.util.Function;
import com.josemgu91.habittune.domain.util.ListMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

public class GetRoutineEntriesByDate extends AbstractUseCase<GetRoutineEntriesByDate.Input, LiveData<List<GetRoutineEntriesByDate.Output>>> {

    private final RoutineDataGateway routineDataGateway;
    private final RoutineEntryDataGateway routineEntryDataGateway;
    private final Function<List<RoutineEntry>, List<Output>> listMapper;

    private final ScheduleCalculator scheduleCalculator;

    public GetRoutineEntriesByDate(@NonNull Executor outputExecutor, @NonNull Executor useCaseExecutor, RoutineEntryDataGateway routineEntryDataGateway, RoutineDataGateway routineDataGateway) {
        super(outputExecutor, useCaseExecutor);
        this.routineEntryDataGateway = routineEntryDataGateway;
        this.routineDataGateway = routineDataGateway;
        this.listMapper = new ListMapper<>(new RoutineEntryMapper());
        this.scheduleCalculator = new ScheduleCalculator();
    }

    @Override
    protected void executeUseCase(@Nullable Input input, @NonNull UseCaseOutput<LiveData<List<Output>>> output) {
        output.inProgress();
        try {
            final Date inputDate = input.date;
            final LiveData<List<Routine>> allRoutines = routineDataGateway.subscribeToAllRoutines(true);
            final MediatorLiveData<List<Routine>> routinesWithEntriesPerDate = new MediatorLiveData<>();
            routinesWithEntriesPerDate.addSource(allRoutines, new Observer<List<Routine>>() {
                @Override
                public void onChanged(@Nullable List<Routine> routines) {
                    final List<RoutineEntry> routineEntries = new ArrayList<>();
                    for (final Routine routine : routines) {
                        for (final RoutineEntry routineEntry : routine.getRoutineEntries()) {
                            final int currentDayNumber = scheduleCalculator.getRoutineDayNumber(inputDate, routine.getStartDate(), routine.getNumberOfDays());
                            if (currentDayNumber != routineEntry.getDay().getDay()) {
                                continue;
                            }
                            routineEntries.add(routineEntry);
                        }
                    }
                    Collections.sort(routineEntries, (o1, o2) -> o1.getStartTime().getTime() - o2.getStartTime().getTime());
                }
            });

            output.onSuccess(outputLiveData);
        } catch (DataGatewayException e) {
            e.printStackTrace();
            output.onError();
        }
    }

    private void

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

        @NonNull
        private final String id;
        private final int cycleNumber;
        private final int startTime;
        private final int endTime;
        @NonNull
        private final GetRoutineEntries.Output.Activity activity;
        @NonNull
        private final AssistanceRegister assistanceRegister;

        public Output(@NonNull String id, int cycleNumber, int startTime, int endTime, @NonNull GetRoutineEntries.Output.Activity activity, @NonNull AssistanceRegister assistanceRegister) {
            this.id = id;
            this.cycleNumber = cycleNumber;
            this.startTime = startTime;
            this.endTime = endTime;
            this.activity = activity;
            this.assistanceRegister = assistanceRegister;
        }

        @NonNull
        public String getId() {
            return id;
        }

        public int getCycleNumber() {
            return cycleNumber;
        }

        public int getStartTime() {
            return startTime;
        }

        public int getEndTime() {
            return endTime;
        }

        @NonNull
        public GetRoutineEntries.Output.Activity getActivity() {
            return activity;
        }

        @NonNull
        public AssistanceRegister getAssistanceRegister() {
            return assistanceRegister;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Output output = (Output) o;
            return cycleNumber == output.cycleNumber &&
                    startTime == output.startTime &&
                    endTime == output.endTime &&
                    Objects.equals(id, output.id) &&
                    Objects.equals(activity, output.activity) &&
                    Objects.equals(assistanceRegister, output.assistanceRegister);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, cycleNumber, startTime, endTime, activity, assistanceRegister);
        }

        @Override
        public String toString() {
            return "Output{" +
                    "id='" + id + '\'' +
                    ", cycleNumber=" + cycleNumber +
                    ", startTime=" + startTime +
                    ", endTime=" + endTime +
                    ", activity=" + activity +
                    ", assistanceRegister=" + assistanceRegister +
                    '}';
        }

        public static final class AssistanceRegister {

            public final static int UNDEFINED = -1;

            private final int startTime;
            private final int endTime;

            public AssistanceRegister(int startTime, int endTime) {
                this.startTime = startTime;
                this.endTime = endTime;
            }

            public int getStartTime() {
                return startTime;
            }

            public int getEndTime() {
                return endTime;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                AssistanceRegister that = (AssistanceRegister) o;
                return startTime == that.startTime &&
                        endTime == that.endTime;
            }

            @Override
            public int hashCode() {
                return Objects.hash(startTime, endTime);
            }

            @Override
            public String toString() {
                return "AssistanceRegister{" +
                        "startTime=" + startTime +
                        ", endTime=" + endTime +
                        '}';
            }
        }

    }

}
