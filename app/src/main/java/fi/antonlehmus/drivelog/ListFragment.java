package fi.antonlehmus.drivelog;



import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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


        new readJourneys().execute();

        View view = inflater.inflate(R.layout.fragment_list, container, false);
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


    private class readJourneys extends AsyncTask<Void, Void,Void> {
        private ArrayList<Journey> journeyArray = new ArrayList<Journey>();

        @Override
        protected Void doInBackground(Void... params) {
            List<Journey> journeys = databaseHelper.getAllJourneys();
            for (Journey journey : journeys) {
                journeyArray.add(journey);
                //hurr durr
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void nothing) {
            // Create the adapter to convert the array to views
            JourneysAdapter adapter = new JourneysAdapter(getActivity(), journeyArray);
            // Attach the adapter to a ListView
            ListView listView = (ListView) getActivity().findViewById(R.id.journeyList);
            listView.setAdapter(adapter);
        }

    }
}
