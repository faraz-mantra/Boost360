package com.nowfloats.riachatsdk.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.riachatsdk.R;
import com.nowfloats.riachatsdk.models.Section;

import java.util.List;

/**
 * Created by Admin on 02-08-2017.
 */

public class CarousalAdapter extends RecyclerView.Adapter<CarousalAdapter.MyCarousalItemHolder> {

    private List<Section> sectionList;
    public CarousalAdapter(List<Section> sections){
        sectionList = sections;
    }
    @Override
    public MyCarousalItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_crousal_item_layout,parent,false);
        return new MyCarousalItemHolder(view);
    }

    @Override
    public void onBindViewHolder(MyCarousalItemHolder holder, int position) {
        if(holder == null){
            return;
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    class MyCarousalItemHolder extends RecyclerView.ViewHolder{

        ImageView logoImage;
        TextView fbPageNameTextView, likeTextView;
        public MyCarousalItemHolder(View itemView) {
            super(itemView);
            logoImage = (ImageView) itemView.findViewById(R.id.img_logo);
            fbPageNameTextView = (TextView) itemView.findViewById(R.id.tv_fbname);
            likeTextView = (TextView) itemView.findViewById(R.id.tv_likes);
        }
    }
}
