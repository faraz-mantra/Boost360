package nowfloats.nfkeyboard.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import nowfloats.nfkeyboard.R;
import nowfloats.nfkeyboard.interface_contracts.ItemClickListener;
import nowfloats.nfkeyboard.models.AllSuggestionModel;
import nowfloats.nfkeyboard.util.MethodUtils;

/**
 * Created by Admin on 27-02-2018.
 */

class ProductAdapter extends BaseAdapter<AllSuggestionModel> {
    ProductAdapter(Context context, ItemClickListener listener) {
        super(context,listener);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view =  LayoutInflater.from(mContext).inflate(R.layout.adapter_item_product,parent,false);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, AllSuggestionModel suggestion) {
        if (holder instanceof ImageHolder){
            ImageHolder myHolder = (ImageHolder) holder;
            myHolder.setModelData(suggestion);
        }
    }

    class ImageHolder extends RecyclerView.ViewHolder{
        private TextView nameTv, priceTv, discountTv, descriptionTv;
        private ImageView productImage;
        private AllSuggestionModel dataModel;
        ImageHolder(View itemView) {
            super(itemView);
            setViewLayoutSize(itemView);
            productImage = itemView.findViewById(R.id.imageView);
            nameTv = itemView.findViewById(R.id.tv_name);
            priceTv = itemView.findViewById(R.id.tv_price);
            discountTv = itemView.findViewById(R.id.tv_discount);
            descriptionTv = itemView.findViewById(R.id.tv_description);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClicked(dataModel);
                }
            });
        }

        void setModelData(AllSuggestionModel model){
            dataModel = model;
            if (model.getImageUrl() != null) {
                Glide.with(mContext).load(model.getImageUrl()).into(productImage);
            }
            priceTv.setText(MethodUtils.fromHtml(String.format("Price: <b>%s</b>", model.getPrice())));
            discountTv.setText(MethodUtils.fromHtml(String.format("Discount: <b>%s</b>", model.getDiscount())));
            descriptionTv.setText(MethodUtils.fromHtml(String.format("Description: <b>%s</b>", model.getDescription())));
            nameTv.setText(MethodUtils.fromHtml(String.format("Name: <b>%s</b>", model.getText())));
        }
    }
}
