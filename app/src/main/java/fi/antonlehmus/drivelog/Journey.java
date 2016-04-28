package fi.antonlehmus.drivelog;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = DBJourney.class)
public class Journey extends BaseModel {
    @Column
    @PrimaryKey
    int id;

    @Column
    @Unique
    long odometerStart;
    @Column
    @Unique
    long odometerStop;
    @Column
    boolean type; //private = true
    @Column
    String dateTime;
    @Column
    String description;
}
