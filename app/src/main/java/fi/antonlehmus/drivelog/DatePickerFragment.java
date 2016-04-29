package fi.antonlehmus.drivelog;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance(TimeZone.getDefault());
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        //save times in gmt
        Calendar myCalendar = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, month);
        myCalendar.set(Calendar.DAY_OF_MONTH, day);
        myCalendar.set(Calendar.HOUR_OF_DAY, 0);
        myCalendar.set(Calendar.MINUTE, 0);
        myCalendar.set(Calendar.SECOND, 0);
        myCalendar.set(Calendar.MILLISECOND, 0);

        //show in local time
        Calendar uiCal = Calendar.getInstance(TimeZone.getDefault());
        uiCal.set(Calendar.YEAR, year);
        uiCal.set(Calendar.MONTH, month);
        uiCal.set(Calendar.DAY_OF_MONTH, day);
        uiCal.set(Calendar.HOUR_OF_DAY, 0);
        uiCal.set(Calendar.MINUTE, 0);
        uiCal.set(Calendar.SECOND, 0);
        uiCal.set(Calendar.MILLISECOND, 0);

        //save time
        SharedPreferences sharedPref = getActivity().getSharedPreferences(constants.SAVED_DATE_TIME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(constants.KEY_DATE, myCalendar.getTimeInMillis ());
        editor.apply();

        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        String  currentTime = df.format(uiCal.getTime());

        TextView tv = (TextView) getActivity().findViewById(R.id.datePicker);
        tv.setText(currentTime);
    }
}