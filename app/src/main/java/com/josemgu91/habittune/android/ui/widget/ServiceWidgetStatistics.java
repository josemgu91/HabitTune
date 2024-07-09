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
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.github.mikephil.charting.charts.PieChart;
import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.ActivityMain;
import com.josemgu91.habittune.android.Application;
import com.josemgu91.habittune.android.ui.common.DateFormatter;
import com.josemgu91.habittune.android.ui.statistics.ChartHelper;
import com.josemgu91.habittune.domain.usecases.CalculateAssistanceStatistics;
import com.josemgu91.habittune.domain.usecases.GetActivity;
import com.josemgu91.habittune.domain.usecases.common.UseCaseOutput;

import java.util.concurrent.CountDownLatch;

public class ServiceWidgetStatistics extends IntentService {

    private static final String FOREGROUND_NOTIFICATION_CHANNEL_ID = "ServiceWidgetStatistics";
    private static final int FOREGROUND_NOTIFICATION_ID = 1;

    public final static String ARG_WIDGET_IDS = "widgetIds";

    private AppWidgetManager appWidgetManager;

    private final static int GRAPH_WIDTH_PX = 1024;
    private final static int GRAPH_HEIGHT_PX = 1024;

    public final static String TAG = "ServiceWidgetStatistics";

    private ChartHelper chartHelper;

    private CalculateAssistanceStatistics calculateAssistanceStatistics;
    private GetActivity getActivity;

    public static void start(@NonNull final Context context, @NonNull final int[] appWidgetIds) {
        final Intent intent = new Intent(context, ServiceWidgetStatistics.class);
        intent.putExtra(ARG_WIDGET_IDS, appWidgetIds);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public ServiceWidgetStatistics() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
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
        final Application application = (Application) getApplicationContext();
        calculateAssistanceStatistics = application.getUseCaseFactory().createCalculateAssistanceStatistics();
        getActivity = application.getUseCaseFactory().createGetActivity();
        appWidgetManager = AppWidgetManager.getInstance(this);
        chartHelper = new ChartHelper(this, new DateFormatter());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (isIntentValid(intent)) {
            final Bundle intentExtras = intent.getExtras();
            final int[] widgetIds = intentExtras.getIntArray(ARG_WIDGET_IDS);
            updateWidgets(widgetIds);
        }
    }

    private boolean isIntentValid(final Intent intent) {
        if (intent == null) {
            return false;
        }
        final Bundle intentExtras = intent.getExtras();
        if (intentExtras == null) {
            return false;
        }
        if (!intentExtras.containsKey(ARG_WIDGET_IDS)) {
            return false;
        }
        return true;
    }

    private void updateWidgets(final int[] widgetIds) {
        for (final int appWidgetId : widgetIds) {
            updateWidget(appWidgetId);
        }
    }

    private void updateWidget(final int widgetId) {
        final Bundle options = appWidgetManager.getAppWidgetOptions(widgetId);
        final String activityId = options.getString(WidgetProviderStatistics.OPTION_ACTIVITY_ID);
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        final Object[] responses = new Object[2];
        calculateAssistanceStatistics.execute(new CalculateAssistanceStatistics.Input(activityId), new UseCaseOutput<CalculateAssistanceStatistics.Output>() {
            @Override
            public void onSuccess(@Nullable CalculateAssistanceStatistics.Output output) {
                //Warning: This runs on UI thread. The Bitmap generation can potentially block it.
                //TODO: Is there a way to get the RemoteView size?
                final Bitmap statisticalGraphicsBitmap = createStatisticalGraphic(output, GRAPH_WIDTH_PX, GRAPH_HEIGHT_PX);
                responses[0] = statisticalGraphicsBitmap;
                countDownLatch.countDown();
            }

            @Override
            public void inProgress() {
            }

            @Override
            public void onError() {
                countDownLatch.countDown();
            }
        });
        getActivity.execute(new GetActivity.Input(activityId), new UseCaseOutput<GetActivity.Output>() {
            @Override
            public void onSuccess(@Nullable GetActivity.Output output) {
                responses[1] = output.getName();
                countDownLatch.countDown();
            }

            @Override
            public void inProgress() {

            }

            @Override
            public void onError() {
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final Bitmap chartBitmap = (Bitmap) responses[0];
        final String activityName = (String) responses[1];
        if (chartBitmap != null && activityName != null) {
            updateRemoteView(widgetId, chartBitmap, activityName, activityId);
        } else {
            updateRemoteViewActivityNotFound(widgetId);
        }
    }

    private Bitmap createStatisticalGraphic(final CalculateAssistanceStatistics.Output assistanceStatistics, final int widthPx, final int heightPx) {
        final PieChart pieChart = new PieChart(this);
        chartHelper.populateChart(pieChart, assistanceStatistics);
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

    private void updateRemoteView(final int widgetId, final Bitmap bitmap, final String activityName, final String activityId) {
        final RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget_statistics);
        remoteViews.setViewVisibility(R.id.textViewActivityDeletedMessage, View.GONE);
        remoteViews.setViewVisibility(R.id.linearLayoutResult, View.VISIBLE);
        remoteViews.setBitmap(R.id.imageViewStatisticalGraphics, "setImageBitmap", bitmap);
        remoteViews.setTextViewText(R.id.textViewActivityName, activityName);
        //Request code must be unique because the PendingIntent doesn't difference the Intent extras.
        remoteViews.setOnClickPendingIntent(R.id.linearLayout, PendingIntent.getActivity(
                this,
                Integer.valueOf(activityId),
                new Intent(this, ActivityMain.class)
                        .putExtra(ActivityMain.OPT_ARG_ACTIVITY_ID, activityId),
                PendingIntent.FLAG_UPDATE_CURRENT
        ));
        appWidgetManager.updateAppWidget(widgetId, remoteViews);
    }

    private void updateRemoteViewActivityNotFound(final int widgetId) {
        final RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget_statistics);
        remoteViews.setViewVisibility(R.id.textViewActivityDeletedMessage, View.VISIBLE);
        remoteViews.setViewVisibility(R.id.linearLayoutResult, View.GONE);
        appWidgetManager.updateAppWidget(widgetId, remoteViews);
    }
}
