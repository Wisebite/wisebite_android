package dev.wisebite.wisebite.repository;

import android.content.Context;

import com.firebase.client.DataSnapshot;

import java.util.Date;

import dev.wisebite.wisebite.domain.Image;
import dev.wisebite.wisebite.domain.OpenTime;
import dev.wisebite.wisebite.utils.FirebaseRepository;

/**
 * Created by albert on 13/03/17.
 * @author albert
 */
public class OpenTimeRepository extends FirebaseRepository<OpenTime> {

    public static final String OBJECT_REFERENCE = "openTime";
    public static final String START_DATE_REFERENCE = "startDate";
    public static final String END_DATE_REFERENCE = "endDate";

    /**
     * Constructor class
     * @param context Repository's context
     */
    public OpenTimeRepository(Context context) {
        super(context);
    }

    @Override
    protected OpenTime convert(DataSnapshot data) {
        OpenTime openTime = new OpenTime();
        openTime.setId(data.getKey());
        for (DataSnapshot d : data.getChildren()) {
            if (d.getKey().equals(START_DATE_REFERENCE)) {
                openTime.setStartDate(d.getValue(Date.class));
            } else if (d.getKey().equals(END_DATE_REFERENCE)) {
                openTime.setEndDate(d.getValue(Date.class));
            }
        }
        return openTime;
    }

    @Override
    public String getObjectReference() {
        return OBJECT_REFERENCE;
    }
}
