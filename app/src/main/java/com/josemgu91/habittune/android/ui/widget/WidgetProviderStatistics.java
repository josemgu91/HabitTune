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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.widget.RemoteViews;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.josemgu91.habittune.R;

import java.util.ArrayList;
import java.util.List;

public class WidgetProviderStatistics extends AppWidgetProvider {

    private final static int GRAPH_WIDTH_PX = 1024;
    private final static int GRAPH_HEIGHT_PX = 1024;

    public final static String ARG_ACTIVITY_ID = "activityId";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //TODO: Is there a way to get the RemoteView size?
        final Bitmap statisticalGraphicsBitmap = createStatisticalGraphic(context, GRAPH_WIDTH_PX, GRAPH_HEIGHT_PX);
        for (final int appWidgetId : appWidgetIds) {
            renderWidget(context, appWidgetManager, appWidgetId, statisticalGraphicsBitmap, "Test");
        }
    }

    private Bitmap createStatisticalGraphic(final Context context, final int widthPx, final int heightPx) {
        final List<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(0.5f, "Test Data 1"));
        pieEntries.add(new PieEntry(0.5f, "Test Data 2"));
        final PieDataSet pieDataSet = new PieDataSet(pieEntries, "Test Data Set");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        final PieData pieData = new PieData(pieDataSet);
        final PieChart pieChart = new PieChart(context);
        pieChart.setData(pieData);
        pieChart.invalidate();
        final int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(widthPx, View.MeasureSpec.EXACTLY);
        final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(heightPx, View.MeasureSpec.EXACTLY);
        pieChart.measure(widthMeasureSpec, heightMeasureSpec);
        final int measuredWidth = pieChart.getMeasuredWidth();
        final int measuredHeight = pieChart.getMeasuredHeight();
        pieChart.layout(0, 0, measuredWidth, measuredHeight);
        final Bitmap bitmap = Bitmap.createBitmap(
                measuredWidth,
                measuredHeight,
                Bitmap.Config.ARGB_8888
        );
        final Canvas canvas = new Canvas(bitmap);
        pieChart.draw(canvas);
        return bitmap;
    }

    private void renderWidget(final Context context, final AppWidgetManager appWidgetManager, final int widgetId, final Bitmap bitmap, final String activityName) {
        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_statistics);
        remoteViews.setBitmap(R.id.imageViewStatisticalGraphics, "setImageBitmap", bitmap);
        remoteViews.setTextViewText(R.id.textViewActivityName, activityName);
        appWidgetManager.updateAppWidget(widgetId, remoteViews);
    }
}
