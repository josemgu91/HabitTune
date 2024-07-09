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

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.util.Objects;

public class FragmentKey implements Parcelable {

    private final String fragmentTag;
    @Nullable
    private final Bundle arguments;

    public FragmentKey(String fragmentTag, @Nullable Bundle arguments) {
        this.fragmentTag = fragmentTag;
        this.arguments = arguments;
    }

    protected FragmentKey(Parcel in) {
        fragmentTag = in.readString();
        arguments = in.readBundle();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fragmentTag);
        dest.writeBundle(arguments);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FragmentKey> CREATOR = new Creator<FragmentKey>() {
        @Override
        public FragmentKey createFromParcel(Parcel in) {
            return new FragmentKey(in);
        }

        @Override
        public FragmentKey[] newArray(int size) {
            return new FragmentKey[size];
        }
    };

    public String getFragmentTag() {
        return fragmentTag;
    }

    @Nullable
    public Bundle getArguments() {
        return arguments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FragmentKey that = (FragmentKey) o;
        return Objects.equals(fragmentTag, that.fragmentTag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fragmentTag);
    }
}