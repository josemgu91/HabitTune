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

import com.josemgu91.habittune.domain.datagateways.AssistanceRegisterDataGateway;
import com.josemgu91.habittune.domain.datagateways.DataGatewayException;
import com.josemgu91.habittune.domain.entities.AssistanceRegister;
import com.josemgu91.habittune.domain.entities.Time;
import com.josemgu91.habittune.domain.usecases.common.AbstractUseCase;
import com.josemgu91.habittune.domain.usecases.common.UseCaseOutput;

import java.util.Objects;
import java.util.concurrent.Executor;

public class RegisterAssistance extends AbstractUseCase<RegisterAssistance.Input, Void> {

    private final AssistanceRegisterDataGateway assistanceRegisterDataGateway;

    public RegisterAssistance(@NonNull Executor outputExecutor, @NonNull Executor useCaseExecutor, AssistanceRegisterDataGateway assistanceRegisterDataGateway) {
        super(outputExecutor, useCaseExecutor);
        this.assistanceRegisterDataGateway = assistanceRegisterDataGateway;
    }

    @Override
    protected void executeUseCase(@Nullable Input input, @NonNull UseCaseOutput<Void> output) {
        output.inProgress();
        try {
            final AssistanceRegister assistanceRegister = assistanceRegisterDataGateway.createOrUpdateAssistanceRegister(
                    new AssistanceRegister(
                            input.cycleNumber,
                            input.startHour,
                            input.endHour
                    ),
                    input.routineEntryId
            );
            output.onSuccess(null);
        } catch (DataGatewayException e) {
            e.printStackTrace();
            output.onError();
        }
    }

    public static final class Input {

        @NonNull
        private final String routineEntryId;
        private final int cycleNumber;
        @NonNull
        private final Time startHour;
        @Nullable
        private final Time endHour;

        public Input(@NonNull String routineEntryId, int cycleNumber, @NonNull Time startHour, @Nullable Time endHour) {
            this.routineEntryId = routineEntryId;
            this.cycleNumber = cycleNumber;
            this.startHour = startHour;
            this.endHour = endHour;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Input input = (Input) o;
            return cycleNumber == input.cycleNumber &&
                    Objects.equals(routineEntryId, input.routineEntryId) &&
                    Objects.equals(startHour, input.startHour) &&
                    Objects.equals(endHour, input.endHour);
        }

        @Override
        public int hashCode() {

            return Objects.hash(routineEntryId, cycleNumber, startHour, endHour);
        }

        @Override
        public String toString() {
            return "Input{" +
                    "routineEntryId='" + routineEntryId + '\'' +
                    ", cycleNumber=" + cycleNumber +
                    ", startHour=" + startHour +
                    ", endHour=" + endHour +
                    '}';
        }
    }

}
