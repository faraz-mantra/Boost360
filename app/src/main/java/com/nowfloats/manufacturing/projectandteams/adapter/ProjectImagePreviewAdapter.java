package com.nowfloats.manufacturing.projectandteams.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.thinksity.R;

import java.util.ArrayList;

public class ProjectImagePreviewAdapter extends RecyclerView.Adapter<ProjectImagePreviewAdapter.ViewHolder> {

    Context context;
    ArrayList<String> imageList;

    public ProjectImagePreviewAdapter(ArrayList<String> list) {
        this.imageList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_image_preview_item, null);
        context = v.getContext();
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        v.setLayoutParams(lp);
        return new ProjectImagePreviewAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(imageList.get(position)).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.preview_image);

        }
    }
}
