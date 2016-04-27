package fi.antonlehmus.drivelog;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class JourneysAdapter extends ArrayAdapter<Journey> {
    public JourneysAdapter(Context context, ArrayList<Journey> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Journey journey = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_item, parent, false);
        }
        // Lookup view for data population
        TextView tvType = (TextView) convertView.findViewById(R.id.listJourneyType);
        TextView tvDescription = (TextView) convertView.findViewById(R.id.listJourneyDescription);
        TextView tvDateTime = (TextView) convertView.findViewById(R.id.listJourneyDateTime);
        TextView tvLength = (TextView) convertView.findViewById(R.id.listJourneyLength);
        // Populate the data into the template view using the data object

        if(journey.type) {
            tvType.setText(R.string.personal);
        }
        else{
            tvType.setText(R.string.work);
        }
        tvDescription.setText(journey.description);
        tvDateTime.setText(journey.dateTime);
        tvLength.setText(String.valueOf(journey.odometerStop-journey.odometerStart));

        // Return the completed view to render on screen
        return convertView;
    }
}