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

package com.josemgu91.habittune.android.navigation;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.zhuinden.simplestack.StateChange;

public class FragmentStateChanger {
    private final FragmentManager fragmentManager;
    @IdRes
    private final int fragmentContainerId;

    private final FragmentKeyFactory.FragmentFactory fragmentFactory;

    public FragmentStateChanger(FragmentManager fragmentManager, int fragmentContainerId, FragmentKeyFactory.FragmentFactory fragmentFactory) {
        this.fragmentManager = fragmentManager;
        this.fragmentContainerId = fragmentContainerId;
        this.fragmentFactory = fragmentFactory;
    }

    public void handleStateChange(StateChange stateChange) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().disallowAddToBackStack();
        if (stateChange.getDirection() == StateChange.FORWARD) {
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (stateChange.getDirection() == StateChange.BACKWARD) {
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (stateChange.getDirection() == StateChange.REPLACE) {
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        for (final FragmentKey oldKey : stateChange.<FragmentKey>getPreviousKeys()) {
            final Fragment fragment = fragmentManager.findFragmentByTag(oldKey.getFragmentTag());
            if (fragment != null) {
                if (!stateChange.getNewKeys().contains(oldKey)) {
                    fragmentTransaction.remove(fragment);
                } else if (!fragment.isDetached()) {
                    fragmentTransaction.detach(fragment);
                }
            }
        }
        for (final FragmentKey newKey : stateChange.<FragmentKey>getNewKeys()) {
            Fragment fragment = fragmentManager.findFragmentByTag(newKey.getFragmentTag());
            if (newKey.equals(stateChange.topNewKey())) {
                if (fragment != null) {
                    if (fragment.isDetached()) {
                        fragmentTransaction.attach(fragment);
                    }
                } else {
                    fragment = fragmentFactory.createFragment(newKey);
                    fragmentTransaction.add(fragmentContainerId, fragment, newKey.getFragmentTag());
                }
            } else {
                if (fragment != null && !fragment.isDetached()) {
                    fragmentTransaction.detach(fragment);
                }
            }
        }
        fragmentTransaction.commitNow();
    }
}
