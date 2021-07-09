package com.nowfloats.NavigationDrawer;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thinksity.R;

/**
 * Created by Dell on 13-01-2015.
 */
public class SidePanelAdapter extends RecyclerView.Adapter<SidePanelAdapter.MyViewHolder> {

    private LayoutInflater inflater;

    public SidePanelAdapter(Context context)
    {
        inflater=LayoutInflater.from(context);

    }


    @Override
    public SidePanelAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view=inflater.inflate(R.layout.single_row_card, viewGroup,false);
        MyViewHolder holder=new MyViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
//        Information current=data.get(position);
//        holder.title.setText(current.title);
//        holder.icon.setImageResource(current.iconId);
        myViewHolder.title.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // mListener.onClick(v, position);
                return false;
            }
        });
    }


    @Override
    public int getItemCount() {
        return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView icon;
        public MyViewHolder(View itemView) {
            super(itemView);
           // title= (TextView) itemView.findViewById(R.id.listText);
           // icon= (ImageView) itemView.findViewById(R.id.listIcon);
        }
    }
}
