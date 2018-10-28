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

package com.josemgu91.habittune.android;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.navigation.FragmentKey;
import com.josemgu91.habittune.android.navigation.FragmentKeyFactory;
import com.josemgu91.habittune.android.navigation.FragmentStateChanger;
import com.josemgu91.habittune.android.ui.help.TourActivity;
import com.josemgu91.habittune.android.ui.widget.WidgetProviderStatistics;
import com.josemgu91.habittune.databinding.ActivityMainBinding;
import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.History;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;

public class ActivityMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentInteractionListener, StateChanger {

    private DrawerLayout drawerLayout;

    private BackstackDelegate backstackDelegate;
    private FragmentStateChanger fragmentStateChanger;

    private NavigationView navigationView;

    private FragmentKeyFactory fragmentKeyFactory;

    private InputMethodManager inputMethodManager;

    private FirebaseAnalytics firebaseAnalytics;

    public final static String OPT_ARG_ACTIVITY_ID = "activityId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        fragmentKeyFactory = new FragmentKeyFactory();
        backstackDelegate = new BackstackDelegate(null);
        backstackDelegate.onCreate(savedInstanceState,
                getLastCustomNonConfigurationInstance(),
                History.single(fragmentKeyFactory.createScheduleKey()));
        backstackDelegate.registerForLifecycleCallbacks(this);

        super.onCreate(savedInstanceState);

        final ActivityMainBinding activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        drawerLayout = activityMainBinding.drawerLayout;
        navigationView = activityMainBinding.navigationView;
        navigationView.setNavigationItemSelectedListener(this);

        fragmentStateChanger = new FragmentStateChanger(getSupportFragmentManager(), R.id.fragmentContainer, new FragmentKeyFactory.FragmentFactory());
        backstackDelegate.setStateChanger(this);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        final Intent intentThatStartedThisActivity = getIntent();
        handleActivityRedirectionIntent(intentThatStartedThisActivity);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleActivityRedirectionIntent(intent);
    }

    private void handleActivityRedirectionIntent(final Intent intent) {
        if (!intent.hasExtra(OPT_ARG_ACTIVITY_ID)) {
            return;
        }
        final String activityId = intent.getStringExtra(OPT_ARG_ACTIVITY_ID);
        navigateToFragmentStatistics(activityId);
        intent.removeExtra(OPT_ARG_ACTIVITY_ID);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }
        goBack();
    }

    private void goBack() {
        hideSoftKeyboard();
        if (!backstackDelegate.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return backstackDelegate.onRetainCustomNonConfigurationInstance();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigationMenuGoToSchedule:
                logAnalyticsNavigationEvent("schedule");
                backstackDelegate.getBackstack().jumpToRoot();
                break;
            case R.id.navigationMenuGoToRoutines:
                logAnalyticsNavigationEvent("routines");
                backstackDelegate.getBackstack().setHistory(History.of(fragmentKeyFactory.createScheduleKey(), fragmentKeyFactory.createRoutinesKey()), StateChange.REPLACE);
                break;
            case R.id.navigationMenuGoToActivities:
                logAnalyticsNavigationEvent("activities");
                backstackDelegate.getBackstack().setHistory(History.of(fragmentKeyFactory.createScheduleKey(), fragmentKeyFactory.createActivitiesKey()), StateChange.REPLACE);
                break;
            case R.id.navigationMenuGoToStatistics:
                logAnalyticsNavigationEvent("statisticsActivitySelection");
                backstackDelegate.getBackstack().setHistory(History.of(fragmentKeyFactory.createScheduleKey(), fragmentKeyFactory.createStatisticsActivitySelectionKey()), StateChange.REPLACE);
                break;
            case R.id.navigationMenuGoToSettings:
                logAnalyticsNavigationEvent("settings");
                backstackDelegate.getBackstack().goTo(fragmentKeyFactory.createSettingsKey());
                break;
            case R.id.navigationMenuGoToHelp:
                logAnalyticsNavigationEvent("help");
                startActivity(new Intent(this, TourActivity.class));
                break;
        }
        drawerLayout.closeDrawers();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!(item.getItemId() == android.R.id.home)) {
            return super.onOptionsItemSelected(item);
        }
        if (drawerLayout.getDrawerLockMode(GravityCompat.START) == DrawerLayout.LOCK_MODE_LOCKED_CLOSED) {
            goBack();
            return true;
        }
        drawerLayout.openDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
        Log.d("ActivityMain", "State change: " +
                (stateChange.getDirection() == StateChange.REPLACE ? "Replace" :
                        stateChange.getDirection() == StateChange.BACKWARD ? "Backward" :
                                stateChange.getDirection() == StateChange.FORWARD ? "Forward" :
                                        "Unknown")
        );
        if (stateChange.topNewState().equals(stateChange.topPreviousState())) {
            completionCallback.stateChangeComplete();
            return;
        }
        final FragmentKey topStateKey = stateChange.topNewState();
        switch (topStateKey.getFragmentTag()) {
            case FragmentKeyFactory.FRAGMENT_TAG_SCHEDULE:
                navigationView.setCheckedItem(R.id.navigationMenuGoToSchedule);
                break;
            case FragmentKeyFactory.FRAGMENT_TAG_ROUTINES:
                navigationView.setCheckedItem(R.id.navigationMenuGoToRoutines);
                break;
            case FragmentKeyFactory.FRAGMENT_TAG_ACTIVITIES:
                navigationView.setCheckedItem(R.id.navigationMenuGoToActivities);
                break;
            case FragmentKeyFactory.FRAGMENT_TAG_STATISTICS:
                navigationView.setCheckedItem(R.id.navigationMenuGoToStatistics);
                break;
            case FragmentKeyFactory.FRAGMENT_TAG_SETTINGS:
                navigationView.setCheckedItem(R.id.navigationMenuGoToSettings);
                break;
        }
        fragmentStateChanger.handleStateChange(stateChange);
        completionCallback.stateChangeComplete();
    }

    @Override
    public void updateToolbar(String title, int toolbarToggleIcon) {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);
        switch (toolbarToggleIcon) {
            case FragmentInteractionListener.IC_NAVIGATION_CLOSE:
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(R.drawable.ic_navigation_close_black_24dp);
                break;
            case FragmentInteractionListener.IC_NAVIGATION_HAMBURGUER:
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(R.drawable.ic_navigation_menu_black_24dp);
                break;
            case FragmentInteractionListener.IC_NAVIGATION_UP:
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(R.drawable.ic_navigation_up_black_24dp);
                break;
            default:
                throw new RuntimeException("Unknown toolbarToggleIcon constant");
        }
    }

    @Override
    public void updateNavigationDrawer(boolean allowOpening) {
        if (allowOpening) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    @Override
    public void navigateToFragmentNewActivity() {
        logAnalyticsNavigationEvent("newActivity");
        backstackDelegate.getBackstack().goTo(fragmentKeyFactory.createNewActivityKey());
    }

    @Override
    public void navigateToFragmentUpdateActivity(@NonNull String activityId) {
        logAnalyticsNavigationEvent("updateActivity");
        backstackDelegate.getBackstack().goTo(fragmentKeyFactory.createUpdateActivityKey(activityId));
    }

    @Override
    public void navigateToFragmentNewRoutine() {
        logAnalyticsNavigationEvent("newRoutine");
        backstackDelegate.getBackstack().goTo(fragmentKeyFactory.createNewRoutineKey());
    }

    @Override
    public void navigateToFragmentUpdateRoutine(@NonNull String routineId) {
        logAnalyticsNavigationEvent("updateRoutine");
        backstackDelegate.getBackstack().goTo(fragmentKeyFactory.createUpdateRoutineKey(routineId));
    }

    @Override
    public void navigateToFragmentTagEditor() {
        logAnalyticsNavigationEvent("tagEditor");
        backstackDelegate.getBackstack().goTo(fragmentKeyFactory.createTagEditorKey());
    }

    @Override
    public void navigateToFragmentRoutineEditor(@NonNull String routineId) {
        logAnalyticsNavigationEvent("routineEditor");
        backstackDelegate.getBackstack().goTo(fragmentKeyFactory.createRoutineEditorKey(routineId));
    }

    @Override
    public void navigateToFragmentAddRoutineEntry(@NonNull String routineId, int routineDay) {
        logAnalyticsNavigationEvent("addRoutineEntry");
        backstackDelegate.getBackstack().goTo(fragmentKeyFactory.createAddRoutineEntryKey(routineId, routineDay));
    }

    @Override
    public void navigateToFragmentActivitySelection() {
        logAnalyticsNavigationEvent("activitySelection");
        backstackDelegate.getBackstack().goTo(fragmentKeyFactory.createFragmentActivitySelection());
    }

    @Override
    public void navigateToFragmentStatistics(@NonNull String activityId) {
        logAnalyticsNavigationEvent("statisticsShowActivity");
        backstackDelegate.getBackstack().goTo(fragmentKeyFactory.createStatisticsKey(activityId));
    }

    private void logAnalyticsNavigationEvent(final String id) {
        final Bundle bundle = new Bundle();
        bundle.putString("screenId", id);
        firebaseAnalytics.logEvent("navigation", bundle);
    }

    @Override
    public void finishFragment() {
        goBack();
    }

    @Override
    public void setToolbar(final Toolbar toolbar) {
        setSupportActionBar(null);
        setSupportActionBar(toolbar);
    }

    @Override
    public void removeToolbar() {
        setSupportActionBar(null);
    }

    @Override
    public void hideSoftKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    @Override
    public void updateWidgets() {
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        final int[] widgetIds = appWidgetManager
                .getAppWidgetIds(new ComponentName(getApplication(), WidgetProviderStatistics.class));
        final Intent intent = new Intent(this, WidgetProviderStatistics.class)
                .setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
        sendBroadcast(intent);
    }
}
