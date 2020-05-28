package com.nowfloats.GMB.Adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.nowfloats.BusinessProfile.UI.UI.SocialSharingFragment;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.thinksity.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BuilderAdapterBusiness extends RecyclerView.Adapter<BuilderAdapterBusiness.MyViewHolder> {

    private JSONArray locations;

    private SocialSharingFragment socialSharingFragment;

    public BuilderAdapterBusiness(JSONArray array, SocialSharingFragment socialSharingFragment) {
        this.locations = array;
        this.socialSharingFragment = socialSharingFragment;
    }

    @NonNull
    @Override
    public BuilderAdapterBusiness.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.builder_layout_business, parent, false);

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

                    socialSharingFragment.getGmbHandler().setLocationId(locationId);
                    socialSharingFragment.getGmbHandler().setLocationName(locationName);
                    socialSharingFragment.getGmbHandler().updateAccessToken(socialSharingFragment);

                    socialSharingFragment.closeDialog();

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            BoostLog.i(Constants.LogTag, e.toString() + " : Invalid response ");
        }

    }

    @Override
    public int getItemCount() {
        return locations.length();
    }


    protected class MyViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;

        TextView businessName;

        public MyViewHolder(View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.GMBBusinessCheckBox);
            businessName = itemView.findViewById(R.id.Business_Name);
        }
    }
}
