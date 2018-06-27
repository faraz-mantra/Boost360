package com.nowfloats.enablekeyboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by Shimona on 22-06-2018.
 */

public class KeyboardThemesAdapter extends RecyclerView.Adapter<KeyboardThemesAdapter.MyViewHolder> {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private Context context;
    private ArrayList<Integer> keyboardDrawables;
    private int selected;

    public KeyboardThemesAdapter(Context context, ArrayList<Integer> keyboardDrawables, int selected, SharedPreferences sharedPreferences, SharedPreferences.Editor editor) {
        this.context = context;
        this.keyboardDrawables = keyboardDrawables;
        this.selected = selected;
        this.sharedPreferences = sharedPreferences;
        this.editor = editor;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_keyboard_themes, parent, false);
        //AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        holder.ivKeyboardTheme.setImageDrawable(context.getResources().getDrawable(keyboardDrawables.get(position)));

        if (position == selected) {
            holder.overlay.setVisibility(View.VISIBLE);
            holder.ivCheckMark.setVisibility(View.VISIBLE);
            holder.checkMarkbackground.setVisibility(View.VISIBLE);
        } else {
            holder.overlay.setVisibility(View.GONE);
            holder.ivCheckMark.setVisibility(View.GONE);
            holder.checkMarkbackground.setVisibility(View.GONE);
        }

        holder.clKeyboardTheme.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    holder.clKeyboardTheme.setAlpha(0.5f);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (selected != position) {
                        selected = position;
                        editor.putInt("keyboard_theme", position);
                        editor.commit();
                        Snackbar.make(view, "The theme has been applied!", Snackbar.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    }
                    holder.clKeyboardTheme.setAlpha(1f);
                } else {
                    holder.clKeyboardTheme.setAlpha(1f);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return keyboardDrawables.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivKeyboardTheme, ivCheckMark;
        View overlay, checkMarkbackground;
        ConstraintLayout clKeyboardTheme;

        MyViewHolder(View view) {
            super(view);
            ivKeyboardTheme = view.findViewById(R.id.iv_keyboard_theme);
            ivCheckMark = view.findViewById(R.id.iv_check_mark);
            overlay = view.findViewById(R.id.overlay);
            clKeyboardTheme = view.findViewById(R.id.cl_keyboard_theme);
            checkMarkbackground = view.findViewById(R.id.check_mark_background);
        }

    }

}
