package nfkeyboard.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.separ.neural.inputmethod.indic.R;
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_text, parent, false);
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
            suggestionTv = itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (suggestionTv.getText().toString().
                            equalsIgnoreCase("No products available.")) {

                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("nowfloats://" + mContext.getApplicationContext().getPackageName() + ".keyboard.home/addproduct"));
                        intent.putExtra("from", "notification");
                        intent.putExtra("url", "addProduct");
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction(Intent.ACTION_VIEW);
                        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                            mContext.startActivity(intent);
                        }
                    } else if (suggestionTv.getText().toString().
                            equalsIgnoreCase("No updates available.")) {

                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("nowfloats://" + mContext.getApplicationContext().getPackageName() + ".keyboard.home/update"));
                        intent.putExtra("from", "notification");
                        intent.putExtra("url", "update");
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction(Intent.ACTION_VIEW);
                        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                            mContext.startActivity(intent);
                        }
                    } else if (suggestionTv.getText().toString().
                            equalsIgnoreCase("No photos available.")) {

                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("nowfloats://" + mContext.getApplicationContext().getPackageName() + ".keyboard.home/photos"));
                        intent.putExtra("from", "notification");
                        intent.putExtra("url", "imagegallery");
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction(Intent.ACTION_VIEW);
                        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                            mContext.startActivity(intent);
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