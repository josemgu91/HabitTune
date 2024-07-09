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

public class UpdateActivity extends AbstractUseCase<UpdateActivity.Input, Void> {

    private final ActivityDataGateway activityDataGateway;

    public UpdateActivity(@NonNull Executor outputExecutor, @NonNull Executor useCaseExecutor, ActivityDataGateway activityDataGateway) {
        super(outputExecutor, useCaseExecutor);
        this.activityDataGateway = activityDataGateway;
    }

    @Override
    protected void executeUseCase(@Nullable Input input, @NonNull UseCaseOutput<Void> output) {
        output.inProgress();
        try {
            final List<Tag> tags = new ArrayList<>();
            for (final String tagId : input.tagIds) {
                tags.add(new Tag(tagId, ""));
            }
            final boolean activityUpdated = activityDataGateway.updateActivity(
                    new Activity(
                            input.id,
                            input.name,
                            input.description,
                            input.color,
                            tags
                    )
            );
            if (activityUpdated) {
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
        private final String id;
        private final String name;
        private final String description;
        private final int color;
        private final List<String> tagIds;

        public Input(String id, String name, String description, int color, List<String> tagIds) {
            this.id = id;
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
                    Objects.equals(id, input.id) &&
                    Objects.equals(name, input.name) &&
                    Objects.equals(description, input.description) &&
                    Objects.equals(tagIds, input.tagIds);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, description, color, tagIds);
        }

        @Override
        public String toString() {
            return "Input{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", color=" + color +
                    ", tagIds=" + tagIds +
                    '}';
        }
    }
}
