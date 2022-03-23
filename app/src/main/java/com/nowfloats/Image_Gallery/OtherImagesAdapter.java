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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nowfloats.util.Constants;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.thinksity.R;

@Deprecated
public class OtherImagesAdapter extends BaseAdapter {

    public Handler handler = null;
    public Runnable runnable = null;
    private LayoutInflater mInflater;
    private Activity mContext;

    public OtherImagesAdapter(Activity activity) {
        mContext = activity;
        mInflater = LayoutInflater.from(mContext);
    }

    public int getCount() {
        if (Constants.storeSecondaryImages != null) {
            return Constants.storeSecondaryImages.size();
        }

        return 0;
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

        final ImageView imageView = convertView.findViewById(R.id.grid_Image);
        final LinearLayout llSelect = convertView.findViewById(R.id.llSelect);
        llSelect.setTag(position);

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
                            paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

                            Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
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

                    Picasso.get().load("file://" + baseName).transform(t).placeholder(R.drawable.gal).into(imageView);
                    return convertView;
                }
            } else {
                baseName = serverImage;
            }

            Picasso.get().load(baseName).placeholder(R.drawable.gal).resize(200, 0).into(imageView);
        } else {
            Picasso.get().load(R.drawable.gal).into(imageView);
        }

        convertView.setTag(position);
        convertView.setTag(R.string.key_selected, llSelect);
        convertView.setOnClickListener(v -> {

            Intent fullImage = new Intent(mContext, FullScreen_Gallery_Image.class);
            fullImage.putExtra("currentPositon", (int) v.getTag());
            mContext.startActivityForResult(fullImage,202);
            mContext.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        return convertView;
    }
}