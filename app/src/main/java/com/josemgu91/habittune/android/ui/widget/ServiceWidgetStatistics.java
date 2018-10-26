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

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
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

public class ServiceWidgetStatistics extends IntentService {

    private static final String FOREGROUND_NOTIFICATION_CHANNEL_ID = "ServiceWidgetStatistics";
    private static final int FOREGROUND_NOTIFICATION_ID = 1;

    public final static String ARG_WIDGET_IDS = "widgetIds";

    private AppWidgetManager appWidgetManager;
    private int[] widgetIds;

    private final static int GRAPH_WIDTH_PX = 1024;
    private final static int GRAPH_HEIGHT_PX = 1024;

    public ServiceWidgetStatistics() {
        super("ServiceWidgetStatistics");
    }

    public static void update(@NonNull final Context context, @NonNull final int[] appWidgetIds) {
        final Intent intent = new Intent(context, ServiceWidgetStatistics.class);
        intent.putExtra(ARG_WIDGET_IDS, appWidgetIds);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appWidgetManager = AppWidgetManager.getInstance(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final NotificationChannel notificationChannel = new NotificationChannel(
                    FOREGROUND_NOTIFICATION_CHANNEL_ID,
                    getString(R.string.notification_channel_widget_update),
                    NotificationManager.IMPORTANCE_MIN
            );
            final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            } else {
                throw new IllegalStateException("NotificationManager is null!");
            }
            final Notification notification = new NotificationCompat.Builder(this, FOREGROUND_NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(getString(R.string.widget_statistics_notification_update_title))
                    .setContentText(getString(R.string.widget_statistics_notification_update_content))
                    .setSmallIcon(R.drawable.ic_notification_widget_update)
                    .build();
            startForeground(FOREGROUND_NOTIFICATION_ID, notification);
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        checkIntent(intent);
        final Bundle intentExtras = intent.getExtras();
        widgetIds = intentExtras.getIntArray(ARG_WIDGET_IDS);
        updateWidgets();
    }

    private void checkIntent(final Intent intent) {
        if (intent == null) {
            throw new IllegalArgumentException("Intent is null!");
        }
        final Bundle intentExtras = intent.getExtras();
        if (intentExtras == null) {
            throw new IllegalArgumentException("Intent extras is null!");
        }
        if (!intentExtras.containsKey(ARG_WIDGET_IDS)) {
            throw new IllegalStateException("The Intent hasn't the ARG_WIDGET_IDS key!");
        }
    }

    private void updateWidgets() {
        //TODO: Is there a way to get the RemoteView size?
        final Bitmap statisticalGraphicsBitmap = createStatisticalGraphic(GRAPH_WIDTH_PX, GRAPH_HEIGHT_PX);
        for (final int appWidgetId : widgetIds) {
            final Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
            final String activityId = options.getString(WidgetProviderStatistics.OPTION_ACTIVITY_ID);
            renderWidget(appWidgetId, statisticalGraphicsBitmap, "Activity Id: " + activityId);
        }
    }

    private Bitmap createStatisticalGraphic(final int widthPx, final int heightPx) {
        final List<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(0.5f, "Test Data 1"));
        pieEntries.add(new PieEntry(0.5f, "Test Data 2"));
        final PieDataSet pieDataSet = new PieDataSet(pieEntries, "Test Data Set");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        final PieData pieData = new PieData(pieDataSet);
        final PieChart pieChart = new PieChart(this);
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

    private void renderWidget(final int widgetId, final Bitmap bitmap, final String activityName) {
        final RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget_statistics);
        remoteViews.setBitmap(R.id.imageViewStatisticalGraphics, "setImageBitmap", bitmap);
        remoteViews.setTextViewText(R.id.textViewActivityName, activityName);
        appWidgetManager.updateAppWidget(widgetId, remoteViews);
    }

    private void finishService() {
        stopSelf();
    }
}
