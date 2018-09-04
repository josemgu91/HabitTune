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

package com.josemgu91.habittune.android.ui.tag_editor;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import com.josemgu91.habittune.android.ui.Response;
import com.josemgu91.habittune.domain.usecases.CreateTag;
import com.josemgu91.habittune.domain.usecases.DeleteTag;
import com.josemgu91.habittune.domain.usecases.GetTags;
import com.josemgu91.habittune.domain.usecases.UpdateTag;
import com.josemgu91.habittune.domain.usecases.UseCaseOutput;

import java.util.List;

public class ViewModelTagEditor extends ViewModel {

    private final GetTags getTags;
    private final CreateTag createTag;
    private final DeleteTag deleteTag;
    private final UpdateTag updateTag;

    private final MutableLiveData<Response<LiveData<List<GetTags.Output>>, Void>> getTagsResponse;
    private final MutableLiveData<Response<LiveData<List<GetTags.Output>>, Void>> createTagResponse;
    private final MutableLiveData<Response<LiveData<List<GetTags.Output>>, Void>> deleteTagResponse;
    private final MutableLiveData<Response<LiveData<List<GetTags.Output>>, Void>> updateTagResponse;

    public ViewModelTagEditor(GetTags getTags, CreateTag createTag, DeleteTag deleteTag, UpdateTag updateTag) {
        this.getTags = getTags;
        this.createTag = createTag;
        this.deleteTag = deleteTag;
        this.updateTag = updateTag;
        this.getTagsResponse = new MutableLiveData<>();
        this.createTagResponse = new MutableLiveData<>();
        this.deleteTagResponse = new MutableLiveData<>();
        this.updateTagResponse = new MutableLiveData<>();
    }

    public void fetchTags() {
        getTags.execute(null, new UseCaseOutput<LiveData<List<GetTags.Output>>>() {
            @Override
            public void onSuccess(@Nullable LiveData<List<GetTags.Output>> listLiveData) {
                getTagsResponse.setValue(new Response<>(Response.Status.SUCCESS, listLiveData, null));
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

    public void createTag(final String tagName) {
        createTag.execute(new CreateTag.Input(tagName), new UseCaseOutput<Void>() {
            @Override
            public void onSuccess(@Nullable Void aVoid) {
                createTagResponse.setValue(new Response<>(Response.Status.SUCCESS, null, null));
            }

            @Override
            public void inProgress() {
                createTagResponse.setValue(new Response<>(Response.Status.LOADING, null, null));
            }

            @Override
            public void onError() {
                createTagResponse.setValue(new Response<>(Response.Status.ERROR, null, null));
            }
        });
    }

    public void deleteTag(final String tagName) {
        deleteTag.execute(new DeleteTag.Input(tagName), new UseCaseOutput<Void>() {
            @Override
            public void onSuccess(@Nullable Void aVoid) {
                deleteTagResponse.setValue(new Response<>(Response.Status.SUCCESS, null, null));
            }

            @Override
            public void inProgress() {
                deleteTagResponse.setValue(new Response<>(Response.Status.LOADING, null, null));
            }

            @Override
            public void onError() {
                deleteTagResponse.setValue(new Response<>(Response.Status.ERROR, null, null));
            }
        });
    }

    public void updateTag(final String currentTagName, final String updatedTagName) {
        updateTag.execute(new UpdateTag.Input(currentTagName, updatedTagName), new UseCaseOutput<Void>() {
            @Override
            public void onSuccess(@Nullable Void aVoid) {
                updateTagResponse.setValue(new Response<>(Response.Status.SUCCESS, null, null));
            }

            @Override
            public void inProgress() {
                updateTagResponse.setValue(new Response<>(Response.Status.LOADING, null, null));
            }

            @Override
            public void onError() {
                updateTagResponse.setValue(new Response<>(Response.Status.ERROR, null, null));
            }
        });
    }

    public MutableLiveData<Response<LiveData<List<GetTags.Output>>, Void>> getGetTagsResponse() {
        return getTagsResponse;
    }

    public MutableLiveData<Response<LiveData<List<GetTags.Output>>, Void>> getCreateTagResponse() {
        return createTagResponse;
    }

    public MutableLiveData<Response<LiveData<List<GetTags.Output>>, Void>> getDeleteTagResponse() {
        return deleteTagResponse;
    }

    public MutableLiveData<Response<LiveData<List<GetTags.Output>>, Void>> getUpdateTagResponse() {
        return updateTagResponse;
    }
}
