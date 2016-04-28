package fi.antonlehmus.drivelog;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class JourneysAdapter extends ArrayAdapter<Journey> {
    public JourneysAdapter(Context context, ArrayList<Journey> journeys) {
        super(context, 0, journeys);
    }

    private static class ViewHolder {
        TextView type;
        TextView description;
        TextView dateTime;
        TextView length;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Journey journey = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_view_item, parent, false);
            viewHolder.type = (TextView) convertView.findViewById(R.id.listJourneyType);
            viewHolder.description = (TextView) convertView.findViewById(R.id.listJourneyDescription);
            viewHolder.dateTime = (TextView) convertView.findViewById(R.id.listJourneyDateTime);
            viewHolder.length = (TextView) convertView.findViewById(R.id.listJourneyLength);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.type.setText(journey.type);
        viewHolder.description.setText(journey.description);

        viewHolder.length.setText(Long.toString(journey.odometerStop-journey.odometerStart));

        //parse calendar datetime
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(TimeUnit.SECONDS.toMillis(journey.dateTime));

        DateFormat df = DateFormat.getDateTimeInstance();

        viewHolder.dateTime.setText(df.format(cal.getTime()));

        // Return the completed view to render on screen
        return convertView;
    }
}