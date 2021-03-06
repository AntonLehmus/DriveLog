package fi.antonlehmus.drivelog;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance(TimeZone.getDefault());
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        //save times in gmt
        Calendar myCalendar = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
        myCalendar.setTimeInMillis(0);//set to start of Unix time
        myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        myCalendar.set(Calendar.MINUTE, minute);
        myCalendar.set(Calendar.SECOND, 0);
        myCalendar.set(Calendar.MILLISECOND, 0);

        //show in local time
        Calendar uiCal = Calendar.getInstance(TimeZone.getDefault());
        uiCal.setTimeInMillis(0);//set to start of Unix time
        uiCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        uiCal.set(Calendar.MINUTE, minute);
        uiCal.set(Calendar.SECOND, 0);
        uiCal.set(Calendar.MILLISECOND, 0);


        //save time
        SharedPreferences sharedPref = getActivity().getSharedPreferences(constants.SAVED_DATE_TIME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(constants.KEY_TIME, myCalendar.getTimeInMillis ());
        editor.apply();

        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String  currentTime = df.format(uiCal.getTime());

        TextView tv = (TextView) getActivity().findViewById(R.id.timePicker);
        tv.setText(currentTime);
    }
}