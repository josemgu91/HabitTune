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

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
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

    private FlexibleAdapter<ActivityItem> flexibleAdapterActivities;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
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
        final ViewModelActivitySelection viewModelActivitySelection = ViewModelProviders.of(this, viewModelFactory).get(ViewModelActivitySelection.class);
        viewModelActivitySelection.fetchActivities();
        viewModelActivitySelection.getGetActivitiesResponse().observe(this, response -> {
            if (response.status != Response.Status.SUCCESS) {
                return;
            }
            response.successData.observe(ActivityStatisticsWidgetConfiguration.this, this::updateActivities);
        });
    }

    private void onActivitySelected(final ActivityItem activityItem) {
        final Intent intentThatStartedThisActivity = getIntent();
        final Bundle intentExtras = intentThatStartedThisActivity.getExtras();
        if (intentExtras == null) {
            finish();
            return;
        }
        final int appWidgetId = intentExtras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
        final Intent resultIntent = new Intent();
        resultIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        final Bundle options = new Bundle();
        options.putString(WidgetProviderStatistics.OPTION_ACTIVITY_ID, activityItem.getId());
        appWidgetManager.updateAppWidgetOptions(appWidgetId, options);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void updateActivities(List<GetActivity.Output> outputs) {
        if (outputs.size() == 0) {
            activityWidgetStatisticsConfigurationBinding.setShowWarning(true);
            activityWidgetStatisticsConfigurationBinding.textViewWarning.setText(R.string.activities_empty);
            return;
        }
        activityWidgetStatisticsConfigurationBinding.setShowWarning(false);
        final List<ActivityItem> activities = new ArrayList<>();
        for (final GetActivity.Output output : outputs) {
            activities.add(new ActivityItem(output.getId(), output.getName(), output.getColor()));
        }
        flexibleAdapterActivities.updateDataSet(activities);
    }
}
