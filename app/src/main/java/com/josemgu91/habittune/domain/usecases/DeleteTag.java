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

import com.josemgu91.habittune.domain.datagateways.DataGatewayException;
import com.josemgu91.habittune.domain.datagateways.TagDataGateway;
import com.josemgu91.habittune.domain.usecases.common.AbstractUseCase;
import com.josemgu91.habittune.domain.usecases.common.UseCaseOutput;

import java.util.Objects;
import java.util.concurrent.Executor;

public class DeleteTag extends AbstractUseCase<DeleteTag.Input, Void> {

    private final TagDataGateway tagDataGateway;

    public DeleteTag(@NonNull Executor outputExecutor, @NonNull Executor useCaseExecutor, TagDataGateway tagDataGateway) {
        super(outputExecutor, useCaseExecutor);
        this.tagDataGateway = tagDataGateway;
    }

    @Override
    protected void executeUseCase(@Nullable Input input, @NonNull UseCaseOutput<Void> output) {
        output.inProgress();
        try {
            final boolean tagDeleted = tagDataGateway.deleteTagById(input.id);
            if (tagDeleted) {
                output.onSuccess(null);
            } else {
                output.onError();
            }
        } catch (DataGatewayException e) {
            e.printStackTrace();
            output.onError();
        }
    }

    public static final class Input {
        private final String id;

        public Input(String id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Input input = (Input) o;
            return Objects.equals(id, input.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return "Input{" +
                    "id='" + id + '\'' +
                    '}';
        }
    }
}
