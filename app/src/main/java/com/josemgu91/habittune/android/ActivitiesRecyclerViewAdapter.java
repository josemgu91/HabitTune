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
import android.widget.ImageView;
import android.widget.TextView;

import com.josemgu91.habittune.R;
import com.josemgu91.habittune.databinding.ElementActivityBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jose on 2/15/18.
 */

public class ActivitiesRecyclerViewAdapter extends RecyclerView.Adapter<ActivitiesRecyclerViewAdapter.ActivityViewHolder> {

    private final LayoutInflater layoutInflater;
    private final Context context;

    private List<String> activityList;

    private OnActivitySelectedListener onActivitySelectedListener;

    public ActivitiesRecyclerViewAdapter(final Context context, final LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
        this.context = context;
        activityList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ElementActivityBinding elementActivityBinding = ElementActivityBinding.inflate(layoutInflater, parent, false);
        return new ActivityViewHolder(elementActivityBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        final String activityName = activityList.get(position);
        holder.bind(activityName, onActivitySelectedListener);
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

    static class ActivityViewHolder extends RecyclerView.ViewHolder {

        private final ElementActivityBinding elementActivityBinding;

        public ActivityViewHolder(final ElementActivityBinding elementActivityBinding) {
            super(elementActivityBinding.getRoot());
            this.elementActivityBinding = elementActivityBinding;
        }

        public void bind(final String activityName,
                         final OnActivitySelectedListener onActivitySelectedListener) {
            elementActivityBinding.textViewActivityName.setText(activityName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onActivitySelectedListener != null) {
                        onActivitySelectedListener.onActivitySelected(activityName);
                    }
                }
            });
        }

    }

    public interface OnActivitySelectedListener {

        void onActivitySelected(String activityName);

    }

}
