package fi.antonlehmus.drivelog;



import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


import com.raizlabs.android.dbflow.list.FlowCursorList;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;


public class ListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private JourneysAdapter adapter;

    public static FlowCursorList<Journey> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        //read all journeys from database
        list = new FlowCursorList<>(SQLite.select().from(Journey.class).orderBy(Journey_Table.dateTime,false));

        return view;
    }
    @Override
    public void onResume(){
        super.onResume();

        // Construct the data source
        final ArrayList<Journey> journeyArray = new ArrayList<>();
        // Create the adapter to convert the array to views
        adapter = new JourneysAdapter(getActivity(), journeyArray);
        // Attach the adapter to a ListView

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onResume, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        list.refresh();
                                    }
                                }
        );

        //listen for listView item clicks
        final ListView listView = (ListView) getActivity().findViewById(R.id.journeyList);
        //listen for long clicks
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                                           long id) {

                FragmentManager fm = getChildFragmentManager();
                DeleteDialogFragment alertDialog = DeleteDialogFragment.newInstance(
                        getString(R.string.deleteDialogTitle),journeyArray.get(position).odometerStart,journeyArray.get(position).odometerStop);
                alertDialog.show(fm, "fragment_alert");
                list.refresh();
                return true;
            }
        });
        listView.setAdapter(adapter);

        //listen for database changes
        list.addOnCursorRefreshListener(new FlowCursorList.OnCursorRefreshListener<Journey>() {
            @Override
            public void onCursorRefreshed(FlowCursorList<Journey> cursorList) {
                refresh();
            }
        });

        list.refresh();
    }

    @Override
    public void onRefresh() {
        list.refresh();
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

    private void refresh(){
        if(!adapter.isEmpty()){
            adapter.clear();
        }
        adapter.addAll(list.getAll());
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

}
