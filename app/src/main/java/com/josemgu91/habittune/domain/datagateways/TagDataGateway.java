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

package com.josemgu91.habittune.domain.datagateways;

import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.josemgu91.habittune.domain.entities.Tag;

import java.util.List;

public interface TagDataGateway {

    @NonNull
    LiveData<List<Tag>> subscribeToAllTags() throws DataGatewayException;

    @NonNull
    LiveData<List<Tag>> subscribeToTagsByIds(@NonNull List<String> tagIds) throws DataGatewayException;

    @NonNull
    Tag createTag(@NonNull final Tag tag) throws DataGatewayException;

    boolean deleteTagById(@NonNull final String id) throws DataGatewayException;

    boolean updateTag(@NonNull final Tag updatedTag) throws DataGatewayException;

}
