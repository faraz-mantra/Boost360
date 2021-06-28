package com.nowfloats.ProductGallery.Adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nowfloats.ProductGallery.Model.Product;
import com.nowfloats.ProductGallery.ProductCatalogActivity;
import com.nowfloats.helper.Helper;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.List;

public class ProductCategoryRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    //    private ProductCatalogActivity appContext;
    private List<Product> productList;
    private OnItemClicked callback;
    private OnShareClicked shareCallback;
    private static final int STORAGE_CODE = 120;
    static ProgressDialog pd;
    Target targetMap = null;

    public ProductCategoryRecyclerAdapter(Context context)//, ProductCatalogActivity appContext)
    {
        this.context = context;
//        this.appContext = appContext;
        this.productList = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item_product_catalog, viewGroup, false);
        return new ProductListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {

        if (holder instanceof ProductListViewHolder) {
            Product model = productList.get(i);
            final ProductListViewHolder viewHolder = (ProductListViewHolder) holder;

            viewHolder.btnEdit.setTag(i);

            viewHolder.tvName.setText(model.Name);
            viewHolder.tvDescription.setText(model.Description);

            String category = model.category == null ? "" : model.category;
            String brand = TextUtils.isEmpty(model.brandName) ? "" : "<b>" + model.brandName + "</b>";
            viewHolder.tvBrand.setVisibility(View.VISIBLE);
            viewHolder.tvMissingInfo.setVisibility(View.GONE);

            if (!category.isEmpty() && !brand.isEmpty()) {
                String value = category.concat(" by ").concat(brand);
                viewHolder.tvBrand.setText(Html.fromHtml(value));
            } else if (!category.isEmpty()) {
                viewHolder.tvBrand.setText(category);
            } else if (!brand.isEmpty()) {
                viewHolder.tvBrand.setText(Html.fromHtml(brand));
            } else {
                viewHolder.tvBrand.setVisibility(View.GONE);
                viewHolder.tvMissingInfo.setVisibility(View.VISIBLE);
            }

            try {
                String formattedPrice = Helper.getCurrencyFormatter().format(model.Price - model.DiscountAmount);
                viewHolder.tvPrice.setText(String.valueOf(model.CurrencyCode + " " + formattedPrice));

                if (model.DiscountAmount != 0) {
                    viewHolder.tvBasePrice.setVisibility(View.VISIBLE);

                    formattedPrice = Helper.getCurrencyFormatter().format(model.Price);
                    viewHolder.tvBasePrice.setPaintFlags(viewHolder.tvBasePrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    viewHolder.tvBasePrice.setText(String.valueOf(model.CurrencyCode + " " + formattedPrice));
                } else {
                    viewHolder.tvBasePrice.setVisibility(View.INVISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            Picasso picasso = Picasso.get();
            String image_url = model.TileImageUri;

            if (image_url != null && image_url.length() > 0 && !image_url.equals("null")) {
                if (!image_url.contains("http")) {
                    image_url = Constants.BASE_IMAGE_URL + model.TileImageUri;
                }

                picasso.load(image_url).placeholder(R.drawable.default_product_image).into(viewHolder.thumbnail);
            } else {
                picasso.load(R.drawable.default_product_image).into(viewHolder.thumbnail);
            }

        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }


    class ProductListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView thumbnail;
        private TextView tvBrand;
        private TextView tvName;
        private TextView tvDescription;
        private TextView tvPrice;
        private TextView tvBasePrice;
        private TextView tvMissingInfo;
        private Button btnEdit;
        private ImageView shareButton;
        private ImageView whatsappShareButton;
        private ImageView facebookShareButton;

        private ProductListViewHolder(View itemView) {
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
            shareButton = itemView.findViewById(R.id.shareData);
            whatsappShareButton = itemView.findViewById(R.id.share_whatsapp);
            facebookShareButton = itemView.findViewById(R.id.share_facebook);
            btnEdit.setOnClickListener(v -> callback.onItemClick(productList.get(getAdapterPosition())));
//            whatsappShareButton.setOnClickListener(v -> share(false, 1, productList.get(getAdapterPosition())));
//            facebookShareButton.setOnClickListener(v -> share(false, 0, productList.get(getAdapterPosition())));
            shareButton.setOnClickListener(v -> shareCallback.onShareClicked(true, 0, productList.get(getAdapterPosition())));
            whatsappShareButton.setOnClickListener(v -> shareCallback.onShareClicked(false, 1, productList.get(getAdapterPosition())));
            facebookShareButton.setOnClickListener(v -> shareCallback.onShareClicked(false, 0, productList.get(getAdapterPosition())));
        }

        @Override
        public void onClick(View v) {
            callback.onItemClick(productList.get(getAdapterPosition()));
        }
    }

    public void share(boolean defaultShare, int type, String productUrl) {

        //type 0 = facebook, type 1= whatsApp, type 3=default
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, productUrl);
        if (!defaultShare) {
            if (type == 1) {
                share.setPackage(context.getString(R.string.whatsapp_package));
            } else if (type == 0) {
                share.setPackage(context.getString(R.string.facebook_package));
            }
        }
        context.startActivity(Intent.createChooser(share, context.getString(R.string.share_updates)));

    }

    public void share(boolean defaultShare, int type, Product product) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            Methods.showDialog(context, context.getString(R.string.storage_permission), "To share your image we need storage permission.",
                    (dialog, which) -> ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_CODE));
            return;
        }

        pd = ProgressDialog.show(context, "", "Sharing . . .");

        Intent share = new Intent(Intent.ACTION_SEND);
        share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (Methods.isOnline((Activity) context)) {
            @SuppressLint("DefaultLocale") String shareText = String.format("*%s*\nPrice: INR. %.2f\n%s\nView more details at: %s", product.Name, product.Price - product.DiscountAmount,
                    product.Description, product.ProductUrl);

            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    pd.dismiss();
                    targetMap = null;
                    try {
                        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                        View view = new View(context);
                        view.draw(new Canvas(mutableBitmap));
                        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), mutableBitmap, "boost_360", null);
                        BoostLog.d("Path is:", path);
                        Uri uri = Uri.parse(path);
                        share.putExtra(Intent.EXTRA_TEXT, shareText);
                        share.putExtra(Intent.EXTRA_STREAM, uri);
                        share.setType("image/*");
                        if (share.resolveActivity(context.getPackageManager()) != null) {
                            if (!defaultShare) {
                                if (type == 0) {
                                    share.setPackage(context.getString(R.string.facebook_package));
                                } else if (type == 1) {
                                    share.setPackage(context.getString(R.string.whatsapp_package));
                                }
                            }
                            ProductCatalogActivity activity = (ProductCatalogActivity) context;
                            activity.startActivityForResult(Intent.createChooser(share, context.getString(R.string.share_updates)), 1);
                        }
                    } catch (OutOfMemoryError e) {
                        Toast.makeText(context, context.getString(R.string.image_size_is_large), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(context, context.getString(R.string.image_not_able_to_share), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    pd.dismiss();
                    targetMap = null;
                    Methods.showSnackBarNegative((Activity) context, context.getString(R.string.failed_to_download_image));
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            targetMap = target;
            Picasso.get().load(product.ImageUri).into(target);
        } else {
            pd.dismiss();
            Methods.showSnackBarNegative((Activity) context, context.getString(R.string.can_not_share_image_offline_mode));
        }

//        Picasso picasso = Picasso.get();
//        Object image = picasso.load(product.ImageUri);
//        picasso.load(product.ImageUri);
//        saveImage(product.ImageUri);
//
//        //type 0 = facebook, type 1= whatsApp, type 3=default
//
//        share.setType("text/plain");
//        if(!defaultShare)
//        {
//            if(type==1)
//            { share.setPackage(getString(R.string.whatsapp_package));}
//            else if(type==0)
//            { share.setPackage(getString(R.string.facebook_package)); }
//        }
//        context.startActivity(Intent.createChooser(share,  context.getString(R.string.share_updates)));
    }

    public void setData(List<Product> productList, boolean flag) {
        if (flag) {
            this.productList.clear();
        }

        this.productList.addAll(productList);
        notifyDataSetChanged();
    }

    public interface OnShareClicked {
        void onShareClicked(boolean defaultShare, int type, Product product);
    }

    public void onShareClickListener(final OnShareClicked shareCallback) {
        this.shareCallback = shareCallback;
    }

    public interface OnItemClicked {
        void onItemClick(Product product);
    }

    public void SetOnItemClickListener(final OnItemClicked callback) {
        this.callback = callback;
    }
}