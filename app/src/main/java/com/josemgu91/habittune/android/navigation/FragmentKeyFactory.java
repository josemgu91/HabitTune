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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.josemgu91.habittune.android.FragmentHelp;
import com.josemgu91.habittune.android.FragmentSchedule;
import com.josemgu91.habittune.android.FragmentSettings;
import com.josemgu91.habittune.android.FragmentStatistics;
import com.josemgu91.habittune.android.ui.activities.FragmentActivities;
import com.josemgu91.habittune.android.ui.activity_selection.FragmentActivitySelection;
import com.josemgu91.habittune.android.ui.activity_create_update.FragmentNewActivity;
import com.josemgu91.habittune.android.ui.new_routine.FragmentNewRoutine;
import com.josemgu91.habittune.android.ui.routine_editor.FragmentRoutineEditor;
import com.josemgu91.habittune.android.ui.routine_entry_add_update.FragmentAddRoutineEntry;
import com.josemgu91.habittune.android.ui.routine_entry_add_update.FragmentUpdateRoutineEntry;
import com.josemgu91.habittune.android.ui.routines.FragmentRoutines;
import com.josemgu91.habittune.android.ui.tag_editor.FragmentTagEditor;

public class FragmentKeyFactory {

    public static final String FRAGMENT_TAG_SCHEDULE = "fragmentSchedule";
    public static final String FRAGMENT_TAG_ROUTINES = "fragmentRoutines";
    public static final String FRAGMENT_TAG_ACTIVITIES = "fragmentActivities";
    public static final String FRAGMENT_TAG_STATISTICS = "fragmentStatistics";
    public static final String FRAGMENT_TAG_SETTINGS = "fragmentSettings";
    public static final String FRAGMENT_TAG_HELP = "fragmentHelp";

    public static final String FRAGMENT_TAG_NEW_ACTIVITY = "fragmentNewActivity";
    public static final String FRAGMENT_TAG_TAG_EDITOR = "fragmentTagEditor";
    public static final String FRAGMENT_TAG_NEW_ROUTINE = "fragmentNewRoutine";
    public static final String FRAGMENT_TAG_ROUTINE_EDITOR = "fragmentRoutineEditor";
    public static final String FRAGMENT_TAG_ADD_ROUTINE_ENTRY = "fragmentAddRoutineEntry";
    public static final String FRAGMENT_TAG_UPDATE_ROUTINE_ENTRY = "fragmentUpdateRoutineEntry";
    public static final String FRAGMENT_TAG_ACTIVITY_SELECTION = "fragmentActivitySelection";

    private static final String FRAGMENT_ARG_ROUTINE_EDITOR_ROUTINE_ID = "routineId";

    private static final String FRAGMENT_ARG_ADD_ROUTINE_ENTRY_ROUTINE_ID = "routineId";
    private static final String FRAGMENT_ARG_ADD_ROUTINE_ENTRY_ROUTINE_DAY = "routineDay";

    private static final String FRAGMENT_ARG_UPDATE_ROUTINE_ENTRY_ROUTINE_ENTRY_ID = "routineEntryId";
    private static final String FRAGMENT_ARG_UPDATE_ROUTINE_ENTRY_ROUTINE_ROUTINE_ID = "routineId";

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

    public FragmentKey createTagEditorKey() {
        return new FragmentKey(FRAGMENT_TAG_TAG_EDITOR, null);
    }

    public FragmentKey createNewRoutineKey() {
        return new FragmentKey(FRAGMENT_TAG_NEW_ROUTINE, null);
    }

    public FragmentKey createRoutineEditorKey(@NonNull final String routineId) {
        final Bundle arguments = new Bundle();
        arguments.putString(FRAGMENT_ARG_ROUTINE_EDITOR_ROUTINE_ID, routineId);
        return new FragmentKey(FRAGMENT_TAG_ROUTINE_EDITOR, arguments);
    }

    public FragmentKey createAddRoutineEntryKey(@NonNull final String routineId, final int routineDay) {
        final Bundle arguments = new Bundle();
        arguments.putString(FRAGMENT_ARG_ADD_ROUTINE_ENTRY_ROUTINE_ID, routineId);
        arguments.putInt(FRAGMENT_ARG_ADD_ROUTINE_ENTRY_ROUTINE_DAY, routineDay);
        return new FragmentKey(FRAGMENT_TAG_ADD_ROUTINE_ENTRY, arguments);
    }

    public FragmentKey createUpdateRoutineEntryKey(@NonNull final String routineEntryId, @NonNull final String routineId) {
        final Bundle arguments = new Bundle();
        arguments.putString(FRAGMENT_ARG_UPDATE_ROUTINE_ENTRY_ROUTINE_ENTRY_ID, routineEntryId);
        arguments.putString(FRAGMENT_ARG_UPDATE_ROUTINE_ENTRY_ROUTINE_ROUTINE_ID, routineId);
        return new FragmentKey(FRAGMENT_TAG_UPDATE_ROUTINE_ENTRY, arguments);
    }


    public FragmentKey createFragmentActivitySelection() {
        return new FragmentKey(FRAGMENT_TAG_ACTIVITY_SELECTION, null);
    }

    public FragmentKey createSettingsKey() {
        return new FragmentKey(FRAGMENT_TAG_SETTINGS, null);
    }

    public FragmentKey createHelpKey() {
        return new FragmentKey(FRAGMENT_TAG_HELP, null);
    }

    public static class FragmentFactory {
        public Fragment createFragment(final FragmentKey fragmentKey) {
            final Bundle arguments = fragmentKey.getArguments();
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
                case FRAGMENT_TAG_TAG_EDITOR:
                    return new FragmentTagEditor();
                case FRAGMENT_TAG_NEW_ROUTINE:
                    return new FragmentNewRoutine();
                case FRAGMENT_TAG_ROUTINE_EDITOR:
                    return FragmentRoutineEditor.newInstance(
                            arguments.getString(FRAGMENT_ARG_ROUTINE_EDITOR_ROUTINE_ID)
                    );
                case FRAGMENT_TAG_ADD_ROUTINE_ENTRY:
                    return FragmentAddRoutineEntry.newInstance(
                            arguments.getString(FRAGMENT_ARG_ADD_ROUTINE_ENTRY_ROUTINE_ID),
                            arguments.getInt(FRAGMENT_ARG_ADD_ROUTINE_ENTRY_ROUTINE_DAY)
                    );
                case FRAGMENT_TAG_UPDATE_ROUTINE_ENTRY:
                    return FragmentUpdateRoutineEntry.newInstance(
                            arguments.getString(FRAGMENT_ARG_UPDATE_ROUTINE_ENTRY_ROUTINE_ENTRY_ID),
                            arguments.getString(FRAGMENT_ARG_UPDATE_ROUTINE_ENTRY_ROUTINE_ROUTINE_ID)
                    );
                case FRAGMENT_TAG_ACTIVITY_SELECTION:
                    return new FragmentActivitySelection();
                default:
                    throw new RuntimeException("Unknown fragment tag!");
            }
        }
    }
}
