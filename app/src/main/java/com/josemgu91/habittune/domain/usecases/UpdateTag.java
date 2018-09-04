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

import com.josemgu91.habittune.domain.datagateways.DataGatewayException;
import com.josemgu91.habittune.domain.datagateways.TagDataGateway;
import com.josemgu91.habittune.domain.entities.Tag;

import java.util.Objects;
import java.util.concurrent.Executor;

public class UpdateTag extends AbstractUseCase<UpdateTag.Input, Void> {

    private final TagDataGateway tagDataGateway;

    public UpdateTag(@NonNull Executor outputExecutor, @NonNull Executor useCaseExecutor, TagDataGateway tagDataGateway) {
        super(outputExecutor, useCaseExecutor);
        this.tagDataGateway = tagDataGateway;
    }

    @Override
    protected void executeUseCase(@Nullable Input input, @NonNull UseCaseOutput<Void> output) {
        output.inProgress();
        try {
            final boolean tagUpdated = tagDataGateway.updateTag(
                    new Tag(input.oldTagName),
                    new Tag(input.newTagName)
            );
            if (tagUpdated) {
                output.onSuccess(null);
            } else {
                output.onError();
            }
        } catch (DataGatewayException e) {
            e.printStackTrace();
            output.onError();
        }
    }

    public static class Input {

        private final String oldTagName;
        private final String newTagName;

        public Input(String oldTagName, String newTagName) {
            this.oldTagName = oldTagName;
            this.newTagName = newTagName;
        }

        public String getOldTagName() {
            return oldTagName;
        }

        public String getNewTagName() {
            return newTagName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Input input = (Input) o;
            return Objects.equals(oldTagName, input.oldTagName) &&
                    Objects.equals(newTagName, input.newTagName);
        }

        @Override
        public int hashCode() {

            return Objects.hash(oldTagName, newTagName);
        }

        @Override
        public String toString() {
            return "Input{" +
                    "oldTagName='" + oldTagName + '\'' +
                    ", newTagName='" + newTagName + '\'' +
                    '}';
        }
    }

}
