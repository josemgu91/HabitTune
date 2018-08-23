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

import com.josemgu91.habittune.domain.datagateways.ActivityDataGateway;
import com.josemgu91.habittune.domain.datagateways.DataGatewayException;
import com.josemgu91.habittune.domain.entities.Activity;
import com.josemgu91.habittune.domain.entities.Tag;
import com.josemgu91.habittune.domain.util.ListMapper;

import java.util.List;
import java.util.Objects;

public class CreateActivity implements UseCase<CreateActivity.Input> {

    private final UseCaseOutput<Boolean> output;
    private final ActivityDataGateway activityDataGateway;

    public CreateActivity(@NonNull final UseCaseOutput<Boolean> output, @NonNull final ActivityDataGateway activityDataGateway) {
        this.output = output;
        this.activityDataGateway = activityDataGateway;
    }

    public void execute(final Input input) {
        output.showInProgress();
        try {
            final boolean activityCreated = activityDataGateway.createActivity(new Activity(
                    input.name,
                    input.description,
                    input.color,
                    new ListMapper<>(Tag::new).apply(input.tags)
            ));
            output.showResult(activityCreated);
        } catch (DataGatewayException e) {
            e.printStackTrace();
            output.showError();
        }
    }

    public static final class Input {
        private final String name;
        private final String description;
        private final int color;
        private final List<String> tags;

        public Input(String name, String description, int color, List<String> tags) {
            this.name = name;
            this.description = description;
            this.color = color;
            this.tags = tags;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Input input = (Input) o;
            return color == input.color &&
                    Objects.equals(name, input.name) &&
                    Objects.equals(description, input.description) &&
                    Objects.equals(tags, input.tags);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, description, color, tags);
        }

        @Override
        public String toString() {
            return "Input{" +
                    "name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", color=" + color +
                    ", tags=" + tags +
                    '}';
        }
    }

}
