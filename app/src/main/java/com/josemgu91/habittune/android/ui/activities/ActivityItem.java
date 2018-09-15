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

package com.josemgu91.habittune.android.ui.activities;

import android.view.View;
import android.widget.TextView;

import com.josemgu91.habittune.R;

import java.util.List;
import java.util.Objects;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

public class ActivityItem extends AbstractFlexibleItem<ActivityItem.ActivityViewHolder> {

    private final String id;
    private final String name;

    public ActivityItem(String id, String name) {
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivityItem that = (ActivityItem) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.element_activity;
    }

    @Override
    public ActivityViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new ActivityViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ActivityViewHolder holder, int position, List<Object> payloads) {
        holder.textViewActivity.setText(name);
    }

    public static class ActivityViewHolder extends FlexibleViewHolder {

        private TextView textViewActivity;

        public ActivityViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            textViewActivity = view.findViewById(R.id.textViewActivityName);
        }
    }
}
