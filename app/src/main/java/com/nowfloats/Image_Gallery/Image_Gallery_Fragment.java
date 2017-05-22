package com.nowfloats.Image_Gallery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;
import com.nowfloats.Login.GetGalleryImagesAsyncTask_Interface;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.NavigationDrawer.RoundCorners_image;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class Image_Gallery_Fragment extends Fragment implements
        UploadPictureAsyncTask.UploadPictureInterface,
        DeleteGalleryImages.DeleteGalleryInterface,
        GetGalleryImagesAsyncTask_Interface.getGalleryImagesInterface{
    public static GridView grid ;
    OtherImagesAdapter gridViewAdapter;
    Bitmap CameraBitmap;
    Uri imageUri;
    String imageUrl = "";
    public static String path = "";
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLERY = 2;
    Dialog imageDialog = null ;
    ImageAdapter adapter;
    ViewPager viewPager;
    FloatingActionButton fabGallery_Button ;
    UserSessionManager session;
    Activity activity;
    private LinearLayout progressLayout,emptyGalleryLayout;
    private final int media_req_id=5;
    private final int gallery_req_id = 6;

    @Override
    public void onResume() {
        super.onResume();
        HomeActivity.plusAddButton.setVisibility(View.GONE);
        HomeActivity.headerText.setText("Photo Gallery");
        if (grid!=null)
            grid.invalidate();
        if (gridViewAdapter!=null)
            gridViewAdapter.notifyDataSetChanged();
        //getActivity().getActionBar().
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        session = new UserSessionManager(activity.getApplicationContext(),activity);
        GetGalleryImagesAsyncTask_Interface gallery = new GetGalleryImagesAsyncTask_Interface(activity,session);
        gallery.setGalleryInterfaceListener(this);
        gallery.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        BoostLog.d("Image_Gallery_Fragment", "onCreateView");
        return inflater.inflate(R.layout.fragment_image__gallery, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BoostLog.d("Image_Gallery_Fragment","onViewCreated");
        grid = (GridView) view.findViewById(R.id.grid);
        fabGallery_Button = (FloatingActionButton) view.findViewById(R.id.fab_gallery);
        emptyGalleryLayout = (LinearLayout)view.findViewById(R.id.emptygallerylayout);
        progressLayout = (LinearLayout)view.findViewById(R.id.progress_gallerylayout);
        progressLayout.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gridViewAdapter = new OtherImagesAdapter(activity);
                        if(gridViewAdapter.getCount()==0){
                            emptyGalleryLayout.setVisibility(View.VISIBLE);
                        }else {
                            emptyGalleryLayout.setVisibility(View.GONE);
                        }
                        grid.setAdapter(gridViewAdapter);

                        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent fullImage = new Intent(activity, FullScreen_Gallery_Image.class);
                                fullImage.putExtra("currentPositon", position);
                                startActivity(fullImage);
                                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        });

                        HomeActivity.plusAddButton.setVisibility(View.GONE);

                        fabGallery_Button.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                switch (event.getAction()) {
                                    case MotionEvent.ACTION_DOWN: {
                                        selectImage();
                                        break;
                                    }
                                    case MotionEvent.ACTION_UP:{
                                        break;
                                    }
                                }
                                return true;
                            }
                        });
                        progressLayout.setVisibility(View.GONE);
                    }
                });
            }
        }).start();

    }

    private void showFullScaleImage(String imageFile, final int currentPos) {
        BoostLog.d("full image","image file : "+imageFile);
        int selectedPOS = currentPos ;

        // File imgFile = new  File(imageFile);
        Bitmap myBitmap = null;
        String baseName = Constants.NOW_FLOATS_API_URL+""+imageFile;



        //  https://api.withfloats.com//FP/Tile/53ffff644ec0a40740921460.jpg

        imageDialog = new Dialog(activity);
        imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        imageDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        imageDialog.getWindow().setLayout(500,500);
        WindowManager.LayoutParams lp = imageDialog.getWindow().getAttributes();
        lp.dimAmount = 15.7f;
        imageDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        //  layout.setMinimumWidth((int)(displayRectangle.width() * 0.9f));
        //  layout.setMinimumHeight((int)(displayRectangle.height() * 0.9f));

        imageDialog.setContentView(R.layout.gallery_view_pager);

        ImageView previousImageView = (ImageView) imageDialog.findViewById(R.id.previousImage);
        ImageView nextImageView = (ImageView) imageDialog.findViewById(R.id.nextImage);
        final TextView currentTextView = (TextView) imageDialog.findViewById(R.id.currentCountValue);
        final TextView maxCountTextView = (TextView) imageDialog.findViewById(R.id.maxCount);
        // ImageView fullImageView = (ImageView) imageDialog.findViewById(R.id.fullImageView);
        // BoostLog.d("Base Name","imageLoader base name : "+baseName);
        // imageLoader.displayImage(baseName, fullImageView);
        //fullImageView.setImageBitmap(myBitmap);

        viewPager = (ViewPager) imageDialog.findViewById(R.id.galleryImageViewpager);
        adapter = new ImageAdapter(activity);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(selectedPOS);
        final int maxNumberofImages = adapter.getCount();
        currentTextView.setText(Integer.toString(selectedPOS+1));
        maxCountTextView.setText(Integer.toString(maxNumberofImages));
        //currentTextView.setId(R.id.custom_view_pager);
        // viewPager.setId(R.id.custom_view_pager);

        previousImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // BoostLog.d("Image_Gallery_Fragment","Current POS : "+selectedPOS);
                int selectedPosition = getItem(-1);
                viewPager.setCurrentItem(selectedPosition, true);
                currentTextView.setText(Integer.toString(selectedPosition));

            }
        });

        nextImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BoostLog.d("Image_Gallery_Fragment","Current POS : "+ currentPos);
                int selectedPosition = getItem(+1);
                viewPager.setCurrentItem(selectedPosition, true);
                currentTextView.setText(Integer.toString(selectedPosition));
            }
        });


        ImageView cancelDialogImageView = (ImageView) imageDialog.findViewById(R.id.galleryCancel);
        cancelDialogImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageDialog.cancel();
            }
        });

        ImageView deleteImageView = (ImageView) imageDialog.findViewById

                (R.id.deleteGalleryImage);
        deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixPanelController.track(EventKeysWL.IMAGE_GALLERY_DELETE_IMAGE,null);
                deleteImage(currentPos);
            }
        });

        imageDialog.show();
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    public void deleteImage(int deletePosition){
        DeleteGalleryImages task = new DeleteGalleryImages(activity,adapter,deletePosition);
        task.setOnDeleteListener(this);
        task.execute();

//        UploadPictureAsyncTask upload = new UploadPictureAsyncTask(activity,imageUrl);
//        upload.execute();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        if(requestCode==media_req_id)
        {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                cameraIntent();

            }

        }
        else if(requestCode==gallery_req_id)
        {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                galleryIntent();

            }

        }
    }
    public void cameraIntent() {
        try {
            // use standard intent to capture an image
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                    PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)!=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        media_req_id);
            }
            else {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");

                imageUri = activity.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(captureIntent, PICK_FROM_CAMERA);
            }
        } catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = getString(R.string.device_does_not_support_capturing_image);
            // Util.toast(errorMessage, FloatAnImage.this);

        }
    }

    private void selectImage() {
        final MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .customView(R.layout.featuredimage_popup, true)
                .show();
        final PorterDuffColorFilter whiteLabelFilter_pop_ip = new PorterDuffColorFilter(getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_IN);
        MixPanelController.track("AddImage",null);
        View view = dialog.getCustomView();
        TextView header = (TextView) view.findViewById(R.id.textview_heading);
        header.setText(getString(R.string.add_photo));
        LinearLayout takeCamera = (LinearLayout) view.findViewById(R.id.cameraimage);
        LinearLayout takeGallery = (LinearLayout) view.findViewById(R.id.galleryimage);
        ImageView   cameraImg = (ImageView) view.findViewById(R.id.pop_up_camera_imag);
        final ImageView galleryImg = (ImageView) view.findViewById(R.id.pop_up_gallery_img);
        cameraImg.setColorFilter(whiteLabelFilter_pop_ip);
        galleryImg.setColorFilter(whiteLabelFilter_pop_ip);

        takeCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixPanelController.track(EventKeysWL.IMAGE_GALLERY_IMAGE_CAMERA,null);
                cameraIntent();
                dialog.hide();
            }
        });

        takeGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixPanelController.track(EventKeysWL.IMAGE_GALLERY_IMAGE_GALLERY,null);
                galleryIntent();
                dialog.hide();
            }
        });
    }

    private void galleryIntent()
    {
        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                    PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)!=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        gallery_req_id);
            }
            else {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //intent.setType("image/*");
                startActivityForResult(
                        Intent.createChooser(intent, getString(R.string.select_file)),
                        PICK_FROM_GALLERY);
            }
        } catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = getString(R.string.device_does_not_support_capturing_image);
            Toast toast = Toast.makeText(getActivity(),errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Toast.makeText(Image_Gallery_MainActivity.this,"Camera : "+requestCode+" Data : "+data.getData(),Toast.LENGTH_SHORT).show();

        if (requestCode == PICK_FROM_CAMERA) {
            try {
                CameraBitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), imageUri);
                imageUrl = getRealPathFromURI(imageUri);
                path = imageUrl;

                UploadPictureAsyncTask upload = new UploadPictureAsyncTask(activity,imageUrl);
                upload.setOnUploadListener(Image_Gallery_Fragment.this);
                upload.execute();

                path = Util.saveCameraBitmap(path, activity, 720,
                        session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG) + System.currentTimeMillis());
            } catch (Exception e) {
                e.printStackTrace();
                //Util.toast(""+e.toString(), this);
            }catch(OutOfMemoryError E){
                //BoostLog.d("ANDRO_ASYNC",String.format("catch Out Of Memory error"));
                E.printStackTrace();
                System.gc();
                //Util.toast("Uh oh. Something went wrong. Please try again", this);
            }

            if (!Util.isNullOrEmpty(path)) {
                try {
                    CameraBitmap = Util.getBitmap(path, activity);
                    if (CameraBitmap != null) {
                        // bmp = Bitmap.createScaledBitmap(bmp, 300, 300,true);
                        //RoundCorners_image roundCorner = new RoundCorners_image();
                        CameraBitmap = RoundCorners_image.getRoundedCornerBitmap(CameraBitmap, 30);

                        String eol = System.getProperty("line.separator");
                        // updateHint.setText("CHANGE" + eol + "PHOTO");
                    }
                } catch(Exception e){e.printStackTrace();
                } catch(OutOfMemoryError E){
                    E.printStackTrace();
                    System.gc();
                }
            }
        }

        if (requestCode == PICK_FROM_GALLERY) {
            try {
                Uri extras2 = data.getData();
                // if (extras2 != null) {

                String filepath = getGalleryImagePath(data);

                UploadPictureAsyncTask upload = new UploadPictureAsyncTask(activity, filepath);
                upload.setOnUploadListener(Image_Gallery_Fragment.this);
                upload.execute();
                // sent_check if the specified image exists.
            }catch(Exception e){e.printStackTrace();
            } catch(OutOfMemoryError E){
                E.printStackTrace();
                System.gc();
            }

        }


    }

    public String getRealPathFromURI(Uri contentUri) {
        try{
            String[] proj = { MediaStore.Images.Media.DATA };
            Cursor cursor = activity.managedQuery(contentUri, proj, null, null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }catch (Exception e) {
        }
        return null;
    }

    public String getGalleryImagePath(Intent data) {
        Uri imgUri = data.getData();
        String filePath = "";
        if (data.getType() == null) {
            // For getting images from default gallery app.
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = activity.getContentResolver().query(imgUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        } else if (data.getType().equals("image/jpeg") || data.getType().equals("image/png")) {
            // For getting images from dropbox or any other gallery apps.
            filePath = imgUri.getPath();
        }
        return filePath;
    }


    @Override
    public void uploadedPictureListener(String imageURL) {
        BoostLog.d("Image", "uploadPictureListener imageURL : "+imageURL);
        MixPanelController.track("AddImageSuccess",null);
        // Toast.makeText(activity, "imageURL : " + imageURL, Toast.LENGTH_SHORT).show();
        if(progressLayout!=null){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressLayout.setVisibility(View.VISIBLE);
            }
        });
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                GetGalleryImagesAsyncTask_Interface gallery = new GetGalleryImagesAsyncTask_Interface(activity,session);
                gallery.setGalleryInterfaceListener(Image_Gallery_Fragment.this);
                gallery.execute();
            }
        }, 6000);

//        GetGalleryImagesAsyncTask_Interface gallery = new GetGalleryImagesAsyncTask_Interface(activity);
//        gallery.setGalleryInterfaceListener(Image_Gallery_Fragment.this);
//        gallery.execute();

        // grid.invalidate();
        //gridViewAdapter.notifyDataSetChanged();
//        Bitmap img = BitmapFactory.decodeFile(imageURL);
//        testImageView.setImageBitmap(img);

    }

    @Override
    public void galleryImageDeleted() {
        grid.invalidate();
        gridViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void imagesReceived() {
        // BoostLog.d("Image_Gallery_Fragment","imagesREceived : "+Constants.storeSecondaryImages.size());
        // gridViewAdapter = new OtherImagesAdapter(activity);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(progressLayout!=null)
                {
                    progressLayout.setVisibility(View.GONE);
                }
            }
        });
        if(grid != null) {
            if(gridViewAdapter != null)
                gridViewAdapter.notifyDataSetChanged();
            if(emptyGalleryLayout!=null)
                emptyGalleryLayout.setVisibility(View.GONE);
            grid.invalidateViews();
            // grid.setAdapter(gridViewAdapter);
            //  grid.invalidate();

        }
        // BoostLog.d("Login_MainActivity","Constants.storeSecondaryImages : "+ Constants.storeSecondaryImages);
        //session.storeGalleryImages(Constants.storeSecondaryImages.toString());
    }
}
