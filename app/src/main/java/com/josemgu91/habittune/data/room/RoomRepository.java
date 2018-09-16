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

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.josemgu91.habittune.data.room.model.ActivityTagJoin;
import com.josemgu91.habittune.domain.datagateways.DataGatewayException;
import com.josemgu91.habittune.domain.datagateways.Repository;
import com.josemgu91.habittune.domain.entities.Activity;
import com.josemgu91.habittune.domain.entities.Routine;
import com.josemgu91.habittune.domain.entities.Tag;

import java.util.ArrayList;
import java.util.List;

public class RoomRepository implements Repository {

    private final LocalRoomDatabase localRoomDatabase;

    public RoomRepository(LocalRoomDatabase localRoomDatabase) {
        this.localRoomDatabase = localRoomDatabase;
    }

    @NonNull
    @Override
    public LiveData<List<Activity>> subscribeToAllActivities() throws DataGatewayException {
        final LiveData<List<com.josemgu91.habittune.data.room.model.Activity>> roomActivityListLiveData = localRoomDatabase.getActivityDao().subscribeToAllActivities();
        Transformations.map(roomActivityListLiveData, roomActivityList -> {
            for (final com.josemgu91.habittune.data.room.model.Activity roomActivity : roomActivityList) {
                final LiveData<List<com.josemgu91.habittune.data.room.model.Tag>> roomActivityTagsLiveData = localRoomDatabase.getActivityTagJoinDao().subscribeToAllTagsByActivityId(roomActivity.id);
                Transformations.map(roomActivityTagsLiveData, roomActivityTags -> mapToEntityActivity(roomActivity, roomActivityTags));
            }
            return null;
        });
        return null;
    }

    @NonNull
    @Override
    public LiveData<List<Activity>> subscribeToAllActivitiesButWithoutTags() throws DataGatewayException {
        try {
            return Transformations.map(localRoomDatabase.getActivityDao().subscribeToAllActivities(), input -> mapList(input, RoomRepository::mapToEntityActivity));
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    @Override
    public boolean deleteActivityById(String id) throws DataGatewayException {
        try {
            return localRoomDatabase.getActivityDao().deleteActivity(new com.josemgu91.habittune.data.room.model.Activity(Long.valueOf(id))) != 0;
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    @Override
    public boolean createActivity(@NonNull Activity activity) throws DataGatewayException {
        try {
            final long activityId = localRoomDatabase.getActivityDao().insertActivity(new com.josemgu91.habittune.data.room.model.Activity(
                    activity.getName(),
                    activity.getDescription(),
                    activity.getColor()
            ));
            if (activityId <= 0) {
                return false;
            }
            for (final Tag tag : activity.getTags()) {
                localRoomDatabase.getActivityTagJoinDao().insertActivityTagJoin(
                        new ActivityTagJoin(activityId, Long.valueOf(tag.getId()))
                );
            }
            return true;
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    @Override
    public boolean updateActivity(@NonNull Activity updatedActivity) {
        return false;
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
    public LiveData<List<Tag>> subscribeToTagsByIds(List<String> tagIds) throws DataGatewayException {
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
    public boolean createTag(@NonNull Tag tag) throws DataGatewayException {
        try {
            final long insertedTagId = localRoomDatabase.getTagDao().insertTag(new com.josemgu91.habittune.data.room.model.Tag(
                    0,
                    tag.getName()
            ));
            return insertedTagId != -1;
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    @Override
    public boolean deleteTagById(String id) throws DataGatewayException {
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
    public LiveData<List<Routine>> subscribeToAllRoutines() throws DataGatewayException {
        try {
            return Transformations.map(localRoomDatabase.getRoutineDao().subscribeToAllRoutines(), input -> mapList(input, RoomRepository::mapToEntityRoutine));
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    @Override
    public boolean createRoutine(@NonNull Routine routine) throws DataGatewayException {
        try {
            final long insertedRoutineId = localRoomDatabase.getRoutineDao().insertRoutine(new com.josemgu91.habittune.data.room.model.Routine(
                    routine.getName(),
                    routine.getDescription(),
                    routine.getColor(),
                    routine.getNumberOfDays()
            ));
            return insertedRoutineId != -1;
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

    private static <I, O> List<O> mapList(List<I> inList, Function<I, O> function) {
        final List<O> outList = new ArrayList<>();
        for (final I element : inList) {
            outList.add(function.apply(element));
        }
        return outList;
    }

    private static Activity mapToEntityActivity(final com.josemgu91.habittune.data.room.model.Activity roomActivity) {
        return new Activity(
                String.valueOf(roomActivity.id),
                roomActivity.name,
                roomActivity.description,
                roomActivity.color,
                null
        );
    }

    private static Activity mapToEntityActivity(final com.josemgu91.habittune.data.room.model.Activity roomActivity, final List<com.josemgu91.habittune.data.room.model.Tag> roomTags) {
        final ArrayList<Tag> tags = new ArrayList<>();
        for (final com.josemgu91.habittune.data.room.model.Tag roomTag : roomTags) {
            tags.add(mapToEntityTag(roomTag));
        }
        return new Activity(
                String.valueOf(roomActivity.id),
                roomActivity.name,
                roomActivity.description,
                roomActivity.color,
                tags
        );
    }

    private static Tag mapToEntityTag(final com.josemgu91.habittune.data.room.model.Tag roomTag) {
        return new Tag(
                String.valueOf(roomTag.id),
                roomTag.name
        );
    }

    private static Routine mapToEntityRoutine(final com.josemgu91.habittune.data.room.model.Routine roomRoutine) {
        return new Routine(
                String.valueOf(roomRoutine.id),
                roomRoutine.name,
                roomRoutine.description,
                roomRoutine.color,
                roomRoutine.numberOfDays
        );
    }
}
