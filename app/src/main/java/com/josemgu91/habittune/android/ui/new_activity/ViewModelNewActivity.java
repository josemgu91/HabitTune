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

package com.josemgu91.habittune.android.ui.new_activity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.josemgu91.habittune.android.ui.Response;
import com.josemgu91.habittune.android.ui.RestorableViewModel;
import com.josemgu91.habittune.domain.usecases.CreateActivity;
import com.josemgu91.habittune.domain.usecases.GetTags;
import com.josemgu91.habittune.domain.usecases.common.UseCaseOutput;

import java.util.ArrayList;
import java.util.List;

public class ViewModelNewActivity extends ViewModel implements RestorableViewModel {

    private final static String SAVED_INSTANCE_STATE_KEY_COLOR = "color";
    private final static String SAVED_INSTANCE_STATE_KEY_SELECTED_TAGS_IDS = "selectedTagsIds";

    private final CreateActivity createActivity;
    private final GetTags getTags;

    private final MutableLiveData<Response<Void, Void>> createActivityResponse;
    private final MutableLiveData<Response<LiveData<List<GetTags.Output>>, Void>> getTagsResponse;
    private final MutableLiveData<Integer> selectedColor;

    public ViewModelNewActivity(final CreateActivity createActivity, final GetTags getTags) {
        this.createActivity = createActivity;
        this.getTags = getTags;
        this.createActivityResponse = new MutableLiveData<>();
        this.getTagsResponse = new MutableLiveData<>();
        this.selectedColor = new MutableLiveData<>();
    }

    public void createActivity(final CreateActivity.Input activity) {
        createActivity.execute(activity, new UseCaseOutput<Void>() {
            @Override
            public void onSuccess(@Nullable Void aVoid) {
                createActivityResponse.setValue(new Response<>(Response.Status.SUCCESS, null, null));
            }

            @Override
            public void inProgress() {
                createActivityResponse.setValue(new Response<>(Response.Status.LOADING, null, null));
            }

            @Override
            public void onError() {
                createActivityResponse.setValue(new Response<>(Response.Status.ERROR, null, null));
            }
        });
    }

    public void getTags(final List<String> tagIds) {
        getTags.execute(new GetTags.Input(GetTags.Input.BY_IDS, tagIds), new UseCaseOutput<LiveData<List<GetTags.Output>>>() {
            @Override
            public void onSuccess(@Nullable LiveData<List<GetTags.Output>> tagsLiveData) {
                getTagsResponse.setValue(new Response<>(Response.Status.SUCCESS, tagsLiveData, null));
            }

            @Override
            public void inProgress() {
                getTagsResponse.setValue(new Response<>(Response.Status.LOADING, null, null));
            }

            @Override
            public void onError() {
                getTagsResponse.setValue(new Response<>(Response.Status.ERROR, null, null));
            }
        });
    }

    public MutableLiveData<Response<Void, Void>> getCreateActivityResponse() {
        return createActivityResponse;
    }

    public MutableLiveData<Response<LiveData<List<GetTags.Output>>, Void>> getGetTagsResponse() {
        return getTagsResponse;
    }

    public MutableLiveData<Integer> getSelectedColor() {
        return selectedColor;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        final int color = selectedColor.getValue();
        outState.putInt(SAVED_INSTANCE_STATE_KEY_COLOR, color);
        if (getTagsResponse.getValue() == null || getTagsResponse.getValue().successData == null) {
            return;
        }
        final List<GetTags.Output> tags = getTagsResponse.getValue().successData.getValue();
        if (tags == null) {
            return;
        }
        final ArrayList<String> selectedTagIds = new ArrayList<>();
        for (final GetTags.Output tag : tags) {
            selectedTagIds.add(tag.getId());
        }
        outState.putStringArrayList(SAVED_INSTANCE_STATE_KEY_SELECTED_TAGS_IDS, selectedTagIds);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        //TODO: This is called for every rotation but should be applied only after a process kill! I'm going to think a better approach.
        final int color = savedInstanceState.getInt(SAVED_INSTANCE_STATE_KEY_COLOR);
        selectedColor.setValue(color);
        final ArrayList<String> selectedTagIds = savedInstanceState.getStringArrayList(SAVED_INSTANCE_STATE_KEY_SELECTED_TAGS_IDS);
        if (selectedTagIds == null) {
            return;
        }
        getTags(selectedTagIds);
    }
}
