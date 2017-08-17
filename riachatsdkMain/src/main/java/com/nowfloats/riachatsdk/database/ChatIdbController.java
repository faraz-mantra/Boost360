package com.nowfloats.riachatsdk.database;

import android.net.Uri;

import java.util.Map;


/**
 * Created by RAJA on 22-06-2016.
 */
public interface ChatIdbController {

    /*
     * methods for Chat History
    */
    String getChatHistory();
    String getChatNextNode();
    Uri postChatHistory(String value,String nextNode);

    /*
    * methods for Chat Data
   */
    Map<String, String> getChatValues();
    Uri postChatData(String value);

    void clearData();
    void deleteDataBase();


}
