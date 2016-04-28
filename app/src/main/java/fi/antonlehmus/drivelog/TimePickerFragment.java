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

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar myCalendar = Calendar.getInstance();
        myCalendar.setTimeInMillis(0);//set to start of Unix time
        myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        myCalendar.set(Calendar.MINUTE, minute);

        //save time
        SharedPreferences sharedPref = getActivity().getSharedPreferences(constants.SAVED_DATE_TIME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(constants.KEY_TIME, myCalendar.getTimeInMillis ());
        editor.apply();

        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String  currentTime = df.format(myCalendar.getTime());

        TextView tv = (TextView) getActivity().findViewById(R.id.timePicker);
        tv.setText(currentTime);
    }
}