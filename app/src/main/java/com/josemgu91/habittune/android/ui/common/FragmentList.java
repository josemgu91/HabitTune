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
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.FragmentInteractionListener;
import com.josemgu91.habittune.android.ui.BaseFragment;
import com.josemgu91.habittune.databinding.FragmentListBinding;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.helpers.ItemTouchHelperCallback;
import eu.davidea.flexibleadapter.items.IFlexible;

public abstract class FragmentList<E extends IFlexible> extends BaseFragment {

    private FragmentListBinding fragmentListBinding;

    protected FlexibleAdapter<E> recyclerViewFlexibleAdapter;

    private ConfirmationDialog deletionConfirmationDialog;

    private final static String FRAGMENT_TAG_DELETION_DIALOG = "deletionDialog";

    private E itemToDelete;

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
        itemToDelete = null;
    }

    protected abstract void saveItemToDeleteState(Bundle outState);

    protected abstract void restoreItemToDeleteState(Bundle savedInstanceState);

    protected abstract ConfirmationDialog createDeletionConfirmationDialog();

    protected abstract FlexibleAdapter<E> createFlexibleAdapter();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentListBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);
        recyclerViewFlexibleAdapter = createFlexibleAdapter();
        fragmentListBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentListBinding.recyclerView.setAdapter(recyclerViewFlexibleAdapter);
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
        recyclerViewFlexibleAdapter.onRestoreInstanceState(savedInstanceState);
        restoreItemToDeleteState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        recyclerViewFlexibleAdapter.onSaveInstanceState(outState);
        saveItemToDeleteState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentInteractionListener.updateToolbar(getString(R.string.routines_title), FragmentInteractionListener.IC_NAVIGATION_HAMBURGUER);
        fragmentInteractionListener.updateNavigationDrawer(true);
    }
}
