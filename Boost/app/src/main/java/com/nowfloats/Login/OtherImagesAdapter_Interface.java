package com.nowfloats.Login;

import android.app.Activity;
import android.graphics.Bitmap;
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

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.thinksity.R;
import com.nowfloats.util.Constants;

import java.util.ArrayList;

public class OtherImagesAdapter_Interface extends BaseAdapter {
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

		protected ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options;

    public interface OtherImagesAdapterInterface{

        public void imagesDownloaded(String value);

    }

    OtherImagesAdapterInterface imagedDownloadedInterface ;


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


	    public OtherImagesAdapter_Interface(Activity c) {
	        mContext = c;
	        mInflater = LayoutInflater.from(mContext);
	        
	        options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.gal)
			.showImageForEmptyUri(R.drawable.gal)
			.showImageOnFail(R.drawable.gal)
			.cacheInMemory(true)
			.cacheOnDisk(true)
			.considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();

            imagedDownloadedInterface = (OtherImagesAdapterInterface) mContext ;

            session = new UserSessionManager(c.getApplicationContext(),c);


            imageLoader.init(ImageLoaderConfiguration.createDefault(c.getApplicationContext()));
	         mSparseBooleanArray = new SparseBooleanArray();
				mList = new ArrayList<Integer>();
				//this.mList = mThumbIds;
            Log.d("OtherImagesAdapter","Constants.storeSecondaryImages : "+ Constants.storeSecondaryImages);
           // Constants.storeSecondaryImages = session.getStoredGalleryImages();
			if (Constants.storeSecondaryImages != null) {
				imagesList = Constants.storeSecondaryImages;
				size = imagesList.size();
                Log.d(TAG,"imagesList : "+imagesList+" Size : "+size);
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
	    		count = imagesList.size();
            Log.d(TAG,"Count : "+count);
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

            Log.d("OtherImagesAdapter","getView");
	    	if(convertView == null) {
				convertView = mInflater.inflate(R.layout.grid_view_list_item, null);
			}


			final ImageView imageView = (ImageView) convertView.findViewById(R.id.grid_Image);
			
			String serverImage = imagesList.get(position);
            Log.d(TAG,"server position : "+serverImage);
            //Log.d(TAG, "Server Image : "+serverImage);
			String baseName = serverImage;
			if(!serverImage.contains("Android"))
			{
				baseName = "https://api.withfloats.com/"+serverImage;

                Log.d(TAG,"Base Name : "+baseName);
			}
			else{
				String fileName = imagesList.get(position).substring(imagesList.get(position).lastIndexOf('/')+1).trim();
				//baseName = OtherImagesFragment.imageUrl;
				imageLoader.displayImage("file://"+	baseName, imageView, options);

		        return convertView;
			}
			
			
			
			imageLoader.displayImage(baseName, imageView, options);

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