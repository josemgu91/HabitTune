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

package com.josemgu91.habittune.android.ui.help;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;
import com.github.appintro.model.SliderPage;
import com.josemgu91.habittune.R;

public class TourActivity extends AppIntro {

    private final int[] drawables = new int[]{
            R.drawable.tour_habittune,
            R.drawable.tour_activity,
            R.drawable.tour_routine,
            R.drawable.tour_routine_entry,
            R.drawable.tour_schedule
    };

    private final int[] background = new int[]{
            R.color.primary,
            R.color.primary,
            R.color.primary,
            R.color.primary,
            R.color.primary
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (int i = 0; i < 5; i++) {
            final SliderPage sliderPage = new SliderPage();
            sliderPage.setTitle(getResources().getStringArray(R.array.help_titles)[i]);
            sliderPage.setDescription(getResources().getStringArray(R.array.help_descriptions)[i]);
            sliderPage.setImageDrawable(drawables[i]);
            sliderPage.setBackgroundColorRes(background[i]);
            sliderPage.setDescriptionColorRes(R.color.primaryText);
            sliderPage.setTitleColorRes(R.color.primaryText);
            addSlide(AppIntroFragment.newInstance(sliderPage));
        }
        setSkipButtonEnabled(false);
        setColorDoneText(ContextCompat.getColor(this, R.color.primaryText));
        setIndicatorColor(
                ContextCompat.getColor(this, R.color.primaryText),
                ContextCompat.getColor(this, R.color.primaryDark)
        );
        setNextArrowColor(ContextCompat.getColor(this, R.color.primaryText));
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        finish();
    }
}
