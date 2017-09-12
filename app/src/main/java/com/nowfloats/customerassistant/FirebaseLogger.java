package com.nowfloats.customerassistant;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nowfloats.customerassistant.models.MessageDO;

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


    public interface SAMSTATUS {
        public int BUBBLE_CLICKED = 0;
        public int HAS_DATA = 1;
        public int HAS_NO_DATA = 2;
        public int SERVER_ERROR = -100;
        public int SELECTED_MESSAGES = 3;
        public int ACTION_CALL = 4;
        public int ACTION_SHARE = 5;
    }

    private FirebaseLogger() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void logSAMEvent(String messageId, int status, String fpId, String appVersion) {

        Calendar calendar = Calendar.getInstance();
        String currentTime = String.valueOf(calendar.getTimeInMillis());


        MessageDO messageDO = new MessageDO();
        messageDO.setMessageId(messageId);
        messageDO.setFpId(fpId);
        messageDO.setDateTime(currentTime);
        messageDO.setAppVersion(appVersion);
        messageDO.setStatus(status);

        mDatabase.child(DB_SAM_CHILD_NAME).push().setValue(messageDO);

    }
}
