package com.nowfloats.Image_Gallery;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.thinksity.R;

import java.util.ArrayList;

public class OtherImagesAdapter extends BaseAdapter {

    private static final String TAG = "Other Images";

    private ArrayList<Integer> arrSelectedImages;

    private LayoutInflater mInflater;

    private Activity mContext;

    private ArrayList<String> imagesList = null;

    private int size = 0, count = 0;

    public Handler handler = null;

    public Runnable runnable = null;

    private UserSessionManager session;

    public void resetCheckers() {
        showChecker = false;
        arrSelectedImages.clear();
        notifyDataSetChanged();
    }

    public boolean showChecker = false;

    public OtherImagesAdapter(Activity c) {
        mContext = c;
        mInflater = LayoutInflater.from(mContext);
        session = new UserSessionManager(c.getApplicationContext(), c);

        arrSelectedImages = new ArrayList<Integer>();

        if (Constants.storeSecondaryImages != null) {
            imagesList = Constants.storeSecondaryImages;
            size = imagesList.size();
        }
        showChecker = false;
    }

    public int getCheckedCount() {
        return arrSelectedImages.size();
    }
    public ArrayList<Integer> getSelectedImages() {
        return arrSelectedImages;
    }

    public void setList(ArrayList<String> List) {
        imagesList = List;
        if (imagesList != null)
            count = imagesList.size();
        arrSelectedImages.clear();
    }

    public int getCount() {
        if (Constants.storeSecondaryImages != null)
            count = Constants.storeSecondaryImages.size();
        Log.d(TAG, "Count : " + count);
        return count;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.grid_view_list_item, null);
        }

        final ImageView imageView = (ImageView) convertView.findViewById(R.id.grid_Image);
        final LinearLayout llSelect = (LinearLayout) convertView.findViewById(R.id.llSelect);
        llSelect.setTag(position);

        if (arrSelectedImages.contains(position)) {
            llSelect.setVisibility(View.VISIBLE);
        } else {
            llSelect.setVisibility(View.GONE);
        }

        String serverImage = null;
        if (Constants.storeSecondaryImages != null) {
            serverImage = Constants.storeSecondaryImages.get(position);
        }
        String baseName = serverImage;
        if (serverImage != null && serverImage.length() > 0 && !serverImage.equals("null")) {
            if (!serverImage.contains("http")) {
                if (!serverImage.contains("Android")) {
                    baseName = Constants.BASE_IMAGE_URL + "" + serverImage;
                } else {
                    final int radius = 50;
                    final int margin = 0;
                    Transformation t = new Transformation() {
                        @Override
                        public Bitmap transform(Bitmap source) {
                            final Paint paint = new Paint();
                            paint.setAntiAlias(true);
                            paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP,
                                    Shader.TileMode.CLAMP));

                            Bitmap output = Bitmap.createBitmap(source.getWidth(),
                                    source.getHeight(), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(output);
                            canvas.drawRoundRect(new RectF(margin, margin, source.getWidth()
                                    - margin, source.getHeight() - margin), radius, radius, paint);

                            if (source != output) {
                                source.recycle();
                            }
                            return output;
                        }

                        @Override
                        public String key() {
                            return "rounded";
                        }
                    };
                    Picasso.with(mContext).load("file://" + baseName).transform(t).placeholder(R.drawable.gal).into(imageView);
                    return convertView;
                }
            } else {
                baseName = serverImage;
            }
            Picasso.with(mContext).load(baseName).placeholder(R.drawable.gal).resize(200, 0).into(imageView);
        } else {
            Picasso.with(mContext).load(R.drawable.gal).into(imageView);
        }

        convertView.setTag(position);
        convertView.setTag(R.string.key_selected, llSelect);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!showChecker) {
                    Intent fullImage = new Intent(mContext, FullScreen_Gallery_Image.class);
                    fullImage.putExtra("currentPositon", (int) v.getTag());
                    mContext.startActivity(fullImage);
                    mContext.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    LinearLayout llSelect = (LinearLayout) v.getTag(R.string.key_selected);
                    llSelect.setVisibility(View.VISIBLE);
                    arrSelectedImages.add((Integer) v.getTag());
                    ((ImageGalleryActivity) mContext).showActionDelete(arrSelectedImages.size());
//                    if(llSelect.getVisibility() == View.VISIBLE){
//                        llSelect.setVisibility(View.GONE);
//                    }
                }
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (!showChecker) {
                    showChecker = true;
                    arrSelectedImages.add((Integer) v.getTag());
                    notifyDataSetChanged();
                    ((ImageGalleryActivity) mContext).showActionDelete(arrSelectedImages.size());
                }
                return false;
            }
        });

        llSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrSelectedImages.remove((Integer) v.getTag());
                v.setVisibility(View.GONE);
                if (arrSelectedImages.size() == 0) {
                    showChecker = false;
                    ((ImageGalleryActivity) mContext).hideActionDelete();
                    notifyDataSetChanged();
                } else {
                    ((ImageGalleryActivity) mContext).showActionDelete(arrSelectedImages.size());
                }
            }
        });
        return convertView;
    }

}