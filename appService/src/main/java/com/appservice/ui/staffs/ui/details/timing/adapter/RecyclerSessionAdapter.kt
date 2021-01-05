package com.newfloats.staffs.ui.details.timing.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.framework.views.customViews.CustomTextView;
import com.newfloats.staffs.R;
import com.newfloats.staffs.ui.customviews.LabeledToggle;
import com.newfloats.staffs.ui.details.timing.model.StaffTimingModel;

public class RecyclerSessionAdapter extends RecyclerView.Adapter<RecyclerSessionAdapter.ViewHolder> {
    RecyclerItemClick recyclerItemClick;

    public RecyclerSessionAdapter(RecyclerItemClick recyclerItemClick) {
        this.recyclerItemClick = recyclerItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_session, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StaffTimingModel staffTimingModel = StaffTimingModel.getDefaultTimings().get(position);
        if (staffTimingModel.isAppliedOnAllDaysViewVisible()) {
            holder.session.setVisibility(View.VISIBLE);
            holder.toggle.setOn(true);

        } else {
            holder.session.setVisibility(View.GONE);
//            holder.toggle.setColorBorder(Color.parseColor("#EC9A81"));
//            holder.toggle.setColorOff(Color.parseColor("#EC9A81"));
//            holder.toggle.setColorDisabled(Color.parseColor("#EC9A81"));
//            holder.toggle.setBackgroundColor(Color.parseColor("#EC9A81"));
            holder.toggle.setOn(false);
        }
        holder.dayTitle.setText(staffTimingModel.getTitle());
    }

    @Override
    public int getItemCount() {
        return StaffTimingModel.getDefaultTimings().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout session;
        LabeledToggle toggle;
        CustomTextView dayTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            session = itemView.findViewById(R.id.layout_session_create);
            toggle = itemView.findViewById(R.id.toggle_on_off);
            dayTitle = itemView.findViewById(R.id.ccv_title_day);
        }
    }

    public interface RecyclerItemClick {
        void onToggle();

        void onAddSession();

        void onApplyAllDays();

    }
}
