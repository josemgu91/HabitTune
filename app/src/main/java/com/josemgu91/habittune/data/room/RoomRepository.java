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

package com.josemgu91.habittune.data.room;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.josemgu91.habittune.data.room.backup.LocalRoomDatabaseBackupRepository;
import com.josemgu91.habittune.data.room.csv.CsvExporter;
import com.josemgu91.habittune.data.room.custom_responses.RoutineEntry;
import com.josemgu91.habittune.data.room.model.ActivityTagJoin;
import com.josemgu91.habittune.data.room.model.RoutineActivityJoin;
import com.josemgu91.habittune.domain.DomainException;
import com.josemgu91.habittune.domain.datagateways.DataGatewayException;
import com.josemgu91.habittune.domain.datagateways.Repository;
import com.josemgu91.habittune.domain.entities.Activity;
import com.josemgu91.habittune.domain.entities.AssistanceRegister;
import com.josemgu91.habittune.domain.entities.Day;
import com.josemgu91.habittune.domain.entities.Routine;
import com.josemgu91.habittune.domain.entities.Tag;
import com.josemgu91.habittune.domain.entities.Time;
import com.josemgu91.habittune.domain.usecases.common.ScheduleCalculator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

public class RoomRepository implements Repository {

    private final LocalRoomDatabase localRoomDatabase;
    private final LocalRoomDatabaseBackupRepository localRoomDatabaseBackupRepository;
    private final CsvExporter csvExporter;

    private final Executor repositoryExecutor;

    private final ScheduleCalculator scheduleCalculator;

    public RoomRepository(@NonNull final Context context, @NonNull final LocalRoomDatabase localRoomDatabase, @NonNull final Executor repositoryExecutor) {
        this.localRoomDatabase = localRoomDatabase;
        this.repositoryExecutor = repositoryExecutor;
        this.localRoomDatabaseBackupRepository = new LocalRoomDatabaseBackupRepository(localRoomDatabase, context);
        this.csvExporter = new CsvExporter(localRoomDatabase, context);
        this.scheduleCalculator = new ScheduleCalculator();
    }

    @NonNull
    @Override
    public LiveData<List<Activity>> subscribeToAllActivities() throws DataGatewayException {
        try {
            final MediatorLiveData<List<Activity>> result = new MediatorLiveData<>();
            result.addSource(localRoomDatabase.getActivityDao().subscribeToAllActivities(), roomActivities -> repositoryExecutor.execute(() -> {
                final List<Activity> activities = mapList(roomActivities, roomActivity -> mapToEntityActivity(roomActivity, localRoomDatabase.getActivityTagJoinDao().getAllTagsByActivityId(roomActivity.id)));
                result.postValue(activities);
            }));
            return result;
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    @NonNull
    @Override
    public Activity getActivityById(@NonNull String id) throws DataGatewayException {
        try {
            return mapToEntityActivity(
                    localRoomDatabase.getActivityDao().getActivityById(Long.valueOf(id)),
                    localRoomDatabase.getActivityTagJoinDao().getAllTagsByActivityId(Long.valueOf(id))
            );
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    @Override
    public boolean deleteActivityById(@NonNull String id) throws DataGatewayException {
        try {
            return localRoomDatabase.getActivityDao().deleteActivity(new com.josemgu91.habittune.data.room.model.Activity(Long.valueOf(id))) != 0;
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    @Override
    @NonNull
    public Activity createActivity(@NonNull Activity activity) throws DataGatewayException {
        try {
            final long activityId = localRoomDatabase.getActivityDao().insertActivity(new com.josemgu91.habittune.data.room.model.Activity(
                    activity.getName(),
                    activity.getDescription(),
                    activity.getColor()
            ));
            for (final Tag tag : activity.getTags()) {
                localRoomDatabase.getActivityTagJoinDao().insertActivityTagJoin(
                        new ActivityTagJoin(activityId, Long.valueOf(tag.getId()))
                );
            }
            return new Activity(
                    String.valueOf(activityId),
                    activity.getName(),
                    activity.getDescription(),
                    activity.getColor(),
                    activity.getTags()
            );
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    @Override
    public boolean updateActivity(@NonNull Activity activity) throws DataGatewayException {
        try {
            localRoomDatabase.getActivityTagJoinDao().deleteActivityTagJoinsByActivityId(Long.valueOf(activity.getId()));
            for (final Tag tag : activity.getTags()) {
                localRoomDatabase.getActivityTagJoinDao().insertActivityTagJoin(new ActivityTagJoin(
                        Long.valueOf(activity.getId()),
                        Long.valueOf(tag.getId())
                ));
            }
            localRoomDatabase.getActivityDao().updateActivity(new com.josemgu91.habittune.data.room.model.Activity(
                    Long.valueOf(activity.getId()),
                    activity.getName(),
                    activity.getDescription(),
                    activity.getColor()
            ));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataGatewayException(e.getMessage());
        }
    }

    @NonNull
    @Override
    public LiveData<List<Tag>> subscribeToAllTags() throws DataGatewayException {
        try {
            return Transformations.map(localRoomDatabase.getTagDao().subscribeToAllTags(), input -> mapList(input, RoomRepository::mapToEntityTag));
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    @NonNull
    @Override
    public LiveData<List<Tag>> subscribeToTagsByIds(@NonNull List<String> tagIds) throws DataGatewayException {
        final long[] ids = new long[tagIds.size()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = Long.valueOf(tagIds.get(i));
        }
        try {
            return Transformations.map(localRoomDatabase.getTagDao().subscribeToTagsByIds(ids), input -> mapList(input, RoomRepository::mapToEntityTag));
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    @Override
    @NonNull
    public Tag createTag(@NonNull Tag tag) throws DataGatewayException {
        try {
            final long insertedTagId = localRoomDatabase.getTagDao().insertTag(new com.josemgu91.habittune.data.room.model.Tag(
                    0,
                    tag.getName()
            ));
            return new Tag(String.valueOf(insertedTagId), tag.getName());
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    @Override
    public boolean deleteTagById(@NonNull String id) throws DataGatewayException {
        try {
            return localRoomDatabase.getTagDao().deleteTag(
                    new com.josemgu91.habittune.data.room.model.Tag(
                            Long.valueOf(id),
                            ""
                    )
            ) != 0;
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    @Override
    public boolean updateTag(@NonNull Tag updatedTag) throws DataGatewayException {
        try {
            return localRoomDatabase.getTagDao().updateTag(new com.josemgu91.habittune.data.room.model.Tag(
                    Long.valueOf(updatedTag.getId()),
                    updatedTag.getName()
            )) != 0;
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    @NonNull
    @Override
    public LiveData<List<Routine>> subscribeToAllRoutines(final boolean includeRoutineEntries) throws DataGatewayException {
        try {
            if (includeRoutineEntries) {
                final MediatorLiveData<List<Routine>> result = new MediatorLiveData<>();
                result.addSource(localRoomDatabase.getRoutineDao().subscribeToAllRoutines(), roomRoutines -> repositoryExecutor.execute(() -> {
                    final List<Routine> routines = mapList(roomRoutines, roomRoutine -> {
                        final List<com.josemgu91.habittune.domain.entities.RoutineEntry> routineEntries = mapList(
                                localRoomDatabase.getRoutineActivityJoinDao().getAllRoutineActivityJoinsByRoutineId(roomRoutine.id),
                                routineActivityJoin -> {
                                    try {
                                        return mapRoutineActivityJoinToRoutineEntry(
                                                routineActivityJoin,
                                                getActivityById(String.valueOf(routineActivityJoin.activityId))
                                        );
                                    } catch (DomainException e) {
                                        e.printStackTrace();
                                        throw new RuntimeException(e.getMessage());
                                    }
                                }
                        );
                        return mapToEntityRoutine(roomRoutine, routineEntries);
                    });
                    result.postValue(routines);
                }));
                return result;
            }
            return Transformations.map(
                    localRoomDatabase.getRoutineDao().subscribeToAllRoutines(),
                    roomRoutines -> mapList(roomRoutines, roomRoutine -> mapToEntityRoutine(roomRoutine, null))
            );
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    @NonNull
    @Override
    public Routine getRoutineWithoutEntriesById(@NonNull String id) throws DataGatewayException {
        try {
            return mapToEntityRoutine(localRoomDatabase.getRoutineDao().getRoutineById(id), null);
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    @Override
    @NonNull
    public Routine createRoutineWithoutEntries(@NonNull Routine routine) throws DataGatewayException {
        try {
            final long insertedRoutineId = localRoomDatabase.getRoutineDao().insertRoutine(new com.josemgu91.habittune.data.room.model.Routine(
                    routine.getName(),
                    routine.getDescription(),
                    routine.getColor(),
                    routine.getNumberOfDays(),
                    routine.getStartDate().getTime(),
                    routine.isEnabled(),
                    routine.getCreationDate().getTime(),
                    routine.isEnabled() ? 0 : routine.getDeactivationDate().getTime()
            ));
            return new Routine(
                    String.valueOf(insertedRoutineId),
                    routine.getName(),
                    routine.getDescription(),
                    routine.getColor(),
                    routine.getNumberOfDays(),
                    routine.getStartDate(),
                    null,
                    routine.isEnabled(),
                    routine.getCreationDate(),
                    routine.getDeactivationDate()
            );
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    @Override
    public boolean deleteRoutineById(@NonNull String id) throws DataGatewayException {
        try {
            return localRoomDatabase.getRoutineDao().deleteRoutine(
                    new com.josemgu91.habittune.data.room.model.Routine(Long.valueOf(id))
            ) != 0;
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    @Override
    public boolean updateRoutine(@NonNull String id, @NonNull String name, @NonNull String description, int color) throws DataGatewayException {
        try {
            return localRoomDatabase.getRoutineDao().updateRoutine(id, name, description, color) != 0;
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    @Override
    @NonNull
    public LiveData<List<com.josemgu91.habittune.domain.entities.RoutineEntry>> subscribeToAllRoutineEntriesByRoutineId(@NonNull final String routineId) throws DataGatewayException {
        try {
            final LiveData<List<RoutineActivityJoin>> routineActivityJoinsLiveData = localRoomDatabase.getRoutineActivityJoinDao().subscribeToAllRoutineActivityJoinsByRoutineId(Long.valueOf(routineId));
            return transformRoutineActivityJoinListLiveDataToRoutineEntryListLiveData(routineActivityJoinsLiveData);
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    @Override
    @NonNull
    public LiveData<List<com.josemgu91.habittune.domain.entities.RoutineEntry>> subscribeToAllRoutineEntriesByRoutineIdAndDay(@NonNull String routineId, int dayNumber) throws DataGatewayException {
        try {
            final LiveData<List<RoutineActivityJoin>> routineActivityJoinsLiveData = localRoomDatabase.getRoutineActivityJoinDao().subscribeToAllRoutineActivityJoinsByRoutineIdAndDay(Long.valueOf(routineId), dayNumber);
            return transformRoutineActivityJoinListLiveDataToRoutineEntryListLiveData(routineActivityJoinsLiveData);
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    private LiveData<List<com.josemgu91.habittune.domain.entities.RoutineEntry>> transformRoutineActivityJoinListLiveDataToRoutineEntryListLiveData(final LiveData<List<RoutineActivityJoin>> routineActivityJoinsLiveData) {
        final MediatorLiveData<List<com.josemgu91.habittune.domain.entities.RoutineEntry>> result = new MediatorLiveData<>();
        result.addSource(routineActivityJoinsLiveData, routineActivityJoins -> repositoryExecutor.execute(() -> {
            final List<com.josemgu91.habittune.domain.entities.RoutineEntry> routineEntries = new ArrayList<>();
            for (final RoutineActivityJoin routineActivityJoin : routineActivityJoins) {
                try {
                    final Activity activity = getActivityById(String.valueOf(routineActivityJoin.activityId));
                    routineEntries.add(mapRoutineActivityJoinToRoutineEntry(routineActivityJoin, activity));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            result.postValue(routineEntries);
        }));
        return result;
    }

    @NonNull
    @Override
    public com.josemgu91.habittune.domain.entities.RoutineEntry getRoutineEntryById(@NonNull final String id) throws DataGatewayException {
        try {
            final RoutineActivityJoin routineActivityJoin = localRoomDatabase.getRoutineActivityJoinDao().getRoutineActivityJoin(Long.valueOf(id));
            final Activity activity = getActivityById(String.valueOf(routineActivityJoin.activityId));
            return mapRoutineActivityJoinToRoutineEntry(routineActivityJoin, activity);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataGatewayException(e.getMessage());
        }
    }

    @Override
    @NonNull
    public com.josemgu91.habittune.domain.entities.RoutineEntry createRoutineEntry(@NonNull final com.josemgu91.habittune.domain.entities.RoutineEntry routineEntry, @NonNull final String routineId) throws DataGatewayException {
        try {
            final long id = localRoomDatabase.getRoutineActivityJoinDao().insertRoutineActivityJoin(new RoutineActivityJoin(
                    Long.valueOf(routineId),
                    Long.valueOf(routineEntry.getActivity().getId()),
                    routineEntry.getDay().getDay(),
                    routineEntry.getStartTime().getTime(),
                    routineEntry.getEndTime().getTime(),
                    routineEntry.isEnabled(),
                    routineEntry.getCreationDate().getTime(),
                    routineEntry.isEnabled() ? 0 : routineEntry.getDeactivationDate().getTime()
            ));
            return new com.josemgu91.habittune.domain.entities.RoutineEntry(String.valueOf(id),
                    routineEntry.getDay(),
                    routineEntry.getStartTime(),
                    routineEntry.getEndTime(),
                    routineEntry.getActivity(),
                    routineEntry.isEnabled(),
                    routineEntry.getCreationDate(),
                    routineEntry.getDeactivationDate(),
                    null);
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    @Override
    public boolean deleteRoutineEntry(@NonNull String id) throws DataGatewayException {
        try {
            return localRoomDatabase.getRoutineActivityJoinDao().deleteRoutineActivityJoin(
                    new RoutineActivityJoin(Long.valueOf(id))
            ) != 0;
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    @NonNull
    @Override
    public AssistanceRegister createOrUpdateAssistanceRegister(@NonNull AssistanceRegister assistanceRegister, @NonNull String routineEntryId) throws DataGatewayException {
        try {
            final Time assistanceRegisterEndTime = assistanceRegister.getEndTime();
            final long assistanceRegisterId = localRoomDatabase.getAssistanceRegisterDao().insertAssistanceRegister(
                    new com.josemgu91.habittune.data.room.model.AssistanceRegister(
                            assistanceRegister.getCycleNumber(),
                            assistanceRegister.getStartTime().getTime(),
                            assistanceRegisterEndTime == null ? -1 : assistanceRegisterEndTime.getTime(),
                            Long.valueOf(routineEntryId)
                    )
            );
            return new AssistanceRegister(
                    String.valueOf(assistanceRegisterId),
                    assistanceRegister.getCycleNumber(),
                    assistanceRegister.getStartTime(),
                    assistanceRegisterEndTime
            );
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    @Override
    public boolean deleteAssistanceRegister(int cycleNumber, @NonNull String routineEntryId) throws DataGatewayException {
        try {
            return localRoomDatabase.getAssistanceRegisterDao().deleteAssistanceRegisterByCycleNumberAndRoutineActivityJoinId(
                    cycleNumber,
                    Long.valueOf(routineEntryId)
            ) != 0;
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    @NonNull
    @Override
    public LiveData<AssistanceRegister> subscribeToAssistanceRegisterByCycleNumberAndRoutineEntryId(int cycleNumber, @NonNull String routineEntryId) throws DataGatewayException {
        try {
            return Transformations.map(localRoomDatabase.getAssistanceRegisterDao().subscribeToAssistanceRegisterByCycleNumberAndRoutineEntryId(
                    cycleNumber, Long.valueOf(routineEntryId)
            ), input -> {
                if (input == null) {
                    return null;
                }
                return mapToEntityAssistanceRegister(input);
            });
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    @Override
    public List<AssistanceRegister> getAssistanceRegistersByRoutineEntryBetweenCycleNumbers(@NonNull String routineEntryId, int fromCycleNumber, int toCycleNumber) throws DataGatewayException {
        try {
            final List<com.josemgu91.habittune.data.room.model.AssistanceRegister> roomAssistanceRegisters =
                    localRoomDatabase.getAssistanceRegisterDao().getAssistanceRegistersByRoutineEntryBetweenCycleNumbers(
                            Long.valueOf(routineEntryId),
                            fromCycleNumber,
                            toCycleNumber
                    );
            return mapList(roomAssistanceRegisters, RoomRepository::mapToEntityAssistanceRegister);
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    @Override
    public int countCompletedAssistanceRegistersByActivityId(@NonNull String activityId) throws DataGatewayException {
        try {
            return localRoomDatabase.getStatisticsDao().countCompletedAssistanceRegistersByActivityId(
                    Long.valueOf(activityId)
            );
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    @Override
    public int calculateTotalSinceCreationByActivityId(@NonNull final String activityId, @NonNull final Date toDate) throws DataGatewayException {
        try {
            int totalCount = 0;
            final List<RoutineEntry> routineEntries = localRoomDatabase.getStatisticsDao().getRoutineActivityJoinsWithRoutineInfoByActivityId(
                    Long.valueOf(activityId)
            );
            for (final RoutineEntry routineEntry : routineEntries) {
                final int numberOfRoutineEntryEventsBetweenDates = scheduleCalculator.computeNumberOfRoutineEntryEventsBetweenDates(
                        new Date(routineEntry.routineActivityJoinCreationDateTimestamp),
                        toDate,
                        new Date(routineEntry.routineStartDateTimestamp),
                        new Date(routineEntry.routineActivityJoinCreationDateTimestamp),
                        routineEntry.routineNumberOfDays,
                        routineEntry.routineActivityJoinEntryDay
                );
                totalCount += numberOfRoutineEntryEventsBetweenDates;
            }
            return totalCount;
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    @Override
    public void importFrom(@NonNull String fileUri) throws DataGatewayException {
        localRoomDatabaseBackupRepository.importFrom(fileUri);
    }

    @Override
    public void exportTo(@NonNull String fileUri) throws DataGatewayException {
        localRoomDatabaseBackupRepository.exportTo(fileUri);
    }

    @Override
    public void exportToCsv(@NonNull String activityId, @NonNull String fileUri) throws DataGatewayException {
        csvExporter.exportToCsv(activityId, fileUri);
    }

    private static <I, O> List<O> mapList(List<I> inList, Function<I, O> function) {
        final List<O> outList = new ArrayList<>();
        for (final I element : inList) {
            outList.add(function.apply(element));
        }
        return outList;
    }

    private static AssistanceRegister mapToEntityAssistanceRegister(final com.josemgu91.habittune.data.room.model.AssistanceRegister roomAssistanceRegister) {
        try {
            return new AssistanceRegister(
                    String.valueOf(roomAssistanceRegister.id),
                    roomAssistanceRegister.cycleNumber,
                    new Time(roomAssistanceRegister.startTime),
                    roomAssistanceRegister.endTime == -1 ? null : new Time(roomAssistanceRegister.endTime)
            );
        } catch (DomainException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private static com.josemgu91.habittune.domain.entities.RoutineEntry mapRoutineActivityJoinToRoutineEntry(final RoutineActivityJoin routineActivityJoin, final Activity activity) throws DomainException {
        return new com.josemgu91.habittune.domain.entities.RoutineEntry(
                String.valueOf(routineActivityJoin.id),
                new Day(routineActivityJoin.day),
                new Time(routineActivityJoin.startTime),
                new Time(routineActivityJoin.endTime),
                activity,
                routineActivityJoin.enabled,
                new Date(routineActivityJoin.creationDateTimestamp),
                routineActivityJoin.enabled ? null : new Date(routineActivityJoin.deactivationDateTimestamp),
                null
        );
    }

    private static Activity mapToEntityActivity(final com.josemgu91.habittune.data.room.model.Activity roomActivity, final List<com.josemgu91.habittune.data.room.model.Tag> roomTags) {
        return new Activity(
                String.valueOf(roomActivity.id),
                roomActivity.name,
                roomActivity.description,
                roomActivity.color,
                mapList(roomTags, RoomRepository::mapToEntityTag)
        );
    }

    private static Tag mapToEntityTag(final com.josemgu91.habittune.data.room.model.Tag roomTag) {
        return new Tag(
                String.valueOf(roomTag.id),
                roomTag.name
        );
    }

    private static Routine mapToEntityRoutine(@NonNull final com.josemgu91.habittune.data.room.model.Routine roomRoutine, @Nullable final List<com.josemgu91.habittune.domain.entities.RoutineEntry> routineEntries) {
        return new Routine(
                String.valueOf(roomRoutine.id),
                roomRoutine.name,
                roomRoutine.description,
                roomRoutine.color,
                roomRoutine.numberOfDays,
                new Date(roomRoutine.startDateTimestamp),
                routineEntries,
                roomRoutine.enabled,
                new Date(roomRoutine.creationDateTimestamp),
                roomRoutine.enabled ? null : new Date(roomRoutine.deactivationDateTimestamp)
        );
    }
}
