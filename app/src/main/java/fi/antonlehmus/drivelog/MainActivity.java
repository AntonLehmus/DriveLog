package fi.antonlehmus.drivelog;


import android.content.Context;
import android.content.SharedPreferences;
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


import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import java.util.List;
import java.util.concurrent.TimeUnit;

import SlidingTab.SlidingTabLayout;


public class MainActivity extends AppCompatActivity {

    public  String odoStartStr;
    public  String odoStopStr;
    public  String descStr;
    public  Long dateTime;

    private String journeyType;
    private Toolbar toolbar;
    private ViewPager pager;
    private PagerAdapter adapter;
    private SlidingTabLayout tabs;
    private int Numboftabs = 2;

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

        // Assign the Sliding Tab Layout View
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

        journeyType = this.getString(R.string.personal);

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
                    journeyType = this.getString(R.string.personal);
                }
                break;
            case R.id.radio_work:
                if (checked) {
                    journeyType = this.getString(R.string.work);;
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
        //read user input
        android.support.design.widget.TextInputEditText odometerStart =
                (android.support.design.widget.TextInputEditText) findViewById(R.id.odometerStart);
        odoStartStr = odometerStart.getText().toString();

        android.support.design.widget.TextInputEditText odometerStop =
                (android.support.design.widget.TextInputEditText) findViewById(R.id.odometerStop);
        odoStopStr = odometerStop.getText().toString();


        TextView dateView = (TextView) findViewById(R.id.datePicker);
        String dateStr = dateView.getText().toString();
        TextView timeView = (TextView) findViewById(R.id.timePicker);
        String timeStr = timeView.getText().toString();

        SharedPreferences sharedPref =  getSharedPreferences(constants.SAVED_DATE_TIME, Context.MODE_PRIVATE);
        dateTime = sharedPref.getLong(constants.KEY_DATE,0)+sharedPref.getLong(constants.KEY_TIME,0);

        EditText description = (EditText) findViewById(R.id.description);
        descStr = description.getText().toString();

        if(!(odoStartStr.equals("")) && !(odoStopStr.equals("")) && !(dateStr.equals(""))) {
            if(Long.parseLong(odoStartStr) >= Long.parseLong(odoStopStr)){
                Toast.makeText(this,getText(R.string.odometerStartLargerThanStop), Toast.LENGTH_LONG).show();
            }
            else{
                FlowManager.getDatabase(DBJourney.class).executeTransaction(new ITransaction() {
                    @Override
                    public void execute(DatabaseWrapper databaseWrapper) {
                        Journey newJourney = new Journey();
                        //save user input
                        newJourney.setOdometerStart(Long.parseLong(odoStartStr));
                        newJourney.setOdometerStop(Long.parseLong(odoStopStr));
                        newJourney.setType(journeyType);
                        newJourney.setDateTime((int) TimeUnit.MILLISECONDS.toSeconds(dateTime));
                        newJourney.setDescription(descStr);

                        newJourney.save(databaseWrapper); // use wrapper (from BaseModel)
                    }
                });

                Toast.makeText(this,getText(R.string.saveSuccess), Toast.LENGTH_LONG).show();
                discardData(getWindow().getDecorView());
                ListFragment.list.refresh();
            }
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
        tvDesc.setText("");

        //reset odometer readings
        resetOdoMeterFields(getWindow().getDecorView());

    }

    public static void resetOdoMeterFields(View view){
        List<Journey> latestOdoStop = new Select(Journey_Table.odometerStop).from(Journey.class).orderBy(Journey_Table.dateTime,false).limit(1).queryList();

        if(latestOdoStop.size()>0) {
            String odoStartStr = Long.toString(latestOdoStop.get(0).odometerStop);

            //reset odometerStart reading
            android.support.design.widget.TextInputEditText odoMeterStart = (android.support.design.widget.TextInputEditText) view.findViewById(R.id.odometerStart);
            odoMeterStart.setText(odoStartStr);

            //reset odometerStop reading
            android.support.design.widget.TextInputEditText odoMeterStop = (android.support.design.widget.TextInputEditText) view.findViewById(R.id.odometerStop);

            //pre-fill odometerStop field
            String odoStopStr = odoStartStr.substring(0, (int) (odoStartStr.length() * 0.7));

            odoMeterStop.setText(odoStopStr);
        }

    }

}
