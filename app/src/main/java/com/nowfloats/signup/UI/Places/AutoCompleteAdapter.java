package com.nowfloats.signup.UI.Places;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nowfloats.signup.UI.UI.PreSignUpActivity;
import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by guru on 28/07/2015.
 */
public class AutoCompleteAdapter extends ArrayAdapter<String> {

    final String TAG = "AutocompleteCustomArrayAdapter.java";

    Context mContext;
    int layoutResourceId;
    ArrayList<String> data = null;

    public AutoCompleteAdapter(Context mContext, int layoutResourceId, ArrayList<String> data) {

        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try{
            /*
             * The convertView argument is essentially a "ScrapView" as described is Lucas post
             * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
             * It will have a non-null value when ListView is asking you recycle the row layout.
             * So, when convertView is not null, you should simply update its contents instead of inflating a new row layout.
             */
            if(convertView==null){
                // inflate the layout
                LayoutInflater inflater = ((PreSignUpActivity) mContext).getLayoutInflater();
                convertView = inflater.inflate(layoutResourceId, parent, false);
            }

//            String result = " ";
//            try {
//                // object item based on the position
//                JSONObject jsonObject =new JSONObject(data.get(position));
//                JSONArray objectItem = jsonObject.getJSONArray("terms");
//                result = objectItem.getJSONObject(0).getString("value")+","+objectItem.getJSONObject(objectItem.length()-1).getString("value");
//            }catch(Exception e){e.printStackTrace();}

            // get the TextView and then set the text (item name) and tag (item ID) values
            TextView textViewItem = (TextView) convertView.findViewById(R.id.autocomplete_info_tv);
            textViewItem.setText(data.get(position));


        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;

    }
}
