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

package com.josemgu91.habittune.domain.datagateways;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.josemgu91.habittune.domain.entities.Routine;
import com.josemgu91.habittune.domain.entities.RoutineEntry;

import java.util.List;

public interface RoutineDataGateway {

    @NonNull
    LiveData<List<Routine>> subscribeToAllRoutines() throws DataGatewayException;

    @NonNull
    Routine getRoutineById(@NonNull final String id) throws DataGatewayException;

    boolean deleteRoutineById(@NonNull final String id) throws DataGatewayException;

    Routine createRoutine(@NonNull final Routine routine) throws DataGatewayException;

    LiveData<List<RoutineEntry>> subscribeToAllRoutineEntriesByRoutineId(@NonNull final String routineId) throws DataGatewayException;

    RoutineEntry createRoutineEntry(@NonNull final RoutineEntry routineEntry, @NonNull final String routineId) throws DataGatewayException;
}

