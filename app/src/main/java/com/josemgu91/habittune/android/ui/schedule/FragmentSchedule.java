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

package com.josemgu91.habittune.android.ui.schedule;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.FragmentInteractionListener;
import com.josemgu91.habittune.android.ui.BaseFragment;
import com.josemgu91.habittune.databinding.FragmentScheduleBinding;
import com.josemgu91.habittune.domain.usecases.GetRoutineEntries;
import com.josemgu91.habittune.domain.usecases.GetRoutines;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

public class FragmentSchedule extends BaseFragment {

    private final static int ROUTINE_ENTRY_START_DAY = Calendar.MONDAY;

    private ViewModelSchedule viewModelSchedule;
    private FragmentScheduleBinding fragmentScheduleBinding;

    private FlexibleAdapter<ActivityItem> activityItemFlexibleAdapter;

    private ScheduleCalculator scheduleCalculator;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        viewModelSchedule = ViewModelProviders.of(this, viewModelFactory).get(ViewModelSchedule.class);
        scheduleCalculator = new ScheduleCalculator();
    }

    @Override
    protected ToolbarOptions createToolbarOptions() {
        return new ToolbarOptions(true);
    }

    @NonNull
    @Override
    public View createView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentScheduleBinding = FragmentScheduleBinding.inflate(inflater, container, false);
        activityItemFlexibleAdapter = new FlexibleAdapter<>(null);
        fragmentScheduleBinding.recyclerView.setAdapter(activityItemFlexibleAdapter);
        fragmentScheduleBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return fragmentScheduleBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentInteractionListener.updateToolbar(
                /*getString(R.string.schedule_title)*/getCurrentDay(),
                FragmentInteractionListener.IC_NAVIGATION_HAMBURGUER
        );
        fragmentInteractionListener.updateNavigationDrawer(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            /* FIXME: A better approach must be used. What if the process is killed by the system (savedInstanceState != null)?
             * The ViewModel will start in a clean state but the savedInstanceState will not be null because the system saves
             * the instance state when killing the process.*/
            viewModelSchedule.fetchRoutines();
        }
        viewModelSchedule.getGetRoutinesResponse().observe(getViewLifecycleOwner(), response -> {
            switch (response.status) {
                case LOADING:
                    break;
                case ERROR:
                    break;
                case SUCCESS:
                    response.successData.observe(getViewLifecycleOwner(), this::onRoutinesUpdated);
                    break;
            }
        });
    }

    private String getCurrentDay() {
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return dateFormat.format(calendar.getTime());
    }

    private String formatHour(final int hourInSeconds) {
        final DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        final Calendar calendar = Calendar.getInstance();
        final int hours = hourInSeconds / 3600;
        final int minutes = (hourInSeconds % 3600) / 60;
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        return dateFormat.format(calendar.getTime());
    }

    private void onRoutinesUpdated(final List<GetRoutines.Output> routines) {
        final Date currentDate = new Date();
        final List<GetRoutineEntries.Output> routineEntries = new ArrayList<>();
        for (final GetRoutines.Output routine : routines) {
            for (final GetRoutineEntries.Output routineEntry : routine.getRoutineEntries()) {
                final int currentDayNumber = scheduleCalculator.getRoutineDayNumber(currentDate, routine.getStartDate(), routine.getNumberOfDays());
                if (currentDayNumber != routineEntry.getDay()) {
                    continue;
                }
                routineEntries.add(routineEntry);
            }
        }
        Collections.sort(routineEntries, (o1, o2) -> o1.getStartTime() - o2.getStartTime());
        final ArrayList<ActivityItem> activityItems = new ArrayList<>();
        for (final GetRoutineEntries.Output routineEntry : routineEntries) {
            activityItems.add(new ActivityItem(
                    routineEntry.getId(),
                    formatHour(routineEntry.getStartTime()),
                    formatHour(routineEntry.getEndTime()),
                    routineEntry.getActivity().getName()
            ));
        }
        activityItemFlexibleAdapter.updateDataSet(activityItems);
    }

    private static class ActivityItem extends AbstractFlexibleItem<ActivityItem.ViewHolder> {

        @NonNull
        private final String routineEntryId;
        @NonNull
        private final String activityStartHour;
        @NonNull
        private final String activityEndHour;
        @NonNull
        private final String activityName;

        public ActivityItem(@NonNull String routineEntryId, @NonNull String activityStartHour, @NonNull String activityEndHour, @NonNull String activityName) {
            this.routineEntryId = routineEntryId;
            this.activityStartHour = activityStartHour;
            this.activityEndHour = activityEndHour;
            this.activityName = activityName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ActivityItem that = (ActivityItem) o;
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
            return R.layout.element_schedule_entry;
        }

        @Override
        public ViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
            return new ViewHolder(view, adapter);
        }

        @Override
        public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ViewHolder holder, int position, List<Object> payloads) {
            holder.textViewName.setText(activityName);
            holder.textViewStartHour.setText(activityStartHour);
            holder.textViewEndHour.setText(activityEndHour);
        }

        public static class ViewHolder extends FlexibleViewHolder {

            private final TextView textViewStartHour;
            private final TextView textViewEndHour;
            private final TextView textViewName;

            public ViewHolder(View view, FlexibleAdapter adapter) {
                super(view, adapter);
                textViewStartHour = view.findViewById(R.id.textViewActivityStartHour);
                textViewEndHour = view.findViewById(R.id.textViewActivityEndHour);
                textViewName = view.findViewById(R.id.textViewActivityName);
            }
        }

    }
}
