package nfkeyboard.adapter;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nfkeyboard.interface_contracts.ItemClickListener;
import nfkeyboard.models.AllSuggestionModel;

/**
 * Created by Admin on 01-03-2018.
 */

public class EmptyListAdapter extends BaseAdapter<AllSuggestionModel> {

    EmptyListAdapter(Context context, ItemClickListener listener) {
        super(context, listener);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(nowfloats.nfkeyboard.R.layout.adapter_item_text, parent, false);
        return new EmptyHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, AllSuggestionModel suggestion) {
        if (holder instanceof EmptyHolder) {
            EmptyHolder myHolder = (EmptyHolder) holder;
            myHolder.setModelData(suggestion);
        }
    }

    class EmptyHolder extends RecyclerView.ViewHolder {
        TextView suggestionTv;
        AllSuggestionModel dataModel;

        public EmptyHolder(View itemView) {
            super(itemView);
            linLayoutParams.setMargins(metrics.widthPixels * 12 / 100, topSpace, 0, topSpace);
            itemView.setLayoutParams(linLayoutParams);
            suggestionTv = itemView.findViewById(nowfloats.nfkeyboard.R.id.textView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (suggestionTv.getText().toString().
                            equalsIgnoreCase("No products available.")) {
                        final PackageManager manager = mContext.getPackageManager();
                        Intent intent = manager.getLaunchIntentForPackage(mContext.getPackageName());
                        if (intent == null) return;
                        intent.putExtra("from", "notification");
                        intent.putExtra("url", "addProduct");
                        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                        try {
                            pendingIntent.send(mContext, 0, intent);
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        void setModelData(AllSuggestionModel model) {
            dataModel = model;
            suggestionTv.setText(model.getText());
        }
    }
}