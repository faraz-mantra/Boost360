package com.nowfloats.GMB.Adapter;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.nowfloats.BusinessProfile.UI.UI.SocialSharingFragment;
import com.nowfloats.util.Constants;
import com.thinksity.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BuilderAdapter extends RecyclerView.Adapter<BuilderAdapter.MyViewHolder> {

    JSONArray arr ;

    SocialSharingFragment closer;

    String account_name;

    String accountnumber = "";

    SharedPreferences sharedPreferences;

    public BuilderAdapter(JSONArray arr, SocialSharingFragment f){
        this.arr = arr;
        closer = f;
    }

    @NonNull
    @Override
    public BuilderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gmb_builder_row,parent,false);

        sharedPreferences = closer.getFragmentSharedPreference();

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BuilderAdapter.MyViewHolder holder, int position) {

        SharedPreferences.Editor edit = sharedPreferences.edit();

        try {
            JSONObject child = arr.getJSONObject(holder.getAdapterPosition());

             account_name = child.getString("accountName");

            holder.account_name.setText(account_name);

            String name = child.getString("name");

             accountnumber = (name.split("/"))[1];



            Log.i("android23235616",accountnumber);

            edit.putString(Constants.GMBAccountId,accountnumber);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.checkBox.setChecked(false);

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                closer.GMBSetAccountIdandAccountName(accountnumber,account_name);

                closer.getLocations(accountnumber);
                closer.closer();

                closer.showLoader("Getting your businesses.");


            }
        });

    }

    @Override
    public int getItemCount() {
        return arr.length();
    }

    protected class MyViewHolder extends RecyclerView.ViewHolder{

        CheckBox checkBox;
        TextView account_name;

        public MyViewHolder(View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.GMBAccountCheckBox);
            account_name = itemView.findViewById(R.id.Account_name);


        }
    }

}
