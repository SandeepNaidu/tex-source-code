package com.project.tex.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;


public class MonthPickerDialog extends AlertDialog implements OnClickListener, OnDateChangedListener {

    protected MonthPickerDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

    }
}