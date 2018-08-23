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

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.josemgu91.habittune.domain.datagateways.ActivityDataGateway;
import com.josemgu91.habittune.domain.datagateways.DataGatewayException;
import com.josemgu91.habittune.domain.entities.Activity;
import com.josemgu91.habittune.domain.util.Function;
import com.josemgu91.habittune.domain.util.ListMapper;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

public class GetActivities extends AbstractUseCase<Void, LiveData<List<GetActivities.Output>>> {

    private final ActivityDataGateway activityDataGateway;
    private final Function<List<Activity>, List<Output>> listMapper;

    public GetActivities(@NonNull Executor outputExecutor, @NonNull Executor useCaseExecutor, @NonNull UseCaseOutput<LiveData<List<Output>>> useCaseOutput, @NonNull ActivityDataGateway activityDataGateway) {
        super(outputExecutor, useCaseExecutor, useCaseOutput);
        this.activityDataGateway = activityDataGateway;
        this.listMapper = new ListMapper<>(new ActivityMapper());
    }

    @Override
    protected void executeUseCase(@Nullable Void aVoid) {
        output.inProgress();
        try {
            final LiveData<List<Activity>> result = activityDataGateway.subscribeToAllActivitiesButWithoutTags();
            final LiveData<List<Output>> outputLiveData = Transformations.map(result, listMapper::apply);
            output.onSuccess(outputLiveData);
        } catch (DataGatewayException e) {
            e.printStackTrace();
            output.onError();
        }
    }

    private final class ActivityMapper implements Function<Activity, Output> {

        @Override
        public Output apply(Activity input) {
            return new Output(
                    input.getName(),
                    input.getDescription(),
                    input.getColor()
            );
        }
    }

    public static final class Output {

        private final String name;
        private final String description;
        private final int color;

        public Output(String name, String description, int color) {
            this.name = name;
            this.description = description;
            this.color = color;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Output output = (Output) o;
            return color == output.color &&
                    Objects.equals(name, output.name) &&
                    Objects.equals(description, output.description);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, description, color);
        }

        @Override
        public String toString() {
            return "Output{" +
                    "name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", color=" + color +
                    '}';
        }
    }

}
