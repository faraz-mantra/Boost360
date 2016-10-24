package com.nowfloats.Analytics_Screen.Graph;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tushar on 19-05-2015.
 */
public class TotalCountParser {

    public int parse(JSONObject jObject) {

        JSONArray entityArray = null;
        int tc = 0;

        try {
            /** Retrieves all the elements in the 'places' array */
            entityArray = jObject.getJSONArray("Entity");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int noOfEntries = entityArray.length();

        try {
            JSONObject entity = (JSONObject) entityArray.get(0);
            tc = Integer.parseInt(entity.getString("NoOfViews"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tc;
    }
}