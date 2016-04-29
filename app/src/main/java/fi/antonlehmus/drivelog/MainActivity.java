package fi.antonlehmus.drivelog;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import com.opencsv.CSVWriter;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.list.FlowCursorList;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import SlidingTab.SlidingTabLayout;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int FILE_PERMISSIONS_REQUEST = 1;

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
        if (id == R.id.action_export) {

            if (Build.VERSION.SDK_INT >= 23){
                getPermissionToStorage();
            }

            ExportDatabaseCSVTask task=new ExportDatabaseCSVTask();
            task.execute();
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

    @TargetApi(23)
    public void getPermissionToStorage() {
        //Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI
            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    FILE_PERMISSIONS_REQUEST);
        }
    }

    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (requestCode == FILE_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }




    private class ExportDatabaseCSVTask extends AsyncTask<String, Void, Boolean> {

        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        File file=null;

        @Override
        protected void onPreExecute() {

            this.dialog.setMessage("Exporting database...");
            this.dialog.show();

        }

        @Override
        protected Boolean doInBackground(final String... args){

            File dbFile=getDatabasePath(DBJourney.NAME);
            Log.v(TAG, "Db path is: "+dbFile);  //get the path of db

            File exportDir = new File(Environment.getExternalStorageDirectory(), "");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            file = new File(exportDir, getString(R.string.app_name)+".csv");
            try {

                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));

                Journey journey=null;

                // this is the Column of the table and same for Header of CSV file
                String arrStr1[] ={getString(R.string.type),getString(R.string.date), getString(R.string.length)};
                csvWrite.writeNext(arrStr1);

                if( ListFragment.list.getCount() > 0)
                {
                    for(int index=0; index < ListFragment.list.getCount(); index++)
                    {
                        journey = ListFragment.list.getItem(index);

                        //parse calendar datetime
                        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
                        cal.setTimeInMillis(TimeUnit.SECONDS.toMillis(journey.getDateTime()));

                        //full date, short time.
                        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.SHORT);
                        df.setTimeZone(TimeZone.getTimeZone("gmt"));

                        String arrStr[] ={journey.getType(),df.format(cal.getTime()),Long.toString(journey.getOdometerStop()- journey.getOdometerStart())};
                        csvWrite.writeNext(arrStr);
                    }
                }

                csvWrite.close();
                return true;
            }
            catch (IOException e){
                Log.e("MainActivity", e.getMessage(), e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (this.dialog.isShowing()){
                this.dialog.dismiss();
            }
            if (success){
                Toast.makeText(MainActivity.this,getString(R.string.exportSuccess), Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(MainActivity.this, getString(R.string.exportFail), Toast.LENGTH_LONG).show();
            }
        }
    }

}
