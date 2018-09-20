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

package com.josemgu91.habittune.android.ui;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.josemgu91.habittune.R;

public class DefaultToolbarViewWrapper implements ViewWrapper {

    private final View viewToWrap;

    public DefaultToolbarViewWrapper(@NonNull View viewToWrap) {
        this.viewToWrap = viewToWrap;
    }

    @Override
    @NonNull
    public View wrap(@NonNull LayoutInflater layoutInflater, @NonNull ViewGroup root) {
        final View wrapper = layoutInflater.inflate(R.layout.default_toolbar_wrapper, root, false);
        final ViewGroup container = wrapper.findViewById(R.id.container);
        container.addView(viewToWrap);
        return wrapper;
    }
}
