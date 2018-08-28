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

package com.josemgu91.habittune.android.ui.tag_editor;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.Application;
import com.josemgu91.habittune.android.ui.ViewModelFactory;
import com.josemgu91.habittune.databinding.ElementTagBinding;
import com.josemgu91.habittune.databinding.FragmentTagEditorBinding;

public class FragmentTagEditor extends Fragment {

    private FragmentTagEditorBinding fragmentTagEditorBinding;
    private ViewModelTagEditor viewModelTagEditor;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final ViewModelFactory viewModelFactory = ((Application) context.getApplicationContext()).getViewModelFactory();
        viewModelTagEditor = ViewModelProviders.of(this, viewModelFactory).get(ViewModelTagEditor.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentTagEditorBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_activity, container, false);
        return fragmentTagEditorBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private static class TagViewHolder extends RecyclerView.ViewHolder {

        private final ElementTagBinding elementTagBinding;

        public TagViewHolder(ElementTagBinding elementTagBinding) {
            super(elementTagBinding.getRoot());
            this.elementTagBinding = elementTagBinding;
        }

    }
}
