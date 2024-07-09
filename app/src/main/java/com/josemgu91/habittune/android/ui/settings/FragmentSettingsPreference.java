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

package com.josemgu91.habittune.android.ui.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.preference.PreferenceFragmentCompat;

import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.Application;
import com.josemgu91.habittune.android.FragmentInteractionListener;
import com.josemgu91.habittune.domain.datagateways.DataGatewayException;
import com.josemgu91.habittune.domain.datagateways.Repository;

public class FragmentSettingsPreference extends PreferenceFragmentCompat {

    private final static int REQUEST_CODE_CREATE_BACKUP = 100;
    private final static int REQUEST_CODE_IMPORT_BACKUP = 200;

    private final static String JSON_MEDIA_TYPE = "application/json";
    private final static String ALL_MEDIA_TYPE = "*/*";

    private FragmentInteractionListener fragmentInteractionListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentInteractionListener = (FragmentInteractionListener) context;
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.settings);
        findPreference(getString(R.string.preferenceKeyCreateBackup)).setOnPreferenceClickListener(preference -> {
            createBackup();
            return true;
        });
        findPreference(getString(R.string.preferenceKeyImportBackup)).setOnPreferenceClickListener(preference -> {
            importBackup();
            return true;
        });
        /*findPreference(getString(R.string.preferenceKeyAbout)).setOnPreferenceClickListener(preference -> {
            showAbout();
            return true;
        });*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CREATE_BACKUP && resultCode == Activity.RESULT_OK) {
            final Uri fileUri = data.getData();
            //TODO: Use a use case instead of calling the repository directly.
            final Handler uiThreadHandler = new Handler();
            AsyncTask.execute(() -> {
                final Repository repository = ((Application) getActivity().getApplication()).getRepository();
                try {
                    repository.exportTo(fileUri.toString());
                } catch (DataGatewayException e) {
                    e.printStackTrace();
                    uiThreadHandler.post(() -> showError(R.string.settings_error_create_backup));
                }
            });
        } else if (requestCode == REQUEST_CODE_IMPORT_BACKUP && resultCode == Activity.RESULT_OK) {
            final Uri fileUri = data.getData();
            //TODO: Use a use case instead of calling the repository directly.
            final Handler uiThreadHandler = new Handler();
            AsyncTask.execute(() -> {
                final Repository repository = ((Application) getActivity().getApplication()).getRepository();
                try {
                    repository.importFrom(fileUri.toString());
                    uiThreadHandler.post(() -> fragmentInteractionListener.updateWidgets());
                } catch (DataGatewayException e) {
                    e.printStackTrace();
                    uiThreadHandler.post(() -> showError(R.string.settings_error_import_backup));
                }
            });
        }
    }

    private void createBackup() {
        final Intent createFileIntent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        createFileIntent.setType(JSON_MEDIA_TYPE);
        if (!doesActivityExist(createFileIntent)) {
            showError(R.string.settings_error_file_explorer);
            return;
        }
        startActivityForResult(createFileIntent, REQUEST_CODE_CREATE_BACKUP);
    }

    private void importBackup() {
        final Intent openFileIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        openFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        openFileIntent.setType(ALL_MEDIA_TYPE);
        if (!doesActivityExist(openFileIntent)) {
            showError(R.string.settings_error_file_explorer);
            return;
        }
        startActivityForResult(openFileIntent, REQUEST_CODE_IMPORT_BACKUP);
    }

    private void showAbout() {

    }

    private boolean doesActivityExist(final Intent intent) {
        return intent.resolveActivity(getContext().getPackageManager()) != null;
    }

    private void showError(@StringRes final int errorMessage) {
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }
}
