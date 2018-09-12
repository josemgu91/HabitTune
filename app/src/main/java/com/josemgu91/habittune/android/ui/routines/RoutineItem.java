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

package com.josemgu91.habittune.android.ui.routines;

import android.view.View;
import android.widget.TextView;

import com.josemgu91.habittune.R;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

public class RoutineItem extends AbstractFlexibleItem<RoutineItem.RoutineViewHolder> {

    private final String id;
    private final String name;

    public RoutineItem(String id, String name) {
        this.id = id;
        this.name = name;
        setSwipeable(true);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RoutineItem) {
            return id.equals(((RoutineItem) o).id);
        }
        return false;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.element_routine;
    }

    @Override
    public RoutineViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new RoutineViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, RoutineViewHolder holder, int position, List<Object> payloads) {
        holder.textViewRoutineName.setText(name);
    }

    public static class RoutineViewHolder extends FlexibleViewHolder {

        private TextView textViewRoutineName;

        public RoutineViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            textViewRoutineName = view.findViewById(R.id.textViewRoutineName);
        }
    }
}
