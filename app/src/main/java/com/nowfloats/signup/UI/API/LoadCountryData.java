package com.nowfloats.signup.UI.API;

import android.app.Activity;
import android.os.AsyncTask;

import com.nowfloats.util.Constants;
import com.thinksity.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamal on 13-02-2015.
 */
public class LoadCountryData extends AsyncTask<Void, Integer, Void> {
    static List<String> countries;
    private final Activity activity;

    public interface LoadCountryData_Interface {
        public void LoadCountry_onPostExecute_Completed(String result);
    }

    LoadCountryData_Interface loadCountryData_interface ;

    public LoadCountryData(Activity activity)
    {
        this.activity = activity ;
    }

    public void LoadCountryData_Listener(LoadCountryData_Interface loadCountryData_interface)
    {
        this.loadCountryData_interface = loadCountryData_interface ;
    }

    @Override
    protected void onPostExecute(Void result) {
        boolean loaded = false;
        if (countries != null && countries.size() == 205) {
            loaded = true;
        }

        if (!loaded) {
            //countryEditText.setEnabled(true);
            //countryEditText.setOnClickListener(null);
            loadCountryData_interface.LoadCountry_onPostExecute_Completed("failure");
        } else {
            loadCountryData_interface.LoadCountry_onPostExecute_Completed("success");
            Constants.signUpCountryList = (ArrayList<String>) countries;
//            countryEditText.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    selectC();
//                    //Util.hideInput(requireActivity());
//                }
//            });
        }



    };

    @Override
    protected Void doInBackground(Void... arg0) {
        InputStream is = activity.getResources().openRawResource(R.raw.loc);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
            is.close();

            String jsonString = writer.toString();

            JSONArray arr = new JSONArray(jsonString);
            int len = arr.length();
            countries = new ArrayList<String>();
            for (int i = 0; i < len; i++) {
                JSONObject country = arr.getJSONObject(i);
                countries.add(i, country.getString("-name"));
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}
