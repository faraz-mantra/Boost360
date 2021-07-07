package com.nowfloats.signup.UI.Places;

import com.nowfloats.signup.UI.Model.PlacesModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PlaceJSONParser {

    /**
     * Receives a JSONObject and returns a list
     */
    public ArrayList<String> parse(JSONObject jObject) {

        JSONArray jPlaces = null;
        try {
            /** Retrieves all the elements in the 'places' array */
            jPlaces = jObject.getJSONArray("predictions");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /** Invoking getPlaces with the array of json object
         * where each json object represent a place
         */
        return getPlaces(jPlaces);
    }

    private ArrayList<String> getPlaces(JSONArray jPlaces) {
        int placesCount = jPlaces.length();
        ArrayList<String> placesList = new ArrayList<String>();
//		HashMap<String, ArrayList<PlacesModel>> place = null;
        /** Taking each place, parses and adds to list object */
        for (int i = 0; i < placesCount; i++) {
            try {
                /** Call getPlace with place JSON object to parse the place */
//				place = getPlace((JSONObject)jPlaces.get(i));
                JSONObject jsonObject = jPlaces.getJSONObject(i);
                JSONArray objectItem = jsonObject.getJSONArray("terms");
                if (!objectItem.getJSONObject(0).getString("value").equals(objectItem.getJSONObject(objectItem.length() - 1).getString("value")))
                    placesList.add(objectItem.getJSONObject(0).getString("value") + "," + objectItem.getJSONObject(objectItem.length() - 1).getString("value"));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return placesList;
    }

    /**
     * Parsing the Place JSON object
     */
    private HashMap<String, ArrayList<PlacesModel>> getPlace(JSONObject jPlace) {
        HashMap<String, ArrayList<PlacesModel>> place = new HashMap<String, ArrayList<PlacesModel>>();
        ArrayList<PlacesModel> placesModels = new ArrayList<>();
        String id = "";
        String reference = "";
        String description = "";

        try {
            description = jPlace.getString("description");
            JSONArray array = jPlace.getJSONArray("terms");
            for (int i = 0; i < array.length(); i++) {
                placesModels.add(new PlacesModel(array.getJSONObject(i).getString("value")));
            }

            id = jPlace.getString("id");
            reference = jPlace.getString("reference");

//			place.put("description", description);
//			place.put("_id",id);
//			place.put("reference",reference);
            place.put("terms", placesModels);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return place;
    }
}
