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

package com.josemgu91.habittune.android.ui.statistics;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.FragmentInteractionListener;
import com.josemgu91.habittune.android.ui.BaseFragment;

public class FragmentStatistics extends BaseFragment {

    private final static String ARG_ACTIVITY_ID = "activityId";

    public static FragmentStatistics newInstance(@NonNull final String activityId) {
        Bundle args = new Bundle();
        args.putString(ARG_ACTIVITY_ID, activityId);
        FragmentStatistics fragment = new FragmentStatistics();
        fragment.setArguments(args);
        return fragment;
    }

    private String activityId;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Bundle arguments = new Bundle();
        activityId = arguments.getString(ARG_ACTIVITY_ID);
    }

    @Override
    protected ToolbarOptions createToolbarOptions() {
        return new ToolbarOptions(true);
    }

    @NonNull
    @Override
    public View createView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentInteractionListener.updateToolbar(getString(R.string.statistics_title), FragmentInteractionListener.IC_NAVIGATION_UP);
        fragmentInteractionListener.updateNavigationDrawer(false);
    }
}
