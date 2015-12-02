package com.nowfloats.Image_Gallery;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.thinksity.R;

import java.util.ArrayList;

public class OtherImagesAdapter extends BaseAdapter {
    private static final String TAG = "Other Images" ;
    boolean[] checkedItems ;
    ArrayList<Integer> mList;
    LayoutInflater mInflater;
    Activity mContext;
    SparseBooleanArray mSparseBooleanArray;
    ArrayList<String>	imagesList = null;
    int size = 0;
    int count = 0;
    View right = null;

    public Handler handler = null;
    public Runnable runnable = null;
    UserSessionManager session;

//    protected ImageLoader imageLoader = ImageLoader.getInstance();
//    DisplayImageOptions options;

    public void resetCheckers(View v)
    {
        showChecker = false;
        notifyDataSetChanged();
        if(v!=null)
        {
            v.setVisibility(View.GONE);
        }
    }

    public boolean showChecker = false;
    public OtherImagesAdapter(Activity c) {
        mContext = c;
        mInflater = LayoutInflater.from(mContext);
        session = new UserSessionManager(c.getApplicationContext(),c);

        mSparseBooleanArray = new SparseBooleanArray();
        mList = new ArrayList<Integer>();
        //this.mList = mThumbIds;
        //  Log.d("OtherImagesAdapter","Constants.storeSecondaryImages : "+Constants.storeSecondaryImages);
        // Constants.storeSecondaryImages = session.getStoredGalleryImages();
        if (Constants.storeSecondaryImages != null) {
            //  for (int i = 0 ; i < Constants.storeSecondaryImages.size();i++) {
            //      Constants.storeSecondaryImages.get(i).replace(" ","");
            //  }
            imagesList = Constants.storeSecondaryImages;
            size = imagesList.size();

            //  Log.d(TAG, "imagesList : " + imagesList + " Size : " + size);

            checkedItems = new boolean[size];
            for (int i = 0; i < size; i++) {
                checkedItems[i] = false;
            }
        }
    }

    public int getCheckedCount(){
        int size = 0;
        for(int i = 0; i< getCount() ; i++){
            if(checkedItems[i])
                size = size + 1;
        }
        return size;
    }

    public void setList(ArrayList<String> List){
        imagesList = List;
        if(imagesList != null)
            count = imagesList.size();
        checkedItems = new boolean[count];
        for (int i = 0; i < count; i++) {
            checkedItems[i] = false;
        }
    }

    public int getCount() {
        if(imagesList != null)
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

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        //  Log.d("OtherImagesAdapter","getView");
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.grid_view_list_item, null);
        }


        final ImageView imageView = (ImageView) convertView.findViewById(R.id.grid_Image);

        String serverImage = Constants.storeSecondaryImages.get(position);
        //  Log.d(TAG,"server position : "+serverImage);
        //Log.d(TAG, "Server Image : "+serverImage);
        String baseName = serverImage;
        if(serverImage!=null && serverImage.length()>0 && !serverImage.equals("null")) {
            if (!serverImage.contains("http")) {
                if (!serverImage.contains("Android")) {
                    baseName = Constants.BASE_IMAGE_URL+""+ serverImage;

                    // Log.d(TAG,"Base Name : "+baseName);
                } else {
                    String fileName = imagesList.get(position).substring(imagesList.get(position).lastIndexOf('/') + 1).trim();
                    //baseName = OtherImagesFragment.imageUrl;
//            imageLoader.displayImage("file://"+	baseName, imageView, options);
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
//        imageLoader.displayImage(baseName, imageView, options);
            Picasso.with(mContext).load(baseName).placeholder(R.drawable.gal).into(imageView);
        }else{
            Picasso.with(mContext).load(R.drawable.gal).into(imageView);
        }
        return convertView;
    }
    OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            //mSparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);

            checkedItems[(Integer) buttonView.getTag()] = isChecked;
        }
    };
    // references to our images


    public void delete()
    {

//			DeleteGalleryImages task = new DeleteGalleryImages(mContext,checkedItems,this);
//			task.execute();

    }

}