package com.nowfloats.manufacturing.projectandteams.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nowfloats.manufacturing.projectandteams.Interfaces.ProjectDetailsListener;
import com.thinksity.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProjectDetailsImageAdapter extends RecyclerView.Adapter<ProjectDetailsImageAdapter.ViewHolder> {

    ProjectDetailsListener listener;
    private List<String> itemList;
    private boolean menuStatus = false;
    private Context context;

    public ProjectDetailsImageAdapter(List<String> itemList, ProjectDetailsListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }


    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project_details_image, null);

        context = v.getContext();

        return new ViewHolder(v);
    }

    public void updateList(List<String> list) {
        itemList = list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(context).load(itemList.get(position)).into(holder.image);
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context,"position "+position,Toast.LENGTH_LONG).show();
//            }
//        });

        holder.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.removeURLfromList(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        LinearLayout closeButton;


        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            closeButton = itemView.findViewById(R.id.close_button);
        }
    }
}
