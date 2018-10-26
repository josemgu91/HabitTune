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

package com.josemgu91.habittune.android.ui.widget;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.Application;
import com.josemgu91.habittune.android.ui.Response;
import com.josemgu91.habittune.android.ui.activities.ActivityItem;
import com.josemgu91.habittune.android.ui.activity_selection.ViewModelActivitySelection;
import com.josemgu91.habittune.databinding.ActivityWidgetStatisticsConfigurationBinding;
import com.josemgu91.habittune.domain.usecases.GetActivity;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;

public class ActivityStatisticsWidgetConfiguration extends AppCompatActivity {

    ActivityWidgetStatisticsConfigurationBinding activityWidgetStatisticsConfigurationBinding;

    private ViewModelActivitySelection viewModelActivitySelection;

    private FlexibleAdapter<ActivityItem> flexibleAdapterActivities;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityWidgetStatisticsConfigurationBinding = DataBindingUtil.setContentView(this, R.layout.activity_widget_statistics_configuration);
        setSupportActionBar(activityWidgetStatisticsConfigurationBinding.toolbar);
        setTitle(R.string.activity_selection_title);
        flexibleAdapterActivities = new FlexibleAdapter<>(null, (FlexibleAdapter.OnItemClickListener) (view, position) -> {
            onActivitySelected(flexibleAdapterActivities.getItem(position));
            return true;
        });
        activityWidgetStatisticsConfigurationBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        activityWidgetStatisticsConfigurationBinding.recyclerView.setAdapter(flexibleAdapterActivities);
        final Application application = (Application) getApplication();
        final ViewModelProvider.Factory viewModelFactory = application.getViewModelFactory();
        viewModelActivitySelection = ViewModelProviders.of(this, viewModelFactory).get(ViewModelActivitySelection.class);
        viewModelActivitySelection.fetchActivities();
        viewModelActivitySelection.getGetActivitiesResponse().observe(this, response -> {
            if (response.status != Response.Status.SUCCESS) {
                return;
            }
            response.successData.observe(ActivityStatisticsWidgetConfiguration.this, this::updateActivities);
        });
    }

    private void onActivitySelected(final ActivityItem activityItem) {

    }

    private void updateActivities(List<GetActivity.Output> outputs) {
        final List<ActivityItem> activities = new ArrayList<>();
        for (final GetActivity.Output output : outputs) {
            activities.add(new ActivityItem(output.getId(), output.getName()));
        }
        flexibleAdapterActivities.updateDataSet(activities);
    }
}
