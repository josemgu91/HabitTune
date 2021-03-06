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

import com.josemgu91.habittune.domain.datagateways.Repository;
import com.josemgu91.habittune.domain.usecases.CalculateAssistanceStatistics;
import com.josemgu91.habittune.domain.usecases.CreateActivity;
import com.josemgu91.habittune.domain.usecases.CreateRoutine;
import com.josemgu91.habittune.domain.usecases.CreateRoutineEntry;
import com.josemgu91.habittune.domain.usecases.CreateTag;
import com.josemgu91.habittune.domain.usecases.DeleteActivity;
import com.josemgu91.habittune.domain.usecases.DeleteAssistance;
import com.josemgu91.habittune.domain.usecases.DeleteRoutine;
import com.josemgu91.habittune.domain.usecases.DeleteRoutineEntry;
import com.josemgu91.habittune.domain.usecases.DeleteTag;
import com.josemgu91.habittune.domain.usecases.GetActivities;
import com.josemgu91.habittune.domain.usecases.GetActivity;
import com.josemgu91.habittune.domain.usecases.GetRoutine;
import com.josemgu91.habittune.domain.usecases.GetRoutineEntries;
import com.josemgu91.habittune.domain.usecases.GetRoutineEntriesByDate;
import com.josemgu91.habittune.domain.usecases.GetRoutineEntry;
import com.josemgu91.habittune.domain.usecases.GetRoutines;
import com.josemgu91.habittune.domain.usecases.GetTags;
import com.josemgu91.habittune.domain.usecases.RegisterAssistance;
import com.josemgu91.habittune.domain.usecases.UpdateActivity;
import com.josemgu91.habittune.domain.usecases.UpdateRoutine;
import com.josemgu91.habittune.domain.usecases.UpdateTag;

import java.util.concurrent.Executor;

public class DefaultUseCaseFactory implements UseCaseFactory {

    private final Executor uiThreadExecutor;
    private final Executor defaultThreadPoolExecutor;
    private final Repository repository;

    public DefaultUseCaseFactory(Executor uiThreadExecutor, Executor defaultThreadPoolExecutor, Repository repository) {
        this.uiThreadExecutor = uiThreadExecutor;
        this.defaultThreadPoolExecutor = defaultThreadPoolExecutor;
        this.repository = repository;
    }

    @Override
    public GetActivities createGetActivities() {
        return new GetActivities(
                uiThreadExecutor,
                defaultThreadPoolExecutor,
                repository
        );
    }

    @Override
    public GetActivity createGetActivity() {
        return new GetActivity(
                uiThreadExecutor,
                defaultThreadPoolExecutor,
                repository
        );
    }

    @Override
    public CreateActivity createCreateActivity() {
        return new CreateActivity(
                uiThreadExecutor,
                defaultThreadPoolExecutor,
                repository
        );
    }

    @Override
    public UpdateActivity createUpdateActivity() {
        return new UpdateActivity(
                uiThreadExecutor,
                defaultThreadPoolExecutor,
                repository
        );
    }

    @Override
    public DeleteActivity createDeleteActivity() {
        return new DeleteActivity(
                uiThreadExecutor,
                defaultThreadPoolExecutor,
                repository
        );
    }

    @Override
    public GetTags createGetTags() {
        return new GetTags(
                uiThreadExecutor,
                defaultThreadPoolExecutor,
                repository
        );
    }

    @Override
    public CreateTag createCreateTag() {
        return new CreateTag(
                uiThreadExecutor,
                defaultThreadPoolExecutor,
                repository
        );
    }

    @Override
    public DeleteTag createDeleteTag() {
        return new DeleteTag(
                uiThreadExecutor,
                defaultThreadPoolExecutor,
                repository
        );
    }

    @Override
    public UpdateTag createUpdateTag() {
        return new UpdateTag(
                uiThreadExecutor,
                defaultThreadPoolExecutor,
                repository
        );
    }

    @Override
    public GetRoutines createGetRoutines() {
        return new GetRoutines(
                uiThreadExecutor,
                defaultThreadPoolExecutor,
                repository
        );
    }

    @Override
    public GetRoutine createGetRoutine() {
        return new GetRoutine(
                uiThreadExecutor,
                defaultThreadPoolExecutor,
                repository
        );
    }

    @Override
    public DeleteRoutine createDeleteRoutine() {
        return new DeleteRoutine(
                uiThreadExecutor,
                defaultThreadPoolExecutor,
                repository
        );
    }

    @Override
    public CreateRoutine createCreateRoutine() {
        return new CreateRoutine(
                uiThreadExecutor,
                defaultThreadPoolExecutor,
                repository
        );
    }

    @Override
    public UpdateRoutine createUpdateRoutine() {
        return new UpdateRoutine(
                uiThreadExecutor,
                defaultThreadPoolExecutor,
                repository
        );
    }

    @Override
    public CreateRoutineEntry createCreateRoutineEntry() {
        return new CreateRoutineEntry(
                uiThreadExecutor,
                defaultThreadPoolExecutor,
                repository
        );
    }

    @Override
    public DeleteRoutineEntry createDeleteRoutineEntry() {
        return new DeleteRoutineEntry(
                uiThreadExecutor,
                defaultThreadPoolExecutor,
                repository
        );
    }

    @Override
    public GetRoutineEntries createGetRoutineEntries() {
        return new GetRoutineEntries(
                uiThreadExecutor,
                defaultThreadPoolExecutor,
                repository
        );
    }

    @Override
    public GetRoutineEntry createGetRoutineEntry() {
        return new GetRoutineEntry(
                uiThreadExecutor,
                defaultThreadPoolExecutor,
                repository
        );
    }

    @Override
    public GetRoutineEntriesByDate createGetRoutineEntriesByDate() {
        return new GetRoutineEntriesByDate(
                uiThreadExecutor,
                defaultThreadPoolExecutor,
                repository,
                repository
        );
    }

    @Override
    public RegisterAssistance createRegisterAssistance() {
        return new RegisterAssistance(
                uiThreadExecutor,
                defaultThreadPoolExecutor,
                repository
        );
    }

    @Override
    public DeleteAssistance createDeleteAssistance() {
        return new DeleteAssistance(
                uiThreadExecutor,
                defaultThreadPoolExecutor,
                repository
        );
    }

    @Override
    public CalculateAssistanceStatistics createCalculateAssistanceStatistics() {
        return new CalculateAssistanceStatistics(
                uiThreadExecutor,
                defaultThreadPoolExecutor,
                repository
        );
    }
}
