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

package com.josemgu91.habittune.android.ui.activity_selection;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.FragmentInteractionListener;
import com.josemgu91.habittune.android.ui.BaseFragment;
import com.josemgu91.habittune.databinding.FragmentActivitySelectionBinding;

public class FragmentActivitySelection extends BaseFragment {

    private FragmentActivitySelectionBinding fragmentActivitySelectionBinding;

    private ViewModelActivitySelection viewModelActivitySelection;
    private SharedViewModelActivitySelection sharedViewModelActivitySelection;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        viewModelActivitySelection = ViewModelProviders.of(this, viewModelFactory).get(ViewModelActivitySelection.class);
        sharedViewModelActivitySelection = ViewModelProviders.of(this, viewModelFactory).get(SharedViewModelActivitySelection.class);
    }

    @NonNull
    @Override
    protected View createView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentActivitySelectionBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_activity_selection, container, false);
        return fragmentActivitySelectionBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        fragmentInteractionListener.updateToolbar(getString(R.string.activity_selection_title), FragmentInteractionListener.IC_NAVIGATION_CLOSE);
        fragmentInteractionListener.updateNavigationDrawer(false);
    }

    @Override
    protected ToolbarOptions createToolbarOptions() {
        return new ToolbarOptions(true);
    }
}
