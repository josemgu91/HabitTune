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

package com.josemgu91.habittune;

public class MenuController {

    private final MenuPresenter menuPresenter;
    private final TitleProvider titleProvider;

    public MenuController(MenuPresenter menuPresenter, TitleProvider titleProvider) {
        this.menuPresenter = menuPresenter;
        this.titleProvider = titleProvider;
    }

    interface TitleProvider {

        String getScheduleTitle();

        String getRoutinesTitle();

        String getActivitiesTitle();

        String getStatisticsTitle();

        String getSettingsTitle();

        String getHelpTitle();

    }

    interface MenuPresenter {

        void show(MenuViewModel menuViewModel);

    }

    public static class MenuViewModel {

        public enum Screen {
            SCHEDULE,
            ROUTINES,
            ACTIVITIES,
            STATISTICS,
            SETTINGS,
            HELP
        }

        String title;
        Screen currentScreen;

    }

    private void render(MenuViewModel.Screen screen, String title) {
        final MenuViewModel menuViewModel = new MenuViewModel();
        menuViewModel.currentScreen = screen;
        menuViewModel.title = title;
        menuPresenter.show(menuViewModel);
    }

    public void goToSchedule() {
        render(MenuViewModel.Screen.SCHEDULE, titleProvider.getScheduleTitle());
    }

    public void goToRoutines() {
        render(MenuViewModel.Screen.ROUTINES, titleProvider.getRoutinesTitle());
    }

    public void goToActivities() {
        render(MenuViewModel.Screen.ACTIVITIES, titleProvider.getActivitiesTitle());
    }

    public void goToStatistics() {
        render(MenuViewModel.Screen.STATISTICS, titleProvider.getStatisticsTitle());
    }

    public void goToSettings() {
        render(MenuViewModel.Screen.SETTINGS, titleProvider.getSettingsTitle());
    }

    public void goToHelp() {
        render(MenuViewModel.Screen.HELP, titleProvider.getHelpTitle());
    }

}
