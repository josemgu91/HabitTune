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

package com.josemgu91.habittune.android.ui.common;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ConfirmationDialog extends DialogFragment {

    private final static String ARGUMENT_TITLE = "title";
    private final static String ARGUMENT_MESSAGE = "message";
    private final static String ARGUMENT_POSITIVE_TEXT = "positiveText";
    private final static String ARGUMENT_NEGATIVE_TEXT = "negativeText";

    private OnPositiveClickListener onPositiveClickListener;
    private OnNegativeClickListener onNegativeClickListener;
    private OnDismissListener onDismissListener;

    public void setOnPositiveClickListener(OnPositiveClickListener onPositiveClickListener) {
        this.onPositiveClickListener = onPositiveClickListener;
    }

    public void setOnNegativeClickListener(OnNegativeClickListener onNegativeClickListener) {
        this.onNegativeClickListener = onNegativeClickListener;
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public static ConfirmationDialog newInstance(@StringRes final int title,
                                                 @StringRes final int message,
                                                 @StringRes final int positiveText,
                                                 @StringRes final int negativeText) {
        final Bundle args = new Bundle();
        args.putInt(ARGUMENT_TITLE, title);
        args.putInt(ARGUMENT_MESSAGE, message);
        args.putInt(ARGUMENT_POSITIVE_TEXT, positiveText);
        args.putInt(ARGUMENT_NEGATIVE_TEXT, negativeText);
        final ConfirmationDialog fragment = new ConfirmationDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle arguments = getArguments();
        return new AlertDialog
                .Builder(getContext())
                .setTitle(arguments.getInt(ARGUMENT_TITLE))
                .setMessage(arguments.getInt(ARGUMENT_MESSAGE))
                .setPositiveButton(arguments.getInt(ARGUMENT_POSITIVE_TEXT), (dialog, which) -> {
                    if (onPositiveClickListener != null) {
                        onPositiveClickListener.onPositiveClick();
                    }
                })
                .setNegativeButton(arguments.getInt(ARGUMENT_NEGATIVE_TEXT), (dialog, which) -> {
                    if (onNegativeClickListener != null) {
                        onNegativeClickListener.onNegativeClick();
                    }
                })
                .create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss();
        }
    }

    public interface OnPositiveClickListener {

        void onPositiveClick();
    }

    public interface OnNegativeClickListener {

        void onNegativeClick();
    }

    public interface OnDismissListener {

        void onDismiss();
    }
}
