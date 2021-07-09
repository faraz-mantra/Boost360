package com.nowfloats.signup.UI.Places;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.thinksity.R;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by guru on 28/07/2015.
 */
public class ParserTask extends AsyncTask<String, Integer, ArrayList<String>> {

    JSONObject jObject;
    Activity activity;

    public ParserTask(Activity activity){
            this.activity = activity;
    }

    @Override
    protected ArrayList<String> doInBackground(String... jsonData) {

        ArrayList<String> places = null;

        PlaceJSONParser placeJsonParser = new PlaceJSONParser();

        try{
            jObject = new JSONObject(jsonData[0]);

            // Getting the parsed data as a List construct
            places = placeJsonParser.parse(jObject);

        }catch(Exception e){
            Log.d("Exception", e.toString());
        }
        return places;
    }

    @Override
    protected void onPostExecute(ArrayList<String> result) {
//        ArrayList<PlacesModel> val = result.get(0).get("terms");
//        ArrayList<String> from = new ArrayList<>();

//        String[] from = new String[val.size()];
//        for (int i = 0; i < val.size(); i++) {
//            from[i] = (val.get(i).value);
//        }

//        String[] from = new String[] { "terms"};
//        int[] to = new int[] { android.R.id.text1 };

        // Creating a SimpleAdapter for the AutoCompleteTextView
        AutoCompleteAdapter autoCompleteAdapter = new AutoCompleteAdapter(activity, R.layout.design_single_text,result);
//        SimpleAdapter adapter = new SimpleAdapter(activity, result, android.R.layout.simple_list_item_1, from, to);

        // Setting the adapter
        //PreSignUpActivity.cityEditText.setAdapter(autoCompleteAdapter);
        //PreSignUpActivity.cityProgress.setVisibility(View.GONE);
    }
}