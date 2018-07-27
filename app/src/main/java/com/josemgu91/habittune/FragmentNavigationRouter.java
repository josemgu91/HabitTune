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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class FragmentNavigationRouter implements NavigationRouter {

    private static final String FRAGMENT_TAG_SCHEDULE = "fragment_schedule";
    private static final String FRAGMENT_TAG_ROUTINES = "fragment_routines";
    private static final String FRAGMENT_TAG_ACTIVITIES = "fragment_activities";
    private static final String FRAGMENT_TAG_STATISTICS = "fragment_statistics";
    private static final String FRAGMENT_TAG_SETTINGS = "fragment_settings";
    private static final String FRAGMENT_TAG_HELP = "fragment_help";

    private final FragmentManager fragmentManager;
    @IdRes
    private final int containerId;

    public FragmentNavigationRouter(final FragmentManager fragmentManager, @IdRes final int containerId) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }

    @Override
    public void goToSchedule() {
        Fragment fragment = findFragmentByTag(FRAGMENT_TAG_SCHEDULE);
        if (fragment == null) {
            fragment = new FragmentSchedule();
        }
        showFragment(fragment, FRAGMENT_TAG_SCHEDULE);
    }

    @Override
    public void goToRoutines() {
        Fragment fragment = findFragmentByTag(FRAGMENT_TAG_ROUTINES);
        if (fragment == null) {
            fragment = new FragmentRoutines();
        }
        showFragment(fragment, FRAGMENT_TAG_ROUTINES);

    }

    @Override
    public void goToActivities() {
        Fragment fragment = findFragmentByTag(FRAGMENT_TAG_ACTIVITIES);
        if (fragment == null) {
            fragment = new FragmentActivities();
        }
        showFragment(fragment, FRAGMENT_TAG_ACTIVITIES);
    }

    @Override
    public void goToStatistics() {
        Fragment fragment = findFragmentByTag(FRAGMENT_TAG_STATISTICS);
        if (fragment == null) {
            fragment = new FragmentStatistics();
        }
        showFragment(fragment, FRAGMENT_TAG_STATISTICS);
    }

    @Override
    public void goToSettings() {
        Fragment fragment = findFragmentByTag(FRAGMENT_TAG_SETTINGS);
        if (fragment == null) {
            fragment = new FragmentSettings();
        }
        showFragment(fragment, FRAGMENT_TAG_SETTINGS);
    }

    @Override
    public void goToHelp() {
        Fragment fragment = findFragmentByTag(FRAGMENT_TAG_HELP);
        if (fragment == null) {
            fragment = new FragmentHelp();
        }
        showFragment(fragment, FRAGMENT_TAG_HELP);
    }

    private void showFragment(final Fragment fragment, final String tag) {
        fragmentManager
                .beginTransaction()
                .replace(containerId, fragment, tag)
                .commit();
    }

    private Fragment findFragmentByTag(final String tag) {
        return fragmentManager.findFragmentByTag(tag);
    }

}
