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

package com.josemgu91.habittune.domain.usecases;

import android.support.annotation.NonNull;

import com.josemgu91.habittune.domain.datagateways.ActivityDataGateway;
import com.josemgu91.habittune.domain.datagateways.DataGatewayException;
import com.josemgu91.habittune.domain.entities.Activity;

public class CreateActivity {

    private final UseCaseOutput<Boolean> output;
    private final ActivityDataGateway activityDataGateway;
    private final Activity activity;

    public CreateActivity(@NonNull final UseCaseOutput<Boolean> output, @NonNull ActivityDataGateway activityDataGateway, @NonNull Activity activity) {
        this.output = output;
        this.activityDataGateway = activityDataGateway;
        this.activity = activity;
    }

    public void execute() {
        output.showInProgress();
        try {
            final boolean activityCreated = activityDataGateway.createActivity(activity);
            output.showResult(activityCreated);
        } catch (DataGatewayException e) {
            e.printStackTrace();
            output.showError();
        }
    }

}
