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

package com.josemgu91.habittune.android.ui.statistics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.lifecycle.ViewModelProvider;

import com.josemgu91.habittune.R;
import com.josemgu91.habittune.android.Application;
import com.josemgu91.habittune.android.FragmentInteractionListener;
import com.josemgu91.habittune.android.ui.BaseFragment;
import com.josemgu91.habittune.android.ui.Response;
import com.josemgu91.habittune.android.ui.common.DateFormatter;
import com.josemgu91.habittune.databinding.FragmentStatisticsBinding;
import com.josemgu91.habittune.domain.datagateways.DataGatewayException;
import com.josemgu91.habittune.domain.datagateways.Repository;
import com.josemgu91.habittune.domain.usecases.CalculateAssistanceStatistics;

public class FragmentStatistics extends BaseFragment {

    private final static int REQUEST_CODE_EXPORT_AS_CSV = 100;

    private final static String ARG_ACTIVITY_ID = "activityId";

    private final static String CSV_MEDIA_TYPE = "text/csv";

    public static FragmentStatistics newInstance(@NonNull final String activityId) {
        Bundle args = new Bundle();
        args.putString(ARG_ACTIVITY_ID, activityId);
        FragmentStatistics fragment = new FragmentStatistics();
        fragment.setArguments(args);
        return fragment;
    }

    private String activityId;

    private ViewModelStatistics viewModelStatistics;
    private FragmentStatisticsBinding fragmentStatisticsBinding;

    private ChartHelper chartHelper;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        viewModelStatistics = new ViewModelProvider(this, viewModelFactory).get(ViewModelStatistics.class);
        final Bundle arguments = getArguments();
        activityId = arguments.getString(ARG_ACTIVITY_ID);
        final DateFormatter dateFormatter = new DateFormatter();
        chartHelper = new ChartHelper(context, dateFormatter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_statistics, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionExportToCsv) {
            onExportToCsv();
            return true;
        }
        return false;
    }

    @Override
    protected ToolbarOptions createToolbarOptions() {
        return new ToolbarOptions(true);
    }

    @NonNull
    @Override
    public View createView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentStatisticsBinding = FragmentStatisticsBinding.inflate(inflater, container, false);
        return fragmentStatisticsBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModelStatistics.fetchActivity(activityId);
        viewModelStatistics.getGetActivityResponse().observe(getViewLifecycleOwner(), response -> {
            if (response.status != Response.Status.SUCCESS) {
                return;
            }
            fragmentInteractionListener.updateToolbar(response.successData.getName(), FragmentInteractionListener.IC_NAVIGATION_UP);
        });
        viewModelStatistics.calculateAssistanceStats(activityId);
        viewModelStatistics.getCalculateAssistanceStatsResponse().observe(getViewLifecycleOwner(), response -> {
            if (response.status != Response.Status.SUCCESS) {
                return;
            }
            showAssistanceStatistics(response.successData);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentInteractionListener.updateNavigationDrawer(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_EXPORT_AS_CSV && resultCode == Activity.RESULT_OK) {
            final Uri fileUri = data.getData();
            //TODO: Use a use case instead of calling the repository directly.
            final Handler uiThreadHandler = new Handler();
            AsyncTask.execute(() -> {
                final Repository repository = ((Application) getActivity().getApplication()).getRepository();
                try {
                    repository.exportToCsv(activityId, fileUri.toString());
                } catch (DataGatewayException e) {
                    e.printStackTrace();
                    uiThreadHandler.post(() -> showError(R.string.statistics_error_exporting_csv));
                }
            });
        }
    }

    private void showAssistanceStatistics(CalculateAssistanceStatistics.Output assistanceStatistics) {
        chartHelper.populateChart(fragmentStatisticsBinding.pieChart, assistanceStatistics);
    }

    private void onExportToCsv() {
        final Intent createFileIntent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        createFileIntent.setType(CSV_MEDIA_TYPE);
        if (!doesActivityExist(createFileIntent)) {
            showError(R.string.statistics_error_file_explorer);
            return;
        }
        startActivityForResult(createFileIntent, REQUEST_CODE_EXPORT_AS_CSV);
    }

    private boolean doesActivityExist(final Intent intent) {
        return intent.resolveActivity(getContext().getPackageManager()) != null;
    }

    private void showError(@StringRes final int errorMessage) {
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }
}
