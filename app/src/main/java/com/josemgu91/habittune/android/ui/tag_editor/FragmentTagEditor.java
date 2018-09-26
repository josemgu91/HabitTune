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
import android.support.v4.app.FragmentManager;
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

import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.FragmentInteractionListener;
import com.josemgu91.habittune.android.ui.BaseFragment;
import com.josemgu91.habittune.android.ui.common.ConfirmationDialog;
import com.josemgu91.habittune.databinding.FragmentTagEditorBinding;
import com.josemgu91.habittune.domain.usecases.GetTags;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.SelectableAdapter;
import eu.davidea.flexibleadapter.helpers.ItemTouchHelperCallback;
import eu.davidea.flexibleadapter.items.IFlexible;

public class FragmentTagEditor extends BaseFragment {

    private FragmentTagEditorBinding fragmentTagEditorBinding;
    private ViewModelTagEditor viewModelTagEditor;

    private TagEditorFlexibleAdapter recyclerViewTagsAdapter;

    private InputMethodManager inputMethodManager;

    private ConfirmationDialog tagDeletionConfirmationDialog;

    private FragmentManager fragmentManager;

    private final static String FRAGMENT_TAG_DELETION_DIALOG = "deletionDialog";

    private final static String SAVED_INSTANCE_STATE_KEY_TAG_ID_TO_DELETE = "tagIdToDelete";
    public final static String SAVED_INSTANCE_STATE_KEY_SELECTED_TAGS_IDS = "selectedTagsIds";

    private String tagIdToDelete;

    private List<GetTags.Output> tags;

    private SharedViewModelTagEditor sharedViewModelTagEditor;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        viewModelTagEditor = ViewModelProviders.of(this, viewModelFactory).get(ViewModelTagEditor.class);
        sharedViewModelTagEditor = ViewModelProviders.of(getActivity(), viewModelFactory).get(SharedViewModelTagEditor.class);
        inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
        viewModelTagEditor.fetchTags();
        fragmentManager = getFragmentManager();
        tagDeletionConfirmationDialog = (ConfirmationDialog) fragmentManager.findFragmentByTag(FRAGMENT_TAG_DELETION_DIALOG);
        if (tagDeletionConfirmationDialog == null) {
            tagDeletionConfirmationDialog = ConfirmationDialog.newInstance(
                    R.string.tag_editor_delete_dialog_title,
                    R.string.tag_editor_delete_dialog_content,
                    R.string.action_delete,
                    R.string.action_cancel
            );
        }
        tagDeletionConfirmationDialog.setOnPositiveClickListener(() -> {
            viewModelTagEditor.deleteTag(tagIdToDelete);
            tagIdToDelete = null;
        });
    }

    private void onRestoreInstanceState(Bundle savedInstanceState) {
        tagIdToDelete = savedInstanceState.getString(SAVED_INSTANCE_STATE_KEY_TAG_ID_TO_DELETE);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_INSTANCE_STATE_KEY_TAG_ID_TO_DELETE, tagIdToDelete);
    }

    @Override
    protected ToolbarOptions createToolbarOptions() {
        return new ToolbarOptions(R.id.toolbar);
    }

    @NonNull
    @Override
    public View createView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentTagEditorBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tag_editor, container, false);
        recyclerViewTagsAdapter = new TagEditorFlexibleAdapter(getContext());
        fragmentTagEditorBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentTagEditorBinding.recyclerView.setAdapter(recyclerViewTagsAdapter);
        recyclerViewTagsAdapter.setSwipeEnabled(true);
        recyclerViewTagsAdapter.setMode(SelectableAdapter.Mode.MULTI);
        final ItemTouchHelperCallback itemTouchHelperCallback = recyclerViewTagsAdapter.getItemTouchHelperCallback();
        itemTouchHelperCallback.setSwipeFlags(ItemTouchHelper.RIGHT);
        return fragmentTagEditorBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fragmentTagEditorBinding.setShowProgress(true);
        viewModelTagEditor.getGetTagsResponse().observe(getViewLifecycleOwner(), response -> {
            switch (response.status) {
                case LOADING:
                    fragmentTagEditorBinding.setShowProgress(true);
                    break;
                case ERROR:
                    fragmentTagEditorBinding.setShowProgress(false);
                    break;
                case SUCCESS:
                    fragmentTagEditorBinding.setShowProgress(false);
                    response.successData.observe(getViewLifecycleOwner(), outputs -> {
                        tags = outputs;
                        showTags(outputs);
                    });
                    break;
            }
        });
        fragmentTagEditorBinding.editTextToolbarTextInput.requestFocus();
        fragmentTagEditorBinding.editTextToolbarTextInput.addTextChangedListener(new TextWatcher() {
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
        fragmentTagEditorBinding.editTextToolbarTextInput.setOnEditorActionListener((v, actionId, event) -> {
            if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
                return false;
            }
            createTagAndClearAndDismissKeyboard(fragmentTagEditorBinding.editTextToolbarTextInput.getText().toString());
            return true;
        });
        recyclerViewTagsAdapter.setOnCreateTagItemClickListener(() -> createTagAndClearAndDismissKeyboard(fragmentTagEditorBinding.editTextToolbarTextInput.getText().toString()));
        recyclerViewTagsAdapter.setOnTagNameEditionFinishedListener((position, view, tagId, newName) -> {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            if (tags.contains(new GetTags.Output(tagId, newName))) {
                recyclerViewTagsAdapter.notifyItemChanged(position);
                return;
            }
            viewModelTagEditor.updateTag(tagId, newName);
        });
        recyclerViewTagsAdapter.addListener(new FlexibleAdapter.OnItemSwipeListener() {
            @Override
            public void onItemSwipe(int position, int direction) {
                tagDeletionConfirmationDialog.show(fragmentManager, FRAGMENT_TAG_DELETION_DIALOG);
                final TagEditorFlexibleAdapter.TagItem tagItem = (TagEditorFlexibleAdapter.TagItem) recyclerViewTagsAdapter.getItem(position);
                tagIdToDelete = tagItem.getTagId();
                tagDeletionConfirmationDialog.setOnNegativeClickListener(() -> recyclerViewTagsAdapter.notifyItemChanged(position));
                tagDeletionConfirmationDialog.setOnDismissListener(() -> recyclerViewTagsAdapter.notifyItemChanged(position));
            }

            @Override
            public void onActionStateChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        fragmentInteractionListener.updateToolbar("", FragmentInteractionListener.IC_NAVIGATION_UP);
        fragmentInteractionListener.updateNavigationDrawer(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        sharedViewModelTagEditor.setSelectedTagIds(recyclerViewTagsAdapter.getSelectedTagsIds());
    }

    private void showTags(List<GetTags.Output> outputs) {
        //TODO: Only update the diff in the list (maybe with DiffUtil or with the FlexibleAdapter built in options?).
        final List<IFlexible> tagItems = new ArrayList<>();
        for (final GetTags.Output output : outputs) {
            final TagEditorFlexibleAdapter.TagItem tagItem = new TagEditorFlexibleAdapter.TagItem(output.getId(), output.getName());
            tagItem.setSwipeable(true);
            tagItem.setSelectable(true);
            tagItems.add(tagItem);
        }
        recyclerViewTagsAdapter.clear();
        recyclerViewTagsAdapter.updateDataSet(tagItems);
        //sharedViewModelTagEditor.getSelectedTagIds().observe(this, ids -> recyclerViewTagsAdapter.setSelectedTags(ids));

    }

    private void createTagAndClearAndDismissKeyboard(final String tagName) {
        viewModelTagEditor.createTag(tagName);
        fragmentTagEditorBinding.editTextToolbarTextInput.getText().clear();
        inputMethodManager.hideSoftInputFromWindow(fragmentTagEditorBinding.editTextToolbarTextInput.getWindowToken(), 0);
    }
}
