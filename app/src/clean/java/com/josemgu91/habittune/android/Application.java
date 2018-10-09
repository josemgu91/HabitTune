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

import android.arch.lifecycle.ViewModelProvider;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

import com.josemgu91.habittune.BuildConfig;
import com.josemgu91.habittune.android.executors.DefaultThreadPoolExecutor;
import com.josemgu91.habittune.android.executors.UiThreadExecutor;
import com.josemgu91.habittune.android.ui.ViewModelFactory;
import com.josemgu91.habittune.android.usecases.DefaultUseCaseFactory;
import com.josemgu91.habittune.android.usecases.UseCaseFactory;
import com.josemgu91.habittune.data.room.LocalRoomDatabase;
import com.josemgu91.habittune.data.room.RoomRepository;
import com.josemgu91.habittune.domain.datagateways.Repository;

import java.util.concurrent.Executor;

import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory;

public class Application extends android.app.Application {

    private Executor uiThreadExecutor;
    private Executor defaultThreadPoolExecutor;
    private Repository repository;
    private UseCaseFactory useCaseFactory;
    private ViewModelProvider.Factory viewModelFactory;

    @Override
    public void onCreate() {
        super.onCreate();
        uiThreadExecutor = new UiThreadExecutor();
        defaultThreadPoolExecutor = new DefaultThreadPoolExecutor();
        final RoomDatabase.Builder<LocalRoomDatabase> roomDatabaseBuilder;
        if (BuildConfig.USE_IN_RAM_DATABASE) {
            roomDatabaseBuilder = Room
                    .inMemoryDatabaseBuilder(
                            this,
                            LocalRoomDatabase.class);
        } else {
            roomDatabaseBuilder = Room
                    .databaseBuilder(
                            this,
                            LocalRoomDatabase.class,
                            "habitTune.db"
                    );
        }
        final LocalRoomDatabase localRoomDatabase = roomDatabaseBuilder
                .openHelperFactory(new RequerySQLiteOpenHelperFactory())
                .build();
        repository = new RoomRepository(localRoomDatabase, defaultThreadPoolExecutor);
        useCaseFactory = new DefaultUseCaseFactory(uiThreadExecutor, defaultThreadPoolExecutor, repository);
        viewModelFactory = new ViewModelFactory(useCaseFactory);
    }

    public Executor getUiThreadExecutor() {
        return uiThreadExecutor;
    }

    public Executor getDefaultThreadPoolExecutor() {
        return defaultThreadPoolExecutor;
    }

    public Repository getRepository() {
        return repository;
    }

    public UseCaseFactory getUseCaseFactory() {
        return useCaseFactory;
    }

    public ViewModelProvider.Factory getViewModelFactory() {
        return viewModelFactory;
    }
}
