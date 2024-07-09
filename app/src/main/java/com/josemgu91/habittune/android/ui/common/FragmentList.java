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

package com.josemgu91.habittune.android.ui.common;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.ui.BaseFragment;
import com.josemgu91.habittune.databinding.FragmentListBinding;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.helpers.ItemTouchHelperCallback;
import eu.davidea.flexibleadapter.items.IFlexible;

public abstract class FragmentList<E extends IFlexible> extends BaseFragment {

    protected FragmentListBinding fragmentListBinding;

    protected FlexibleAdapter<E> recyclerViewFlexibleAdapter;

    private ConfirmationDialog deletionConfirmationDialog;

    private final static String FRAGMENT_TAG_DELETION_DIALOG = "deletionDialog";

    protected E itemToDelete;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        deletionConfirmationDialog = (ConfirmationDialog) getFragmentManager().findFragmentByTag(FRAGMENT_TAG_DELETION_DIALOG);
        if (deletionConfirmationDialog == null) {
            deletionConfirmationDialog = createDeletionConfirmationDialog();
        }
        deletionConfirmationDialog.setOnPositiveClickListener(() -> delete());
    }

    private void showDeletionDialog() {
        deletionConfirmationDialog.show(getFragmentManager(), FRAGMENT_TAG_DELETION_DIALOG);
    }

    private void delete() {
        final int position = recyclerViewFlexibleAdapter.getGlobalPositionOf(itemToDelete);
        recyclerViewFlexibleAdapter.removeItem(position);
        onDelete(itemToDelete);
        itemToDelete = null;
    }

    protected abstract void saveItemToDeleteState(Bundle outState);

    protected abstract E restoreItemToDeleteState(Bundle savedInstanceState);

    protected abstract ConfirmationDialog createDeletionConfirmationDialog();

    protected abstract void onDelete(final E itemToDelete);

    protected abstract void onItemSelected(final E item);

    @Override
    protected ToolbarOptions createToolbarOptions() {
        return new ToolbarOptions(true);
    }

    @NonNull
    @Override
    public View createView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentListBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);
        recyclerViewFlexibleAdapter = new FlexibleAdapter<>(null, null, true);
        fragmentListBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentListBinding.recyclerView.setAdapter(recyclerViewFlexibleAdapter);
        recyclerViewFlexibleAdapter.setSwipeEnabled(true);
        recyclerViewFlexibleAdapter.addListener((FlexibleAdapter.OnItemClickListener) (view, position) -> {
            onItemSelected(recyclerViewFlexibleAdapter.getItem(position));
            return true;
        });
        final ItemTouchHelperCallback itemTouchHelperCallback = recyclerViewFlexibleAdapter.getItemTouchHelperCallback();
        itemTouchHelperCallback.setSwipeFlags(ItemTouchHelper.RIGHT);
        return fragmentListBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
        fragmentListBinding.setShowProgress(false);
        recyclerViewFlexibleAdapter.addListener(new FlexibleAdapter.OnItemSwipeListener() {
            @Override
            public void onItemSwipe(int position, int direction) {
                itemToDelete = recyclerViewFlexibleAdapter.getItem(position);
                showDeletionDialog();
                deletionConfirmationDialog.setOnNegativeClickListener(() -> recyclerViewFlexibleAdapter.notifyItemChanged(position));
                deletionConfirmationDialog.setOnDismissListener(() -> recyclerViewFlexibleAdapter.notifyItemChanged(position));
            }

            @Override
            public void onActionStateChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

            }
        });
    }

    private void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        itemToDelete = restoreItemToDeleteState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (itemToDelete != null) {
            saveItemToDeleteState(outState);
        }
    }
}
