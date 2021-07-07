package com.nowfloats.Store.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nowfloats.Store.Model.PackageDetails;
import com.thinksity.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Admin on 14-02-2018.
 */

public class TopUpDialogAdapter extends RecyclerView.Adapter<TopUpDialogAdapter.MyTopUpDialogHolder> {

    private List<PackageDetails> packages;
    private int selectedPos = 0;
    private onItemClickListener listener;

    public TopUpDialogAdapter(List<PackageDetails> packages) {
        this.packages = packages;
    }

    @Override
    public MyTopUpDialogHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_top_up_dialog_item, parent, false);
        return new MyTopUpDialogHolder(view);
    }

    @Override
    public void onBindViewHolder(MyTopUpDialogHolder holder, int position) {
        holder.nameTv.setText(packages.get(position).getName());
        holder.priceTv.setText(String.format("%s %s", packages.get(position).getCurrencyCode(), NumberFormat.getInstance(Locale.US).format(packages.get(position).getPrice())));
        holder.radioButton.setChecked(position == selectedPos);
        holder.itemView.setBackgroundResource(position % 2 == 0 ? R.color.fafafa : R.color.white);
    }

    @Override
    public int getItemCount() {
        return packages.size();
    }

    public void setItemClickCallback(onItemClickListener listener) {
        this.listener = listener;
    }

    public interface onItemClickListener {
        void onItemClick(String id);
    }

    class MyTopUpDialogHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView nameTv, priceTv;
        private RadioButton radioButton;
        private View itemView;

        MyTopUpDialogHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            nameTv = itemView.findViewById(R.id.tv_plan_name);
            priceTv = itemView.findViewById(R.id.tv_plan_price);
            radioButton = itemView.findViewById(R.id.rb_plan);
            nameTv.setOnClickListener(this);
            radioButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.rb_plan:
                case R.id.tv_plan_name:
                    radioButton.setChecked(true);
                    listener.onItemClick(packages.get(getAdapterPosition()).getId());
                    selectedPos = getAdapterPosition();
                    notifyDataSetChanged();
                    break;
            }

        }
    }
}
