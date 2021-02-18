package com.nowfloats.manufacturing.projectandteams.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nowfloats.manufacturing.API.model.GetTeams.Data;

import com.nowfloats.manufacturing.projectandteams.Interfaces.TeamsActivityListener;
import com.nowfloats.manufacturing.webview.WebViewActivity;
import com.thinksity.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.ViewHolder> {

    private List<Data> itemList;
    private int menuPosition = -1;
    private boolean menuStatus = false;
    private Context context;
    private TeamsActivityListener listener;

    public TeamAdapter(List<Data> itemList, TeamsActivityListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }


    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_single_team, null);
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

        Glide.with(context).load(itemList.get(position).getProfileImage().getUrl()).into(holder.profileImage);
        holder.name.setText(itemList.get(position).getName());
        holder.description.setText(itemList.get(position).getDesignation());

        holder.facebookButton.setImageResource(R.drawable.ic_facebook_projectteam);
        holder.twitterButton.setImageResource(R.drawable.ic_twitter_projectteam);
        holder.skypeButton.setImageResource(R.drawable.ic_skype_projectteam);

        holder.facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!itemList.get(position).getFbURL().getUrl().isEmpty()) {
//                    Intent intent = new Intent(context, WebViewActivity.class);
//                    intent.putExtra("url", itemList.get(position).getFbURL().getUrl());
//                    context.startActivity(intent);
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(itemList.get(position).getFbURL().getUrl()));
                        context.startActivity(browserIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Invalid Link Provided.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, "Facebook URL is Empty!!", Toast.LENGTH_LONG).show();
                }
            }
        });

        holder.twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!itemList.get(position).getTwitterURL().getUrl().isEmpty()) {
//                    Intent intent = new Intent(context, WebViewActivity.class);
//                    intent.putExtra("url", itemList.get(position).getTwitterURL().getUrl());
//                    context.startActivity(intent);
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(itemList.get(position).getTwitterURL().getUrl()));
                        context.startActivity(browserIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Invalid Link Provided.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, "Twitter URL is Empty!!", Toast.LENGTH_LONG).show();
                }
            }
        });

        holder.skypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!itemList.get(position).getSkypeHandle().getUrl().isEmpty()) {
//                    Intent intent = new Intent(context, WebViewActivity.class);
//                    intent.putExtra("url", itemList.get(position).getSkypeHandle().getUrl());
//                    context.startActivity(intent);
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(itemList.get(position).getSkypeHandle().getUrl()));
                        context.startActivity(browserIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Invalid Link Provided.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, "Skype URL is Empty!!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView menuButton, profileImage, facebookButton, twitterButton, skypeButton;
        LinearLayout menuOptionLayout;
        TextView name, description, editButton, deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);

            menuButton = (ImageView) itemView.findViewById(R.id.single_item_menu_button);
            menuOptionLayout = (LinearLayout) itemView.findViewById(R.id.menu_options);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            profileImage = itemView.findViewById(R.id.profile_image);
            facebookButton = itemView.findViewById(R.id.facebook);
            twitterButton = itemView.findViewById(R.id.twitter);
            skypeButton = itemView.findViewById(R.id.skype);
            editButton = itemView.findViewById(R.id.edit_button);
            deleteButton = itemView.findViewById(R.id.delete_button);

        }
    }
}
