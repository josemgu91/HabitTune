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
import com.josemgu91.habittune.domain.datagateways.DataGatewayException;
import com.josemgu91.habittune.domain.usecases.common.AbstractUseCase;
import com.josemgu91.habittune.domain.usecases.common.UseCaseOutput;

import java.util.Objects;
import java.util.concurrent.Executor;

public class DeleteAssistance extends AbstractUseCase<DeleteAssistance.Input, Void> {

    private final AssistanceRegisterDataGateway assistanceRegisterDataGateway;

    public DeleteAssistance(@NonNull Executor outputExecutor, @NonNull Executor useCaseExecutor, AssistanceRegisterDataGateway assistanceRegisterDataGateway) {
        super(outputExecutor, useCaseExecutor);
        this.assistanceRegisterDataGateway = assistanceRegisterDataGateway;
    }

    @Override
    protected void executeUseCase(@Nullable Input input, @NonNull UseCaseOutput<Void> output) {
        output.inProgress();
        try {
            final boolean wasDeleted = assistanceRegisterDataGateway.deleteAssistanceRegister(
                    input.cycleNumber,
                    input.routineEntryId
            );
            if (wasDeleted) {
                output.onSuccess(null);
            } else {
                output.onError();
            }
        } catch (DataGatewayException e) {
            e.printStackTrace();
            output.onError();
        }
    }

    public final static class Input {

        private final String routineEntryId;
        private final int cycleNumber;

        public Input(String routineEntryId, int cycleNumber) {
            this.routineEntryId = routineEntryId;
            this.cycleNumber = cycleNumber;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Input input = (Input) o;
            return cycleNumber == input.cycleNumber &&
                    Objects.equals(routineEntryId, input.routineEntryId);
        }

        @Override
        public int hashCode() {

            return Objects.hash(routineEntryId, cycleNumber);
        }

        @Override
        public String toString() {
            return "Input{" +
                    "routineEntryId='" + routineEntryId + '\'' +
                    ", cycleNumber=" + cycleNumber +
                    '}';
        }
    }

}
