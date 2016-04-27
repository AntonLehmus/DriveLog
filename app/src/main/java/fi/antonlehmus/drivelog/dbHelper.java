package fi.antonlehmus.drivelog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class dbHelper extends SQLiteOpenHelper {

    private static final String TAG ="dbHelper";

    private static dbHelper sInstance;

    // Database Info
    private static final String DATABASE_NAME = "driveLogDatabase";
    private static final int DATABASE_VERSION = 4;

    // Table Names
    private static final String TABLE_JOURNEYS = "journeys";

    // Post Table Columns
    private static final String KEY_JOURNEY_ID = "id";
    private static final String KEY_JOURNEY_ODOMETER_START = "odometerStart";
    private static final String KEY_JOURNEY_ODOMETER_STOP = "odometerStop";
    private static final String KEY_JOURNEY_TYPE = "type";
    private static final String KEY_JOURNEY_DATE_TIME = "dateTime";
    private static final String KEY_JOURNEY_DESCRIPTION = "description";


    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private dbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_POSTS_TABLE = "CREATE TABLE " + TABLE_JOURNEYS +
                "(" +
                KEY_JOURNEY_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                KEY_JOURNEY_ODOMETER_START + " UNSIGNED BIG INT," +
                KEY_JOURNEY_ODOMETER_STOP + " UNSIGNED BIG INT," +
                KEY_JOURNEY_TYPE + " TINYINT," +
                KEY_JOURNEY_DATE_TIME + " TEXT," +
                KEY_JOURNEY_DESCRIPTION + " TEXT" +
                ")";

        db.execSQL(CREATE_POSTS_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOURNEYS);
            onCreate(db);
        }
    }

    public static synchronized dbHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new dbHelper(context.getApplicationContext());
        }
        return sInstance;
    }


    public void addJourney(Journey journey) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_JOURNEY_ODOMETER_START, journey.odometerStart);
            values.put(KEY_JOURNEY_ODOMETER_STOP, journey.odometerStop);
            values.put(KEY_JOURNEY_TYPE, journey.type);
            values.put(KEY_JOURNEY_DATE_TIME, journey.dateTime);
            values.put(KEY_JOURNEY_DESCRIPTION, journey.description);

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_JOURNEYS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }

    public List<Journey> getAllJourneys() {
        List<Journey> posts = new ArrayList<>();

        // SELECT * FROM JOURNEYS
        String POSTS_SELECT_QUERY =
                String.format("SELECT * FROM %s",
                        TABLE_JOURNEYS);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Journey newUser = new Journey();
                    newUser.odometerStart = Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_JOURNEY_ODOMETER_START)));
                    newUser.odometerStop = Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_JOURNEY_ODOMETER_STOP)));
                    newUser.type = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(KEY_JOURNEY_TYPE)));
                    newUser.dateTime =cursor.getString(cursor.getColumnIndex(KEY_JOURNEY_DATE_TIME));
                    newUser.description =cursor.getString(cursor.getColumnIndex(KEY_JOURNEY_DESCRIPTION));
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get journeys from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return posts;
    }


    public int updateJourney(Journey journey, int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_JOURNEY_ODOMETER_START, journey.odometerStart);
        values.put(KEY_JOURNEY_ODOMETER_STOP, journey.odometerStop);
        values.put(KEY_JOURNEY_TYPE, journey.type);
        values.put(KEY_JOURNEY_DATE_TIME,journey.dateTime);
        values.put(KEY_JOURNEY_DESCRIPTION,journey.description);


        return db.update(TABLE_JOURNEYS, values, KEY_JOURNEY_ID + " =",
                new String[] { String.valueOf(id) });
    }

    public void deleteJourney(Journey journey, int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Order of deletions is important when foreign key relationships exist.
            db.delete(TABLE_JOURNEYS,KEY_JOURNEY_ID + " =",
                    new String[] { String.valueOf(id) });
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete journeys id= "+id);
        } finally {
            db.endTransaction();
        }
    }

}