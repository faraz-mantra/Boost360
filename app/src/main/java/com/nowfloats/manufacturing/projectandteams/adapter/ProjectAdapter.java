package com.nowfloats.manufacturing.projectandteams.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nowfloats.manufacturing.API.model.GetProjects.Data;
import com.nowfloats.manufacturing.projectandteams.Interfaces.ProjectActivityListener;
import com.nowfloats.manufacturing.projectandteams.ui.project.ProjectActivity;
import com.thinksity.R;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {

    private List<Data> itemList;
    private int menuPosition = -1;
    private boolean menuStatus = false;
    private Context context;
    private ProjectActivity activity;
    private ProjectActivityListener listener;

    public ProjectAdapter(List<Data> itemList, ProjectActivity activity, ProjectActivityListener listener) {
        this.itemList = itemList;
        this.activity = activity;
        this.listener = listener;
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

    public void updateList(List<Data> list) {
        itemList = list;
        notifyDataSetChanged();
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
                    listener.itemMenuOptionStatus(position, true);
                } else {
                    listener.itemMenuOptionStatus(position, false);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.itemMenuOptionStatus(-1, false);
            }
        });

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.editOptionClicked(itemList.get(position));
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.deleteOptionClicked(itemList.get(position));
            }
        });
        ArrayList<String> imageURLs = new ArrayList();
        if (itemList.get(position).getFeaturedImage() != null && !itemList.get(position).getFeaturedImage().getUrl().isEmpty()) {
            imageURLs.add(itemList.get(position).getFeaturedImage().getUrl());
        }

        if (itemList.get(position).getProjectImage2() != null && !itemList.get(position).getProjectImage2().getUrl().isEmpty()) {
            imageURLs.add(itemList.get(position).getProjectImage2().getUrl());
        }

        if (itemList.get(position).getProjectImage3() != null && !itemList.get(position).getProjectImage3().getUrl().isEmpty()) {
            imageURLs.add(itemList.get(position).getProjectImage3().getUrl());
        }

        ProjectImageAdapter adapter = new ProjectImageAdapter(imageURLs, activity);
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

        holder.clientName.setText(itemList.get(position).getProjectClientName());
        holder.clientCategory.setText(itemList.get(position).getProjectClientCategory());
        holder.projectTitle.setText(itemList.get(position).getProjectTitle());
        holder.projectDescription.setText(itemList.get(position).getProjectDescription());
        holder.budget.setText(itemList.get(position).getProjectBudget());
        holder.projectClientRequirement.setText(itemList.get(position).getProjectClientRequirement());
        holder.projectResult.setText(itemList.get(position).getProjectResult());
        holder.ourApproach.setText(itemList.get(position).getProjectWhatWeDid());

        if (itemList.get(position).getProjectResult().equals("COMPLETED")) {
            holder.projectResultLayout.setBackgroundColor(context.getResources().getColor(R.color.success_green));
        } else {
            holder.projectResultLayout.setBackgroundColor(context.getResources().getColor(R.color.primary_color));
        }

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");
        try {
            Date date = inputFormat.parse(itemList.get(position).getProjectCompletedOn());
            holder.completionDate.setText(outputFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.dummyView2.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        holder.dummyView3.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView menuButton, seeMoreLess;
        LinearLayout menuOptionLayout, infoLayout, projectResultLayout;
        RecyclerView imageRecycler;
        boolean seeMoreLessStatus = false;
        TextView clientName, clientCategory, projectTitle, projectDescription, completionDate, budget, projectClientRequirement, ourApproach, projectResult, editButton, deleteButton;
        View dummyView2, dummyView3;

        public ViewHolder(View itemView) {
            super(itemView);

            menuButton = (ImageView) itemView.findViewById(R.id.single_item_menu_button);
            menuOptionLayout = (LinearLayout) itemView.findViewById(R.id.menu_options);
            infoLayout = (LinearLayout) itemView.findViewById(R.id.info_layout);
            imageRecycler = itemView.findViewById(R.id.image_recycler);
            seeMoreLess = itemView.findViewById(R.id.see_more_less);

            clientName = itemView.findViewById(R.id.client_name);
            clientCategory = itemView.findViewById(R.id.client_category);
            projectTitle = itemView.findViewById(R.id.project_title);
            projectDescription = itemView.findViewById(R.id.project_description);
            budget = itemView.findViewById(R.id.budget_value);
            projectClientRequirement = itemView.findViewById(R.id.project_client_requirement);
            ourApproach = itemView.findViewById(R.id.our_approach);
            projectResult = itemView.findViewById(R.id.project_result);

            projectResultLayout = itemView.findViewById(R.id.project_result_layout);
            completionDate = itemView.findViewById(R.id.completion_date);

            editButton = itemView.findViewById(R.id.edit_button);
            deleteButton = itemView.findViewById(R.id.delete_button);

            dummyView2 = itemView.findViewById(R.id.dummy_view2);
            dummyView3 = itemView.findViewById(R.id.dummy_view3);
        }
    }
}
