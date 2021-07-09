package com.nowfloats.signup.UI.UI;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.nowfloats.util.CustomFilterableAdapter;
import com.thinksity.R;
import com.nowfloats.util.HeaderText;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Dell on 10-01-2015.
 */
public class PreSignUpDialog {


    private static final String TAG = "PreSignUpDialog" ;

    interface Dialog_Activity_Interface{
       public void onItemClick(String item);
   }

    static ListView elementList;
    static CustomFilterableAdapter adapter;
    static HeaderText title;

    public static void showDialog(final Context context, String[] elements, String dialogTitle){
        Log.d(TAG, "dialogTitle : "+dialogTitle);

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_with_search);
        elementList = (ListView) dialog.findViewById(R.id.search_dialog_listview);
        ArrayList<String> stringList = new ArrayList<String>(Arrays.asList(elements));
        adapter = new CustomFilterableAdapter(stringList,
                context);
        elementList.setAdapter(adapter);
        title = (HeaderText) dialog
                .findViewById(R.id.message_top_bar_store_txt);
        title.setText(dialogTitle);
        elementList.setTextFilterEnabled(true);
        EditText editTxt = (EditText) dialog.findViewById(R.id.searchString);
        editTxt.setVisibility(View.GONE);

        dialog.show();

        elementList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                String strName = adapter.getItem(arg2);

                Dialog_Activity_Interface listener = (Dialog_Activity_Interface) context;
                listener.onItemClick(strName);
                dialog.dismiss();
            }
        });



    }

    public static void showDialog_WebSiteCreation(final Context context, String message, String dialogTitle)
    {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.website_creation_dialog);

        TextView title = (TextView) dialog.findViewById(R.id.headerTextView);
        title.setText(dialogTitle);

        TextView status = (TextView) dialog.findViewById(R.id.statusTextView);
        status.setText(message);

        dialog.show();


    }
}
