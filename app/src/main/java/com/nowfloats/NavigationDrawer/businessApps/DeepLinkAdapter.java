package com.nowfloats.NavigationDrawer.businessApps;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thinksity.R;

/**
 * Created by Admin on 02-06-2017.
 */

public class DeepLinkAdapter extends RecyclerView.Adapter<DeepLinkAdapter.MyHolder> {

    private Context mContext;
    private String[] mainArray,descriptionArray;
    DeepLinkAdapter(Context context,String[] main,String[] descriptions){
        mContext = context;
        mainArray = main;
        descriptionArray = descriptions;
    }
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_subscribers_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.mainText.setText(mainArray[position]);
        holder.descriptionText.setText(descriptionArray[position]);
    }

    @Override
    public int getItemCount() {
        return mainArray.length;
    }

    class MyHolder extends RecyclerView.ViewHolder{

        TextView mainText,descriptionText;
        MyHolder(View itemView) {
            super(itemView);
            mainText = (TextView) itemView.findViewById(R.id.subscriber_text);
            descriptionText = (TextView) itemView.findViewById(R.id.subscriber_status);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((DeepLinkOnclick)mContext).onDeepLinkClick(getAdapterPosition());
                }
            });
        }
    }

    public interface DeepLinkOnclick{
        void onDeepLinkClick(int pos);
    }
}
