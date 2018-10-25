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

package com.josemgu91.habittune.data.room.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import com.josemgu91.habittune.data.room.model.AssistanceRegister;

import java.util.List;

@Dao
public interface AssistanceRegisterDao {

    @Query("SELECT * FROM assistanceRegisters WHERE cycleNumber = :cycleNumber AND routineActivityJoinId = :routineActivityJoinId")
    LiveData<AssistanceRegister> subscribeToAssistanceRegisterByCycleNumberAndRoutineEntryId(int cycleNumber, long routineActivityJoinId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertAssistanceRegister(@NonNull final AssistanceRegister assistanceRegister);

    @Query("DELETE FROM assistanceRegisters WHERE cycleNumber = :cycleNumber AND routineActivityJoinId = :routineActivityJoinId")
    int deleteAssistanceRegisterByCycleNumberAndRoutineActivityJoinId(final int cycleNumber, final long routineActivityJoinId);

    @Query("SELECT * FROM assistanceRegisters WHERE routineActivityJoinId = :routineActivityJoinId")
    List<AssistanceRegister> getAssistanceRegistersByRoutineActivityJoinId(final long routineActivityJoinId);

    @Query("SELECT * FROM assistanceRegisters")
    List<AssistanceRegister> getAllAssistanceRegisters();

    @Query("SELECT * FROM assistanceRegisters WHERE routineActivityJoinId = :routineActivityJoinId AND cycleNumber BETWEEN :fromCycleNumber AND :toCycleNumber")
    List<AssistanceRegister> getAssistanceRegistersByRoutineEntryBetweenCycleNumbers(long routineActivityJoinId, int fromCycleNumber, int toCycleNumber);

}
