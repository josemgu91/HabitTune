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

package com.josemgu91.habittune.android.executors;

import android.support.annotation.NonNull;

import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DefaultThreadPoolExecutor implements Executor {

    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = 1;
    private static final int MAX_POOL_SIZE = NUMBER_OF_CORES;
    private static final int KEEP_ALIVE_TIME = 120;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    private final AbstractExecutorService threadPoolExecutor;

    public DefaultThreadPoolExecutor() {
        final BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(MAX_POOL_SIZE);
        threadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TIME_UNIT,
                workQueue
        );
    }

    @Override
    public void execute(@NonNull Runnable command) {
        threadPoolExecutor.execute(command);
    }
}
