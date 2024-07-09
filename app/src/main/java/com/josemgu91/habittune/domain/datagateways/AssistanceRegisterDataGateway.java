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

import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.josemgu91.habittune.domain.entities.AssistanceRegister;

import java.util.Date;
import java.util.List;

public interface AssistanceRegisterDataGateway {

    @NonNull
    LiveData<AssistanceRegister> subscribeToAssistanceRegisterByCycleNumberAndRoutineEntryId(int cycleNumber, @NonNull String routineEntryId) throws DataGatewayException;

    @NonNull
    AssistanceRegister createOrUpdateAssistanceRegister(@NonNull AssistanceRegister assistanceRegister, @NonNull String routineEntryId) throws DataGatewayException;

    boolean deleteAssistanceRegister(int cycleNumber, @NonNull String routineEntryId) throws DataGatewayException;

    List<AssistanceRegister> getAssistanceRegistersByRoutineEntryBetweenCycleNumbers(@NonNull String routineEntryId, int fromCycleNumber, int toCycleNumber) throws DataGatewayException;

    //Completed means those who have a registered start and end times.
    int countCompletedAssistanceRegistersByActivityId(@NonNull final String activityId) throws DataGatewayException;

    int calculateTotalSinceCreationByActivityId(@NonNull final String activityId, @NonNull final Date toDate) throws DataGatewayException;
}
