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

package com.josemgu91.habittune.android.ui.routine_editor;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.josemgu91.habittune.R;
import com.josemgu91.habittune.databinding.FragmentRoutineDayBinding;

import java.util.List;
import java.util.Objects;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

public class FragmentRoutineDay extends Fragment {

    private FragmentRoutineDayBinding fragmentRoutineDayBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentRoutineDayBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_routine_day, container, false);
        return fragmentRoutineDayBinding.getRoot();
    }

    private static class RoutineEntryItem extends AbstractFlexibleItem<RoutineEntryItem.RoutineEntryViewHolder> {

        @NonNull
        private final String activityId;
        @NonNull
        private final String activityStartHour;
        @NonNull
        private final String activityEndHour;
        @NonNull
        private final String activityName;

        public RoutineEntryItem(@NonNull String activityId, @NonNull String activityStartHour, @NonNull String activityEndHour, @NonNull String activityName) {
            this.activityId = activityId;
            this.activityStartHour = activityStartHour;
            this.activityEndHour = activityEndHour;
            this.activityName = activityName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RoutineEntryItem that = (RoutineEntryItem) o;
            return Objects.equals(activityId, that.activityId) &&
                    Objects.equals(activityStartHour, that.activityStartHour) &&
                    Objects.equals(activityEndHour, that.activityEndHour) &&
                    Objects.equals(activityName, that.activityName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(activityId, activityStartHour, activityEndHour, activityName);
        }

        @Override
        public int getLayoutRes() {
            return R.layout.element_routine_entry;
        }

        @Override
        public RoutineEntryViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
            return new RoutineEntryViewHolder(view, adapter);
        }

        @Override
        public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, RoutineEntryViewHolder holder, int position, List<Object> payloads) {
            holder.textViewActivityStartHour.setText(activityStartHour);
            holder.textViewActivityEndtHour.setText(activityEndHour);
            holder.textViewActivityName.setText(activityName);
        }

        public static class RoutineEntryViewHolder extends FlexibleViewHolder {

            private final TextView textViewActivityStartHour;
            private final TextView textViewActivityEndtHour;
            private final TextView textViewActivityName;

            public RoutineEntryViewHolder(View view, FlexibleAdapter adapter) {
                super(view, adapter);
                textViewActivityStartHour = view.findViewById(R.id.textViewActivityStartHour);
                textViewActivityEndtHour = view.findViewById(R.id.textViewActivityEndHour);
                textViewActivityName = view.findViewById(R.id.textViewActivityName);
            }
        }
    }
}
