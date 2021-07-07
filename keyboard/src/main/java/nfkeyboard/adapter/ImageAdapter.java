package nfkeyboard.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import io.separ.neural.inputmethod.indic.R;
import nfkeyboard.interface_contracts.ItemClickListener;
import nfkeyboard.models.AllSuggestionModel;

/**
 * Created by Admin on 27-02-2018.
 */

class ImageAdapter extends BaseAdapter<AllSuggestionModel> {
    public ImageAdapter(Context context, ItemClickListener listener) {
        super(context, listener);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_image, parent, false);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, AllSuggestionModel suggestion) {
        if (holder instanceof ImageHolder) {
            ImageHolder myHolder = (ImageHolder) holder;
            myHolder.setModelData(suggestion);
        }
    }

    class ImageHolder extends RecyclerView.ViewHolder {
        private TextView suggestionTv;
        private ImageView suggestionImage;
        private AllSuggestionModel dataModel;

        public ImageHolder(View itemView) {
            super(itemView);
            setViewLayoutSize(itemView);
            suggestionTv = itemView.findViewById(R.id.textView);
            suggestionImage = itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClicked(dataModel);
                }
            });
        }

        void setModelData(AllSuggestionModel model) {
            dataModel = model;
            Glide.with(mContext).load(model.getImageUrl()).into(suggestionImage);
            suggestionTv.setText(model.getText());
        }
    }
}
