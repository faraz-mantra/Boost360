package com.nowfloats.GMB.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nowfloats.BusinessProfile.UI.UI.SocialSharingFragment;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.thinksity.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BuilderAdapter extends RecyclerView.Adapter<BuilderAdapter.MyViewHolder> {

    private JSONArray arr;

    private SocialSharingFragment socialSharingFragment;

    private String accountId = "", accountName = "";


    public BuilderAdapter(JSONArray arr, SocialSharingFragment socialSharingFragment) {
        this.arr = arr;
        this.socialSharingFragment = socialSharingFragment;
    }

    @NonNull
    @Override
    public BuilderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gmb_builder_row, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BuilderAdapter.MyViewHolder holder, int position) {


        try {
            JSONObject child = arr.getJSONObject(holder.getAdapterPosition());

            accountName = child.getString("accountName");

            holder.account_name.setText(accountName);

            String name = child.getString("name");

            accountId = (name.split("/"))[1];

            BoostLog.i(Constants.LogTag, accountId);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.checkBox.setChecked(false);

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                socialSharingFragment.getGmbHandler().setAccountId(accountId);
                socialSharingFragment.getGmbHandler().setAccountName(accountName);

                socialSharingFragment.getGmbHandler().getLocations(socialSharingFragment);
                socialSharingFragment.closeDialog();
                socialSharingFragment.showLoader("Getting your businesses.");

            }
        });

    }

    @Override
    public int getItemCount() {
        return arr.length();
    }

    protected class MyViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;
        TextView account_name;

        public MyViewHolder(View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.GMBAccountCheckBox);
            account_name = itemView.findViewById(R.id.Account_name);


        }
    }

}
