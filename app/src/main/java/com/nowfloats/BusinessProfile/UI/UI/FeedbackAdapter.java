package com.nowfloats.BusinessProfile.UI.UI;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.thinksity.R;

/**
 * Created by Admin on 10-07-2017.
 */

class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.MyHolder> {

    int selectedPos = -1;
    private Context mContext;

    public FeedbackAdapter(Context context) {
        mContext = context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(mContext).inflate(R.layout.feedback_ratting_number, parent, false));
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.number.setText(String.valueOf(position + 1));
        holder.text.setVisibility(View.VISIBLE);
        switch (position) {
            case 0:
                holder.text.setText("unlikely");
                break;
            case 4:
                holder.text.setText("moderate");
                break;
            case 9:
                holder.text.setText("very likely");
                break;
            default:
                holder.text.setVisibility(View.INVISIBLE);
                break;
        }

        if (position != selectedPos) {
            holder.number.setBackgroundResource(R.drawable.light_gray_rounded_edittext);
            holder.number.setTextColor(ContextCompat.getColor(mContext, R.color.primary));
        } else {
            holder.number.setBackgroundResource(R.color.primary);
            holder.number.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        }
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics matrics = new DisplayMetrics();
        display.getMetrics(matrics);
        int px = (int) (10 * matrics.density);
        holder.number.setPadding(px, px, px, px);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView number, text;

        private MyHolder(final View itemView) {
            super(itemView);
            number = (TextView) itemView.findViewById(R.id.tv_number);
            text = (TextView) itemView.findViewById(R.id.tv_text);
            number.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPos = getAdapterPosition();
                    notifyDataSetChanged();
                }
            });
        }
    }
}
