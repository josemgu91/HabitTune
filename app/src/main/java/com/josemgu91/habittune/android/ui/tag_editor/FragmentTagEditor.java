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

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.Application;
import com.josemgu91.habittune.android.FragmentInteractionListener;
import com.josemgu91.habittune.android.ui.ViewModelFactory;
import com.josemgu91.habittune.databinding.FragmentTagEditorBinding;
import com.josemgu91.habittune.domain.usecases.GetTags;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.Payload;
import eu.davidea.flexibleadapter.helpers.ItemTouchHelperCallback;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

public class FragmentTagEditor extends Fragment {

    private FragmentTagEditorBinding fragmentTagEditorBinding;
    private FragmentInteractionListener fragmentInteractionListener;
    private ViewModelTagEditor viewModelTagEditor;

    private TagEditorFlexibleAdapter recyclerViewTagsAdapter;

    private EditText toolbarEditText;
    private InputMethodManager inputMethodManager;

    private DeletionConfirmationDialog deletionConfirmationDialog;

    private FragmentManager fragmentManager;

    private final static String FRAGMENT_TAG_DELETION_DIALOG = "deletion_dialog";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentInteractionListener = (FragmentInteractionListener) getActivity();
        final ViewModelFactory viewModelFactory = ((Application) context.getApplicationContext()).getViewModelFactory();
        viewModelTagEditor = ViewModelProviders.of(this, viewModelFactory).get(ViewModelTagEditor.class);
        inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModelTagEditor.fetchTags();
        fragmentManager = getFragmentManager();
        deletionConfirmationDialog = (DeletionConfirmationDialog) fragmentManager.findFragmentByTag(FRAGMENT_TAG_DELETION_DIALOG);
        if (deletionConfirmationDialog == null) {
            deletionConfirmationDialog = new DeletionConfirmationDialog();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentTagEditorBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tag_editor, container, false);
        recyclerViewTagsAdapter = new TagEditorFlexibleAdapter(getContext());
        fragmentTagEditorBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentTagEditorBinding.recyclerView.setAdapter(recyclerViewTagsAdapter);
        recyclerViewTagsAdapter.setSwipeEnabled(true);
        final ItemTouchHelperCallback itemTouchHelperCallback = recyclerViewTagsAdapter.getItemTouchHelperCallback();
        itemTouchHelperCallback.setSwipeFlags(ItemTouchHelper.RIGHT);
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
        fragmentInteractionListener.updateToolbar(getString(R.string.tag_editor_title), FragmentInteractionListener.IC_NAVIGATION_UP);
        fragmentInteractionListener.updateNavigationDrawer(false);
        fragmentInteractionListener.showToolbarTextInput();
        toolbarEditText = fragmentInteractionListener.getToolbarTextInput();
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
            createTag(toolbarEditText.getText().toString());
            return true;
        });
        recyclerViewTagsAdapter.setOnCreateTagItemClickListener(() -> createTag(toolbarEditText.getText().toString()));
        recyclerViewTagsAdapter.setOnTagNameEditionFinishedListener((view, oldName, newName) -> {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            if (oldName.equals(newName)) {
                return;
            }
            viewModelTagEditor.updateTag(oldName, newName);
            //TODO: Handle update failure (e.g. UNIQUE name constraint failure).
        });
        recyclerViewTagsAdapter.addListener(new FlexibleAdapter.OnItemSwipeListener() {
            @Override
            public void onItemSwipe(int position, int direction) {
                deletionConfirmationDialog.show(fragmentManager, FRAGMENT_TAG_DELETION_DIALOG);
                deletionConfirmationDialog.setOnDeleteClickListener(() -> {
                    //TODO: Handle state restoration (maybe saving the item to delete position in the heap?).
                    final TagItem tagItem = (TagItem) recyclerViewTagsAdapter.getItem(position);
                    viewModelTagEditor.deleteTag(tagItem.tagName);
                });
                deletionConfirmationDialog.setOnCancelClickListener(() -> recyclerViewTagsAdapter.notifyItemChanged(position));
            }

            @Override
            public void onActionStateChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        fragmentInteractionListener.hideToolbarTextInput();
    }

    private void showTags(List<GetTags.Output> outputs) {
        //TODO: Only update the diff in the list (maybe with DiffUtil or with the FlexibleAdapter built in options?).
        final List<IFlexible> tagItems = new ArrayList<>();
        for (final GetTags.Output output : outputs) {
            final TagItem tagItem = new TagItem(output.getName());
            tagItem.setSwipeable(true);
            tagItems.add(tagItem);
        }
        recyclerViewTagsAdapter.clear();
        recyclerViewTagsAdapter.updateDataSet(tagItems);
    }

    private void createTag(final String tagName) {
        viewModelTagEditor.createTag(tagName);
        toolbarEditText.getText().clear();
        inputMethodManager.hideSoftInputFromWindow(toolbarEditText.getWindowToken(), 0);
    }

    private static class TagEditorFlexibleAdapter extends FlexibleAdapter<IFlexible> {

        private boolean isShowingCreateTagItem;
        private CreateTagItem createTagItem;

        private OnCreateTagItemClickListener onCreateTagItemClickListener;
        private OnTagNameEditionFinishedListener onTagNameEditionFinishedListener;

        public void setOnCreateTagItemClickListener(TagEditorFlexibleAdapter.OnCreateTagItemClickListener onCreateTagItemClickListener) {
            this.onCreateTagItemClickListener = onCreateTagItemClickListener;
        }

        public void setOnTagNameEditionFinishedListener(OnTagNameEditionFinishedListener onTagNameEditionFinishedListener) {
            this.onTagNameEditionFinishedListener = onTagNameEditionFinishedListener;
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

        public interface OnCreateTagItemClickListener {

            void onCreateTagItemClick();

        }

        public interface OnTagNameEditionFinishedListener {

            void onTagNameUpdated(final View view, final String oldName, final String newName);
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
            return new TagViewHolder(view, (TagEditorFlexibleAdapter) adapter);
        }

        @Override
        public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, TagItem.TagViewHolder holder, int position, List<Object> payloads) {
            holder.setText(tagName);
            holder.editTextTagName.setEnabled(isEnabled());
        }

        public static class TagViewHolder extends FlexibleViewHolder {

            private final EditText editTextTagName;
            private String originalText;

            public TagViewHolder(View view, TagEditorFlexibleAdapter tagEditorFlexibleAdapter) {
                super(view, tagEditorFlexibleAdapter);
                editTextTagName = view.findViewById(R.id.editTextTagName);
                editTextTagName.setOnFocusChangeListener((v, hasFocus) -> {
                    if (hasFocus) {
                        return;
                    }
                    final TagEditorFlexibleAdapter.OnTagNameEditionFinishedListener onTagNameUpdatedListener =
                            tagEditorFlexibleAdapter.onTagNameEditionFinishedListener;
                    if (onTagNameUpdatedListener != null) {
                        final String newText = editTextTagName.getText().toString();
                        onTagNameUpdatedListener.onTagNameUpdated(editTextTagName, originalText, newText);
                        originalText = newText;
                    }
                });
                editTextTagName.setOnEditorActionListener((v, actionId, event) -> {
                    v.clearFocus();
                    return true;
                });
            }

            public void setText(final String text) {
                editTextTagName.setText(text);
                originalText = text;
            }
        }
    }

    public static class DeletionConfirmationDialog extends DialogFragment {

        private OnDeleteClickListener onDeleteClickListener;
        private OnCancelClickListener onCancelClickListener;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final AlertDialog alertDialog = new AlertDialog
                    .Builder(getContext())
                    .setTitle(R.string.tag_editor_delete_dialog_title)
                    .setMessage(R.string.tag_editor_delete_dialog_content)
                    .setPositiveButton(R.string.action_delete, (dialog, which) -> {
                        if (onDeleteClickListener != null) {
                            onDeleteClickListener.onDeleteClick();
                        }
                    })
                    .setNegativeButton(R.string.action_cancel, (dialog, which) -> {
                        if (onCancelClickListener != null) {
                            onCancelClickListener.onCancelClick();
                        }
                    })
                    .create();
            return alertDialog;
        }

        public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
            this.onDeleteClickListener = onDeleteClickListener;
        }

        public void setOnCancelClickListener(OnCancelClickListener onCancelClickListener) {
            this.onCancelClickListener = onCancelClickListener;
        }

        public interface OnDeleteClickListener {

            void onDeleteClick();
        }

        public interface OnCancelClickListener {

            void onCancelClick();
        }
    }
}
