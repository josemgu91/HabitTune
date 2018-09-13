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

package com.josemgu91.habittune.android.ui.routines;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;

public class RecyclerViewAdapterRoutines extends FlexibleAdapter<IFlexible> {

    private final static String SAVED_INSTANCE_STATE_KEY_SWIPED_ITEMS_IDS = "swipedItemsIds";
    private final ArrayList<Long> swipedItemsIds;

    public RecyclerViewAdapterRoutines(@Nullable List<IFlexible> items, @Nullable Object listeners) {
        super(items, listeners, true);
        swipedItemsIds = new ArrayList<>();
        setSwipeEnabled(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        final long[] swipedItemsIdsArray = new long[swipedItemsIds.size()];
        for (int i = 0; i < swipedItemsIds.size(); i++) {
            swipedItemsIdsArray[i] = swipedItemsIds.get(i);
        }
        outState.putLongArray(SAVED_INSTANCE_STATE_KEY_SWIPED_ITEMS_IDS, swipedItemsIdsArray);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final long[] swipedItemsIdsArray = savedInstanceState.getLongArray(SAVED_INSTANCE_STATE_KEY_SWIPED_ITEMS_IDS);
        if (swipedItemsIdsArray != null) {
            for (long swipedItemId : swipedItemsIdsArray) {
                swipedItemsIds.add(swipedItemId);
            }
        }
    }

    @Override
    public void onItemSwiped(int position, int direction) {
        super.onItemSwiped(position, direction);
        Log.d("onItemSwiped", "Position: " + position + ", direction: " + direction);
        //getItemId(position);
    }
}
