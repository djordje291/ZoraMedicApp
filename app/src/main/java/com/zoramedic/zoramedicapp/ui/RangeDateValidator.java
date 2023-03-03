package com.zoramedic.zoramedicapp.ui;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.util.Log;

import androidx.core.util.Pair;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@SuppressLint("ParcelCreator")
public class RangeDateValidator implements CalendarConstraints.DateValidator{

    private Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    private Date theDay;
    private Calendar calendarStart;
    private Calendar calendarEnd;

    public RangeDateValidator(Pair<Long, Long> pair) {
        calendarStart = Calendar.getInstance();
        calendarStart.setTime(new Date(pair.first));
        calendarStart.add(Calendar.HOUR_OF_DAY, -24);
        calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(new Date(pair.second));
        calendarEnd.add(Calendar.HOUR_OF_DAY, +1);
        calendarEnd.add(Calendar.SECOND, +1);

    }

    @Override
    public boolean isValid(long date) {
        theDay = new Date(date);
        return theDay.after(calendarStart.getTime()) && theDay.before(calendarEnd.getTime());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
