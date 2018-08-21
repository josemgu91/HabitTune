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
import android.database.sqlite.SQLiteConstraintException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.josemgu91.habittune.data.room.LocalRoomDatabase;
import com.josemgu91.habittune.data.room.dao.ActivityDao;
import com.josemgu91.habittune.data.room.dao.ActivityTagJoinDao;
import com.josemgu91.habittune.data.room.dao.TagDao;
import com.josemgu91.habittune.data.room.model.Activity;
import com.josemgu91.habittune.data.room.model.ActivityTagJoin;
import com.josemgu91.habittune.data.room.model.Tag;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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
    public void insertActivity() {
        final Activity activity = testDataGenerator.createActivities(1).get(0);
        final long activityId = activityDao.insertActivity(activity);
        Assert.assertNotEquals(-1, activityId);
    }

    @Test
    public void readActivities() throws Exception {
        final int testSize = 5;
        final List<Activity> testActivities = testDataGenerator.createActivities(testSize);
        final List<Activity> testActivitiesWithId = new ArrayList<>();
        for (final Activity activity : testActivities) {
            final long id = activityDao.insertActivity(activity);
            testActivitiesWithId.add(testDataGenerator.addIdToActivity(activity, id));
        }
        final List<Activity> storedActivities = liveDataTestUtils.getValueSync(activityDao.subscribeToAllActivities());
        Assert.assertEquals(true, storedActivities.containsAll(testActivitiesWithId));
        Assert.assertEquals(testSize, storedActivities.size());
    }

    @Test
    public void countActivities() {
        final int originalCount = activityDao.countActivities();
        Assert.assertEquals(0, originalCount);
        activityDao.insertActivity(testDataGenerator.createActivities(1).get(0));
        final int newCount = activityDao.countActivities();
        Assert.assertEquals(1, newCount);
    }

    @Test
    public void updateActivityName() throws Exception {
        final String originalActivityName = "Piano practice";
        final Activity testActivity = new Activity(0, originalActivityName, "Practice piano lessons.", 0xFFFF0000);
        final long activityId = activityDao.insertActivity(testActivity);
        final Activity testActivityWithId = testDataGenerator.addIdToActivity(testActivity, activityId);
        final String newActivityName = "Practice piano lessons";
        final Activity activityToUpdate = new Activity(
                testActivityWithId.id,
                newActivityName,
                testActivityWithId.description,
                testActivityWithId.color
        );
        final int rowsUpdated = activityDao.updateActivity(activityToUpdate);
        final Activity updatedActivity = liveDataTestUtils.getValueSync(activityDao.subscribeToActivityById(activityId));
        Assert.assertEquals(1, rowsUpdated);
        Assert.assertEquals(activityToUpdate, updatedActivity);
    }

    @Test
    public void assertUniqueActivityName() {
        final String originalActivityName = "Activity 1";
        final String duplicatedActivityName = "Activity 1";
        final Activity originalActivity = new Activity(0, originalActivityName, "test", 0);
        final Activity sameNameActivity = new Activity(0, duplicatedActivityName, "test 2", 1);
        final long originalActivityId = activityDao.insertActivity(originalActivity);
        Assert.assertNotEquals(-1, originalActivityId);
        try {
            activityDao.insertActivity(sameNameActivity);
            Assert.fail();
        } catch (SQLiteConstraintException e) {
            Assert.assertTrue(true);
        }
        try {
            final Activity newActivity = new Activity(0, "Activity 2", "test", 0);
            final long newActivityId = activityDao.insertActivity(newActivity);
            final Activity newActivityWithSameNameToUpdate = new Activity(
                    newActivityId,
                    originalActivityName,
                    newActivity.description,
                    newActivity.color
            );
            activityDao.updateActivity(newActivityWithSameNameToUpdate);
            Assert.fail();
        } catch (SQLiteConstraintException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void deleteActivity() {
        final Activity testActivity = testDataGenerator.createActivities(1).get(0);
        final long testActivityId = activityDao.insertActivity(testActivity);
        final Activity testActivityWithId = testDataGenerator.addIdToActivity(testActivity, testActivityId);
        final int deletedRows = activityDao.deleteActivity(testActivityWithId);
        Assert.assertEquals(1, deletedRows);
    }

    @Test
    public void deleteActivityByName() {
        final Activity testActivity = testDataGenerator.createActivities(1).get(0);
        final long testActivityId = activityDao.insertActivity(testActivity);
        final Activity testActivityWithId = testDataGenerator.addIdToActivity(testActivity, testActivityId);
        final int deletedRows = activityDao.deleteActivityByName(testActivityWithId.name);
        Assert.assertEquals(1, deletedRows);
    }

    @Test
    public void insertTag() {
        final Tag tag = testDataGenerator.createTags(1).get(0);
        final long tagId = tagDao.insertTag(tag);
        Assert.assertNotEquals(-1, tagId);
    }

    @Test
    public void readTags() throws Exception {
        final int testSize = 5;
        final List<Tag> testTags = testDataGenerator.createTags(testSize);
        final List<Tag> testTagsWithId = new ArrayList<>();
        for (final Tag tag : testTags) {
            final long id = tagDao.insertTag(tag);
            testTagsWithId.add(testDataGenerator.addIdToTag(tag, id));
        }
        final List<Tag> storedTags = liveDataTestUtils.getValueSync(tagDao.subscribeToAllTags());
        Assert.assertEquals(true, storedTags.containsAll(testTagsWithId));
        Assert.assertEquals(testSize, storedTags.size());
    }

    @Test
    public void getTagByName() {
        final String testTagName = "My Tag";
        final long insertedTagId = tagDao.insertTag(new Tag(0, testTagName));
        final Tag insertedTag = tagDao.getTagByName(testTagName);
        Assert.assertEquals(insertedTagId, insertedTag.id);
        Assert.assertEquals(testTagName, insertedTag.name);
    }

    @Test
    public void updateTagName() throws Exception {
        final String originalTagName = "My Tag";
        final Tag testTag = new Tag(0, originalTagName);
        final long tagId = tagDao.insertTag(testTag);
        final Tag testTagWithId = testDataGenerator.addIdToTag(testTag, tagId);
        final String newTagName = "Study";
        final Tag tagToUpdate = new Tag(
                testTagWithId.id,
                newTagName
        );
        final int rowsUpdated = tagDao.updateTag(tagToUpdate);
        final Tag updatedTag = liveDataTestUtils.getValueSync(tagDao.subscribeToAllTags()).get(0);
        Assert.assertEquals(1, rowsUpdated);
        Assert.assertEquals(tagToUpdate, updatedTag);
    }

    @Test
    public void assertUniqueTagName() {
        final String originalTagName = "Tag 1";
        final String duplicatedTagName = "Tag 1";
        final Tag originalTag = new Tag(0, originalTagName);
        final Tag sameNameTag = new Tag(0, duplicatedTagName);
        final long originalTagId = tagDao.insertTag(originalTag);
        Assert.assertNotEquals(-1, originalTagId);
        try {
            tagDao.insertTag(sameNameTag);
            Assert.fail();
        } catch (SQLiteConstraintException e) {
            Assert.assertTrue(true);
        }
        try {
            final Tag newTag = new Tag(0, "Tag 2");
            final long newTagId = tagDao.insertTag(newTag);
            final Tag newTagWithSameNameToUpdate = new Tag(
                    newTagId,
                    originalTagName
            );
            tagDao.updateTag(newTagWithSameNameToUpdate);
            Assert.fail();
        } catch (SQLiteConstraintException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void deleteTag() {
        final Tag testTag = testDataGenerator.createTags(1).get(0);
        final long testTagId = tagDao.insertTag(testTag);
        final Tag testTagWithId = testDataGenerator.addIdToTag(testTag, testTagId);
        final int deletedRows = tagDao.deleteTag(testTagWithId);
        Assert.assertEquals(1, deletedRows);
    }

    @Test
    public void insertActivityTagJoin() {
        final Activity insertedActivity = createAndInsertTestActivity("Piano lesson", "Practice piano lessons.", 0xFF0000FF);
        final Tag insertedTag = createAndInsertTestTag("Music");
        final ActivityTagJoin activityTagJoinToInsert = new ActivityTagJoin(
                insertedActivity.id,
                insertedTag.id
        );
        final long insertedActivityTagJoinRowId = activityTagJoinDao.insertActivityTagJoin(activityTagJoinToInsert);
        Assert.assertNotEquals(-1, insertedActivityTagJoinRowId);
        final ActivityTagJoin invalidActivityTagJoin = new ActivityTagJoin(
                4,
                insertedTag.id
        );
        try {
            activityTagJoinDao.insertActivityTagJoin(invalidActivityTagJoin);
            Assert.fail();
        } catch (SQLiteConstraintException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void getActivityTags() throws Exception {
        final Activity practicePianoActivity = createAndInsertTestActivity("Practice piano", "Piano lessons practice.", 0xFF0000FF);
        final Activity practiceMathActivity = createAndInsertTestActivity("Practice math", "Math lessons practice.", 0xFF0000FF);
        final Tag musicTag = createAndInsertTestTag("Music");
        final Tag studyTag = createAndInsertTestTag("Study");
        final Tag funTag = createAndInsertTestTag("Fun");
        activityTagJoinDao.insertActivityTagJoin(new ActivityTagJoin(
                practicePianoActivity.id,
                musicTag.id
        ));
        activityTagJoinDao.insertActivityTagJoin(new ActivityTagJoin(
                practicePianoActivity.id,
                studyTag.id
        ));
        activityTagJoinDao.insertActivityTagJoin(new ActivityTagJoin(
                practicePianoActivity.id,
                funTag.id
        ));
        activityTagJoinDao.insertActivityTagJoin(new ActivityTagJoin(
                practiceMathActivity.id,
                studyTag.id
        ));
        final List<Tag> practicePianoTags = liveDataTestUtils.getValueSync(activityTagJoinDao.subscribeToAllTagsByActivityId(practicePianoActivity.id));
        final List<Tag> practiceMathTags = liveDataTestUtils.getValueSync(activityTagJoinDao.subscribeToAllTagsByActivityId(practiceMathActivity.id));
        Assert.assertEquals(3, practicePianoTags.size());
        Assert.assertEquals(1, practiceMathTags.size());
    }

    @Test
    public void cascadeDeleteActivityTags() throws Exception {
        final Activity practicePianoActivity = createAndInsertTestActivity("Practice piano", "Piano lessons practice.", 0xFF0000FF);
        final Activity practiceMathActivity = createAndInsertTestActivity("Practice math", "Math lessons practice.", 0xFF0000FF);
        final Tag musicTag = createAndInsertTestTag("Music");
        final Tag studyTag = createAndInsertTestTag("Study");
        final Tag funTag = createAndInsertTestTag("Fun");
        activityTagJoinDao.insertActivityTagJoin(new ActivityTagJoin(
                practicePianoActivity.id,
                musicTag.id
        ));
        activityTagJoinDao.insertActivityTagJoin(new ActivityTagJoin(
                practicePianoActivity.id,
                studyTag.id
        ));
        activityTagJoinDao.insertActivityTagJoin(new ActivityTagJoin(
                practicePianoActivity.id,
                funTag.id
        ));
        activityTagJoinDao.insertActivityTagJoin(new ActivityTagJoin(
                practiceMathActivity.id,
                studyTag.id
        ));
        activityDao.deleteActivity(practiceMathActivity);
        final List<Tag> practiceMathTags = liveDataTestUtils.getValueSync(activityTagJoinDao.subscribeToAllTagsByActivityId(practiceMathActivity.id));
        Assert.assertEquals(0, practiceMathTags.size());
        activityDao.deleteActivity(practicePianoActivity);
        final List<Tag> practicePianoTags = liveDataTestUtils.getValueSync(activityTagJoinDao.subscribeToAllTagsByActivityId(practicePianoActivity.id));
        Assert.assertEquals(0, practicePianoTags.size());
    }

    @Test
    public void cascadeDeleteTag() throws Exception {
        final Activity practicePianoActivity = createAndInsertTestActivity("Practice piano", "Piano lessons practice.", 0xFF0000FF);
        final Activity practiceMathActivity = createAndInsertTestActivity("Practice math", "Math lessons practice.", 0xFF0000FF);
        final Tag musicTag = createAndInsertTestTag("Music");
        final Tag studyTag = createAndInsertTestTag("Study");
        final Tag funTag = createAndInsertTestTag("Fun");
        activityTagJoinDao.insertActivityTagJoin(new ActivityTagJoin(
                practicePianoActivity.id,
                musicTag.id
        ));
        activityTagJoinDao.insertActivityTagJoin(new ActivityTagJoin(
                practicePianoActivity.id,
                studyTag.id
        ));
        activityTagJoinDao.insertActivityTagJoin(new ActivityTagJoin(
                practicePianoActivity.id,
                funTag.id
        ));
        activityTagJoinDao.insertActivityTagJoin(new ActivityTagJoin(
                practiceMathActivity.id,
                studyTag.id
        ));
        tagDao.deleteTag(studyTag);
        final List<Tag> practiceMathTags = liveDataTestUtils.getValueSync(activityTagJoinDao.subscribeToAllTagsByActivityId(practiceMathActivity.id));
        Assert.assertEquals(0, practiceMathTags.size());
        final List<Tag> practicePianoTags = liveDataTestUtils.getValueSync(activityTagJoinDao.subscribeToAllTagsByActivityId(practicePianoActivity.id));
        Assert.assertEquals(2, practicePianoTags.size());
    }

    @Test
    public void deleteActivityTags() throws Exception {
        final Activity practicePianoActivity = createAndInsertTestActivity("Practice piano", "Piano lessons practice.", 0xFF0000FF);
        final Tag musicTag = createAndInsertTestTag("Music");
        final Tag studyTag = createAndInsertTestTag("Study");
        final Tag exerciseTag = createAndInsertTestTag("Exercise");
        activityTagJoinDao.insertActivityTagJoin(new ActivityTagJoin(
                practicePianoActivity.id,
                musicTag.id
        ));
        activityTagJoinDao.insertActivityTagJoin(new ActivityTagJoin(
                practicePianoActivity.id,
                studyTag.id
        ));
        activityTagJoinDao.insertActivityTagJoin(new ActivityTagJoin(
                practicePianoActivity.id,
                exerciseTag.id
        ));
        final long removedRows = activityTagJoinDao.deleteActivityTagJoin(new ActivityTagJoin(
                practicePianoActivity.id,
                exerciseTag.id
        ));
        Assert.assertEquals(1, removedRows);
        final List<Tag> practicePianoTags = liveDataTestUtils.getValueSync(activityTagJoinDao.subscribeToAllTagsByActivityId(practicePianoActivity.id));
        Assert.assertEquals(2, practicePianoTags.size());
    }

    private Activity createAndInsertTestActivity(@NonNull final String name, @NonNull final String description, final int color) {
        final Activity activityToInsert = new Activity(
                0,
                name,
                description,
                0xFF0000FF
        );
        final long insertedActivityId = activityDao.insertActivity(activityToInsert);
        return testDataGenerator.addIdToActivity(activityToInsert, insertedActivityId);
    }

    private Tag createAndInsertTestTag(@NonNull final String name) {
        final Tag tagToInsert = new Tag(
                0,
                name
        );
        final long insertedTagId = tagDao.insertTag(tagToInsert);
        return testDataGenerator.addIdToTag(tagToInsert, insertedTagId);
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

        public List<Tag> createTags(final int size) {
            final List<Tag> tags = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                final Tag tag = new Tag(
                        0,
                        "Tag " + (i + 1)
                );
                tags.add(tag);
            }
            return tags;
        }

        public Activity addIdToActivity(final Activity activity, final long id) {
            return new Activity(
                    id,
                    activity.name,
                    activity.description,
                    activity.color
            );
        }

        public Tag addIdToTag(final Tag tag, final long id) {
            return new Tag(
                    id,
                    tag.name
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
