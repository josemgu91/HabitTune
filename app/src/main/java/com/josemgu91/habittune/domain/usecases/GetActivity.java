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
import com.josemgu91.habittune.domain.util.Function;
import com.josemgu91.habittune.domain.util.ListMapper;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

public class GetActivity extends AbstractUseCase<GetActivity.Input, GetActivity.Output> {

    private final ActivityDataGateway activityDataGateway;
    private final ActivityMapper activityMapper;

    public GetActivity(@NonNull Executor outputExecutor, @NonNull Executor useCaseExecutor, ActivityDataGateway activityDataGateway) {
        super(outputExecutor, useCaseExecutor);
        this.activityDataGateway = activityDataGateway;
        this.activityMapper = new ActivityMapper();
    }

    @Override
    protected void executeUseCase(@Nullable Input input, @NonNull UseCaseOutput<Output> output) {
        output.inProgress();
        try {
            final Activity result = activityDataGateway.getActivityById(input.id);
            output.onSuccess(activityMapper.apply(result));
        } catch (DataGatewayException e) {
            e.printStackTrace();
            output.onError();
        }
    }

    protected static final class ActivityMapper implements Function<Activity, Output> {

        @Override
        public Output apply(Activity input) {
            return new Output(
                    input.getId(),
                    input.getName(),
                    input.getDescription(),
                    input.getColor(),
                    new ListMapper<>((Function<Tag, Output.Tag>) tag -> new Output.Tag(
                            tag.getId(),
                            tag.getName()
                    )).apply(input.getTags()));
        }
    }

    public static final class Input {

        private final String id;

        public Input(final String id) {
            this.id = id;
        }
    }

    public static final class Output {

        private final String id;
        private final String name;
        private final String description;
        private final int color;
        private final List<Tag> tags;

        public Output(String id, String name, String description, int color, List<Tag> tags) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.color = color;
            this.tags = tags;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Output output = (Output) o;
            return color == output.color &&
                    Objects.equals(id, output.id) &&
                    Objects.equals(name, output.name) &&
                    Objects.equals(description, output.description) &&
                    Objects.equals(tags, output.tags);
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public int getColor() {
            return color;
        }

        public List<Tag> getTags() {
            return tags;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, description, color, tags);
        }

        @Override
        public String toString() {
            return "Output{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", color=" + color +
                    ", tags=" + tags +
                    '}';
        }

        public final static class Tag {
            private final String id;
            private final String name;

            public Tag(String id, String name) {
                this.id = id;
                this.name = name;
            }

            public String getId() {
                return id;
            }

            public String getName() {
                return name;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Tag tag = (Tag) o;
                return Objects.equals(id, tag.id) &&
                        Objects.equals(name, tag.name);
            }

            @Override
            public int hashCode() {
                return Objects.hash(id, name);
            }

            @Override
            public String toString() {
                return "Tag{" +
                        "id='" + id + '\'' +
                        ", name='" + name + '\'' +
                        '}';
            }
        }
    }
}
