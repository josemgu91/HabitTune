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

import android.content.Context;
import androidx.annotation.NonNull;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.ui.common.DateFormatter;
import com.josemgu91.habittune.domain.usecases.CalculateAssistanceStatistics;

import java.util.ArrayList;
import java.util.List;

public class ChartHelper {

    @NonNull
    private final Context context;
    @NonNull
    private final DateFormatter dateFormatter;

    public ChartHelper(@NonNull Context context, @NonNull DateFormatter dateFormatter) {
        this.context = context;
        this.dateFormatter = dateFormatter;
    }

    public void populateChart(@NonNull final PieChart pieChart, @NonNull final CalculateAssistanceStatistics.Output assistanceStatistics) {
        final int totalEvents = assistanceStatistics.getTotalEvents();
        final int missedEvents = assistanceStatistics.getEventsMissed();
        final int completedEvents = totalEvents - missedEvents;
        final int averageStartErrorInSeconds = assistanceStatistics.getAverageStartTimeErrorInSeconds();
        final int averageEndErrorInSeconds = assistanceStatistics.getAverageEndTimeErrorInSeconds();
        final String formattedUpdateDate = dateFormatter.formatDate(assistanceStatistics.getUpToDate());

        final String totalEventsText = context.getString(R.string.statistics_pie_events_total, totalEvents);
        final String upToDateText = context.getString(R.string.statistics_pie_up_to_date, formattedUpdateDate);
        final int averageStartErrorInMinutes = averageStartErrorInSeconds / 60;
        final int averageEndErrorInMinutes = averageEndErrorInSeconds / 60;
        final String averageStartErrorText = context.getString(R.string.statistics_pie_variation_start, averageStartErrorInMinutes);
        final String averageEndErrorText = context.getString(R.string.statistics_pie_variation_end, averageEndErrorInMinutes);
        final String averageVariationLabel = context.getString(R.string.statistics_pie_variation_average_label);

        final List<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(completedEvents, context.getString(R.string.statistics_pie_events_completed_label)));
        pieEntries.add(new PieEntry(missedEvents, context.getString(R.string.statistics_pie_events_missed_label)));
        final PieDataSet pieDataSet = new PieDataSet(pieEntries, upToDateText);
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        final PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
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
        pieChart.setDescription(description);
        pieChart.setCenterText(totalEventsText);
        pieChart.invalidate();
    }

}
