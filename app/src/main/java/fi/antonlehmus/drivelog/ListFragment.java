package fi.antonlehmus.drivelog;



import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class ListFragment extends Fragment {

    private dbHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Get singleton instance of database
        databaseHelper = dbHelper.getInstance(getActivity());

        List<Journey> journeys = databaseHelper.getAllJourneys();


        View view = inflater.inflate(R.layout.fragment_list, container, false);

        // Construct the data source
        ArrayList<Journey> arrayOfUsers = new ArrayList<Journey>();
        // Create the adapter to convert the array to views
        JourneysAdapter adapter = new JourneysAdapter(getActivity(), arrayOfUsers);
        // Attach the adapter to a ListView
        ListView listView = (ListView) view.findViewById(R.id.journeyList);
        listView.setAdapter(adapter);

        for (Journey journey : journeys) {
            Log.d("ListFragment",journey.toString());
            adapter.add(journey);
        }

        return view;
    }

    //allows both modes
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            Activity a = getActivity();
            if(a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }
    }

}
