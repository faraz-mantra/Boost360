package com.nowfloats.manufacturing.projectandteams.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.thinksity.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProjectDetailsImageAdapter extends RecyclerView.Adapter<ProjectDetailsImageAdapter.ViewHolder> {

    private List<String> itemList;
    private int menuPosition = -1;
    private boolean menuStatus = false;
    private Context context;

    public ProjectDetailsImageAdapter(List<String> itemList) {
        this.itemList = itemList;
    }


    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project_details_image, null);

        context = v.getContext();

        return new ViewHolder(v);
    }

    public void menuOption(int pos, boolean status) {
        menuPosition = pos;
        menuStatus = status;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(context).load("https://images.pexels.com/photos/1236701/pexels-photo-1236701.jpeg").into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"position "+position,Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return 2;//itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;


        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
