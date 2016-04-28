package fi.antonlehmus.drivelog;


import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = DBJourney.NAME, version = DBJourney.VERSION)
public class DBJourney {

    public static final String NAME = "MyDataBase";

    public static final int VERSION = 1;
}
