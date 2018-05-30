package com.nowfloats.GMB.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.nowfloats.BusinessProfile.UI.UI.SocialSharingFragment;
import com.thinksity.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BuilderAdapterBusiness extends RecyclerView.Adapter<BuilderAdapterBusiness.MyViewHolder> {

    JSONArray locations;

    SocialSharingFragment GMBGateway;


    public BuilderAdapterBusiness(JSONArray array, SocialSharingFragment GMBGateway){
        this.locations = array;
        this.GMBGateway = GMBGateway;
    }

    @NonNull
    @Override
    public BuilderAdapterBusiness.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.builder_layout_business,parent,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BuilderAdapterBusiness.MyViewHolder holder, int position) {

        try {
            JSONObject tempJsonObject = locations.getJSONObject(holder.getAdapterPosition());

            String tempid = tempJsonObject.getString("name");

            final String locationId = tempid.split("/")[3]; //location id is at the 3rd index

            final String locationName = tempJsonObject.getString("locationName");

            holder.businessName.setText(locationName);

            holder.checkBox.setChecked(false);

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    GMBGateway.GMBUpdateAccessToken(locationId,locationName);

                    GMBGateway.closer();

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (ArrayIndexOutOfBoundsException e){
            Log.e("android23235616",e.toString()+" : Invalid response ");
        }

    }

    @Override
    public int getItemCount() {
        return locations.length();
    }


    protected class MyViewHolder extends RecyclerView.ViewHolder{

        CheckBox checkBox;

        TextView businessName;

        public MyViewHolder(View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.GMBBusinessCheckBox);
            businessName = itemView.findViewById(R.id.Business_Name);
        }
    }
}
