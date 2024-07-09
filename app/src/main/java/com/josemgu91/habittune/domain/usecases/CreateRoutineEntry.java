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

import com.josemgu91.habittune.domain.datagateways.RoutineEntryDataGateway;
import com.josemgu91.habittune.domain.entities.Activity;
import com.josemgu91.habittune.domain.entities.Day;
import com.josemgu91.habittune.domain.entities.RoutineEntry;
import com.josemgu91.habittune.domain.entities.Time;
import com.josemgu91.habittune.domain.usecases.common.AbstractUseCase;
import com.josemgu91.habittune.domain.usecases.common.UseCaseOutput;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Executor;

public class CreateRoutineEntry extends AbstractUseCase<CreateRoutineEntry.Input, Void> {

    private final RoutineEntryDataGateway routineEntryDataGateway;

    public CreateRoutineEntry(@NonNull Executor outputExecutor, @NonNull Executor useCaseExecutor, RoutineEntryDataGateway routineEntryDataGateway) {
        super(outputExecutor, useCaseExecutor);
        this.routineEntryDataGateway = routineEntryDataGateway;
    }

    @Override
    protected void executeUseCase(@Nullable Input input, @NonNull UseCaseOutput<Void> output) {
        output.inProgress();
        try {
            final RoutineEntry routineEntry = routineEntryDataGateway.createRoutineEntry(new RoutineEntry(
                    new Day(input.day),
                    new Time(input.startTime),
                    new Time(input.endTime),
                    new Activity(input.activityId),
                    true,
                    new Date(),
                    null,
                    null
            ), input.routineId);
            if (routineEntry != null) {
                output.onSuccess(null);
            } else {
                output.onError();
            }
        } catch (Exception e) {
            e.printStackTrace();
            output.onError();
        }
    }

    public static final class Input {

        @NonNull
        private final String routineId;
        @NonNull
        private final String activityId;
        private final int day;
        private final int startTime;
        private final int endTime;

        public Input(@NonNull String routineId, @NonNull String activityId, int day, int startTime, int endTime) {
            this.routineId = routineId;
            this.activityId = activityId;
            this.day = day;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Input input = (Input) o;
            return day == input.day &&
                    startTime == input.startTime &&
                    endTime == input.endTime &&
                    Objects.equals(routineId, input.routineId) &&
                    Objects.equals(activityId, input.activityId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(routineId, activityId, day, startTime, endTime);
        }

        @Override
        public String toString() {
            return "Input{" +
                    "routineId='" + routineId + '\'' +
                    ", activityId='" + activityId + '\'' +
                    ", day=" + day +
                    ", startTime=" + startTime +
                    ", endTime=" + endTime +
                    '}';
        }
    }
}
