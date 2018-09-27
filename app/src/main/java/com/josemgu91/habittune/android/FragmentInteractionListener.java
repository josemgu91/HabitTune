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

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;

public interface FragmentInteractionListener {

    int IC_NAVIGATION_HAMBURGUER = 0;
    int IC_NAVIGATION_UP = 1;
    int IC_NAVIGATION_CLOSE = 2;

    @IntDef({
            IC_NAVIGATION_HAMBURGUER,
            IC_NAVIGATION_UP,
            IC_NAVIGATION_CLOSE
    })
    @interface NavigationIcon {
    }

    void setToolbar(final Toolbar toolbar);

    void removeToolbar();

    void updateToolbar(final String title, @NavigationIcon final int toolbarToggleIcon);

    void updateNavigationDrawer(final boolean allowOpening);

    void navigateToFragmentNewActivity();

    void navigateToFragmentNewRoutine();

    void navigateToFragmentTagEditor();

    void navigateToFragmentRoutineEditor(@NonNull final String routineId);

    void navigateToFragmentAddRoutineEntry(@NonNull final String routineId, final int routineDay);

    void navigateToFragmentUpdateRoutineEntry(@NonNull final String id);

    void navigateToActivitySelection();

    void finishFragment();

    void hideSoftKeyboard();

}
