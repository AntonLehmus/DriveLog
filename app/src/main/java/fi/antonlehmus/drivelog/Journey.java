package fi.antonlehmus.drivelog;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = DBJourney.class)
public class Journey extends BaseModel {

    @Column
    long odometerStart;
    @Column
    @PrimaryKey
    long odometerStop;
    @Column
    String type;
    @Column
    int dateTime;
    @Column
    String description;

    public void setOdometerStart(Long odo) {
        this.odometerStart = odo;
    }
    public void setOdometerStop(Long odo) {
        this.odometerStop = odo;
    }
    public void setType(String type){
        this.type=type;
    }
    public void setDateTime(int dateTime){
        this.dateTime=dateTime;
    }
    public void setDescription(String description){
        this.description=description;
    }

    public long getOdometerStart() {
        return odometerStart;
    }

    public long getOdometerStop() {
        return odometerStop;
    }

    public String getType() {
        return type;
    }

    public int getDateTime() {
        return dateTime;
    }

    public String getDescription() {
        return description;
    }
}
