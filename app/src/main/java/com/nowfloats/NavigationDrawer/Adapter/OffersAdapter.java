package com.nowfloats.NavigationDrawer.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.NavigationDrawer.model.OfferFloatsModel;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.thinksity.R;

import java.util.List;

/**
 * Created by NowFloats on 08-11-2016.
 */

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.MyViewHolder> {

    List<OfferFloatsModel> mListOfferFloatsModel;
    Context mContext;
    ProgressDialog pd;
    Activity activity;
    OnItemClickListener mItemClickListener;

    public OffersAdapter(List<OfferFloatsModel> floatsModelList, Activity activity){
        this.mListOfferFloatsModel = floatsModelList;
        this.activity = activity;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext()).inflate(R.layout.offers_cards_layout, parent, false);
        mContext = parent.getContext();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final OfferFloatsModel currentModel = mListOfferFloatsModel.get(position);
        Picasso.get().load(currentModel.tileImageUri)
                .placeholder(R.drawable.default_product_image)
                .into(holder.ivMainOffer);
        holder.tvOfferTitle.setText(currentModel.message);
        holder.tvOfferContent.setText(currentModel.messageDescription);
        holder.tvOfferTimeLine
                .setText(Methods.getFormattedDate(currentModel.dealStartDate) +
                        " - " + Methods.getFormattedDate(currentModel.dealEndDate));
        holder.tvOfferCreatedOn.setText(Methods.getFormattedDate(currentModel.createdOn));

        holder.ivShareOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageShare = currentModel.tileImageUri;
                MixPanelController.track("SharePost", null);
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        pd = ProgressDialog.show(activity, "", "Sharing . . .");
                    }
                });
                final Intent shareIntent = new Intent();
                if (!imageShare.contains("/Tile/deal.png") && !Util.isNullOrEmpty(imageShare)) {
                    if (Methods.isOnline(activity)) {
                        String url;
                        if (imageShare.contains("BizImages")) {
                            url = Constants.NOW_FLOATS_API_URL + "" + imageShare;
                        } else {
                            url = imageShare;
                        }
                        Picasso.get()
                                .load(url)
                                .into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                                        pd.dismiss();
                                        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                                        View view = new View(activity);
                                        view.draw(new Canvas(mutableBitmap));
                                        try {
                                            String path = MediaStore.Images.Media
                                                    .insertImage(activity.getContentResolver(),
                                                            mutableBitmap, "Nur", null);
                                            BoostLog.d("Path is:", path);
                                            Uri uri = Uri.parse(path);
                                            shareIntent.setType("image/png");
                                            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                            shareIntent.setAction(Intent.ACTION_SEND);
                                            shareIntent.putExtra(Intent.EXTRA_TEXT,
                                                    currentModel.message + "\n" +
                                                            currentModel.messageDescription +
                                                            "\nOffer valid from: " +
                                                            Methods.getFormattedDate(currentModel.dealStartDate) +
                                                            " to " +
                                                            Methods.getFormattedDate(currentModel.dealEndDate));
                                            if (shareIntent.resolveActivity(activity.getPackageManager())
                                                    != null) {
                                                activity.startActivityForResult(Intent.createChooser(shareIntent,
                                                        activity.getString(R.string.share_message)), 1);
                                            } else {
                                                Methods.showSnackBarNegative(activity,
                                                        activity.getString(R.string.no_app_available_for_action));
                                            }
                                        }catch (Exception e){
                                            ActivityCompat.requestPermissions(activity,
                                                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                            android.Manifest.permission.CAMERA}, 2);
                                        }
                                    }

                                    @Override
                                    public void onBitmapFailed(Exception e,Drawable errorDrawable) {
                                        pd.dismiss();
                                        Methods.showSnackBarNegative(activity,
                                                activity.getString(R.string.failed_to_download_image));
                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                    }
                                });


                    } else {
                        pd.dismiss();
                        Methods.showSnackBarNegative(activity, activity.getString(R.string.can_not_share_image_offline_mode));
                    }

                } else {
                    Methods.showSnackBarNegative(activity, activity.getString(R.string.failed_to_share));
                }
            }

        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItemClickListener!=null){
                    mItemClickListener.onItemClick(v, position);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mListOfferFloatsModel.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public ImageView ivMainOffer, ivShareOffer;
        public TextView tvOfferTitle, tvOfferContent, tvOfferTimeLine, tvOfferCreatedOn;

        public MyViewHolder(View itemView) {
            super(itemView);
            ivMainOffer = (ImageView) itemView.findViewById(R.id.imageView);
            ivShareOffer = (ImageView) itemView.findViewById(R.id.shareData);
            tvOfferTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvOfferContent = (TextView) itemView.findViewById(R.id.tv_offer_message);
            tvOfferTimeLine = (TextView) itemView.findViewById(R.id.tv_offer_date);
            tvOfferCreatedOn = (TextView) itemView.findViewById(R.id.textViewEmail);
        }

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public interface OnItemClickListener{
        public void onItemClick(View view, int position);
    }
}
