package com.nowfloats.on_boarding;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thinksity.R;

/**
 * Created by Admin on 16-03-2018.
 */

public class OnBoardingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context mContext;
    public OnBoardingAdapter(Context context){
        mContext = context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType){
            case 0:
                view = LayoutInflater.from(mContext).inflate(R.layout.adapter_onboarding_item,parent,false);
                break;

            default:
                view = LayoutInflater.from(mContext).inflate(R.layout.adapter_onboarding_item,parent,false);
                break;

        }
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 6;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
