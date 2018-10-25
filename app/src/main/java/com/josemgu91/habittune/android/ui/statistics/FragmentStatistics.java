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

package com.josemgu91.habittune.android.ui.statistics;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.FragmentInteractionListener;
import com.josemgu91.habittune.android.ui.BaseFragment;
import com.josemgu91.habittune.android.ui.Response;
import com.josemgu91.habittune.android.ui.common.DateFormatter;
import com.josemgu91.habittune.databinding.FragmentStatisticsBinding;
import com.josemgu91.habittune.domain.usecases.CalculateAssistanceStatistics;

import java.util.ArrayList;
import java.util.List;

public class FragmentStatistics extends BaseFragment {

    private final static String ARG_ACTIVITY_ID = "activityId";

    public static FragmentStatistics newInstance(@NonNull final String activityId) {
        Bundle args = new Bundle();
        args.putString(ARG_ACTIVITY_ID, activityId);
        FragmentStatistics fragment = new FragmentStatistics();
        fragment.setArguments(args);
        return fragment;
    }

    private String activityId;

    private ViewModelStatistics viewModelStatistics;
    private FragmentStatisticsBinding fragmentStatisticsBinding;

    private DateFormatter dateFormatter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        viewModelStatistics = ViewModelProviders.of(this, viewModelFactory).get(ViewModelStatistics.class);
        final Bundle arguments = getArguments();
        activityId = arguments.getString(ARG_ACTIVITY_ID);
        dateFormatter = new DateFormatter();
    }

    @Override
    protected ToolbarOptions createToolbarOptions() {
        return new ToolbarOptions(true);
    }

    @NonNull
    @Override
    public View createView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentStatisticsBinding = FragmentStatisticsBinding.inflate(inflater, container, false);
        return fragmentStatisticsBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModelStatistics.fetchActivity(activityId);
        viewModelStatistics.getGetActivityResponse().observe(getViewLifecycleOwner(), response -> {
            if (response.status != Response.Status.SUCCESS) {
                return;
            }
            fragmentInteractionListener.updateToolbar(response.successData.getName(), FragmentInteractionListener.IC_NAVIGATION_UP);
        });
        viewModelStatistics.calculateAssistanceStats(activityId);
        viewModelStatistics.getCalculateAssistanceStatsResponse().observe(getViewLifecycleOwner(), response -> {
            if (response.status != Response.Status.SUCCESS) {
                return;
            }
            renderAssistanceStatistics(response.successData);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentInteractionListener.updateNavigationDrawer(false);
    }

    private void renderAssistanceStatistics(CalculateAssistanceStatistics.Output assistanceStatistics) {
        final int totalEvents = assistanceStatistics.getTotalEvents();
        final int missedEvents = assistanceStatistics.getEventsMissed();
        final int completedEvents = totalEvents - missedEvents;
        final int averageStartErrorInSeconds = assistanceStatistics.getAverageStartTimeErrorInSeconds();
        final int averageEndErrorInSeconds = assistanceStatistics.getAverageEndTimeErrorInSeconds();
        final String formattedUpdateDate = dateFormatter.formatDate(assistanceStatistics.getUpToDate());

        final String totalEventsText = getString(R.string.statistics_pie_events_total, totalEvents);
        final String upToDateText = getString(R.string.statistics_pie_up_to_date, formattedUpdateDate);
        final int averageStartErrorInMinutes = averageStartErrorInSeconds / 60;
        final int averageEndErrorInMinutes = averageEndErrorInSeconds / 60;
        final String averageStartErrorText = getString(R.string.statistics_pie_variation_start, averageStartErrorInMinutes);
        final String averageEndErrorText = getString(R.string.statistics_pie_variation_end, averageEndErrorInMinutes);
        final String averageVariationLabel = getString(R.string.statistics_pie_variation_average_label);

        final List<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(completedEvents, getString(R.string.statistics_pie_events_completed_label)));
        pieEntries.add(new PieEntry(missedEvents, getString(R.string.statistics_pie_events_missed_label)));
        final PieDataSet pieDataSet = new PieDataSet(pieEntries, upToDateText);
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        final PieData pieData = new PieData(pieDataSet);

        fragmentStatisticsBinding.pieChart.setData(pieData);
        final Description description = new Description();
        description.setText(
                new StringBuilder()
                        .append(averageVariationLabel)
                        .append(" ")
                        .append(averageStartErrorText)
                        .append(" ")
                        .append(averageEndErrorText)
                        .toString()
        );
        fragmentStatisticsBinding.pieChart.setDescription(description);
        fragmentStatisticsBinding.pieChart.setCenterText(totalEventsText);
        fragmentStatisticsBinding.pieChart.invalidate();
    }
}
