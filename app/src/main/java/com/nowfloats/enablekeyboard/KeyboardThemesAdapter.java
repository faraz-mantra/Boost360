package com.nowfloats.enablekeyboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by Shimona on 22-06-2018.
 */

public class KeyboardThemesAdapter extends RecyclerView.Adapter<KeyboardThemesAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Integer> keyboardDrawables;
    private int selected;

    public KeyboardThemesAdapter(Context context, ArrayList<Integer> keyboardDrawables, int selected) {
        this.context = context;
        this.keyboardDrawables = keyboardDrawables;
        this.selected = selected;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_keyboard_themes, parent, false);
        //AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.ivKeyboardTheme.setImageDrawable(context.getResources().getDrawable(keyboardDrawables.get(position)));

        if (position == selected) {
            holder.overlay.setVisibility(View.VISIBLE);
            holder.ivCheckMark.setVisibility(View.VISIBLE);
        } else {
            holder.overlay.setVisibility(View.GONE);
            holder.ivCheckMark.setVisibility(View.GONE);
        }

        holder.clKeyboardTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position != selected) {
                    //to do
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return keyboardDrawables.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivKeyboardTheme, ivCheckMark;
        View overlay;
        ConstraintLayout clKeyboardTheme;

        MyViewHolder(View view) {
            super(view);
            ivKeyboardTheme = view.findViewById(R.id.iv_keyboard_theme);
            ivCheckMark = view.findViewById(R.id.iv_check_mark);
            overlay = view.findViewById(R.id.overlay);
            clKeyboardTheme = view.findViewById(R.id.cl_keyboard_theme);
        }

    }

}
