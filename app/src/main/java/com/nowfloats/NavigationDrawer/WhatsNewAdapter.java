package com.nowfloats.NavigationDrawer;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.NavigationDrawer.model.WhatsNewDataModel;
import com.thinksity.R;

import java.util.List;

/**
 * Created by NowFloats on 02-08-2016.
 */
public class WhatsNewAdapter extends RecyclerView.Adapter<WhatsNewAdapter.WhatsNewViewHolder>{

    private List<WhatsNewDataModel> modelList;

    public WhatsNewAdapter(List<WhatsNewDataModel> modelList){
        this.modelList = modelList;
    }


    @Override
    public WhatsNewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.whats_new_row_layout, null);
        return new WhatsNewViewHolder(v);
    }

    @Override
    public void onBindViewHolder(WhatsNewViewHolder holder, int position) {
        WhatsNewDataModel model = modelList.get(position);
        holder.ivWhatsNew.setImageResource(model.imageResource);
        holder.tvHeaderText.setText(model.headerText);
        holder.tvBodyText.setText(model.bodyText);
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class WhatsNewViewHolder extends RecyclerView.ViewHolder{

        ImageView ivWhatsNew;
        TextView tvHeaderText, tvBodyText;

        public WhatsNewViewHolder(View itemView) {
            super(itemView);
            ivWhatsNew = (ImageView) itemView.findViewById(R.id.iv_text_logo);
            tvHeaderText = (TextView) itemView.findViewById(R.id.tv_text_header);
            tvBodyText = (TextView) itemView.findViewById(R.id.tv_text_body);
        }
    }
}
