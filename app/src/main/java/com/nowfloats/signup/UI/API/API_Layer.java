package com.nowfloats.signup.UI.API;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.nowfloats.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;

import static com.android.volley.toolbox.Volley.newRequestQueue;

/**
 * Created by Dell on 10-01-2015.
 */
public class API_Layer {

    public static String[] cat = null;


    public static void getBusinessCategories(Context context) {
        RequestQueue queue = newRequestQueue(context);
        String url = Constants.NOW_FLOATS_API_URL+"/Discover/v1/floatingPoint/categories";
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {


                    @Override
                    public void onResponse(JSONArray response) {
                        if(response == null ||response.length()==0){
                            return;
                        }
                        cat = new String[response.length()];
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                cat[i] = (String) response.get(i);
                                // Toast.makeText(PreSignUpActivity.this,"Cat : "+cat[i],Toast.LENGTH_SHORT).show();
                                //Log.v("Test: ", cat[i]);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                       Constants.storeBusinessCategories = cat ;
                        //BoostLog.d("Test", "Hello");
                        // selectCats();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.print(error.toString());
            }
        });

        queue.add(jsObjRequest);
        //return Constants.storeBusinessCategories;
    }

}
