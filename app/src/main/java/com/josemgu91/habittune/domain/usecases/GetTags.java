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

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.josemgu91.habittune.domain.datagateways.DataGatewayException;
import com.josemgu91.habittune.domain.datagateways.TagDataGateway;
import com.josemgu91.habittune.domain.entities.Tag;
import com.josemgu91.habittune.domain.usecases.common.AbstractUseCase;
import com.josemgu91.habittune.domain.usecases.common.UseCaseOutput;
import com.josemgu91.habittune.domain.util.Function;
import com.josemgu91.habittune.domain.util.ListMapper;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

public class GetTags extends AbstractUseCase<GetTags.Input, LiveData<List<GetTags.Output>>> {

    private final TagDataGateway tagDataGateway;
    private final Function<List<Tag>, List<Output>> listMapper;

    public GetTags(@NonNull Executor outputExecutor, @NonNull Executor useCaseExecutor, TagDataGateway tagDataGateway) {
        super(outputExecutor, useCaseExecutor);
        this.tagDataGateway = tagDataGateway;
        this.listMapper = new ListMapper<>(input -> new Output(input.getId(), input.getName()));
    }

    @Override
    protected void executeUseCase(@Nullable GetTags.Input input, @NonNull UseCaseOutput<LiveData<List<Output>>> output) {
        output.inProgress();
        try {
            final LiveData<List<Tag>> result;
            if (input.type == Input.ALL) {
                result = tagDataGateway.subscribeToAllTags();
            } else if (input.type == Input.BY_IDS) {
                result = tagDataGateway.subscribeToTagsByIds(input.ids);
            } else {
                output.onError();
                return;
            }
            final LiveData<List<Output>> outputLiveData = Transformations.map(result, listMapper::apply);
            output.onSuccess(outputLiveData);
        } catch (DataGatewayException e) {
            e.printStackTrace();
            output.onError();
        }
    }

    public static final class Input {

        public final static int ALL = 1;
        public final static int BY_IDS = 2;

        public Input(int type, @Nullable List<String> ids) {
            this.type = type;
            this.ids = ids;
        }

        private final int type;
        private final List<String> ids;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Input input = (Input) o;
            return type == input.type &&
                    Objects.equals(ids, input.ids);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, ids);
        }

        @Override
        public String toString() {
            return "Input{" +
                    "type=" + type +
                    ", ids=" + ids +
                    '}';
        }
    }

    public static final class Output {

        private final String id;
        private final String name;

        public Output(String id, String name) {
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
            Output output = (Output) o;
            return Objects.equals(id, output.id) &&
                    Objects.equals(name, output.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }

        @Override
        public String toString() {
            return "Output{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
