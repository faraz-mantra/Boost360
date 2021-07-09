package com.nowfloats.customerassistant.adapters;

import android.app.Activity;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.ProductGallery.Product_Gallery_Fragment;
import com.nowfloats.customerassistant.SuggestionSelectionListner;
import com.nowfloats.customerassistant.models.SugProducts;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guru
 */

public class CAProductsAdapter extends BaseAdapter {

    // View lookup cache
    public static class ViewHolder {
        public ImageView ProductImageView;
        public TextView Product_Name;
        public TextView Currency_Type;
        public TextView OriginalPrice;
        public CheckBox cbUpdate;
    }

    ViewHolder viewHolder;
    Activity activity;
    public String currencyType = "";
    private Resources mResources;
    private Product_Gallery_Fragment.FROM from;
    private ArrayList<SugProducts> productItemModelList;
    private SuggestionSelectionListner mSuggestionSelectionListner;

    public CAProductsAdapter(Activity activity, List<SugProducts> productItemModelList) {
        this.activity = activity;
        this.productItemModelList = (ArrayList<SugProducts>) productItemModelList;
        mResources = activity.getResources();
        this.from = from;
        //this.currencyType = currency;
    }

    public CAProductsAdapter(Activity activity,
                             List<SugProducts> productItemModelList,
                             SuggestionSelectionListner mSuggestionSelectionListner) {
        this.activity = activity;
        this.productItemModelList = (ArrayList<SugProducts>) productItemModelList;
        this.mSuggestionSelectionListner = mSuggestionSelectionListner;
        mResources = activity.getResources();
        this.from = from;
        //this.currencyType = currency;
    }

    @Override
    public int getCount() {
        if (productItemModelList != null && productItemModelList.size() > 0)
            return productItemModelList.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return productItemModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        try {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(activity);
                vi = inflater.inflate(R.layout.ca_product_grid_view_design, null);

                viewHolder = new ViewHolder();
                viewHolder.ProductImageView = (ImageView) vi.findViewById(R.id.proudct_image_view);
                viewHolder.Product_Name = (TextView) vi.findViewById(R.id.product_name);
                viewHolder.OriginalPrice = (TextView) vi.findViewById(R.id.product_price);
                viewHolder.Currency_Type = (TextView) vi.findViewById(R.id.product_currency);
                viewHolder.cbUpdate = (CheckBox) vi.findViewById(R.id.cbUpdate);

                vi.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) vi.getTag();


            final SugProducts productItemModel = (SugProducts) getItem(position);
            vi.setTag(R.string.key_details, productItemModel);
            viewHolder.cbUpdate.setTag(R.string.key_details, productItemModel);
            viewHolder.Product_Name.setText(productItemModel.getProductName());
            final ImageView imageView = viewHolder.ProductImageView;
            Picasso picasso = Picasso.get();
            String image_url = productItemModel.getImage();
            if (image_url != null && image_url.length() > 0 && !image_url.equals("null")) {
//                if (!image_url.contains("http")) {
//                    image_url = Constants.BASE_IMAGE_URL + productItemModel.TileImageUri;
//                }
                picasso.load(image_url).placeholder(R.drawable.default_product_image).into(imageView);
            } else {
                picasso.load(R.drawable.default_product_image).into(imageView);
            }


            vi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateView(view);

                }
            });

            viewHolder.cbUpdate.setChecked(productItemModel.isSelected());
            viewHolder.cbUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateView(view);
                }
            });


        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return vi;
    }

    private void updateView(View view) {
        final SugProducts sugProducts = (SugProducts) view.getTag(R.string.key_details);
        sugProducts.setSelected(!sugProducts.isSelected());
        CheckBox cbUpdate = (CheckBox) view.findViewById(R.id.cbUpdate);
        cbUpdate.setChecked(sugProducts.isSelected());
        mSuggestionSelectionListner.onSelection(sugProducts.isSelected());
    }

    public void refreshDetails(ArrayList<SugProducts> productItemModelList) {
        this.productItemModelList = productItemModelList;
        notifyDataSetChanged();
    }

    public ArrayList<SugProducts> getDetails() {
        return productItemModelList;
    }


}