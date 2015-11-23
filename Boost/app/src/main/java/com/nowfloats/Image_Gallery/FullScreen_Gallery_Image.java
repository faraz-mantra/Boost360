package com.nowfloats.Image_Gallery;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.util.Methods;
import com.thinksity.R;

public class FullScreen_Gallery_Image extends Activity {

    private ImageAdapter adapter;
    ViewPager viewPager;
    private int currentPos;
    //Activity context;

//    public FullScreen_Gallery_Image(Context context) {
//        super(context);
//        this.context =(Activity) context;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        onAttachedToWindow();
        // ...but notify us that it happened.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        setContentView(R.layout.activity_full_screen__gallery__image);
        Methods.isOnline(FullScreen_Gallery_Image.this);




        ImageView previousImageView = (ImageView) findViewById(R.id.previousImage);
        ImageView nextImageView = (ImageView) findViewById(R.id.nextImage);
        final TextView currentTextView = (TextView) findViewById(R.id.currentCountValue);
        final TextView maxCountTextView = (TextView) findViewById(R.id.maxCount);
        // ImageView fullImageView = (ImageView) imageDialog.findViewById(R.id.fullImageView);
        // Log.d("Base Name","imageLoader base name : "+baseName);
        // imageLoader.displayImage(baseName, fullImageView);
        //fullImageView.setImageBitmap(myBitmap);


        Bundle extras = getIntent().getExtras();



        int selectedPOS = extras.getInt("currentPositon");
        currentPos = extras.getInt("currentPositon"); ;

        viewPager = (ViewPager) findViewById(R.id.galleryImageViewpager);
        adapter = new ImageAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(selectedPOS);
        final int maxNumberofImages = adapter.getCount();
        currentTextView.setText(Integer.toString(selectedPOS+1));
        maxCountTextView.setText(Integer.toString(maxNumberofImages));
        //currentTextView.setId(R.id.custom_view_pager);
        // viewPager.setId(R.id.custom_view_pager);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentTextView.setText(""+(position+1));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        previousImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log.d("Image_Gallery_Fragment","Current POS : "+selectedPOS);
                int selectedPosition = getItem(-1);
                viewPager.setCurrentItem(selectedPosition, true);
                if (viewPager.getCurrentItem()==0){
                    currentTextView.setText("1");
                }else{
                    currentTextView.setText(""+(Integer.parseInt(currentTextView.getText().toString())-1));
                }
            }
        });

        nextImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Image_Gallery_Fragment", "Current POS : " + currentPos);
                int selectedPosition = getItem(+1);
                viewPager.setCurrentItem(selectedPosition, true);
                if(viewPager.getAdapter().getCount()-1== viewPager.getCurrentItem()){
                    currentTextView.setText(""+(viewPager.getAdapter().getCount()));
                }else{
                    currentTextView.setText(""+(Integer.parseInt(currentTextView.getText().toString())+1));
                }
            }
        });


        ImageView cancelDialogImageView = (ImageView)findViewById(R.id.galleryCancel);
        cancelDialogImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView deleteImageView = (ImageView) findViewById(R.id.deleteGalleryImage);
        deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImage(currentPos);
            }
        });
    }


    @Override
    protected void onResume() {
       // MixPanelController.track(EventKeysWL.FULL_SCREEN_IMAGE,null);
        super.onResume();
        Methods.isOnline(FullScreen_Gallery_Image.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_full_screen__gallery__image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private int getItem(int i) {
        int position = viewPager.getCurrentItem() + i;
        return position;
    }

    public void deleteImage(int deletePosition){
        DeleteGalleryImages task = new DeleteGalleryImages(this,adapter,deletePosition);
       // task.setOnDeleteListener(this);
        task.execute();

//        UploadPictureAsyncTask upload = new UploadPictureAsyncTask(getActivity(),imageUrl);
//        upload.execute();
    }
}
