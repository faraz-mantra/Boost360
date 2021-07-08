package com.nowfloats.riachatsdk.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by RAJA on 22-06-2016.
 */
public class ChatDbController implements ChatIdbController {

    private static ChatDbController sDbController = null;
    private Context mContext;

    private ChatDbController(Context context) {
        this.mContext = context;
    }

    public static synchronized ChatDbController getDbController(Context context) {
        if (sDbController == null) {
            sDbController = new ChatDbController(context);
        }
        return sDbController;
    }

    @Override
    public String getChatHistory() {
        Cursor c = mContext.getContentResolver().query(getUri(ChatDbConstants.IChatHistory.tableName), null,
                null, null, ChatDbConstants.IChatHistory.ID + " DESC");

        String chatHistory = "";
        if (c.moveToFirst()) {
            chatHistory = c.getString(c.getColumnIndex(ChatDbConstants.IChatHistory.JSON_DATA));
        } else {
            return null;
        }
        c.close();
        return chatHistory;
    }

    @Override
    public String getChatNextNode() {
        Cursor c = mContext.getContentResolver().query(getUri(ChatDbConstants.IChatHistory.tableName), null,
                null, null, ChatDbConstants.IChatHistory.ID + " DESC");

        String chatHistory = "";
        if (c.moveToFirst()) {
            chatHistory = c.getString(c.getColumnIndex(ChatDbConstants.IChatHistory.NEXT_NODE));
        } else {
            return null;
        }
        c.close();
        return chatHistory;
    }


    @Override
    public Uri postChatHistory(String value, String nextNode) {
        ContentValues values = new ContentValues();
        values.put(ChatDbConstants.IChatHistory.JSON_DATA, value);
        values.put(ChatDbConstants.IChatHistory.NEXT_NODE, nextNode);

        return mContext.getContentResolver().insert(getUri(ChatDbConstants.IChatHistory.tableName), values);
    }

    @Override
    public Map<String, String> getChatValues() {

        Map<String, String> map = new HashMap<String, String>();

        Cursor c = mContext.getContentResolver().query(getUri(ChatDbConstants.IChatValues.tableName), null,
                null, null, ChatDbConstants.IChatValues.ID + " DESC");

        String chatValues = "";
        if (c.moveToFirst()) {
            chatValues = c.getString(c.getColumnIndex(ChatDbConstants.IChatHistory.JSON_DATA));
        } else {
            return null;
        }
        c.close();


        try {
            JSONObject jsonObject = new JSONObject(chatValues);

            Iterator<String> keysItr = jsonObject.keys();
            while (keysItr.hasNext()) {
                String key = keysItr.next();
                Object value = jsonObject.get(key);
                map.put(key, (String) value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    @Override
    public Uri postChatData(String value) {
        ContentValues values = new ContentValues();
        values.put(ChatDbConstants.IChatValues.JSON_DATA, value);

        return mContext.getContentResolver().insert(getUri(ChatDbConstants.IChatValues.tableName), values);

    }

    @Override
    public void clearData() {
        mContext.getContentResolver().delete(getUri(ChatDbConstants.IChatHistory.tableName), null, null);
        mContext.getContentResolver().delete(getUri(ChatDbConstants.IChatValues.tableName), null, null);
    }

    /*
     * Product Gallery End
     */


    /*
     * @getUri returns uri from table name
     */
    private Uri getUri(String tableName) {
        return Uri.parse("content://" + ChatDbConstants.AUTHORITY + "/" + tableName);
    }


    public void deleteDataBase() {
        try {
            mContext.deleteDatabase("BoostDefault.db");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
