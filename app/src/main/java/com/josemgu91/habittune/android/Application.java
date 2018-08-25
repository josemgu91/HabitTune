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

package com.josemgu91.habittune.android;

import android.arch.persistence.room.Room;

import com.josemgu91.habittune.android.executors.DefaultThreadPoolExecutor;
import com.josemgu91.habittune.android.executors.UiThreadExecutor;
import com.josemgu91.habittune.android.ui.ViewModelFactory;
import com.josemgu91.habittune.android.usecases.DefaultUseCaseFactory;
import com.josemgu91.habittune.android.usecases.UseCaseFactory;
import com.josemgu91.habittune.data.room.LocalRoomDatabase;
import com.josemgu91.habittune.data.room.RoomRepository;

import java.util.concurrent.Executor;

import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory;

public class Application extends android.app.Application {

    private Executor uiThreadExecutor;
    private Executor defaultThreadPoolExecutor;
    private RoomRepository roomRepository;
    private UseCaseFactory useCaseFactory;
    private ViewModelFactory viewModelFactory;

    @Override
    public void onCreate() {
        super.onCreate();
        uiThreadExecutor = new UiThreadExecutor();
        defaultThreadPoolExecutor = new DefaultThreadPoolExecutor();
        final LocalRoomDatabase localRoomDatabase = Room
                .inMemoryDatabaseBuilder(
                        this,
                        LocalRoomDatabase.class)
                .openHelperFactory(new RequerySQLiteOpenHelperFactory())
                .build();
        roomRepository = new RoomRepository(localRoomDatabase);
        useCaseFactory = new DefaultUseCaseFactory(uiThreadExecutor, defaultThreadPoolExecutor, roomRepository);
        viewModelFactory = new ViewModelFactory(useCaseFactory);
    }

    public Executor getUiThreadExecutor() {
        return uiThreadExecutor;
    }

    public Executor getDefaultThreadPoolExecutor() {
        return defaultThreadPoolExecutor;
    }

    public RoomRepository getRoomRepository() {
        return roomRepository;
    }

    public UseCaseFactory getUseCaseFactory() {
        return useCaseFactory;
    }

    public ViewModelFactory getViewModelFactory() {
        return viewModelFactory;
    }
}
