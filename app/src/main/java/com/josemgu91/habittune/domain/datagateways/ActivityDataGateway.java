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

import com.josemgu91.habittune.domain.entities.Activity;

import java.util.List;

public interface ActivityDataGateway {

    @NonNull
    Activity getActivityById(@NonNull final String id) throws DataGatewayException;

    @NonNull
    LiveData<List<Activity>> subscribeToAllActivities() throws DataGatewayException;

    boolean deleteActivityById(@NonNull final String id) throws DataGatewayException;

    @NonNull
    Activity createActivity(@NonNull final Activity activity) throws DataGatewayException;

    boolean updateActivity(@NonNull final Activity updatedActivity) throws DataGatewayException;

}
