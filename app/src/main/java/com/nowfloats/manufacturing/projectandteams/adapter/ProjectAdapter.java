package com.nowfloats.manufacturing.projectandteams.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nowfloats.manufacturing.projectandteams.ProjectAndTermsActivity;
import com.thinksity.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {

    private List<String> itemList;
    private int menuPosition = -1;
    private boolean menuStatus = false;
    private Context context;
    private ProjectAndTermsActivity activity;

    public ProjectAdapter(List<String> itemList, ProjectAndTermsActivity activity) {
        this.itemList = itemList;
        this.activity = activity;
    }


    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_single_project, null);
        context = v.getContext();
        return new ViewHolder(v);
    }

    public void menuOption(int pos, boolean status) {
        menuPosition = pos;
        menuStatus = status;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.menuOptionLayout.setVisibility(View.GONE);
        if (menuPosition == position) {
            if (menuStatus) {
                holder.menuOptionLayout.setVisibility(View.VISIBLE);
            } else {
                holder.menuOptionLayout.setVisibility(View.GONE);
            }
        }

        holder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.menuOptionLayout.getVisibility() == View.GONE) {
//                    listener.itemMenuOptionStatus(position, true);
                    holder.menuOptionLayout.setVisibility(View.VISIBLE);
                } else {
//                    listener.itemMenuOptionStatus(position, false);
                    holder.menuOptionLayout.setVisibility(View.GONE);
                }
            }
        });

        ProjectImageAdapter adapter = new ProjectImageAdapter(new ArrayList(), activity);
        holder.imageRecycler.setAdapter(adapter);
        holder.imageRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        holder.seeMoreLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.seeMoreLessStatus) {
                    holder.seeMoreLessStatus = true;
                    holder.seeMoreLess.setImageResource(R.drawable.up_arrow);
                    holder.infoLayout.setVisibility(View.VISIBLE);
                } else {
                    holder.seeMoreLessStatus = false;
                    holder.seeMoreLess.setImageResource(R.drawable.down_arrow);
                    holder.infoLayout.setVisibility(View.GONE);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.menuOptionLayout.getVisibility() == View.VISIBLE) {
                    holder.menuOptionLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return 4;//itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView menuButton, seeMoreLess;
        LinearLayout menuOptionLayout, infoLayout;
        RecyclerView imageRecycler;
        boolean seeMoreLessStatus = false;

        public ViewHolder(View itemView) {
            super(itemView);

            menuButton = (ImageView) itemView.findViewById(R.id.single_item_menu_button);
            menuOptionLayout = (LinearLayout) itemView.findViewById(R.id.menu_options);
            infoLayout = (LinearLayout) itemView.findViewById(R.id.info_layout);
            imageRecycler = itemView.findViewById(R.id.image_recycler);
            seeMoreLess = itemView.findViewById(R.id.see_more_less);
        }
    }
}
