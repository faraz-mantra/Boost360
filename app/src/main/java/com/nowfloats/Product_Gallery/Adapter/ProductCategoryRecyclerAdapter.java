package com.nowfloats.Product_Gallery.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.Product_Gallery.Model.Product;
import com.nowfloats.helper.Helper;
import com.nowfloats.util.Constants;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.List;

public class ProductCategoryRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnItemClicked callback;

    public ProductCategoryRecyclerAdapter(Context context)
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
            Product model = productList.get(i);
            final ProductListViewHolder viewHolder = (ProductListViewHolder) holder;

            viewHolder.btnEdit.setTag(i);

            viewHolder.tvName.setText(model.Name);
            viewHolder.tvDescription.setText(model.Description);

            String category = model.category == null ? "" : model.category;
            String brand = model.brandName == null ? "" : model.brandName;

            viewHolder.tvBrand.setVisibility(View.VISIBLE);
            viewHolder.tvMissingInfo.setVisibility(View.GONE);

            if(!category.isEmpty() && !brand.isEmpty())
            {
                String value = category.concat(" by ").concat(brand);
                viewHolder.tvBrand.setText(value);
            }

            else if(!category.isEmpty())
            {
                viewHolder.tvBrand.setText(category);
            }

            else if(!brand.isEmpty())
            {
                viewHolder.tvBrand.setText(brand);
            }

            else
            {
                viewHolder.tvBrand.setVisibility(View.GONE);
                viewHolder.tvMissingInfo.setVisibility(View.VISIBLE);
            }

            try
            {
                String formattedPrice = Helper.getCurrencyFormatter().format(model.Price - model.DiscountAmount);
                viewHolder.tvPrice.setText(String.valueOf(model.CurrencyCode + " " + formattedPrice));

                if(model.DiscountAmount != 0)
                {
                    viewHolder.tvBasePrice.setVisibility(View.VISIBLE);

                    formattedPrice = Helper.getCurrencyFormatter().format(model.Price);
                    viewHolder.tvBasePrice.setPaintFlags(viewHolder.tvBasePrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    viewHolder.tvBasePrice.setText(String.valueOf(model.CurrencyCode + " " + formattedPrice));
                }

                else
                {
                    viewHolder.tvBasePrice.setVisibility(View.INVISIBLE);
                }
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }


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
        private TextView tvBasePrice;
        private TextView tvMissingInfo;
        private Button btnEdit;

        private ProductListViewHolder(View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);

            thumbnail = itemView.findViewById(R.id.thumbnail);
            tvBrand = itemView.findViewById(R.id.label_brand);
            tvName = itemView.findViewById(R.id.label_name);
            tvDescription = itemView.findViewById(R.id.label_description);
            tvPrice = itemView.findViewById(R.id.label_price);
            tvBasePrice = itemView.findViewById(R.id.label_base_price);
            tvMissingInfo = itemView.findViewById(R.id.label_missing_info);
            btnEdit = itemView.findViewById(R.id.button_edit);

            btnEdit.setOnClickListener(v -> callback.onItemClick(productList.get(getAdapterPosition())));
        }

        @Override
        public void onClick(View v)
        {
            callback.onItemClick(productList.get(getAdapterPosition()));
        }
    }

    public void setData(List<Product> productList, boolean flag)
    {
        if(flag)
        {
            this.productList.clear();
        }

        this.productList.addAll(productList);
        notifyDataSetChanged();
    }

    public interface OnItemClicked
    {
        void onItemClick(Product product);
    }

    public void SetOnItemClickListener(final OnItemClicked callback)
    {
        this.callback = callback;
    }
}