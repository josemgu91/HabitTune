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

import com.josemgu91.habittune.domain.datagateways.ActivityDataGateway;
import com.josemgu91.habittune.domain.datagateways.DataGatewayException;
import com.josemgu91.habittune.domain.entities.Activity;
import com.josemgu91.habittune.domain.entities.Tag;
import com.josemgu91.habittune.domain.usecases.common.AbstractUseCase;
import com.josemgu91.habittune.domain.usecases.common.UseCaseOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

public class CreateActivity extends AbstractUseCase<CreateActivity.Input, Void> {

    private final ActivityDataGateway activityDataGateway;

    public CreateActivity(@NonNull Executor outputExecutor, @NonNull Executor useCaseExecutor, @NonNull ActivityDataGateway activityDataGateway) {
        super(outputExecutor, useCaseExecutor);
        this.activityDataGateway = activityDataGateway;
    }

    @Override
    protected void executeUseCase(@Nullable final Input input, @NonNull final UseCaseOutput<Void> output) {
        output.inProgress();
        try {
            final List<Tag> tags = new ArrayList<>();
            for (final String tagId : input.tagIds) {
                tags.add(new Tag(tagId, ""));
            }
            final boolean activityCreated = activityDataGateway.createActivity(new Activity(
                    "",
                    input.name,
                    input.description,
                    input.color,
                    tags
            ));
            if (activityCreated) {
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
        private final String name;
        private final String description;
        private final int color;
        private final List<String> tagIds;

        public Input(String name, String description, int color, List<String> tagIds) {
            this.name = name;
            this.description = description;
            this.color = color;
            this.tagIds = tagIds;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Input input = (Input) o;
            return color == input.color &&
                    Objects.equals(name, input.name) &&
                    Objects.equals(description, input.description) &&
                    Objects.equals(tagIds, input.tagIds);
        }

        @Override
        public int hashCode() {

            return Objects.hash(name, description, color, tagIds);
        }

        @Override
        public String toString() {
            return "Input{" +
                    "name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", color=" + color +
                    ", tagIds=" + tagIds +
                    '}';
        }
    }

}
