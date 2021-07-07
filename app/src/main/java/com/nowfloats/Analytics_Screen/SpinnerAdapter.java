package com.nowfloats.Analytics_Screen;

import android.content.Context;

import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.nowfloats.Analytics_Screen.model.SubscriberModel;
import com.nowfloats.util.Constants;
import com.thinksity.R;

import java.util.ArrayList;


/**
 * Created by Admin on 03-04-2017.
 */

public class SpinnerAdapter extends BaseAdapter implements Filterable {
    ArrayList<SubscriberModel> originalList;
    ArrayList<SubscriberModel> suggestions = new ArrayList<>();
    Context mContext;
    private Filter filter = new CustomFilter();

    SpinnerAdapter(Context context, ArrayList<SubscriberModel> list) {
        originalList = list;
        mContext = context;
    }

    @Override
    public int getCount() {
        return suggestions.size();
    }

    @Override
    public Object getItem(int position) {
        return suggestions.get(position).getUserMobile();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }


    private View getCustomView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_spinner_image_item, parent, false);
            holder = new Holder();
            holder.email = (TextView) convertView.findViewById(R.id.subscriber_email);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        SubscriberModel model = suggestions.get(position);

        holder.email.setText(model.getUserMobile());
        if (Integer.parseInt(model.getSubscriptionStatus()) == Constants.SubscriberStatus.SUBSCRIBED.value) {
            holder.email.setTextColor(ContextCompat.getColor(mContext, R.color.primary_color));
        } else {
            holder.email.setTextColor(ContextCompat.getColor(mContext, R.color.gray));
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private class Holder {
        TextView email;
    }

    private class CustomFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            suggestions.clear();

            if (originalList != null && constraint != null) { // sent_check if the Original List and Constraint aren't null.
                for (int i = 0; i < originalList.size(); i++) {
                    if (originalList.get(i).getUserMobile().toLowerCase().contains(constraint)) { // Compare item in original list if it contains constraints.
                        suggestions.add(originalList.get(i)); // If TRUE add item in Suggestions.
                    }
                }
            }
            FilterResults results = new FilterResults(); // Create new Filter Results and return this to publishResults;
            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

}
