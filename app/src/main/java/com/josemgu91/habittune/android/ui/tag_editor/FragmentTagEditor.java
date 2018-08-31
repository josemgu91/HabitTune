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
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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
import eu.davidea.flexibleadapter.Payload;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

public class FragmentTagEditor extends Fragment {

    private FragmentTagEditorBinding fragmentTagEditorBinding;
    private FragmentInteractionListener fragmentInteractionListener;
    private ViewModelTagEditor viewModelTagEditor;

    private TagEditorFlexibleAdapter recyclerViewTagsAdapter;

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
        recyclerViewTagsAdapter = new TagEditorFlexibleAdapter(getContext());
        recyclerViewTagsAdapter.addListener(new FlexibleAdapter.OnItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position) {
                Toast.makeText(getContext(), "Click!", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
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
        toolbarEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final String text = s.toString();
                if (text.length() > 0) {
                    if (!recyclerViewTagsAdapter.isShowingCreateTagItem()) {
                        recyclerViewTagsAdapter.showCreateTagItem();
                    }
                    recyclerViewTagsAdapter.updateCreateTagItem(text);
                } else {
                    recyclerViewTagsAdapter.dismissCreateTagItem();
                }
            }
        });
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
        final List<IFlexible> tagItems = new ArrayList<>();
        for (final GetTags.Output output : outputs) {
            tagItems.add(new TagItem(output.getName()));
        }
        recyclerViewTagsAdapter.clear();
        recyclerViewTagsAdapter.updateDataSet(tagItems);
    }

    private static class TagEditorFlexibleAdapter extends FlexibleAdapter<IFlexible> {

        private boolean isShowingCreateTagItem;
        private CreateTagItem createTagItem;

        public TagEditorFlexibleAdapter(final Context context) {
            super(null, null, true);
            this.isShowingCreateTagItem = false;
            this.createTagItem = new CreateTagItem(context);
        }

        public void showCreateTagItem() {
            if (isShowingCreateTagItem) {
                return;
            }
            addItem(0, createTagItem);
            isShowingCreateTagItem = true;
        }

        public void updateCreateTagItem(final String tagName) {
            if (isShowingCreateTagItem) {
                createTagItem.updateTagName(tagName);
                updateItem(createTagItem, Payload.CHANGE);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List payloads) {
            super.onBindViewHolder(holder, position, payloads);
        }

        public boolean isShowingCreateTagItem() {
            return isShowingCreateTagItem;
        }

        public void dismissCreateTagItem() {
            if (!isShowingCreateTagItem) {
                return;
            }
            removeItemsOfType(createTagItem.getItemViewType());
            isShowingCreateTagItem = false;
        }

    }

    private static class CreateTagItem extends AbstractFlexibleItem<CreateTagItem.CreateTagViewHolder> {

        private String tagCreationText;

        private final Context context;

        public CreateTagItem(final Context context) {
            tagCreationText = context.getString(R.string.tag_editor_tag_create, "");
            this.context = context;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof CreateTagItem) {
                return true;
            }
            return false;
        }

        @Override
        public int getLayoutRes() {
            return R.layout.element_tag_create;
        }

        @Override
        public CreateTagViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
            return new CreateTagViewHolder(view, adapter);
        }

        public void updateTagName(final String tagName) {
            tagCreationText = context.getString(R.string.tag_editor_tag_create, tagName);
        }

        @Override
        public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, CreateTagViewHolder holder, int position, List<Object> payloads) {
            if (payloads.size() == 1 && payloads.get(0) == Payload.CHANGE) {
                holder.textViewCreate.setText(tagCreationText);
            } else {
                holder.textViewCreate.setText(tagCreationText);
                holder.textViewCreate.setEnabled(isEnabled());
            }
        }

        public static class CreateTagViewHolder extends FlexibleViewHolder {

            public final TextView textViewCreate;

            public CreateTagViewHolder(View view, FlexibleAdapter adapter) {
                super(view, adapter);
                textViewCreate = view.findViewById(R.id.textViewCreate);
            }
        }
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
