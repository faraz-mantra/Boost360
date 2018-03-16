package com.nowfloats.NavigationDrawer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.edmodo.cropper.CropImageView;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

public class EditImageActivity extends AppCompatActivity {
    // Static final constants
    private static final int DEFAULT_ASPECT_RATIO_VALUES = 10;
    private static final int ROTATE_NINETY_DEGREES = 90;
    private static final String ASPECT_RATIO_X = "ASPECT_RATIO_X";
    private static final String ASPECT_RATIO_Y = "ASPECT_RATIO_Y";

    // Instance variables
    private int mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES;
    private int mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES;
    Bitmap croppedImage;

    // Saves the state upon rotating the screen/restarting the activity
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(ASPECT_RATIO_X, mAspectRatioX);
        bundle.putInt(ASPECT_RATIO_Y, mAspectRatioY);
    }

    // Restores the state upon rotating the screen/restarting the activity
    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        mAspectRatioX = bundle.getInt(ASPECT_RATIO_X);
        mAspectRatioY = bundle.getInt(ASPECT_RATIO_Y);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.edit_image);

        MixPanelController.track("EditPhoto", null);
        // Initialize components of the app
        final CropImageView cropImageView = (CropImageView) findViewById(R.id.CropImageView);

        if(getIntent().hasExtra("image")){
            try {
                cropImageView.setImageBitmap(Util.getBitmap( getIntent().getStringExtra("image"),EditImageActivity.this));
                cropImageView.setFixedAspectRatio(getIntent().getBooleanExtra("isFixedAspectRatio",false));
            }catch(OutOfMemoryError error){error.printStackTrace(); System.gc();
            }catch(Exception e){e.printStackTrace();}
        }

        //Sets the rotate button
        final Button rotateButton = (Button) findViewById(R.id.Button_rotate);
        rotateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cropImageView.rotateImage(ROTATE_NINETY_DEGREES);
            }
        });

        // Sets initial aspect ratio to 10/10, for demonstration purposes
        cropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES, DEFAULT_ASPECT_RATIO_VALUES);

        final Button cropButton = (Button) findViewById(R.id.Button_crop);
        cropButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try{
                    if (cropImageView.getCropOverlayViewVisible()){
                        croppedImage = cropImageView.getCroppedImage();
                        if(croppedImage!=null) {
                            cropImageView.setImageBitmap(croppedImage);
                            cropImageView.setCropOverlayViewVisible(false);
                        }
                    }else{
                        cropImageView.setCropOverlayViewVisible(true);
                    }
                }catch(Exception e){System.gc();e.printStackTrace();}
            }
        });

        Button save = (Button)findViewById(R.id.Button_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    croppedImage = cropImageView.getCroppedImage();
                    if(croppedImage!=null) {
                        Intent in = new Intent();
                        String path = Util.saveCameraBitmap(croppedImage, EditImageActivity.this, "Edit" + System.currentTimeMillis());
                        //Log.v("ggg","edit path "+path);
                        in.putExtra("edit_image", path);
                        setResult(RESULT_OK, in);
                        finish();
                    }
                }catch(Exception e){System.gc();e.printStackTrace();}
            }
        });
    }
}