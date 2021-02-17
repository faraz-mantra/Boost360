package com.nowfloats.manufacturing.projectandteams.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nowfloats.manufacturing.projectandteams.ui.imagepopup.ProjectImagePopUpFragment;
import com.nowfloats.manufacturing.projectandteams.ui.project.ProjectActivity;
import com.thinksity.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ProjectImageAdapter extends RecyclerView.Adapter<ProjectImageAdapter.ViewHolder> {

    private ArrayList<String> itemList;
    private int menuPosition = -1;
    private boolean menuStatus = false;
    private Context context;
    private ProjectActivity activity;

    public ProjectImageAdapter(ArrayList<String> itemList, ProjectActivity activity) {
        this.itemList = itemList;
        this.activity = activity;
    }


    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project_image, null);

        context = v.requireContext();

        return new ViewHolder(v);
    }

    public void menuOption(int pos, boolean status) {
        menuPosition = pos;
        menuStatus = status;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(context).load(itemList.get(position)).into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProjectImagePopUpFragment projectImagePopUpFragment = new ProjectImagePopUpFragment();
                Bundle args = new Bundle();
                args.putStringArrayList("imageList", itemList);
                args.putInt("pos", position);
                projectImagePopUpFragment.setArguments(args);
                projectImagePopUpFragment.show(activity.getSupportFragmentManager(), "PROJECT_IMAGE_POPUP");
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;


        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
