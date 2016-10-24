package com.nowfloats.Product_Gallery;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.Product_Gallery.Model.ProductListModel;
import com.nowfloats.util.Constants;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import java.text.DecimalFormat;

/**
 * Created by guru
 */

public class ProductGalleryAdapter extends BaseAdapter {

    // View lookup cache
    public static class ViewHolder {
        public ImageView ProductImageView;
        public TextView Product_Name;
        public TextView Currency_Type;
        public TextView OriginalPrice;
    }

    ViewHolder viewHolder;
    Activity activity;
    public String currencyType = "";

    public ProductGalleryAdapter(Activity activity,String currency) {
        this.activity=activity;
        //this.currencyType = currency;
    }

    @Override
    public int getCount() {
        return Product_Gallery_Fragment.productItemModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return Product_Gallery_Fragment.productItemModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        // Check if an existing view is being reused, otherwise inflate the view
        // view lookup cache stored in tag

        View vi = convertView;
        try {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(activity);
                vi = inflater.inflate(R.layout.product_grid_view_design, null);

                viewHolder = new ViewHolder();
                viewHolder.ProductImageView = (ImageView) vi.findViewById(R.id.proudct_image_view);
                viewHolder.Product_Name = (TextView) vi.findViewById(R.id.product_name);
                viewHolder.OriginalPrice = (TextView) vi.findViewById(R.id.product_price);
                viewHolder.Currency_Type = (TextView) vi.findViewById(R.id.product_currency);
                vi.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) vi.getTag();

            final ProductListModel productItemModel = (ProductListModel) getItem(position);
            viewHolder.Product_Name.setText(productItemModel.Name);
            ImageView imageView = viewHolder.ProductImageView;
            Picasso picasso = Picasso.with(activity);
            String image_url = productItemModel.TileImageUri;
            if(image_url!=null && image_url.length()>0 && !image_url.equals("null")) {
                if (!image_url.contains("http")) {
                    image_url = Constants.BASE_IMAGE_URL + productItemModel.TileImageUri;
                }
                picasso.load(image_url).placeholder(R.drawable.default_product_image).into(imageView);
            }else{
                picasso.load(R.drawable.default_product_image).into(imageView);
            }

            String originalPrice = productItemModel.Price;
            String disc = productItemModel.DiscountAmount;
            if (disc!=null && disc.trim().length()>0
                    && !disc.equals("0")){
                double discAmt = Double.parseDouble(disc);
                if (originalPrice!=null && originalPrice.trim().length()>0
                        && !originalPrice.equals("0")){
                    double actual = Double.parseDouble(originalPrice);
                    double result = actual-discAmt;
                    originalPrice = result+"";
                }
            }
            if (originalPrice!=null){
                viewHolder.Currency_Type.setText(productItemModel.CurrencyCode);
                DecimalFormat formatter = new DecimalFormat("#,##,##,##,##,##,##,###");
                String yourFormattedString = formatter.format(Float.parseFloat(originalPrice));
                viewHolder.OriginalPrice.setText(yourFormattedString);
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return vi;
    }
}