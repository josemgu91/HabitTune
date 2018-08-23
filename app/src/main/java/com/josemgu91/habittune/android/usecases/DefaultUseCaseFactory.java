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

package com.josemgu91.habittune.android.usecases;

import android.arch.lifecycle.LiveData;

import com.josemgu91.habittune.data.room.RoomRepository;
import com.josemgu91.habittune.domain.usecases.CreateActivity;
import com.josemgu91.habittune.domain.usecases.GetActivities;
import com.josemgu91.habittune.domain.usecases.UseCaseOutput;

import java.util.List;
import java.util.concurrent.Executor;

public class DefaultUseCaseFactory implements UseCaseFactory {

    private final Executor uiThreadExecutor;
    private final Executor defaultThreadPoolExecutor;
    private final RoomRepository roomRepository;

    public DefaultUseCaseFactory(Executor uiThreadExecutor, Executor defaultThreadPoolExecutor, RoomRepository roomRepository) {
        this.uiThreadExecutor = uiThreadExecutor;
        this.defaultThreadPoolExecutor = defaultThreadPoolExecutor;
        this.roomRepository = roomRepository;
    }

    @Override
    public GetActivities createGetActivities(UseCaseOutput<LiveData<List<GetActivities.Output>>> output) {
        return new GetActivities(
                uiThreadExecutor,
                defaultThreadPoolExecutor,
                output,
                roomRepository
        );
    }

    @Override
    public CreateActivity createCreateActivity(UseCaseOutput<Void> output) {
        return new CreateActivity(
                uiThreadExecutor,
                defaultThreadPoolExecutor,
                output,
                roomRepository
        );
    }
}
