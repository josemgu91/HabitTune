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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;
import com.josemgu91.habittune.R;

public class TourActivity extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (int i = 0; i < 5; i++) {
            final SliderPage sliderPage = new SliderPage();
            sliderPage.setTitle("Title " + i);
            sliderPage.setDescription("Description " + i);
            sliderPage.setImageDrawable(R.mipmap.ic_launcher);
            sliderPage.setBgColor(ContextCompat.getColor(this, R.color.primary));
            sliderPage.setDescColor(ContextCompat.getColor(this, R.color.primaryText));
            sliderPage.setTitleColor(ContextCompat.getColor(this, R.color.primaryText));
            addSlide(AppIntroFragment.newInstance(sliderPage));
        }
        showSkipButton(false);
        setColorDoneText(ContextCompat.getColor(this, R.color.primaryText));
        setNextArrowColor(ContextCompat.getColor(this, R.color.primaryText));
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        finish();
    }
}
