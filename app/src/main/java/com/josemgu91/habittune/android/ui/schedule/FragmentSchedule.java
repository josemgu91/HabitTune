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

package com.josemgu91.habittune.android.ui.schedule;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.FragmentInteractionListener;
import com.josemgu91.habittune.android.ui.BaseFragment;
import com.josemgu91.habittune.databinding.FragmentScheduleBinding;
import com.josemgu91.habittune.domain.usecases.GetRoutines;

import java.util.List;

public class FragmentSchedule extends BaseFragment {

    private ViewModelSchedule viewModelSchedule;
    private FragmentScheduleBinding fragmentScheduleBinding;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        viewModelSchedule = ViewModelProviders.of(this, viewModelFactory).get(ViewModelSchedule.class);
    }

    @Override
    protected ToolbarOptions createToolbarOptions() {
        return new ToolbarOptions(true);
    }

    @NonNull
    @Override
    public View createView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentScheduleBinding = FragmentScheduleBinding.inflate(inflater, container, false);
        return fragmentScheduleBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentInteractionListener.updateToolbar(getString(R.string.schedule_title), FragmentInteractionListener.IC_NAVIGATION_HAMBURGUER);
        fragmentInteractionListener.updateNavigationDrawer(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            viewModelSchedule.fetchRoutines();
        }
        viewModelSchedule.getGetRoutinesResponse().observe(getViewLifecycleOwner(), response -> {
            switch (response.status) {
                case LOADING:
                    break;
                case ERROR:
                    break;
                case SUCCESS:
                    response.successData.observe(getViewLifecycleOwner(), this::onRoutinesUpdated);
                    break;
            }
        });
    }

    private void onRoutinesUpdated(final List<GetRoutines.Output> routines) {
        Log.d("FragmentSchedule", routines.toString());
    }
}
