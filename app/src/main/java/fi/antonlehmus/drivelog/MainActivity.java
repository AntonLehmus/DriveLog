package fi.antonlehmus.drivelog;




import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;

import SlidingTab.SlidingTabLayout;


public class MainActivity extends AppCompatActivity {

    private static Boolean journeyType = true; //personal

    private Toolbar toolbar;
    private ViewPager pager;
    private PagerAdapter adapter;
    private SlidingTabLayout tabs;
    private int Numboftabs = 2;
    private dbHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] Titles = new String[]{this.getString(R.string.log_fragment_title), this.getString(R.string.list_fragment_title)};
        Numboftabs = Titles.length;

        // Creating The Toolbar and setting it as the Toolbar for the activity

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);


        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new PagerAdapter(getSupportFragmentManager(), Titles, Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.colorAccent);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

        // Get singleton instance of database
        databaseHelper = dbHelper.getInstance(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //implement later
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_personal:
                if (checked) {
                    journeyType = true;
                }
                break;
            case R.id.radio_work:
                if (checked) {
                    journeyType = false;
                }
                break;
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void saveData(View view) {
        Journey current = new Journey();

        //read user input
        android.support.design.widget.TextInputEditText odometerStart =
                (android.support.design.widget.TextInputEditText) findViewById(R.id.odometerStart);
        String odoStartStr;
        odoStartStr = odometerStart.getText().toString();

        android.support.design.widget.TextInputEditText odometerStop =
                (android.support.design.widget.TextInputEditText) findViewById(R.id.odometerStop);
        String odoStopStr;
        odoStopStr = odometerStop.getText().toString();


        TextView dateView = (TextView) findViewById(R.id.datePicker);
        String dateStr = dateView.getText().toString();
        TextView timeView = (TextView) findViewById(R.id.timePicker);
        String timeStr = timeView.getText().toString();

        String dateTimeStr = new String();
        dateTimeStr = dateTimeStr.concat(dateStr).
                concat(" ").
                concat(timeStr);

        EditText description = (EditText) findViewById(R.id.description);
        String descStr = "";
        descStr = description.getText().toString();

        if(!(odoStartStr.equals("")) && !(odoStopStr.equals("")) && !(dateStr.equals(""))) {
            //save user input
            current.odometerStart = Long.parseLong(odoStartStr);
            current.odometerStop = Long.parseLong(odoStopStr);
            current.type = journeyType;
            current.dateTime = dateTimeStr;
            current.description = descStr;
            databaseHelper.addJourney(current);

            Toast.makeText(this,getText(R.string.saveSuccess), Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this,getText(R.string.errEmptyFields), Toast.LENGTH_LONG).show();
        }

    }

    public void discardData(View view) {

        //reset the radio buttons
        RadioButton work = (RadioButton) findViewById(R.id.radio_work);
        RadioButton personal = (RadioButton) findViewById(R.id.radio_personal);
        personal.setChecked(true);
        work.setChecked(false);

        //reset date & time
        TextView tvTime = (TextView) this.findViewById(R.id.timePicker);
        tvTime.setText(this.getText(R.string.time));
        TextView tvDate = (TextView) this.findViewById(R.id.datePicker);
        tvDate.setText(this.getText(R.string.date));

        //reset description
        TextView tvDesc = (TextView) this.findViewById(R.id.description);
        tvDesc.setText(this.getText(R.string.description));

        //reset odometer readings

    }


    private class readJourneys extends AsyncTask<String, Void, List<Journey>> {

        @Override
        protected List<Journey> doInBackground(String... params) {
            List<Journey> journeys = databaseHelper.getAllJourneys();
            for (Journey journey : journeys) {

            }
            return journeys;
        }

        @Override
        protected void onPostExecute(List<Journey> result) {

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

}
