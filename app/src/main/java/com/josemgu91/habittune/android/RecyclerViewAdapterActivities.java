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

package com.josemgu91.habittune.android;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.josemgu91.habittune.databinding.ElementActivityBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jose on 2/15/18.
 */

public class RecyclerViewAdapterActivities extends RecyclerView.Adapter<RecyclerViewAdapterActivities.ViewHolderActivity> {

    private final LayoutInflater layoutInflater;
    private final Context context;

    private List<String> activityList;

    private OnActivitySelectedListener onActivitySelectedListener;
    private OnMultiSelectionModeListener onMultiSelectionModeListener;

    private OnItemClickListener onItemClickListener;

    private boolean isInMultiSelectionMode;
    private List<Integer> selectedItems;

    public RecyclerViewAdapterActivities(final Context context, final LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
        this.context = context;
        activityList = new ArrayList<>();
        isInMultiSelectionMode = false;
        selectedItems = new ArrayList<>();
        onItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemLongClickListener(int position) {
                if (isInMultiSelectionMode) {
                    handleWhenMultiSelectionModeEnabled(position);
                    return;
                }
                isInMultiSelectionMode = true;
                selectedItems.add(position);
                if (onMultiSelectionModeListener != null) {
                    onMultiSelectionModeListener.onMultiSelectionModeEnabled();
                }
            }

            @Override
            public void onItemClickListener(int position) {
                if (isInMultiSelectionMode) {
                    handleWhenMultiSelectionModeEnabled(position);
                    return;
                }
                if (onActivitySelectedListener != null) {
                    onActivitySelectedListener.onActivitySelected(activityList.get(position));
                }
            }

            private void handleWhenMultiSelectionModeEnabled(int position) {
                if (selectedItems.contains(position)) {
                    selectedItems.remove(position);
                } else {
                    selectedItems.add(position);
                }
                if (selectedItems.size() > 0) {
                    return;
                }
                isInMultiSelectionMode = false;
                if (onMultiSelectionModeListener != null) {
                    onMultiSelectionModeListener.onMultiSelectionModeDisabled();
                }
            }

        };
    }

    @NonNull
    @Override
    public ViewHolderActivity onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ElementActivityBinding elementActivityBinding = ElementActivityBinding.inflate(layoutInflater, parent, false);
        return new ViewHolderActivity(elementActivityBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderActivity holder, int position) {
        final String activityName = activityList.get(position);
        holder.bind(activityName, onItemClickListener, position);
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public void setActivities(final List<String> activities) {
        activityList = activities;
        notifyDataSetChanged();
    }

    public void setOnActivitySelectedListener(OnActivitySelectedListener onActivitySelectedListener) {
        this.onActivitySelectedListener = onActivitySelectedListener;
    }

    public void setOnMultiSelectionModeListener(OnMultiSelectionModeListener onMultiSelectionModeListener) {
        this.onMultiSelectionModeListener = onMultiSelectionModeListener;
    }

    public List<String> getSelectedActivities() {
        return null;
    }

    public boolean isInMultiSelectionMode() {
        return isInMultiSelectionMode;
    }

    public void setMultiSelectionMode(final boolean enableMultiSelectionMode) {
        isInMultiSelectionMode = enableMultiSelectionMode;
    }

    static class ViewHolderActivity extends RecyclerView.ViewHolder {

        private final ElementActivityBinding elementActivityBinding;

        private boolean isSelected;

        public ViewHolderActivity(final ElementActivityBinding elementActivityBinding) {
            super(elementActivityBinding.getRoot());
            this.elementActivityBinding = elementActivityBinding;
        }

        public void bind(final String activityName,
                         final OnItemClickListener onItemClickListener,
                         final int position) {
            elementActivityBinding.textViewActivityName.setText(activityName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClickListener(position);
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemLongClickListener(position);
                        return true;
                    }
                    return false;
                }
            });
        }

    }

    private interface OnItemClickListener {

        void onItemLongClickListener(final int position);

        void onItemClickListener(final int position);
    }

    public interface OnActivitySelectedListener {

        void onActivitySelected(final String activityName);

    }

    public interface OnMultiSelectionModeListener {

        void onMultiSelectionModeEnabled();

        void onMultiSelectionModeDisabled();

    }

}
