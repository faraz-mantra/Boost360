package com.nowfloats.NavigationDrawer.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.thinksity.R;


/**
 * Created by Admin on 05-04-2017.
 */

public class QuikrAdapter extends RecyclerView.Adapter<QuikrAdapter.MyHolder> {
    Context mContext;
    String [] guidelines;
    private final int BUTTON_TYPE = 1, TEXT_TYPE = 0;
    public QuikrAdapter(Context context,String[] guidelines){
        mContext = context;
        this.guidelines = guidelines;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        if(viewType == BUTTON_TYPE){
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_button_item, parent, false);
        }else if(viewType == TEXT_TYPE) {
            view = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_activated_1, parent, false);
        }
        return new MyHolder(view,viewType);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        if(position == guidelines.length-1){

        }
        else {
            holder.text.setText(guidelines[position]);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == guidelines.length-1){
            return BUTTON_TYPE;
        }else{
            return TEXT_TYPE;
        }

    }

    @Override
    public int getItemCount() {
        return guidelines.length;
    }
    class MyHolder extends RecyclerView.ViewHolder{

        TextView text;
        Button button;
        MyHolder(View itemView, int viewType) {
            super(itemView);
            if(viewType == BUTTON_TYPE){
                button = (Button) itemView.findViewById(R.id.button1);
            }
            else{
                text = (TextView) itemView.findViewById(R.id.text1);
            }

        }
    }
}
