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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.Application;
import com.josemgu91.habittune.android.FragmentInteractionListener;
import com.josemgu91.habittune.android.ui.ViewModelFactory;
import com.josemgu91.habittune.databinding.FragmentTagEditorBinding;
import com.josemgu91.habittune.domain.usecases.GetTags;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.helpers.ItemTouchHelperCallback;
import eu.davidea.flexibleadapter.items.IFlexible;

public class FragmentTagEditor extends Fragment {

    private FragmentTagEditorBinding fragmentTagEditorBinding;
    private FragmentInteractionListener fragmentInteractionListener;
    private ViewModelTagEditor viewModelTagEditor;

    private TagEditorFlexibleAdapter recyclerViewTagsAdapter;

    private EditText toolbarEditText;
    private InputMethodManager inputMethodManager;

    private TagDeletionConfirmationDialog tagDeletionConfirmationDialog;

    private FragmentManager fragmentManager;

    private final static String FRAGMENT_TAG_DELETION_DIALOG = "deletion_dialog";

    private final static String SAVED_INSTANCE_STATE_KEY_TAG_NAME_TO_DELETE = "tag_name_to_delete";

    private String tagNameToDelete;

    private List<GetTags.Output> tags;

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
        if (savedInstanceState != null) {
            tagNameToDelete = savedInstanceState.getString(SAVED_INSTANCE_STATE_KEY_TAG_NAME_TO_DELETE);
        }
        viewModelTagEditor.fetchTags();
        fragmentManager = getFragmentManager();
        tagDeletionConfirmationDialog = (TagDeletionConfirmationDialog) fragmentManager.findFragmentByTag(FRAGMENT_TAG_DELETION_DIALOG);
        if (tagDeletionConfirmationDialog == null) {
            tagDeletionConfirmationDialog = new TagDeletionConfirmationDialog();
        }
        tagDeletionConfirmationDialog.setOnDeleteClickListener(() -> {
            viewModelTagEditor.deleteTag(tagNameToDelete);
            tagNameToDelete = null;
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_INSTANCE_STATE_KEY_TAG_NAME_TO_DELETE, tagNameToDelete);
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
                    response.successData.observe(this, outputs -> {
                        tags = outputs;
                        showTags(outputs);
                    });
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
            if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
                return false;
            }
            createTagAndClearAndDismissKeyboard(toolbarEditText.getText().toString());
            return true;
        });
        recyclerViewTagsAdapter.setOnCreateTagItemClickListener(() -> createTagAndClearAndDismissKeyboard(toolbarEditText.getText().toString()));
        recyclerViewTagsAdapter.setOnTagNameEditionFinishedListener((position, view, oldName, newName) -> {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            if (oldName.equals(newName)) {
                return;
            }
            if (tags.contains(new GetTags.Output(newName))) {
                recyclerViewTagsAdapter.notifyItemChanged(position);
                return;
            }
            viewModelTagEditor.updateTag(oldName, newName);
        });
        recyclerViewTagsAdapter.addListener(new FlexibleAdapter.OnItemSwipeListener() {
            @Override
            public void onItemSwipe(int position, int direction) {
                tagDeletionConfirmationDialog.show(fragmentManager, FRAGMENT_TAG_DELETION_DIALOG);
                final TagEditorFlexibleAdapter.TagItem tagItem = (TagEditorFlexibleAdapter.TagItem) recyclerViewTagsAdapter.getItem(position);
                tagNameToDelete = tagItem.getTagName();
                tagDeletionConfirmationDialog.setOnCancelClickListener(() -> recyclerViewTagsAdapter.notifyItemChanged(position));
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
            final TagEditorFlexibleAdapter.TagItem tagItem = new TagEditorFlexibleAdapter.TagItem(output.getName());
            tagItem.setSwipeable(true);
            tagItems.add(tagItem);
        }
        recyclerViewTagsAdapter.clear();
        recyclerViewTagsAdapter.updateDataSet(tagItems);
    }

    private void createTagAndClearAndDismissKeyboard(final String tagName) {
        viewModelTagEditor.createTag(tagName);
        toolbarEditText.getText().clear();
        inputMethodManager.hideSoftInputFromWindow(toolbarEditText.getWindowToken(), 0);
    }

    public static class TagDeletionConfirmationDialog extends DialogFragment {

        private OnDeleteClickListener onDeleteClickListener;
        private OnCancelClickListener onCancelClickListener;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog
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
