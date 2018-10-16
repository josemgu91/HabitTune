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

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
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
import com.josemgu91.habittune.domain.DomainException;
import com.josemgu91.habittune.domain.entities.Time;
import com.josemgu91.habittune.domain.usecases.GetRoutineEntriesByDate;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

public class FragmentSchedule extends BaseFragment {

    private ViewModelSchedule viewModelSchedule;
    private FragmentScheduleBinding fragmentScheduleBinding;

    private FlexibleAdapter<ActivityItem> activityItemFlexibleAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        viewModelSchedule = ViewModelProviders.of(this, viewModelFactory).get(ViewModelSchedule.class);
    }

    @Override
    protected ToolbarOptions createToolbarOptions() {
        return new ToolbarOptions(true);
    }

    @NonNull
    @Override
    public View createView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentScheduleBinding = FragmentScheduleBinding.inflate(inflater, container, false);
        activityItemFlexibleAdapter = new FlexibleAdapter<>(null, (FlexibleAdapter.OnItemClickListener) (view, position) -> {
            markAssistance(activityItemFlexibleAdapter.getItem(position));
            return true;
        });
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
        final Date currentDate = new Date();
        viewModelSchedule.fetchRoutines(currentDate);
        viewModelSchedule.getGetRoutineEntriesByDateResponse().observe(getViewLifecycleOwner(), response -> {
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

    private void onRoutinesUpdated(final List<GetRoutineEntriesByDate.Output> routineEntries) {
        final ArrayList<ActivityItem> activityItems = new ArrayList<>();
        for (GetRoutineEntriesByDate.Output routineEntry : routineEntries) {
            activityItems.add(new ActivityItem(
                    routineEntry.getId(),
                    routineEntry.getActivity().getName(),
                    routineEntry.getStartTime(),
                    routineEntry.getEndTime(),
                    routineEntry.getCycleNumber(),
                    routineEntry.getAssistanceRegisterLiveData(),
                    getViewLifecycleOwner()
            ));
            activityItemFlexibleAdapter.updateDataSet(activityItems);
        }
    }

    private void markAssistance(final ActivityItem activityItem) {
        try {
            viewModelSchedule.registerAssistance(
                    activityItem.routineEntryId,
                    activityItem.cycleNumber,
                    new Time(3600),
                    null
            );
        } catch (DomainException e) {
            e.printStackTrace();
        }
    }

    private static class ActivityItem extends AbstractFlexibleItem<ActivityItem.ViewHolder> {

        public final static int UNDEFINED_ASSISTANCE_TIME = -1;
        private final static String ASSISTANCE_CHAR = "\u2713";

        @NonNull
        private final String routineEntryId;
        @NonNull
        private final String activityName;
        private final int activityStartHour;
        private final int activityEndHour;
        private final int cycleNumber;
        private final LiveData<GetRoutineEntriesByDate.Output.AssistanceRegister> assistanceRegisterLiveData;
        private final LifecycleOwner lifecycleOwner;

        public ActivityItem(@NonNull String routineEntryId, @NonNull String activityName, int activityStartHour, int activityEndHour, int cycleNumber, LiveData<GetRoutineEntriesByDate.Output.AssistanceRegister> assistanceRegisterLiveData, LifecycleOwner lifecycleOwner) {
            this.routineEntryId = routineEntryId;
            this.activityName = activityName;
            this.activityStartHour = activityStartHour;
            this.activityEndHour = activityEndHour;
            this.cycleNumber = cycleNumber;
            this.assistanceRegisterLiveData = assistanceRegisterLiveData;
            this.lifecycleOwner = lifecycleOwner;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ActivityItem that = (ActivityItem) o;
            return activityStartHour == that.activityStartHour &&
                    activityEndHour == that.activityEndHour &&
                    cycleNumber == that.cycleNumber &&
                    Objects.equals(routineEntryId, that.routineEntryId) &&
                    Objects.equals(activityName, that.activityName) &&
                    Objects.equals(assistanceRegisterLiveData, that.assistanceRegisterLiveData) &&
                    Objects.equals(lifecycleOwner, that.lifecycleOwner);
        }

        @Override
        public int hashCode() {
            return Objects.hash(routineEntryId, activityName, activityStartHour, activityEndHour, cycleNumber, assistanceRegisterLiveData, lifecycleOwner);
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
            holder.textViewStartHour.setText(formatHour(activityStartHour));
            holder.textViewEndHour.setText(formatHour(activityEndHour));
            assistanceRegisterLiveData.observe(lifecycleOwner, assistanceRegister -> {
                if (assistanceRegister.getStartTime() == UNDEFINED_ASSISTANCE_TIME) {
                    holder.textViewAssistance.setText("");
                    return;
                }
                if (assistanceRegister.getEndTime() == UNDEFINED_ASSISTANCE_TIME) {
                    holder.textViewAssistance.setText(ASSISTANCE_CHAR);
                    return;
                }
                holder.textViewAssistance.setText(String.format("%s%s", ASSISTANCE_CHAR, ASSISTANCE_CHAR));
            });
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

        public static class ViewHolder extends FlexibleViewHolder {

            private final TextView textViewStartHour;
            private final TextView textViewEndHour;
            private final TextView textViewName;
            private final TextView textViewAssistance;

            public ViewHolder(View view, FlexibleAdapter adapter) {
                super(view, adapter);
                textViewStartHour = view.findViewById(R.id.textViewActivityStartHour);
                textViewEndHour = view.findViewById(R.id.textViewActivityEndHour);
                textViewName = view.findViewById(R.id.textViewActivityName);
                textViewAssistance = view.findViewById(R.id.textViewAssistance);
            }
        }

    }
}
