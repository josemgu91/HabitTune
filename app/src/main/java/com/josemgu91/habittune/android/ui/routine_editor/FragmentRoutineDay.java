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

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.Application;
import com.josemgu91.habittune.databinding.FragmentRoutineDayBinding;
import com.josemgu91.habittune.domain.usecases.GetRoutineEntries;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

public class FragmentRoutineDay extends Fragment {

    private final static String ARG_ROUTINE_DAY = "routineDay";
    private final static String ARG_ROUTINE_ID = "routineId";

    public static FragmentRoutineDay newInstance(final String routineId, final int day) {
        final Bundle args = new Bundle();
        args.putInt(ARG_ROUTINE_DAY, day);
        args.putString(ARG_ROUTINE_ID, routineId);
        FragmentRoutineDay fragment = new FragmentRoutineDay();
        fragment.setArguments(args);
        return fragment;
    }

    private FlexibleAdapter<RoutineEntryItem> routineEntryItemFlexibleAdapter;
    private ViewModelRoutineDay viewModelRoutineDay;
    private int routineDay;
    private String routineId;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        @NonNull final Bundle arguments = getArguments();
        routineDay = arguments.getInt(ARG_ROUTINE_DAY);
        routineId = arguments.getString(ARG_ROUTINE_ID);
        final ViewModelProvider.Factory viewModelFactory = ((Application) context.getApplicationContext()).getViewModelFactory();
        viewModelRoutineDay = ViewModelProviders.of(this, viewModelFactory).get(ViewModelRoutineDay.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentRoutineDayBinding fragmentRoutineDayBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_routine_day, container, false);
        routineEntryItemFlexibleAdapter = new RoutineEntriesFlexibleAdapter(viewModelRoutineDay::deleteRoutineEntry, null);
        fragmentRoutineDayBinding.recyclerViewRoutineDayEntries.setAdapter(routineEntryItemFlexibleAdapter);
        fragmentRoutineDayBinding.recyclerViewRoutineDayEntries.setLayoutManager(new LinearLayoutManager(getContext()));
        return fragmentRoutineDayBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModelRoutineDay.fetchRoutineEntries(routineId, routineDay);
        viewModelRoutineDay.getGetRoutineEntriesResponse().observe(getViewLifecycleOwner(), response -> {
            switch (response.status) {
                case LOADING:
                    break;
                case ERROR:
                    break;
                case SUCCESS:
                    response.successData.observe(getViewLifecycleOwner(), this::updateRoutineEntries);
                    break;
            }
        });
    }

    private void updateRoutineEntries(List<GetRoutineEntries.Output> routineEntries) {
        final DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        final Calendar calendar = Calendar.getInstance();
        final List<RoutineEntryItem> routineEntryItems = new ArrayList<>();
        for (final GetRoutineEntries.Output routineEntry : routineEntries) {
            final String formattedStartTime = formatHour(dateFormat, calendar, routineEntry.getStartTime());
            final String formattedEndTime = formatHour(dateFormat, calendar, routineEntry.getEndTime());
            routineEntryItems.add(new RoutineEntryItem(
                    routineEntry.getId(),
                    formattedStartTime,
                    formattedEndTime,
                    routineEntry.getActivity().getName()
            ));
        }
        routineEntryItemFlexibleAdapter.updateDataSet(routineEntryItems);
    }

    //TODO: Reuse.
    private String formatHour(final DateFormat dateFormat, final Calendar calendar, final int hourInSeconds) {
        final int hours = hourInSeconds / 3600;
        final int minutes = (hourInSeconds % 3600) / 60;
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        return dateFormat.format(calendar.getTime());
    }

    public static class RoutineEntriesFlexibleAdapter extends FlexibleAdapter<RoutineEntryItem> {

        @Nullable
        private final OnRoutineEntryDeleteListener onRoutineEntryDeleteListener;

        public RoutineEntriesFlexibleAdapter(@Nullable final OnRoutineEntryDeleteListener onRoutineEntryDeleteListener, @Nullable final OnRoutineEntryClickListener onRoutineEntryClickListener) {
            super(null);
            this.onRoutineEntryDeleteListener = onRoutineEntryDeleteListener;
            if (onRoutineEntryClickListener == null) {
                return;
            }
            addListener((OnItemClickListener) (view, position) -> {
                final RoutineEntryItem routineEntryItem = getItem(position);
                onRoutineEntryClickListener.onRoutineEntryClick(routineEntryItem.routineEntryId);
                return true;
            });
        }

        public interface OnRoutineEntryDeleteListener {

            void onRoutineEntryDelete(final String routineEntryId);
        }

        public interface OnRoutineEntryClickListener {

            void onRoutineEntryClick(final String routineEntryId);
        }
    }

    public static class RoutineEntryItem extends AbstractFlexibleItem<RoutineEntryItem.RoutineEntryViewHolder> {

        @NonNull
        private final String routineEntryId;
        @NonNull
        private final String activityStartHour;
        @NonNull
        private final String activityEndHour;
        @NonNull
        private final String activityName;

        public RoutineEntryItem(@NonNull String routineEntryId, @NonNull String activityStartHour, @NonNull String activityEndHour, @NonNull String activityName) {
            this.routineEntryId = routineEntryId;
            this.activityStartHour = activityStartHour;
            this.activityEndHour = activityEndHour;
            this.activityName = activityName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RoutineEntryItem that = (RoutineEntryItem) o;
            return Objects.equals(routineEntryId, that.routineEntryId) &&
                    Objects.equals(activityStartHour, that.activityStartHour) &&
                    Objects.equals(activityEndHour, that.activityEndHour) &&
                    Objects.equals(activityName, that.activityName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(routineEntryId, activityStartHour, activityEndHour, activityName);
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
            holder.routineEntryId = routineEntryId;
            holder.textViewActivityStartHour.setText(activityStartHour);
            holder.textViewActivityEndtHour.setText(activityEndHour);
            holder.textViewActivityName.setText(activityName);
        }

        public static class RoutineEntryViewHolder extends FlexibleViewHolder {

            private String routineEntryId;

            private final TextView textViewActivityStartHour;
            private final TextView textViewActivityEndtHour;
            private final TextView textViewActivityName;
            private final ImageView imageViewOverflowButton;

            public RoutineEntryViewHolder(View view, FlexibleAdapter adapter) {
                super(view, adapter);
                textViewActivityStartHour = view.findViewById(R.id.textViewActivityStartHour);
                textViewActivityEndtHour = view.findViewById(R.id.textViewActivityEndHour);
                textViewActivityName = view.findViewById(R.id.textViewActivityName);
                imageViewOverflowButton = view.findViewById(R.id.imageViewOverflowButton);
                final PopupMenu popupMenu = new PopupMenu(view.getContext(), imageViewOverflowButton);
                popupMenu.inflate(R.menu.element_routine_entry);
                popupMenu.setOnMenuItemClickListener(item -> {
                    final RoutineEntriesFlexibleAdapter.OnRoutineEntryDeleteListener onRoutineEntryDeleteListener = ((RoutineEntriesFlexibleAdapter) adapter).onRoutineEntryDeleteListener;
                    if (item.getItemId() == R.id.actionDeleteRoutineEntry && onRoutineEntryDeleteListener != null) {
                        onRoutineEntryDeleteListener.onRoutineEntryDelete(routineEntryId);
                        return true;
                    }
                    return false;
                });
                imageViewOverflowButton.setOnClickListener(v -> popupMenu.show());
            }
        }
    }
}
