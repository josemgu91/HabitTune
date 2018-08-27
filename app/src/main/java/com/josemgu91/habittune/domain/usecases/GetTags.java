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

import com.josemgu91.habittune.domain.datagateways.DataGatewayException;
import com.josemgu91.habittune.domain.datagateways.TagDataGateway;
import com.josemgu91.habittune.domain.entities.Tag;
import com.josemgu91.habittune.domain.util.Function;
import com.josemgu91.habittune.domain.util.ListMapper;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

public class GetTags extends AbstractUseCase<Void, LiveData<List<GetTags.Output>>> {

    private final TagDataGateway tagDataGateway;
    private final Function<List<Tag>, List<Output>> listMapper;

    public GetTags(@NonNull Executor outputExecutor, @NonNull Executor useCaseExecutor, TagDataGateway tagDataGateway) {
        super(outputExecutor, useCaseExecutor);
        this.tagDataGateway = tagDataGateway;
        this.listMapper = new ListMapper<>(input -> new Output(input.getName()));
    }

    @Override
    protected void executeUseCase(@Nullable Void aVoid, @NonNull UseCaseOutput<LiveData<List<Output>>> output) {
        output.inProgress();
        try {
            final LiveData<List<Tag>> result = tagDataGateway.subscribeToAllTags();
            final LiveData<List<Output>> outputLiveData = Transformations.map(result, listMapper::apply);
            output.onSuccess(outputLiveData);
        } catch (DataGatewayException e) {
            e.printStackTrace();
            output.onError();
        }
    }

    public static final class Output {

        private final String name;

        public Output(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Output output = (Output) o;
            return Objects.equals(name, output.name);
        }

        @Override
        public int hashCode() {

            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return "Output{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
}
