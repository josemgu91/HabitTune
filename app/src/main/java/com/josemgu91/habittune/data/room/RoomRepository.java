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
import com.josemgu91.habittune.domain.datagateways.ActivityDataGateway;
import com.josemgu91.habittune.domain.datagateways.DataGatewayException;
import com.josemgu91.habittune.domain.datagateways.TagDataGateway;
import com.josemgu91.habittune.domain.entities.Activity;
import com.josemgu91.habittune.domain.entities.Tag;

import java.util.ArrayList;
import java.util.List;

public class RoomRepository implements ActivityDataGateway, TagDataGateway {

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
    public int countActivities() {
        return localRoomDatabase.getActivityDao().countActivities();
    }

    @Override
    public boolean deleteActivityByName(String name) throws DataGatewayException {
        try {
            return localRoomDatabase.getActivityDao().deleteActivityByName(name) != 0;
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    @Override
    public boolean createActivity(@NonNull Activity activity) throws DataGatewayException {
        try {
            final long insertedActivityId = localRoomDatabase.getActivityDao().insertActivity(new com.josemgu91.habittune.data.room.model.Activity(
                    0,
                    activity.getName(),
                    activity.getDescription(),
                    activity.getColor()
            ));
            if (activity.getTags() != null) {
                for (Tag tag : activity.getTags()) {
                    final com.josemgu91.habittune.data.room.model.Tag roomTag = localRoomDatabase.getTagDao().getTagByName(tag.getName());
                    localRoomDatabase.getActivityTagJoinDao().insertActivityTagJoin(new ActivityTagJoin(
                            insertedActivityId,
                            roomTag.id
                    ));
                }
            }
            return insertedActivityId != 0;
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    @Override
    public boolean updateActivity(@NonNull Activity oldActivity, @NonNull Activity newActivity) throws DataGatewayException {
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
    public boolean deleteTagByName(String name) throws DataGatewayException {
        try {
            return localRoomDatabase.getTagDao().deleteTagByName(name) != 0;
        } catch (Exception e) {
            throw new DataGatewayException(e.getMessage());
        }
    }

    @Override
    public boolean updateTag(@NonNull Tag oldTag, @NonNull Tag newTag) throws DataGatewayException {
        return false;
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
                roomActivity.name,
                roomActivity.description,
                roomActivity.color,
                tags
        );
    }

    private static Tag mapToEntityTag(final com.josemgu91.habittune.data.room.model.Tag roomTag) {
        return new com.josemgu91.habittune.domain.entities.Tag(
                roomTag.name
        );
    }
}
