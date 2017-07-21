package com.nowfloats.swipecard.adapters;

import android.app.Activity;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.Product_Gallery.Product_Gallery_Fragment;
import com.nowfloats.swipecard.models.SugProducts;
import com.nowfloats.swipecard.models.SugUpdates;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guru
 */

public class SugProductsAdapter extends BaseAdapter {

    // View lookup cache
    public static class ViewHolder {
        public ImageView ProductImageView;
        public TextView Product_Name;
        public TextView Currency_Type;
        public TextView OriginalPrice;
        public FrameLayout flMain;
        public FrameLayout flOverlay;
        public View vwOverlay;
    }

    ViewHolder viewHolder;
    Activity activity;
    public String currencyType = "";
    private Resources mResources;
    private Product_Gallery_Fragment.FROM from;
    private ArrayList<SugProducts> productItemModelList;

    public SugProductsAdapter(Activity activity, List<SugProducts> productItemModelList) {
        this.activity = activity;
        this.productItemModelList = (ArrayList<SugProducts>) productItemModelList;
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
                viewHolder.flMain = (FrameLayout) vi.findViewById(R.id.flMain);
                viewHolder.flOverlay = (FrameLayout) vi.findViewById(R.id.flOverlay);
                viewHolder.vwOverlay = (View) vi.findViewById(R.id.vwOverlay);

                vi.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) vi.getTag();


            final SugUpdates productItemModel = (SugUpdates) getItem(position);
            vi.setTag(R.string.key_details, productItemModel);
            viewHolder.Product_Name.setText(productItemModel.getName());
            final ImageView imageView = viewHolder.ProductImageView;
            Picasso picasso = Picasso.with(activity);
            String image_url = productItemModel.getImage();
            if (image_url != null && image_url.length() > 0 && !image_url.equals("null")) {
//                if (!image_url.contains("http")) {
//                    image_url = Constants.BASE_IMAGE_URL + productItemModel.TileImageUri;
//                }
                picasso.load(image_url).placeholder(R.drawable.default_product_image).into(imageView);
            } else {
                picasso.load(R.drawable.default_product_image).into(imageView);
            }
//            String originalPrice = productItemModel.Price;
//            String disc = productItemModel.DiscountAmount;
//            if (disc != null && disc.trim().length() > 0
//                    && !disc.equals("0")) {
//                double discAmt = Double.parseDouble(disc);
//                if (originalPrice != null && originalPrice.trim().length() > 0
//                        && !originalPrice.equals("0")) {
//                    double actual = Double.parseDouble(originalPrice);
//                    double result = actual - discAmt;
//                    originalPrice = result + "";
//                }
//            }
//            if (originalPrice != null) {
//                viewHolder.Currency_Type.setText(productItemModel.CurrencyCode);
//                DecimalFormat formatter = new DecimalFormat("#,##,##,##,##,##,##,###");
//                String yourFormattedString = formatter.format(Float.parseFloat(originalPrice));
//                viewHolder.OriginalPrice.setText(yourFormattedString);
//            }


            if (productItemModel.isSelected()) {
                viewHolder.flOverlay.setVisibility(View.VISIBLE);
            } else {
                viewHolder.flOverlay.setVisibility(View.GONE);
            }

            setOverlay(viewHolder.vwOverlay, 200, viewHolder.flMain.getWidth(), viewHolder.flMain.getHeight());

        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return vi;
    }

    public void refreshDetails(ArrayList<SugProducts> productItemModelList) {
        this.productItemModelList = productItemModelList;
        notifyDataSetChanged();
    }

    public ArrayList<SugProducts> getDetails() {
        return productItemModelList;
    }

    public void setOverlay(View v, int opac, int width, int height) {
        int opacity = opac; // from 0 to 255
        v.setBackgroundColor(opacity * 0x1000000); // black with a variable alpha
        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(width, height);
        params.gravity = Gravity.NO_GRAVITY;
        v.setLayoutParams(params);
        v.invalidate();
    }
}