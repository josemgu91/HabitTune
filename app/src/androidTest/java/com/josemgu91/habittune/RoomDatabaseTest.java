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

package com.josemgu91.habittune;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.josemgu91.habittune.data.room.LocalRoomDatabase;
import com.josemgu91.habittune.data.room.dao.ActivityDao;
import com.josemgu91.habittune.data.room.dao.ActivityTagJoinDao;
import com.josemgu91.habittune.data.room.dao.TagDao;
import com.josemgu91.habittune.data.room.model.Activity;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class RoomDatabaseTest {

    private ActivityDao activityDao;
    private TagDao tagDao;
    private ActivityTagJoinDao activityTagJoinDao;
    private LiveDataTestUtils liveDataTestUtils;

    private LocalRoomDatabase localRoomDatabase;

    private TestDataGenerator testDataGenerator;

    @Before
    public void createDatabase() {
        final Context context = InstrumentationRegistry.getTargetContext();
        localRoomDatabase = Room.inMemoryDatabaseBuilder(context, LocalRoomDatabase.class).build();
        activityDao = localRoomDatabase.getActivityDao();
        tagDao = localRoomDatabase.getTagDao();
        activityTagJoinDao = localRoomDatabase.getActivityTagJoinDao();
        testDataGenerator = new TestDataGenerator();
        liveDataTestUtils = new LiveDataTestUtils();
    }

    @After
    public void closeDatabase() throws IOException {
        localRoomDatabase.close();
    }

    @Test
    public void writeActivities() throws Exception {
        final List<Activity> testActivities = testDataGenerator.createActivities(5);
        final List<Activity> testActivitiesWithId = new ArrayList<>();
        for (final Activity activity : testActivities) {
            final long id = activityDao.insertActivity(activity);
            testActivitiesWithId.add(testDataGenerator.addIdToActivity(activity, id));
        }
        final List<Activity> storedActivities = liveDataTestUtils.getValueSync(activityDao.getAllActivities());
        Assert.assertEquals(storedActivities.containsAll(testActivitiesWithId), true);
        Assert.assertEquals(storedActivities.size(), 5);
    }

    private static class TestDataGenerator {

        public List<Activity> createActivities(final int size) {
            final List<Activity> activities = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                final Activity activity = new Activity(
                        0,
                        "Activity " + (i + 1),
                        "Activity description " + (i + 1),
                        0xFF00FF00
                );
                activities.add(activity);
            }
            return activities;
        }

        public Activity addIdToActivity(final Activity activity, final long id) {
            return new Activity(
                    id,
                    activity.name,
                    activity.description,
                    activity.color
            );
        }

    }

    private static class LiveDataTestUtils {

        public <T> T getValueSync(final LiveData<T> liveData) throws InterruptedException {
            final Object[] data = new Object[1];
            final CountDownLatch latch = new CountDownLatch(1);
            Observer<T> observer = new Observer<T>() {
                @Override
                public void onChanged(@Nullable T o) {
                    data[0] = o;
                    latch.countDown();
                    liveData.removeObserver(this);
                }
            };
            liveData.observeForever(observer);
            latch.await(2, TimeUnit.SECONDS);
            //noinspection unchecked
            return (T) data[0];
        }

    }

}
