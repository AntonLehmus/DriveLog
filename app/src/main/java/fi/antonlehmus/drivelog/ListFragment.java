package fi.antonlehmus.drivelog;



import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.raizlabs.android.dbflow.list.FlowQueryList;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;


public class ListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);
        return view;
    }
    @Override
    public void onResume(){

        //read all journeys from database
        FlowQueryList<Journey> list = new FlowQueryList<>(SQLite.select().from(Journey.class).orderBy(Journey_Table.dateTime,false));

        // Construct the data source
        ArrayList<Journey> journeyArray = new ArrayList<Journey>();
        // Create the adapter to convert the array to views
        JourneysAdapter adapter = new JourneysAdapter(getActivity(), journeyArray);
        // Attach the adapter to a ListView
        ListView listView = (ListView) getActivity().findViewById(R.id.journeyList);
        listView.setAdapter(adapter);

        adapter.addAll(list);

        super.onResume();
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
