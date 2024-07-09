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

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class SharedViewModelTagEditor extends ViewModel {

    private List<String> selectedTagIds;

    public SharedViewModelTagEditor() {
        this.selectedTagIds = null;
    }

    public void setSelectedTagIds(List<String> selectedTagIds) {
        this.selectedTagIds = new ArrayList<>(selectedTagIds);
    }

    public List<String> getSelectedTagIds() {
        return selectedTagIds;
    }

    public void clear() {
        selectedTagIds = null;
    }

}
