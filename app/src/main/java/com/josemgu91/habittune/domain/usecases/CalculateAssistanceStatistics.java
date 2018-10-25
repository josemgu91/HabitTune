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

import com.josemgu91.habittune.domain.datagateways.AssistanceRegisterDataGateway;
import com.josemgu91.habittune.domain.usecases.common.AbstractUseCase;
import com.josemgu91.habittune.domain.usecases.common.UseCaseOutput;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Executor;

public class CalculateAssistanceStatistics extends AbstractUseCase<CalculateAssistanceStatistics.Input, CalculateAssistanceStatistics.Output> {

    private final AssistanceRegisterDataGateway assistanceRegisterDataGateway;

    public CalculateAssistanceStatistics(@NonNull Executor outputExecutor, @NonNull Executor useCaseExecutor, @NonNull AssistanceRegisterDataGateway assistanceRegisterDataGateway) {
        super(outputExecutor, useCaseExecutor);
        this.assistanceRegisterDataGateway = assistanceRegisterDataGateway;
    }

    @Override
    protected void executeUseCase(@Nullable Input input, @NonNull UseCaseOutput<Output> output) {
        output.inProgress();
        try {
            final Date todayDate = new Date();
            final int totalEvents = assistanceRegisterDataGateway.calculateTotalSinceCreationByActivityId(input.activityId, todayDate);
            final int totalRegisteredEvents = assistanceRegisterDataGateway.countCompletedAssistanceRegistersByActivityId(
                    input.activityId
            );
            final int eventsMissed = totalEvents - totalRegisteredEvents;
            //TODO: Calculate average start and end time errors.
            final int averageStartTimeErrorInSeconds = 0;
            final int averageEndTimeErrorInSeconds = 0;
            output.onSuccess(new Output(
                    input.activityId,
                    todayDate,
                    totalEvents,
                    eventsMissed,
                    averageStartTimeErrorInSeconds,
                    averageEndTimeErrorInSeconds
            ));
        } catch (Exception e) {
            e.printStackTrace();
            output.onError();
        }
    }

    public static final class Input {

        @NonNull
        private final String activityId;

        public Input(@NonNull String activityId) {
            this.activityId = activityId;
        }
    }

    public static final class Output {

        @NonNull
        private final String activityId;
        @NonNull
        private final Date upToDate;
        private final int totalEvents;
        private final int eventsMissed;
        private final int averageStartTimeErrorInSeconds;
        private final int averageEndTimeErrorInSeconds;

        public Output(@NonNull String activityId, @NonNull Date upToDate, int totalEvents, int eventsMissed, int averageStartTimeErrorInSeconds, int averageEndTimeErrorInSeconds) {
            this.activityId = activityId;
            this.upToDate = upToDate;
            this.totalEvents = totalEvents;
            this.eventsMissed = eventsMissed;
            this.averageStartTimeErrorInSeconds = averageStartTimeErrorInSeconds;
            this.averageEndTimeErrorInSeconds = averageEndTimeErrorInSeconds;
        }

        @NonNull
        public String getActivityId() {
            return activityId;
        }

        @NonNull
        public Date getUpToDate() {
            return upToDate;
        }

        public int getTotalEvents() {
            return totalEvents;
        }

        public int getEventsMissed() {
            return eventsMissed;
        }

        public int getAverageStartTimeErrorInSeconds() {
            return averageStartTimeErrorInSeconds;
        }

        public int getAverageEndTimeErrorInSeconds() {
            return averageEndTimeErrorInSeconds;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Output output = (Output) o;
            return totalEvents == output.totalEvents &&
                    eventsMissed == output.eventsMissed &&
                    averageStartTimeErrorInSeconds == output.averageStartTimeErrorInSeconds &&
                    averageEndTimeErrorInSeconds == output.averageEndTimeErrorInSeconds &&
                    Objects.equals(activityId, output.activityId) &&
                    Objects.equals(upToDate, output.upToDate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(activityId, upToDate, totalEvents, eventsMissed, averageStartTimeErrorInSeconds, averageEndTimeErrorInSeconds);
        }

        @Override
        public String toString() {
            return "Output{" +
                    "activityId='" + activityId + '\'' +
                    ", upToDate=" + upToDate +
                    ", totalEvents=" + totalEvents +
                    ", eventsMissed=" + eventsMissed +
                    ", averageStartTimeErrorInSeconds=" + averageStartTimeErrorInSeconds +
                    ", averageEndTimeErrorInSeconds=" + averageEndTimeErrorInSeconds +
                    '}';
        }
    }

}
