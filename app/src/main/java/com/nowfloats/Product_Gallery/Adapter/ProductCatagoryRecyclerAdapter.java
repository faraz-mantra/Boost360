package com.nowfloats.Product_Gallery.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.Product_Gallery.Model.ProductListModel;
import com.nowfloats.helper.Helper;
import com.nowfloats.util.Constants;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ProductCatagoryRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<ProductListModel> productList;
    private OnItemClicked callback;

    public ProductCatagoryRecyclerAdapter(Context context)
    {
        this.context = context;
        this.productList = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item_product_catalog, viewGroup, false);
        return new ProductListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {

        if (holder instanceof ProductListViewHolder)
        {
            ProductListModel model = productList.get(i);
            final ProductListViewHolder viewHolder = (ProductListViewHolder) holder;

            viewHolder.tvName.setText(model.Name);
            viewHolder.tvDescription.setText(model.Description);

            if (model.Price != null)
            {
                try
                {
                    String formattedPrice = Helper.getCurrencyFormatter().format(Float.parseFloat(model.Price));
                    viewHolder.tvPrice.setText(String.valueOf(model.CurrencyCode + " " + formattedPrice));
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            else
            {
                viewHolder.tvPrice.setText(String.valueOf(model.CurrencyCode + " 0.00"));
            }

            viewHolder.tvDiscountedPrice.setPaintFlags(viewHolder.tvDiscountedPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            Picasso picasso = Picasso.with(context);
            String image_url = model.TileImageUri;

            if (image_url != null && image_url.length() > 0 && !image_url.equals("null"))
            {
                if (!image_url.contains("http"))
                {
                    image_url = Constants.BASE_IMAGE_URL + model.TileImageUri;
                }

                picasso.load(image_url).placeholder(R.drawable.default_product_image).into(viewHolder.thumbnail);
            }

            else
            {
                picasso.load(R.drawable.default_product_image).into(viewHolder.thumbnail);
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return productList.size();
    }


    class ProductListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private ImageView thumbnail;
        private TextView tvBrand;
        private TextView tvName;
        private TextView tvDescription;
        private TextView tvPrice;
        private TextView tvDiscountedPrice;

        private ProductListViewHolder(View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);

            thumbnail = itemView.findViewById(R.id.thumbnail);
            tvBrand = itemView.findViewById(R.id.label_brand);
            tvName = itemView.findViewById(R.id.label_name);
            tvDescription = itemView.findViewById(R.id.label_description);
            tvPrice = itemView.findViewById(R.id.label_price);
            tvDiscountedPrice = itemView.findViewById(R.id.label_discounted_price);
        }

        @Override
        public void onClick(View v)
        {
            callback.onItemClick(productList.get(getAdapterPosition()));
        }
    }

    public void setData(List<ProductListModel> productList)
    {
        this.productList.addAll(productList);
        notifyDataSetChanged();
    }

    public interface OnItemClicked
    {
        void onItemClick(ProductListModel productListModel);
    }

    public void SetOnItemClickListener(final OnItemClicked callback)
    {
        this.callback = callback;
    }
}