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
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class WidgetProviderStatistics extends AppWidgetProvider {

    public final static String OPTION_ACTIVITY_ID = "activityId";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final List<Integer> appWidgetsIdsToUpdateList = new ArrayList<>();
        for (final int appWidgetId : appWidgetIds) {
            final Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
            if (options.containsKey(OPTION_ACTIVITY_ID)) {
                appWidgetsIdsToUpdateList.add(appWidgetId);
            }
        }
        if (appWidgetsIdsToUpdateList.size() == 0) {
            return;
        }
        final int[] appWidgetsIdsToUpdateArray = new int[appWidgetsIdsToUpdateList.size()];
        for (int i = 0; i < appWidgetsIdsToUpdateList.size(); i++) {
            appWidgetsIdsToUpdateArray[i] = appWidgetsIdsToUpdateList.get(i);
        }
        ServiceWidgetStatistics.update(context, appWidgetsIdsToUpdateArray);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        ServiceWidgetStatistics.update(context, new int[]{appWidgetId});
    }
}