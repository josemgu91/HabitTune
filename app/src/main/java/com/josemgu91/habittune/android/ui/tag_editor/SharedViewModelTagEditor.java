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

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.Bundle;

import com.josemgu91.habittune.android.ui.RestorableViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SharedViewModelTagEditor extends ViewModel implements RestorableViewModel {

    public final static String SAVED_INSTANCE_STATE_KEY_SELECTED_TAGS_IDS = "selectedTagsIds";

    private final MutableLiveData<List<String>> selectedTagIds;

    public SharedViewModelTagEditor() {
        this.selectedTagIds = new MutableLiveData<>();
    }

    public void setSelectedTagIds(List<String> selectedTagIds) {
        this.selectedTagIds.setValue(selectedTagIds);
    }

    public void setSelectedTagIds(String... selectedTagIds) {
        this.selectedTagIds.setValue(Arrays.asList(selectedTagIds));
    }

    public MutableLiveData<List<String>> getSelectedTagIds() {
        return selectedTagIds;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (selectedTagIds.getValue() == null) {
            return;
        }
        final ArrayList<String> lastSelectedTagIds = new ArrayList<>(selectedTagIds.getValue());
        outState.putStringArrayList(SAVED_INSTANCE_STATE_KEY_SELECTED_TAGS_IDS, new ArrayList<>(lastSelectedTagIds));
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        final List<String> lastSelectedTagIds = savedInstanceState.getStringArrayList(SAVED_INSTANCE_STATE_KEY_SELECTED_TAGS_IDS);
        if (lastSelectedTagIds == null) {
            return;
        }
        selectedTagIds.setValue(lastSelectedTagIds);
    }
}
