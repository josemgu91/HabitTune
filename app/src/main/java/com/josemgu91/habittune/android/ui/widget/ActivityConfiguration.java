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

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.josemgu91.habittune.R;
import com.josemgu91.habittune.databinding.ActivityWidgetStatisticsConfigurationBinding;

public class ActivityConfiguration extends AppCompatActivity {

    ActivityWidgetStatisticsConfigurationBinding activityWidgetStatisticsConfigurationBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityWidgetStatisticsConfigurationBinding = DataBindingUtil.setContentView(this, R.layout.activity_widget_statistics_configuration);
        setSupportActionBar(activityWidgetStatisticsConfigurationBinding.toolbar);
    }
}
