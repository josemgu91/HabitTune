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

package com.josemgu91.habittune;

import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class ActivityMainController implements NavigationRouter, FragmentManager.OnBackStackChangedListener {

    private static final String FRAGMENT_TAG_SCHEDULE = "fragment_schedule";
    private static final String FRAGMENT_TAG_ROUTINES = "fragment_routines";
    private static final String FRAGMENT_TAG_ACTIVITIES = "fragment_activities";
    private static final String FRAGMENT_TAG_STATISTICS = "fragment_statistics";
    private static final String FRAGMENT_TAG_SETTINGS = "fragment_settings";
    private static final String FRAGMENT_TAG_HELP = "fragment_help";

    private final FragmentManager fragmentManager;
    @IdRes
    private final int containerId;

    private MainMenuPresenter mainMenuPresenter;

    public ActivityMainController(final MainMenuPresenter mainMenuPresenter, FragmentManager fragmentManager, @IdRes int containerId) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
        this.mainMenuPresenter = mainMenuPresenter;
        this.fragmentManager.addOnBackStackChangedListener(this);
    }

    public void init() {
        Fragment fragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG_SCHEDULE);
        if (fragment == null) {
            fragment = new FragmentSchedule();
        }
        fragmentManager
                .beginTransaction()
                .replace(containerId, fragment, FRAGMENT_TAG_SCHEDULE)
                .commit();
        mainMenuPresenter.onScreenChanged(R.string.title_schedule, true);
    }

    @Override
    public void goToSchedule() {
        Fragment fragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG_SCHEDULE);
        if (fragment == null) {
            fragment = new FragmentSchedule();
        }
        fragmentManager
                .beginTransaction()
                .replace(containerId, fragment, FRAGMENT_TAG_SCHEDULE)
                .commit();
        mainMenuPresenter.onScreenChanged(R.string.title_schedule, true);
    }

    @Override
    public void goToRoutines() {
        Fragment fragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG_ROUTINES);
        if (fragment == null) {
            fragment = new FragmentRoutines();
        }
        fragmentManager
                .beginTransaction()
                .replace(containerId, fragment, FRAGMENT_TAG_ROUTINES)
                .commit();
        mainMenuPresenter.onScreenChanged(R.string.title_routines, true);
    }

    @Override
    public void goToActivities() {
        Fragment fragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG_ACTIVITIES);
        if (fragment == null) {
            fragment = new FragmentActivities();
        }
        fragmentManager
                .beginTransaction()
                .replace(containerId, fragment, FRAGMENT_TAG_ACTIVITIES)
                .commit();
        mainMenuPresenter.onScreenChanged(R.string.title_activities, true);
    }

    @Override
    public void goToStatistics() {
        Fragment fragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG_STATISTICS);
        if (fragment == null) {
            fragment = new FragmentStatistics();
        }
        fragmentManager
                .beginTransaction()
                .replace(containerId, fragment, FRAGMENT_TAG_STATISTICS)
                .commit();
        mainMenuPresenter.onScreenChanged(R.string.title_statistics, true);
    }

    @Override
    public void goToSettings() {
        Fragment fragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG_SETTINGS);
        if (fragment == null) {
            fragment = new FragmentSettings();
        }
        fragmentManager
                .beginTransaction()
                .replace(containerId, fragment, FRAGMENT_TAG_SETTINGS)
                .commit();
        mainMenuPresenter.onScreenChanged(R.string.title_settings, false);
    }

    @Override
    public void goToHelp() {
        Fragment fragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG_HELP);
        if (fragment == null) {
            fragment = new FragmentHelp();
        }
        fragmentManager
                .beginTransaction()
                .replace(containerId, fragment, FRAGMENT_TAG_HELP)
                .commit();
        mainMenuPresenter.onScreenChanged(R.string.title_help, false);
    }

    @Override
    public void onBackStackChanged() {
        //fragmentManager.
    }

    interface MainMenuPresenter {

        void onScreenChanged(@StringRes final int toolbarTitle, final boolean canOpenDrawer);
    }

}
