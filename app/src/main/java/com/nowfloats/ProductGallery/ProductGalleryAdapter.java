package com.nowfloats.ProductGallery;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.ProductGallery.Model.ProductListModel;
import com.nowfloats.util.Constants;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by guru
 */

public class ProductGalleryAdapter extends BaseAdapter {

    public class ViewHolder {
        public ImageView ProductImageView, shareImage;
        public TextView Product_Name;
        public TextView Currency_Type;
        public TextView OriginalPrice;
        public FrameLayout flMain;
        public FrameLayout flOverlay;
        public View vwOverlay;
        private int position = -1;


        ViewHolder(View vi){
            ProductImageView = (ImageView) vi.findViewById(R.id.proudct_image_view);
            Product_Name = (TextView) vi.findViewById(R.id.product_name);
            OriginalPrice = (TextView) vi.findViewById(R.id.product_price);
            Currency_Type = (TextView) vi.findViewById(R.id.product_currency);
            flMain = (FrameLayout) vi.findViewById(R.id.flMain);
            flOverlay = (FrameLayout) vi.findViewById(R.id.flOverlay);
            vwOverlay = (View) vi.findViewById(R.id.vwOverlay);
            shareImage = vi.findViewById(R.id.share_img);
            shareImage.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View view) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, productItemModelList.get(position).ProductUrl);
                    sendIntent.setType("text/plain");
                    sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(sendIntent);
                }
            });
        }
        void setPosition(int position){
            this.position = position;
        }
    }

    private ViewHolder viewHolder;

    private Activity activity;

    private Resources mResources;

    private Product_Gallery_Fragment.FROM from;

    private ArrayList<ProductListModel> productItemModelList;

    public boolean showChecker = false;

    public ProductGalleryAdapter(Activity activity, String currency, Product_Gallery_Fragment.FROM from) {
        this.activity = activity;
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
        // sent_check if an existing view is being reused, otherwise inflate the view
        // view lookup cache stored in tag

        View vi = convertView;
        try {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(activity);
                vi = inflater.inflate(R.layout.product_grid_view_design, null);

                viewHolder = new ViewHolder(vi);
                vi.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) vi.getTag();
            viewHolder.setPosition(position);

            final ProductListModel productItemModel = (ProductListModel) getItem(position);
            vi.setTag(R.string.key_details, productItemModel);
            vi.setTag(R.string.key_selected, position);
            viewHolder.Product_Name.setText(productItemModel.Name);
            final ImageView imageView = viewHolder.ProductImageView;
            Picasso picasso = Picasso.get();
            String image_url = productItemModel.TileImageUri;
            if (image_url != null && image_url.length() > 0 && !image_url.equals("null")) {
                if (!image_url.contains("http")) {
                    image_url = Constants.BASE_IMAGE_URL + productItemModel.TileImageUri;
                }
                picasso.load(image_url).placeholder(R.drawable.default_product_image).into(imageView);
            } else {
                picasso.load(R.drawable.default_product_image).into(imageView);
            }
            String originalPrice = productItemModel.Price;

            /*String disc = productItemModel.DiscountAmount;

            if (disc != null && disc.trim().length() > 0
                    && !disc.equals("0")) {
                double discAmt = Double.parseDouble(disc);
                if (originalPrice != null && originalPrice.trim().length() > 0
                        && !originalPrice.equals("0")) {
                    double actual = Double.parseDouble(originalPrice);
                    double result = actual - discAmt;
                    originalPrice = result + "";
                }
            }*/

            if (originalPrice != null) {
                viewHolder.Currency_Type.setText(productItemModel.CurrencyCode);
                DecimalFormat formatter = new DecimalFormat("#,##,##,##,##,##,##,###.##");
                String yourFormattedString = formatter.format(Float.parseFloat(originalPrice));
                viewHolder.OriginalPrice.setText(yourFormattedString);
            }

            if (from == Product_Gallery_Fragment.FROM.BUBBLE) {

                if (productItemModel.isProductSelected) {
                    viewHolder.flOverlay.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.flOverlay.setVisibility(View.GONE);
                }

                setOverlay(viewHolder.vwOverlay, 200, viewHolder.flMain.getWidth(), viewHolder.flMain.getHeight());
            } else {
                viewHolder.flOverlay.setVisibility(View.GONE);
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }

        return vi;
    }

    public void refreshDetails(ArrayList<ProductListModel> productItemModelList) {
        this.productItemModelList = productItemModelList;
        notifyDataSetChanged();
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