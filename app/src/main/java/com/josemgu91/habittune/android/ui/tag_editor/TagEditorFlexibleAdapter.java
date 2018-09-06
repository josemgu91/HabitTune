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

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.josemgu91.habittune.R;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.Payload;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

public class TagEditorFlexibleAdapter extends FlexibleAdapter<IFlexible> {

    private final static String SAVED_INSTANCE_STATE_KEY_IS_SHOWING_CREATE_TAG_ITEM = "isShowingCreateTagItem";

    private boolean isShowingCreateTagItem;
    private CreateTagItem createTagItem;

    private OnCreateTagItemClickListener onCreateTagItemClickListener;
    private OnTagNameEditionFinishedListener onTagNameEditionFinishedListener;
    private OnTagSelectionChangeListener onTagSelectionChangeListener;

    public void setOnCreateTagItemClickListener(TagEditorFlexibleAdapter.OnCreateTagItemClickListener onCreateTagItemClickListener) {
        this.onCreateTagItemClickListener = onCreateTagItemClickListener;
    }

    public void setOnTagNameEditionFinishedListener(OnTagNameEditionFinishedListener onTagNameEditionFinishedListener) {
        this.onTagNameEditionFinishedListener = onTagNameEditionFinishedListener;
    }

    public void setOnTagSelectionChangeListener(OnTagSelectionChangeListener onTagSelectionChangeListener) {
        this.onTagSelectionChangeListener = onTagSelectionChangeListener;
    }

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
        createTagItem.setSwipeable(false);
        createTagItem.setSelectable(false);
        isShowingCreateTagItem = true;
    }

    public void updateCreateTagItem(final String tagName) {
        if (isShowingCreateTagItem) {
            createTagItem.updateTagName(tagName);
            updateItem(createTagItem, Payload.CHANGE);
        }
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

    /*private void onTagItemSelectionChange(final int position, final boolean selected, final View view) {
        if (selected) {
            addSelection(position);
        } else {
            removeSelection(position);
        }
        if (onTagSelectionChangeListener != null) {
            onTagSelectionChangeListener.onTagSelectionChange(selected, position, view);
        }
    }*/

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public interface OnCreateTagItemClickListener {

        void onCreateTagItemClick();
    }

    public interface OnTagNameEditionFinishedListener {

        void onTagNameUpdated(final int position, final View view, final String oldName, final String newName);
    }

    public interface OnTagSelectionChangeListener {

        void onTagSelectionChange(final boolean selected, final int position, final View view);
    }

    public static class CreateTagItem extends AbstractFlexibleItem<CreateTagItem.CreateTagViewHolder> {

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
            return new CreateTagViewHolder(view, (TagEditorFlexibleAdapter) adapter);
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

            public CreateTagViewHolder(View view, TagEditorFlexibleAdapter tagEditorFlexibleAdapter) {
                super(view, tagEditorFlexibleAdapter);
                view.setOnClickListener(v -> {
                    final TagEditorFlexibleAdapter.OnCreateTagItemClickListener onCreateTagItemClickListener =
                            tagEditorFlexibleAdapter.onCreateTagItemClickListener;
                    if (onCreateTagItemClickListener != null) {
                        onCreateTagItemClickListener.onCreateTagItemClick();
                    }
                });
                textViewCreate = view.findViewById(R.id.textViewCreate);
            }
        }
    }

    public static class TagItem extends AbstractFlexibleItem<TagItem.TagViewHolder> {

        private final String tagName;

        public TagItem(String tagName) {
            this.tagName = tagName;
        }

        public String getTagName() {
            return tagName;
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
            return new TagViewHolder(view, (TagEditorFlexibleAdapter) adapter);
        }

        @Override
        public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, TagItem.TagViewHolder holder, int position, List<Object> payloads) {
            holder.setText(tagName);
            holder.checkBoxTagSelection.setChecked(adapter.isSelected(position));
        }

        public static class TagViewHolder extends FlexibleViewHolder {

            private final EditText editTextTagName;
            private final CheckBox checkBoxTagSelection;
            private String originalText;

            public TagViewHolder(View view, TagEditorFlexibleAdapter tagEditorFlexibleAdapter) {
                super(view, tagEditorFlexibleAdapter);
                checkBoxTagSelection = view.findViewById(R.id.checkBox);
                checkBoxTagSelection.setOnClickListener(v -> {
                    mAdapter.toggleSelection(getAdapterPosition());
                    toggleActivation();
                    //tagEditorFlexibleAdapter.onTagItemSelectionChange(getAdapterPosition(), checkBoxTagSelection.isSelected(), v);
                });
                editTextTagName = view.findViewById(R.id.editTextTagName);
                editTextTagName.setOnFocusChangeListener((v, hasFocus) -> {
                    if (hasFocus) {
                        return;
                    }
                    final TagEditorFlexibleAdapter.OnTagNameEditionFinishedListener onTagNameUpdatedListener =
                            tagEditorFlexibleAdapter.onTagNameEditionFinishedListener;
                    if (onTagNameUpdatedListener != null) {
                        final String newText = editTextTagName.getText().toString();
                        onTagNameUpdatedListener.onTagNameUpdated(getAdapterPosition(), editTextTagName, originalText, newText);
                        originalText = newText;
                    }
                });
                editTextTagName.setOnEditorActionListener((v, actionId, event) -> {
                    v.clearFocus();
                    return true;
                });
            }

            @Override
            public void toggleActivation() {
                super.toggleActivation();
                final boolean isSelected = mAdapter.isSelected(getAdapterPosition());
                checkBoxTagSelection.setChecked(isSelected);
            }

            public void setText(final String text) {
                editTextTagName.setText(text);
                originalText = text;
            }
        }
    }

}