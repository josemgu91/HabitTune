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
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.Application;
import com.josemgu91.habittune.android.FragmentInteractionListener;
import com.josemgu91.habittune.android.ui.ViewModelFactory;
import com.josemgu91.habittune.databinding.FragmentTagEditorBinding;
import com.josemgu91.habittune.domain.usecases.CreateTag;
import com.josemgu91.habittune.domain.usecases.GetTags;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

public class FragmentTagEditor extends Fragment {

    private FragmentTagEditorBinding fragmentTagEditorBinding;
    private FragmentInteractionListener fragmentInteractionListener;
    private ViewModelTagEditor viewModelTagEditor;

    private FlexibleAdapter<TagItem> recyclerViewTagsAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentInteractionListener = (FragmentInteractionListener) getActivity();
        final ViewModelFactory viewModelFactory = ((Application) context.getApplicationContext()).getViewModelFactory();
        viewModelTagEditor = ViewModelProviders.of(this, viewModelFactory).get(ViewModelTagEditor.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModelTagEditor.fetchTags();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentTagEditorBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tag_editor, container, false);
        recyclerViewTagsAdapter = new FlexibleAdapter<>(null);
        //TEMP
        viewModelTagEditor.createTag(new CreateTag.Input("Test " + new Random().nextInt()));
        //
        fragmentTagEditorBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentTagEditorBinding.recyclerView.setAdapter(recyclerViewTagsAdapter);
        return fragmentTagEditorBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fragmentTagEditorBinding.setShowProgress(true);
        viewModelTagEditor.getGetTagsResponse().observe(this, response -> {
            switch (response.status) {
                case LOADING:
                    fragmentTagEditorBinding.setShowProgress(true);
                    break;
                case ERROR:
                    fragmentTagEditorBinding.setShowProgress(false);
                    break;
                case SUCCESS:
                    fragmentTagEditorBinding.setShowProgress(false);
                    response.successData.observe(this, this::showTags);
                    break;
            }
        });
        viewModelTagEditor.getCreateTagResponse().observe(this, response -> {
            switch (response.status) {
                case LOADING:
                    Log.d("FragmentTagEditor", "LOADING");
                    break;
                case ERROR:
                    Log.d("FragmentTagEditor", "ERROR");
                    break;
                case SUCCESS:
                    Log.d("FragmentTagEditor", "SUCCESS");
                    break;
            }
        });
        fragmentInteractionListener.updateToolbar(getString(R.string.tag_editor_title), FragmentInteractionListener.IC_NAVIGATION_UP);
        fragmentInteractionListener.updateNavigationDrawer(false);
        fragmentInteractionListener.showToolbarTextInput();
        final EditText toolbarEditText = fragmentInteractionListener.getToolbarTextInput();
        toolbarEditText.requestFocus();
        toolbarEditText.setOnEditorActionListener((v, actionId, event) -> {
            Toast.makeText(getContext(), v.getText().toString(), Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        fragmentInteractionListener.hideToolbarTextInput();
    }

    private void showTags(List<GetTags.Output> outputs) {
        final List<TagItem> tagItems = new ArrayList<>();
        for (final GetTags.Output output : outputs) {
            tagItems.add(new TagItem(output.getName()));
        }
        recyclerViewTagsAdapter.clear();
        recyclerViewTagsAdapter.addItems(0, tagItems);
    }

    private static class TagItem extends AbstractFlexibleItem<TagItem.TagViewHolder> {

        private final String tagName;

        public TagItem(String tagName) {
            this.tagName = tagName;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof TagItem) {
                return tagName.equals(((TagItem) o).tagName);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return tagName.hashCode();
        }

        @Override
        public int getLayoutRes() {
            return R.layout.element_tag;
        }

        @Override
        public TagItem.TagViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
            return new TagViewHolder(view, adapter);
        }

        @Override
        public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, TagItem.TagViewHolder holder, int position, List<Object> payloads) {
            holder.textViewTagName.setText(tagName);
            holder.textViewTagName.setEnabled(isEnabled());
        }

        public static class TagViewHolder extends FlexibleViewHolder {

            public final TextView textViewTagName;

            public TagViewHolder(View view, FlexibleAdapter adapter) {
                super(view, adapter);
                textViewTagName = view.findViewById(R.id.textViewTagName);
            }
        }
    }
}
