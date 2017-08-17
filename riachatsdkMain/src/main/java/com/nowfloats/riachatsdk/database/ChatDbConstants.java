package com.nowfloats.riachatsdk.database;

import com.nowfloats.riachatsdk.BuildConfig;

/**
 * Created by RAJA on 20-06-2016.
 */
public class ChatDbConstants {

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";

    public interface IChatHistory {
        String tableName = "chat_history";
        String ID = "_ID";
        String JSON_DATA = "json_data";
        String NEXT_NODE = "next_node";
        String CREATE_CHAT_HISTORY_TABLE = "CREATE TABLE " + tableName + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + JSON_DATA + " TEXT,"+ NEXT_NODE + " TEXT)";
    }

    public interface IChatValues {
        String tableName = "chat_values";
        String ID = "_ID";
        String JSON_DATA = "json_data";
        String CREATE_CHAT_VALUES_TABLE = "CREATE TABLE " + tableName + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + JSON_DATA + " TEXT)";
    }

}
