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

package com.josemgu91.habittune.android.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.Application;
import com.josemgu91.habittune.android.FragmentInteractionListener;

public abstract class BaseFragment extends Fragment {

    protected FragmentInteractionListener fragmentInteractionListener;
    protected ViewModelProvider.Factory viewModelFactory;

    private ToolbarOptions toolbarOptions;
    private Toolbar toolbar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentInteractionListener = (FragmentInteractionListener) getActivity();
        viewModelFactory = ((Application) context.getApplicationContext()).getViewModelFactory();
        toolbarOptions = createToolbarOptions();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @NonNull final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        if (toolbarOptions.hasDefaultToolbar) {
            final View view = new DefaultToolbarViewFactory((layoutInflater, root) -> createView(layoutInflater, root, savedInstanceState)).createView(inflater, container);
            toolbar = view.findViewById(R.id.toolbar);
            return view;
        }
        final View view = createView(inflater, container, savedInstanceState);
        toolbar = view.findViewById(toolbarOptions.customToolbarId);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fragmentInteractionListener.setToolbar(toolbar);
    }

    @Override
    public void onStop() {
        super.onStop();
        fragmentInteractionListener.removeToolbar();
    }

    @NonNull
    protected abstract View createView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState);

    protected abstract ToolbarOptions createToolbarOptions();

    protected final static class ToolbarOptions {

        private final boolean hasDefaultToolbar;
        @IdRes
        private final int customToolbarId;

        public ToolbarOptions(boolean hasDefaultToolbar) {
            this.hasDefaultToolbar = hasDefaultToolbar;
            this.customToolbarId = 0;
        }

        public ToolbarOptions(@IdRes int customToolbarId) {
            this.hasDefaultToolbar = false;
            this.customToolbarId = customToolbarId;
        }
    }
}
