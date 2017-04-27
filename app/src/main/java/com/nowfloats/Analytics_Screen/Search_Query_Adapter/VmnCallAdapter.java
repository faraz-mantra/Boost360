package com.nowfloats.Analytics_Screen.Search_Query_Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nowfloats.Analytics_Screen.model.VmnCallModel;
import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by Admin on 27-04-2017.
 */

public class VmnCallAdapter extends RecyclerView.Adapter<VmnCallAdapter.MyHolder> {

    private final Context mContext;
    ArrayList<VmnCallModel> list;

    public VmnCallAdapter(Context context, ArrayList<VmnCallModel> list){
        mContext = context;
        this.list = list;
    }
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_vmn_call_item,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        public MyHolder(View itemView) {
            super(itemView);
        }
    }
}
