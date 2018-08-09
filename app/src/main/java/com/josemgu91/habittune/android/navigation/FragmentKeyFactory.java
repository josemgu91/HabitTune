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

package com.josemgu91.habittune.android.navigation;

import android.support.v4.app.Fragment;

import com.josemgu91.habittune.android.FragmentActivities;
import com.josemgu91.habittune.android.FragmentHelp;
import com.josemgu91.habittune.android.FragmentNewActivity;
import com.josemgu91.habittune.android.FragmentRoutines;
import com.josemgu91.habittune.android.FragmentSchedule;
import com.josemgu91.habittune.android.FragmentSettings;
import com.josemgu91.habittune.android.FragmentStatistics;

public class FragmentKeyFactory {

    public static final String FRAGMENT_TAG_SCHEDULE = "fragment_schedule";
    public static final String FRAGMENT_TAG_ROUTINES = "fragment_routines";
    public static final String FRAGMENT_TAG_ACTIVITIES = "fragment_activities";
    public static final String FRAGMENT_TAG_STATISTICS = "fragment_statistics";
    public static final String FRAGMENT_TAG_SETTINGS = "fragment_settings";
    public static final String FRAGMENT_TAG_HELP = "fragment_help";

    public static final String FRAGMENT_TAG_NEW_ACTIVITY = "fragment_new_activity";

    public FragmentKey createScheduleKey() {
        return new FragmentKey(FRAGMENT_TAG_SCHEDULE, null);
    }

    public FragmentKey createRoutinesKey() {
        return new FragmentKey(FRAGMENT_TAG_ROUTINES, null);
    }

    public FragmentKey createActivitiesKey() {
        return new FragmentKey(FRAGMENT_TAG_ACTIVITIES, null);
    }

    public FragmentKey createStatisticsKey() {
        return new FragmentKey(FRAGMENT_TAG_STATISTICS, null);
    }

    public FragmentKey createNewActivityKey() {
        return new FragmentKey(FRAGMENT_TAG_NEW_ACTIVITY, null);
    }

    public FragmentKey createSettingsKey() {
        return new FragmentKey(FRAGMENT_TAG_SETTINGS, null);
    }

    public FragmentKey createHelpKey() {
        return new FragmentKey(FRAGMENT_TAG_HELP, null);
    }

    public static class FragmentFactory {
        public Fragment createFragment(final FragmentKey fragmentKey) {
            switch (fragmentKey.getFragmentTag()) {
                case FRAGMENT_TAG_SCHEDULE:
                    return new FragmentSchedule();
                case FRAGMENT_TAG_ROUTINES:
                    return new FragmentRoutines();
                case FRAGMENT_TAG_ACTIVITIES:
                    return new FragmentActivities();
                case FRAGMENT_TAG_STATISTICS:
                    return new FragmentStatistics();
                case FRAGMENT_TAG_SETTINGS:
                    return new FragmentSettings();
                case FRAGMENT_TAG_HELP:
                    return new FragmentHelp();
                case FRAGMENT_TAG_NEW_ACTIVITY:
                    return new FragmentNewActivity();
                default:
                    throw new RuntimeException("Unknown fragment tag!");
            }
        }
    }
}
