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

package com.josemgu91.habittune.android.ui.tag_editor;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.josemgu91.habittune.R;

public class TagDeletionConfirmationDialog extends DialogFragment {

    private OnDeleteClickListener onDeleteClickListener;
    private OnCancelListener onCancelListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog
                .Builder(getContext())
                .setTitle(R.string.tag_editor_delete_dialog_title)
                .setMessage(R.string.tag_editor_delete_dialog_content)
                .setPositiveButton(R.string.action_delete, (dialog, which) -> {
                    if (onDeleteClickListener != null) {
                        onDeleteClickListener.onDeleteClick();
                    }
                })
                .setNegativeButton(R.string.action_cancel, (dialog, which) -> {
                    if (onCancelListener != null) {
                        onCancelListener.onCancel();
                    }
                })
                .create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (onCancelListener != null) {
            onCancelListener.onCancel();
        }
    }

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    public void setOnCancelListener(OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    public interface OnDeleteClickListener {

        void onDeleteClick();
    }

    public interface OnCancelListener {

        void onCancel();
    }
}
