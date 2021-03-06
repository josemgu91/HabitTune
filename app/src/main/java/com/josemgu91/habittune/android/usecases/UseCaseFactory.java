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

public interface UseCaseFactory {

    GetActivities createGetActivities();

    GetActivity createGetActivity();

    CreateActivity createCreateActivity();

    UpdateActivity createUpdateActivity();

    DeleteActivity createDeleteActivity();

    GetTags createGetTags();

    CreateTag createCreateTag();

    DeleteTag createDeleteTag();

    UpdateTag createUpdateTag();

    GetRoutines createGetRoutines();

    GetRoutine createGetRoutine();

    DeleteRoutine createDeleteRoutine();

    CreateRoutine createCreateRoutine();

    UpdateRoutine createUpdateRoutine();

    CreateRoutineEntry createCreateRoutineEntry();

    DeleteRoutineEntry createDeleteRoutineEntry();

    GetRoutineEntries createGetRoutineEntries();

    GetRoutineEntry createGetRoutineEntry();

    GetRoutineEntriesByDate createGetRoutineEntriesByDate();

    RegisterAssistance createRegisterAssistance();

    DeleteAssistance createDeleteAssistance();

    CalculateAssistanceStatistics createCalculateAssistanceStatistics();

}
