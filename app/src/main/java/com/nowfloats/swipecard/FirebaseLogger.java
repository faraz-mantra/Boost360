package com.nowfloats.swipecard;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nowfloats.swipecard.models.MessageDO;

import java.util.Calendar;

/**
 * Created by admin on 7/18/2017.
 */

/**
 * Created by NowFloats on 30-03-2017.
 */

public class FirebaseLogger {

    private static FirebaseLogger mFirebaseLogger;
    private DatabaseReference mDatabase;
    private static final String DB_SAM_CHILD_NAME = "SAM";

    public static boolean lastEventStatus;

    public static FirebaseLogger getInstance() {
        if (mFirebaseLogger == null) {
            mFirebaseLogger = new FirebaseLogger();
        }

        return mFirebaseLogger;
    }


    public enum EventStatus {
        COMPLETED(0), DROPPED(1);
        private final int value;

        private EventStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private FirebaseLogger() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void logSAMEvent(String messageId, int status, String fpId) {

        Calendar calendar = Calendar.getInstance();
        String currentTime = String.valueOf(calendar.getTimeInMillis());


        MessageDO messageDO = new MessageDO();
        messageDO.setMessageId(messageId);
        messageDO.setFpId(fpId);
        messageDO.setDateTime(currentTime);
        messageDO.setStatus(status);

        mDatabase.child(DB_SAM_CHILD_NAME).push().setValue(messageDO);

    }
}
