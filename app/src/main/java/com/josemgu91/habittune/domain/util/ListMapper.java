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

package com.josemgu91.habittune.domain.util;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ListMapper<I, O> implements Function<List<I>, List<O>> {

    @NonNull
    private final Function<I, O> function;

    public ListMapper(@NonNull final Function<I, O> function) {
        this.function = function;
    }

    @Override
    public List<O> apply(List<I> inputList) {
        final List<O> outputList = new ArrayList<>();
        for (final I inputListElement : inputList) {
            outputList.add(function.apply(inputListElement));
        }
        return outputList;
    }

}
