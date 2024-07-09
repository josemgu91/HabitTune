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

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.josemgu91.habittune.domain.datagateways.AssistanceRegisterDataGateway;
import com.josemgu91.habittune.domain.datagateways.DataGatewayException;
import com.josemgu91.habittune.domain.datagateways.RoutineDataGateway;
import com.josemgu91.habittune.domain.entities.AssistanceRegister;
import com.josemgu91.habittune.domain.entities.Routine;
import com.josemgu91.habittune.domain.entities.RoutineEntry;
import com.josemgu91.habittune.domain.usecases.common.AbstractUseCase;
import com.josemgu91.habittune.domain.usecases.common.ScheduleCalculator;
import com.josemgu91.habittune.domain.usecases.common.UseCaseOutput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

public class GetRoutineEntriesByDate extends AbstractUseCase<GetRoutineEntriesByDate.Input, LiveData<List<GetRoutineEntriesByDate.Output>>> {

    private final RoutineDataGateway routineDataGateway;
    private final AssistanceRegisterDataGateway assistanceRegisterDataGateway;

    private final ScheduleCalculator scheduleCalculator;

    public GetRoutineEntriesByDate(@NonNull Executor outputExecutor, @NonNull Executor useCaseExecutor, AssistanceRegisterDataGateway assistanceRegisterDataGateway, RoutineDataGateway routineDataGateway) {
        super(outputExecutor, useCaseExecutor);
        this.assistanceRegisterDataGateway = assistanceRegisterDataGateway;
        this.routineDataGateway = routineDataGateway;
        this.scheduleCalculator = new ScheduleCalculator();
    }

    @Override
    protected void executeUseCase(@Nullable Input input, @NonNull UseCaseOutput<LiveData<List<Output>>> output) {
        output.inProgress();
        try {
            final Date inputDate = input.date;
            final LiveData<List<Routine>> allRoutines = routineDataGateway.subscribeToAllRoutines(true);
            final MediatorLiveData<List<Output>> routinesWithEntriesPerDate = new MediatorLiveData<>();
            routinesWithEntriesPerDate.addSource(allRoutines, routines -> useCaseExecutor.execute(() -> {
                try {
                    final List<Output> useCaseOutputList = new ArrayList<>();
                    for (final Routine routine : routines) {
                        for (final RoutineEntry routineEntry : routine.getRoutineEntries()) {
                            final boolean isInRoutineEntryDay = scheduleCalculator.isInRoutineEntryDay(inputDate, routine.getStartDate(), routine.getNumberOfDays(), routineEntry.getDay().getDay());
                            if (!isInRoutineEntryDay) {
                                continue;
                            }
                            final int cycleNumber = scheduleCalculator.computeRoutineEntryCycleNumber(inputDate, routine.getStartDate(), routine.getNumberOfDays());
                            final LiveData<AssistanceRegister> assistanceRegisterLiveData = assistanceRegisterDataGateway.subscribeToAssistanceRegisterByCycleNumberAndRoutineEntryId(
                                    cycleNumber,
                                    routineEntry.getId()
                            );
                            useCaseOutputList.add(buildOutput(routineEntry, cycleNumber, assistanceRegisterLiveData));
                        }
                    }
                    Collections.sort(useCaseOutputList, (o1, o2) -> o1.startTime - o2.startTime);
                    routinesWithEntriesPerDate.postValue(useCaseOutputList);
                } catch (DataGatewayException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }));
            output.onSuccess(routinesWithEntriesPerDate);
        } catch (DataGatewayException e) {
            e.printStackTrace();
            output.onError();
        }
    }

    private Output buildOutput(final RoutineEntry routineEntry, final int cycleNumber, final LiveData<AssistanceRegister> assistanceRegisterDomainEntityLiveData) {
        final LiveData<Output.AssistanceRegister> assistanceRegisterLiveData = Transformations.map(
                assistanceRegisterDomainEntityLiveData,
                this::mapToAssistanceRegister
        );
        return new Output(
                routineEntry.getId(),
                cycleNumber,
                routineEntry.getStartTime().getTime(),
                routineEntry.getEndTime().getTime(),
                new GetRoutineEntries.Output.Activity(
                        routineEntry.getActivity().getId(),
                        routineEntry.getActivity().getName(),
                        routineEntry.getActivity().getDescription(),
                        routineEntry.getActivity().getColor()
                ),
                assistanceRegisterLiveData
        );
    }

    private Output.AssistanceRegister mapToAssistanceRegister(final AssistanceRegister assistanceRegister) {
        if (assistanceRegister == null) {
            return new Output.AssistanceRegister(
                    Output.AssistanceRegister.UNDEFINED,
                    Output.AssistanceRegister.UNDEFINED
            );
        }
        return new Output.AssistanceRegister(
                assistanceRegister.getStartTime().getTime(),
                assistanceRegister.getEndTime() == null ? Output.AssistanceRegister.UNDEFINED : assistanceRegister.getEndTime().getTime()
        );
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
        private final LiveData<AssistanceRegister> assistanceRegisterLiveData;

        public Output(@NonNull String id, int cycleNumber, int startTime, int endTime, @NonNull GetRoutineEntries.Output.Activity activity, @NonNull LiveData<AssistanceRegister> assistanceRegisterLiveData) {
            this.id = id;
            this.cycleNumber = cycleNumber;
            this.startTime = startTime;
            this.endTime = endTime;
            this.activity = activity;
            this.assistanceRegisterLiveData = assistanceRegisterLiveData;
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
        public LiveData<AssistanceRegister> getAssistanceRegisterLiveData() {
            return assistanceRegisterLiveData;
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
                    Objects.equals(assistanceRegisterLiveData, output.assistanceRegisterLiveData);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, cycleNumber, startTime, endTime, activity, assistanceRegisterLiveData);
        }

        @Override
        public String toString() {
            return "Output{" +
                    "id='" + id + '\'' +
                    ", cycleNumber=" + cycleNumber +
                    ", startTime=" + startTime +
                    ", endTime=" + endTime +
                    ", activity=" + activity +
                    ", assistanceRegisterLiveData=" + assistanceRegisterLiveData +
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
