package com.nowfloats.Image_Gallery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.framework.models.firestore.FirestoreManager;
import com.google.gson.Gson;
import com.nowfloats.Login.GetGalleryImagesAsyncTask_Interface;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.RoundCorners_image;
import com.nowfloats.NavigationDrawer.floating_view.ImagePickerBottomSheetDialog;
import com.nowfloats.signup.UI.Model.Get_Feature_Details;
import com.nowfloats.signup.UI.Model.Get_Feature_DetailsItem;
import com.nowfloats.signup.UI.Model.Get_Properties_Details;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.WebEngageController;
import com.thinksity.R;

import java.util.ArrayList;

import static com.framework.webengageconstant.EventLabelKt.MANAGE_CONTENT;
import static com.framework.webengageconstant.EventLabelKt.UPDATE_GALLERY_IMAGES;
import static com.framework.webengageconstant.EventNameKt.GALLERY_IMAGE_ADDED;
import static com.framework.webengageconstant.EventNameKt.UPLOAD_GALLERY_IMAGE;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class Image_Gallery_Fragment extends Fragment implements
        UploadPictureAsyncTask.UploadPictureInterface,
        DeleteGalleryImages.DeleteGalleryInterface,
        GetGalleryImagesAsyncTask_Interface.getGalleryImagesInterface {

    public GridView gvImages;

    private OtherImagesAdapter otherImagesAdapter;

    private Bitmap CameraBitmap;

    private Uri imageUri;

    private String imageUrl = "";

    public static String path = "";

    private static final int PICK_FROM_CAMERA = 1;

    private static final int PICK_FROM_GALLERY = 2;

    private UserSessionManager session;

    private Activity activity;

    private LinearLayout progressLayout, emptyGalleryLayout;

    private final int media_req_id = 5;

    private final int gallery_req_id = 6;

    ArrayList<String> purchasedWidgetList = new ArrayList<String>();
    String featureList = null;
    int limitValue = 0;
    int imageCount = 0;
    ImageGalleryListener imageGalleryListener = null;

    public Image_Gallery_Fragment getInstance(ImageGalleryListener mListener) {
        Log.d("imageGalleryListener", " " + mListener);
        imageGalleryListener = mListener;
        return new Image_Gallery_Fragment();
    }


    @Override
    public void onResume() {
        super.onResume();

        if (gvImages != null)
            gvImages.invalidate();
        if (otherImagesAdapter != null)
            otherImagesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image__gallery, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeControls(view);
    }

    private void initializeControls(View view) {

        activity = getActivity();
        session = new UserSessionManager(getContext(), activity);
        gvImages = view.findViewById(R.id.grid);
        emptyGalleryLayout = view.findViewById(R.id.layout_empty);
        progressLayout = view.findViewById(R.id.layout_progress);
        progressLayout.setVisibility(View.VISIBLE);
        otherImagesAdapter = new OtherImagesAdapter(activity);
        if (otherImagesAdapter.getCount() == 0) {
            emptyGalleryLayout.setVisibility(View.VISIBLE);
        } else {
            emptyGalleryLayout.setVisibility(View.GONE);
        }
        Bundle bundle = activity.getIntent().getExtras();
        if (bundle != null) {
            purchasedWidgetList = bundle.getStringArrayList("userPurchsedWidgets");
            featureList = bundle.getString("featureWidgets");
        }
        Get_Feature_Details valueFeature = new Gson().fromJson(featureList, Get_Feature_Details.class);
        for (Get_Feature_DetailsItem aList : valueFeature) {
            Log.v("Get_Feature_DetailsItem", " "+ aList.getFeatureKey());
            /*if (aList.getFeatureKey().equals("IMAGEGALLERY")) {
                for (Get_Properties_Details pList : aList.getProperties()) {
                    if (pList.getKey().equals("LIMIT")) {
                        limitValue = Integer.parseInt(pList.getValue());
                    }
                }
            } else*/ if (aList.getFeatureKey().equals("UNLIMITED_CONTENT")) {
Log.v("valueFeature", " "+ "UNLIMITED_CONTENT");
                for (Get_Properties_Details pList : aList.getProperties()) {
                    /*if (pList.getKey().equals("LATESTUPDATES")) {
                        limitValue = Integer.parseInt(pList.getValue());
                    }else if (pList.getKey().equals("PRODUCT_CATALOUGE")){
                        limitValue = Integer.parseInt(pList.getValue());
                    }
                    else if (pList.getKey().equals("CUSTOM_PAGE")){
                        limitValue = Integer.parseInt(pList.getValue());
                    }
                    else */if (pList.getKey().equals("IMAGE")){
                        Log.v("valueFeature", " "+ "IMAGE");
                        limitValue = Integer.parseInt(pList.getValue());
                    }


                }
                imageGalleryListener.imageLimit(limitValue);
            }
        }
        gvImages.setAdapter(otherImagesAdapter);

        GetGalleryImagesAsyncTask_Interface gallery = new GetGalleryImagesAsyncTask_Interface(activity, session);
        gallery.setGalleryInterfaceListener(this);
        gallery.execute();

    }

    @Override
    public void imagesReceived() {
        activity.runOnUiThread(() -> {
            if (progressLayout != null) progressLayout.setVisibility(View.GONE);
        });
        if (gvImages != null) {
            gvImages.invalidateViews();
            if (otherImagesAdapter != null) otherImagesAdapter.notifyDataSetChanged();
            if (emptyGalleryLayout != null && otherImagesAdapter.getCount() != 0) {
                emptyGalleryLayout.setVisibility(View.GONE);
                imageCount = otherImagesAdapter.getCount();
                Log.d("imageGalleryListener", " 3 :  " + imageGalleryListener);
                imageGalleryListener.imageCount(imageCount);
                Log.d("imagesReceived", " 3 :  " + imageCount);
            }
        }
        ArrayList<String> serverImage = Constants.storeSecondaryImages;
        onImageGalleryAddedOrUpdated(serverImage != null && !serverImage.isEmpty());
    }

    private void onImageGalleryAddedOrUpdated(Boolean isAdded) {
        FirestoreManager instance = FirestoreManager.INSTANCE;
        if (instance.getDrScoreData().getMetricdetail() == null) return;
        instance.getDrScoreData().getMetricdetail().setBoolean_image_uploaded_to_gallery(isAdded);
        instance.updateDocument();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == media_req_id) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                cameraIntent();

            }

        } else if (requestCode == gallery_req_id) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                galleryIntent();

            }

        }
    }

    public void cameraIntent() {
        try {
            // use standard intent to capture an image
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, media_req_id);
            } else {
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
//        if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("-1")) {
//            Methods.showFeatureNotAvailDialog(getContext());
//            return;
//        }

        final ImagePickerBottomSheetDialog imagePickerBottomSheetDialog = new ImagePickerBottomSheetDialog(this::onClickImagePicker);
        imagePickerBottomSheetDialog.show(getParentFragmentManager(), ImagePickerBottomSheetDialog.class.getName());
        WebEngageController.trackEvent(UPLOAD_GALLERY_IMAGE, UPDATE_GALLERY_IMAGES, session.getFpTag());

//        final MaterialDialog dialog = new MaterialDialog.Builder(activity)
//                .customView(R.layout.featuredimage_popup, true)
//                .show();
//        final PorterDuffColorFilter whiteLabelFilter_pop_ip = new PorterDuffColorFilter(ContextCompat.getColor(getContext(), R.color.primaryColor), PorterDuff.Mode.SRC_IN);
//        View view = dialog.getCustomView();
//        if (view != null) {
//            TextView header = (TextView) view.findViewById(R.id.textview_heading);
//            header.setText(getString(R.string.add_photo));
//            LinearLayout takeCamera = (LinearLayout) view.findViewById(R.id.cameraimage);
//            LinearLayout takeGallery = (LinearLayout) view.findViewById(R.id.galleryimage);
//            ImageView cameraImg = (ImageView) view.findViewById(R.id.pop_up_camera_imag);
//            final ImageView galleryImg = (ImageView) view.findViewById(R.id.pop_up_gallery_img);
//            cameraImg.setColorFilter(whiteLabelFilter_pop_ip);
//            galleryImg.setColorFilter(whiteLabelFilter_pop_ip);
//
//            takeCamera.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    MixPanelController.track(EventKeysWL.IMAGE_GALLERY_IMAGE_CAMERA, null);
//                    cameraIntent();
//                    dialog.hide();
//                }
//            });
//
//            takeGallery.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    MixPanelController.track(EventKeysWL.IMAGE_GALLERY_IMAGE_GALLERY, null);
//                    galleryIntent();
//                    dialog.hide();
//                }
//            });
//        }
    }

    private void onClickImagePicker(ImagePickerBottomSheetDialog.IMAGE_CLICK_TYPE image_click_type) {
        if (image_click_type.name().equals(ImagePickerBottomSheetDialog.IMAGE_CLICK_TYPE.CAMERA.name())) {
            MixPanelController.track(EventKeysWL.IMAGE_GALLERY_IMAGE_CAMERA, null);
            cameraIntent();
        } else if (image_click_type.name().equals(ImagePickerBottomSheetDialog.IMAGE_CLICK_TYPE.GALLERY.name())) {
            MixPanelController.track(EventKeysWL.IMAGE_GALLERY_IMAGE_GALLERY, null);
            galleryIntent();
        }
    }

    private void galleryIntent() {
        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        gallery_req_id);
            } else {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(intent, getString(R.string.select_file)),
                        PICK_FROM_GALLERY);
            }
        } catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = getString(R.string.device_does_not_support_capturing_image);
            Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            imageGalleryListener = (ImageGalleryListener) activity;
        } catch (Exception e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ImageGalleryListener");
        }












































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
                WebEngageController.trackEvent(GALLERY_IMAGE_ADDED, MANAGE_CONTENT, session.getFpTag());
                UploadPictureAsyncTask upload = new UploadPictureAsyncTask(activity, imageUrl);
                upload.setOnUploadListener(Image_Gallery_Fragment.this);
                upload.execute();

                path = Util.saveCameraBitmap(path, activity, 720,
                        session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG) + System.currentTimeMillis());
            } catch (Exception e) {
                e.printStackTrace();
                //Util.toast(""+e.toString(), this);
            } catch (OutOfMemoryError E) {
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
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (OutOfMemoryError E) {
                    E.printStackTrace();
                    System.gc();
                }
            }
        }

        if (requestCode == PICK_FROM_GALLERY) {
            try {
                Uri extras2 = data.getData();
                // if (extras2 != null) {
                WebEngageController.trackEvent(GALLERY_IMAGE_ADDED, MANAGE_CONTENT, session.getFpTag());
                String filepath = "";
                UploadPictureAsyncTask upload = null;
                if (data.getData() != null) {
                    filepath = getGalleryImagePath(data);
                    upload = new UploadPictureAsyncTask(activity, filepath);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && data.getClipData() != null) {
                    upload = new UploadPictureAsyncTask(activity, getGalleryImagesMultiple(data));
                }
                upload.setOnUploadListener(Image_Gallery_Fragment.this);
                upload.execute();
                // CHEQUE if the specified image exists.
            } catch (Exception e) {
                e.printStackTrace();
            } catch (OutOfMemoryError E) {
                E.printStackTrace();
                System.gc();
            }

        }


    }

    public String getRealPathFromURI(Uri contentUri) {
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = activity.managedQuery(contentUri, proj, null, null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getGalleryImagePath(Intent data) {
        Uri imgUri = data.getData();
        String filePath = "";
        if (data.getType() == null) {
            // For getting images from default gallery app.
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = activity.getContentResolver().query(imgUri, filePathColumn, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                filePath = cursor.getString(columnIndex);
                cursor.close();
            }

            if (TextUtils.isEmpty(filePath))
                filePath = getPath(getActivity(), imgUri);
        } else if (data.getType().equals("image/jpeg") || data.getType().equals("image/png")) {
            // For getting images from dropbox or any other gallery apps.
            filePath = imgUri.getPath();
        }
        return filePath;
    }

    private ArrayList<String> getGalleryImagesMultiple(Intent data) {
        ArrayList<String> arrPaths = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (data.getClipData() != null) {
                arrPaths = new ArrayList<>();
                ClipData mClipData = data.getClipData();
                ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                for (int i = 0; i < mClipData.getItemCount(); i++) {

                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    mArrayUri.add(uri);

                    String filePath = "";
                    Cursor cursor = activity.getContentResolver().query(uri, filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        filePath = cursor.getString(columnIndex);
                        cursor.close();
                    }

                    if (TextUtils.isEmpty(filePath)) {
                        filePath = getPath(getActivity(), uri);
                    }

                    if (!TextUtils.isEmpty(filePath))
                        arrPaths.add(filePath);
                }
            }
        }
        return arrPaths;
    }


    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {

        //final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    @Override
    public void uploadedPictureListener(String imageURL) {
        BoostLog.d("Image", "uploadPictureListener imageURL : " + imageURL);
        MixPanelController.track("AddImageSuccess", null);
        /*if (progressLayout != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressLayout.setVisibility(View.VISIBLE);
                }
            });
        }*/
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (progressLayout != null) progressLayout.setVisibility(View.VISIBLE);
            GetGalleryImagesAsyncTask_Interface gallery = new GetGalleryImagesAsyncTask_Interface(activity, session);
            gallery.setGalleryInterfaceListener(Image_Gallery_Fragment.this);
            gallery.execute();
        }, 500);

    }

    @Override
    public void galleryImageDeleted() {
        gvImages.invalidate();
    }

    public void addImage() {
        selectImage();
    }

    public int getImageCount() {
        Log.d("imagesReceived", " 4 : " + imageCount);
        return imageCount;
    }
}
