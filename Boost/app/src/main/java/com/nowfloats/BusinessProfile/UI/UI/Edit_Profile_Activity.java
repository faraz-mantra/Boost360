package com.nowfloats.BusinessProfile.UI.UI;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.BusinessProfile.UI.API.SetBusinessCategoryAsyncTask;
import com.nowfloats.BusinessProfile.UI.API.UploadProfileAsyncTask;
import com.nowfloats.BusinessProfile.UI.API.uploadIMAGEURI;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NotificationCenter.AlertArchive;
import com.nowfloats.signup.UI.API.API_Layer;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class Edit_Profile_Activity extends AppCompatActivity {
    public static EditText yourname, category, buzzname, buzzdescription;
    public static ImageView featuredImage, businessCategoryImage;
    private Toolbar toolbar;

    Boolean flag4name = false, flag4category = false, flag4buzzname = false,flag4buzzdescriptn = false,allBoundaryCondtn = true ;
    public static String msgtxt4_name , msgtxt4buzzname ,msgtxt4buzzdescriptn ,msgtxtcategory;
    String[] profilesattr =new String[20];
    private String[] businessCategoryList;
    public static ImageView saveTextView;
    ContentValues values;
    Uri imageUri ;
    private static final int GALLERY_PHOTO = 2;
    private static final int CAMERA_PHOTO = 1;
    Bitmap CameraBitmap;
    String path = null;
    String imageUrl ="";
    public static ImageView editProfileImageView,select_pic;
    UserSessionManager session ;
//    TextView yourName_textlineTextView,businessName_textlineTextView,businessDesciption_textlineTextView ;


    private final int media_req_id = 5;
    private final int gallery_req_id = 6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__profile_);
        Methods.isOnline(Edit_Profile_Activity.this);

        final PorterDuffColorFilter whiteLabelFilter_pop_ip = new PorterDuffColorFilter(getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_IN);
        final PorterDuffColorFilter whitecolorFilter = new PorterDuffColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);

        session = new UserSessionManager(getApplicationContext(),Edit_Profile_Activity.this);
        editProfileImageView = (ImageView) findViewById(R.id.editbusinessprofileimage);
        select_pic = (ImageView)findViewById(R.id.select_businessprofileimage);
        yourname = (EditText)findViewById(R.id.profileName);
        buzzname = (EditText)findViewById(R.id.businessName);
        category = (EditText)findViewById(R.id.businessCategory);
        buzzdescription = (EditText) findViewById(R.id.businessDesciption);

//        yourName_textlineTextView = (TextView) findViewById(R.id.yourName_textline);
//        businessName_textlineTextView = (TextView) findViewById(R.id.businessName_textline);
//        businessDesciption_textlineTextView = (TextView) findViewById(R.id.businessDesciption_textline);

        //category_text = (EditText) findViewById(R.id.buss_address_select_buzz_category);

        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCats();
            }
        });
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        saveTextView = (ImageView) toolbar.findViewById(R.id.saveTextView);
        saveTextView.setColorFilter(whitecolorFilter);
        TextView titleTextView = (TextView) toolbar.findViewById(R.id.titleTextView);
        titleTextView.setText("Basic Information");


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProfile();
                //UploadProfileAsyncTask upload = new UploadProfileAsyncTask(Edit_Profile_Activity.this,)
            }
        });




        select_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final MaterialDialog dialog = new MaterialDialog.Builder(Edit_Profile_Activity.this)
                        .customView(R.layout.featuredimage_popup,true)
                        .show();

                View view = dialog.getCustomView();

                LinearLayout takeCamera = (LinearLayout) view.findViewById(R.id.cameraimage);
                LinearLayout takeGallery = (LinearLayout) view.findViewById(R.id.galleryimage);
                ImageView   cameraImg = (ImageView) view.findViewById(R.id.pop_up_camera_imag);
                ImageView galleryImg = (ImageView) view.findViewById(R.id.pop_up_gallery_img);
                cameraImg.setColorFilter(whiteLabelFilter_pop_ip);
                galleryImg.setColorFilter(whiteLabelFilter_pop_ip);

                takeCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        cameraIntent();
                        dialog.hide();
                    }
                });

                takeGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        galleryIntent();
                        dialog.hide();

                    }
                });

            }
        });


        yourname.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                flag4name = true;
                try {
                    //MixPanelController.track(EventKeys.business_profile_name,null);
                    msgtxt4_name = yourname.getText().toString().trim();
                    int len = s.length();//msgtxt4_name.length();
                    if (len > 0) {
//                        yourName_textlineTextView.setVisibility(View.VISIBLE);
                        saveTextView.setVisibility(View.VISIBLE);
//                        findViewById(R.id.buzz_profile_save_txt).setVisibility(View.VISIBLE);
                    } else {
//                        yourName_textlineTextView.setVisibility(View.GONE);
                        saveTextView.setVisibility(View.GONE);
//                        findViewById(R.id.buzz_profile_save_txt).setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        category.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                flag4category = true;
                try {
                    msgtxtcategory = category.getText().toString().trim();
                    int len = s.length();//msgtxtcategory.length();
                    if (len > 0) {
                        // businessName_textlineTextView.setVisibility(View.VISIBLE);
                        saveTextView.setVisibility(View.VISIBLE);
//                        findViewById(R.id.buzz_profile_save_txt).setVisibility(View.VISIBLE);
                    } else {
                        //  businessName_textlineTextView.setVisibility(View.GONE);
                        saveTextView.setVisibility(View.GONE);
//                        findViewById(R.id.buzz_profile_save_txt).setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        buzzname.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String msg_txt = "";
                flag4buzzname = true;
                try {

                    msgtxt4buzzname = buzzname.getText().toString()
                            .trim();
                    int len = s.length();//msgtxt4buzzname.length();
                    if (len > 0) {
//                        businessName_textlineTextView.setVisibility(View.VISIBLE);
                        saveTextView.setVisibility(View.VISIBLE);
//                        findViewById(R.id.buzz_profile_save_txt).setVisibility(View.VISIBLE);
                    } else {
//                        businessName_textlineTextView.setVisibility(View.GONE);
                        saveTextView.setVisibility(View.GONE);
//                        findViewById(R.id.buzz_profile_save_txt).setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        buzzdescription.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String msg_txt = "";
                flag4buzzdescriptn = true;
                try {

                    msgtxt4buzzdescriptn = buzzdescription.getText()
                            .toString().trim();
                    int len = s.length();//msgtxt4buzzdescriptn.length();
                    if (len > 0) {
//                        businessDesciption_textlineTextView.setVisibility(View.VISIBLE);
                        saveTextView.setVisibility(View.VISIBLE);
//                        findViewById(R.id.buzz_profile_save_txt).setVisibility(View.VISIBLE);
                    } else {
//                        businessDesciption_textlineTextView.setVisibility(View.GONE);
                        saveTextView.setVisibility(View.GONE);
//                        findViewById(R.id.buzz_profile_save_txt).setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });



        Initdata();
        //selectCats();
    }

    @Override
    public void onResume(){
        super.onResume();
        Initdata();
    }

    private void businessCategoryDialog() {

//        if (cat != null) {
//            selectCats();
//        } else {
//            if (Util.isNetworkStatusAvialable(this)) {
//                loadCats();
//            } else {
//                Toast.makeText(this,
//                        "Please check your internet connection and try again",
//                        Toast.LENGTH_SHORT).show();
//            }
//        }
    }

    public void selectCats() {

//        final Dialog dialog = new Dialog(this);
//
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.custom_dialog_with_search);
//
//        l = (ListView) dialog.findViewById(R.id.search_dialog_listview);
//        ArrayList<String> stringList = new ArrayList<String>(Arrays.asList(cat));
//        adapter = new CustomFilterableAdapter(stringList, this);
//        l.setAdapter(adapter);
//        title = (HeaderText) dialog
//                .findViewById(R.id.message_top_bar_store_txt);
//        title.setText("Select a Category");
//        l.setTextFilterEnabled(true);
//
//        EditText editTxt = (EditText) dialog.findViewById(R.id.searchString);
//        editTxt.setVisibility(View.GONE);
//
//        dialog.show();

//        l.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//                                    long arg3) {
//                String strName = adapter.getItem(arg2);
//                category.setText(strName);
//                int index = (int) ((Math.random() * 10000000) % 23f);
//                MainFragment main = new MainFragment();
//                main.setIndex(index);
//                dialog.dismiss();
//
////				SetBusinessCategoryAsyncTask buc = new SetBusinessCategoryAsyncTask(BusinessProfileFragment.this, strName);
////				buc.execute();
//            }
//        });

        /*if(businessCategoryList == null)
        {
            businessCategoryList = API_Layer.getBusinessCategories(Edit_Profile_Activity.this);
        }
        else {

            // PreSignUpDialog.showDialog(PreSignUpActivity.this, businessCategoryList, "Select a Category");
            new MaterialDialog.Builder(Edit_Profile_Activity.this)
                    .title("Select a Category")
                    .items(businessCategoryList)
                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            category.setText(text);
                            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY,category.getText().toString());
                            //Util.changeDefaultBackgroundImage(text.toString());
                            return false;
                        }
                    })
                    .show();
        }*/
        new FetchCategory().execute();

    }


    public void uploadProfile() {
        int i=0;
        JSONObject offerObj = new JSONObject();
        JSONArray ja = new JSONArray();
        JSONObject  obj1 = new JSONObject();
        JSONObject obj2 = new JSONObject();
        JSONObject obj3 = new JSONObject();

        if (flag4name) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(yourname.getWindowToken(), 0);
            try {
                obj1.put("key", "CONTACTNAME");
                obj1.put("value", msgtxt4_name);
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_CONTACTNAME,msgtxt4_name);
            } catch (Exception ex) {
                System.out.println();
            }
            ja.put(obj1);
            profilesattr[i] = "CONTACTNAME";
            i++;
        }

        if (flag4buzzname) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(buzzname.getWindowToken(), 0);

            if(msgtxt4buzzname.length()<=3)
            {

                //Util.toast("Business Name has to be more than 3 characters", this);
                Methods.showSnackBarNegative(Edit_Profile_Activity.this,"Business Name should be more than 3 letters");
                allBoundaryCondtn=false;
            }
            try {
                obj2.put("key", "NAME");
                obj2.put("value", msgtxt4buzzname);
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME,msgtxt4buzzname);
            } catch (Exception ex) {
                System.out.println();
            }
            ja.put(obj2);
            profilesattr[i] = "NAME";
            i++;
        }

        if (flag4buzzdescriptn) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(buzzdescription.getWindowToken(), 0);
            try {

                obj3.put("key", "DESCRIPTION");
                obj3.put("value", msgtxt4buzzdescriptn);
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION,msgtxt4buzzdescriptn);
            } catch (Exception ex) {
                System.out.println();
            }
            ja.put(obj3);
            profilesattr[i] = "DESCRIPTION";
            i++;
        }

        try {
            Constants con = new Constants(this);
            offerObj.put("clientId", Constants.clientId);
            offerObj.put("fpTag", session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toUpperCase());
            offerObj.put("updates", ja);
        } catch (Exception ex) {

        }

        if(allBoundaryCondtn && flag4category)
        {
            SetBusinessCategoryAsyncTask buzcat = new SetBusinessCategoryAsyncTask(Edit_Profile_Activity.this, msgtxtcategory,flag4category,session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));
            buzcat.execute();
        }

        if(allBoundaryCondtn && !flag4category ){
            UploadProfileAsyncTask upa = new UploadProfileAsyncTask(this,offerObj,profilesattr);
            upa.execute();
        }
        else
        {
            allBoundaryCondtn = true;
        }
        //update alert archive
        new AlertArchive(Constants.alertInterface,"PROFILE",session.getFPID());

//        MixPanelController.setProperties(EventKeysWL.SELF_NAME,msgtxt4buzzname);
//        MixPanelController.setProperties(EventKeysWL.SITE_SCORE_Businesss_CATEGORY,msgtxtcategory);

        // upa = new UploadPictureMessageAsyncTask(this, path, offerObj,
        // postUser, postPage);
        // upa.execute();

    }


    public void loadCats() {
//        RequestQueue queue = Volley.newRequestQueue(this);
//        String url = Constants.NOW_FLOATS_API_URL
//                + "/Discover/v1/floatingPoint/categories";
//        JsonArrayRequest jsObjRequest = new JsonArrayRequest(url,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        cat = new String[response.length()];
//                        for (int i = 0; i < response.length(); i++) {
//                            try {
//                                cat[i] = (String) response.get(i);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        // selectCats();
//                    }
//                }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                System.out.print(error.toString());
//            }
//        });
//
//        queue.add(jsObjRequest);
    }


    private void Initdata(){

        buzzdescription.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION));
        buzzname.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
        yourname.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CONTACTNAME));
        category.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY));

       // String baseNameProfileImage = "https://api.withfloats.com/"+ Constants.storePrimaryImage;
        /*if(!Constants.IMAGEURIUPLOADED)
        {*/
            String iconUrl = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_IMAGE_URI);
            if(iconUrl.length()>0 && iconUrl.contains("BizImages") && !iconUrl.contains("http")) {
                String baseNameProfileImage = Constants.BASE_IMAGE_URL+"" + iconUrl;
                Picasso.with(Edit_Profile_Activity.this).load(baseNameProfileImage).placeholder(R.drawable.featured_photo_default).into(editProfileImageView);
            }else{
                if(iconUrl!=null && iconUrl.length()>0){
                    Picasso.with(Edit_Profile_Activity.this).load(iconUrl).placeholder(R.drawable.featured_photo_default).into(editProfileImageView);
                }else{
                    Picasso.with(Edit_Profile_Activity.this).load(R.drawable.featured_photo_default).into(editProfileImageView);
                }
            }
        //}

        if(session.getIsSignUpFromFacebook().contains("true"))
        {
            Picasso.with(Edit_Profile_Activity.this)
                    .load(session.getFacebookPageURL()).placeholder(R.drawable.featured_photo_default)
                            // optional
                   // .resize(150, 100)                        // optional
                    .rotate(90)                             // optional
                    .into(editProfileImageView);

            buzzdescription.setText(session.getFacebookProfileDescription());
        }

        saveTextView.setVisibility(View.GONE);
        flag4category = false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit__profile, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
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

    public void cameraIntent(){

        try {
            // use standard intent to capture an image
            if (ActivityCompat.checkSelfPermission(Edit_Profile_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                    PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(Edit_Profile_Activity.this, Manifest.permission.CAMERA)!=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Edit_Profile_Activity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        media_req_id);
            }
            else {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");

                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(captureIntent, CAMERA_PHOTO);
            }
        } catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support capturing images!";
            // Util.toast(errorMessage, FloatAnImage.this);

        }
    }

    public void galleryIntent(){
        try {
            if (ActivityCompat.checkSelfPermission(Edit_Profile_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                    PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(Edit_Profile_Activity.this, Manifest.permission.CAMERA)!=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Edit_Profile_Activity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        gallery_req_id);
            }
            else {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

                startActivityForResult(i, GALLERY_PHOTO);
            }
        } catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support capturing images!";
            Toast toast = Toast.makeText(getApplicationContext(),
                    errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode == RESULT_OK && (CAMERA_PHOTO == requestCode)) {

                try {
                    path = null;
                    if (imageUri!=null){
                        path = getRealPathFromURI(imageUri);
                        CameraBitmap = Util.getBitmap(path, Edit_Profile_Activity.this);
                        imageUrl = getRealPathFromURI(imageUri);
                        path = Util.saveBitmap(path, Edit_Profile_Activity.this, "ImageFloat" + System.currentTimeMillis());
                    }else{
                        if (data != null) {
                            imageUri = data.getData();
                            if (imageUri == null) {
                                CameraBitmap = (Bitmap) data.getExtras().get("data");
                                if(CameraBitmap!=null){
                                    path = Util.saveCameraBitmap(CameraBitmap,Edit_Profile_Activity.this,"ImageFloat" + System.currentTimeMillis());
                                    imageUri = Uri.parse(path);
                                }
                            }else{
                                path = getRealPathFromURI(imageUri);
                                CameraBitmap = Util.getBitmap(path, Edit_Profile_Activity.this);
                                imageUrl = getRealPathFromURI(imageUri);
                                path = Util.saveBitmap(path, Edit_Profile_Activity.this, "ImageFloat" + System.currentTimeMillis());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (OutOfMemoryError E) {
                    E.printStackTrace();
                    CameraBitmap.recycle();
                    System.gc();
                    Methods.showSnackBar(Edit_Profile_Activity.this, "Try again....");
                }

                if (!Util.isNullOrEmpty(path)) {
                    uploadPrimaryPicture(path);
                }  else Methods.showSnackBar(Edit_Profile_Activity.this, "Please select an image to upload");
            }
            else if (resultCode == RESULT_OK && (GALLERY_PHOTO == requestCode)) {
                {
                    Uri picUri = data.getData();
                    if (picUri != null) {
                        path = getPath(picUri);
                        path = Util.saveBitmap(path, Edit_Profile_Activity.this, "ImageFloat" + System.currentTimeMillis());
                        if (!Util.isNullOrEmpty(path)) {
                            uploadPrimaryPicture(path);
                        } else
                            Toast.makeText(getApplicationContext(), "Please select an image to upload",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public String getPath(Uri uri) {
        try {
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = managedQuery(uri, projection, null, null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);

        } catch (Exception e) {

        }
        return null;
    }
    public void uploadPrimaryPicture(String path) {
        //Picasso.with(Edit_Profile_Activity.this).load(path).placeholder(R.drawable.featured_photo_default).into(editProfileImageView);
        uploadIMAGEURI uploadAsyncTask = new uploadIMAGEURI(Edit_Profile_Activity.this,
                path,session.getFPID());
        uploadAsyncTask.execute();



//        UUID uuid;
//
//        uuid = UUID.randomUUID();
//        String s_uuid = uuid.toString();
//        s_uuid = s_uuid.replace("-", "");
//
//
//       // ImageUpload_Interface service = ServiceGenerator.createService(ImageUpload_Interface.class, ImageUpload_Interface.BASE_URL);
//        //TypedFile typedFile = new TypedFile("binary/octet-stream", new File(path));
//
//        File file = new File(path);
//        ImageUpload_Interface service = Constants.restAdapter.create(ImageUpload_Interface.class);
//
//        String mimeType = "image/png";
//        TypedFile fileToSend = new TypedFile(mimeType, file);
//       // ImageUpload_Interface service = restAdapter.create(FileWebService.class);
//        //fileWebService.upload(fileToSend);
//
//        service.put_uploadIMAGEURI(Constants.clientId, session.getFPID(), s_uuid, fileToSend, new Callback<String>() {
//            @Override
//            public void success(String s, Response response) {
//
//                Log.e("Upload", "success : "+s);
//
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//
//            }
//
//        });

//        UploadPictureAsyncTask upa = new UploadPictureAsyncTask(Edit_Profile_Activity.this, path, true,false,session.getFPID());
//        upa.execute();
    }
    private class FetchCategory extends AsyncTask<String, Void, String> {

        ProgressDialog pd = null;

        @Override
        protected void onPreExecute() {

            pd = ProgressDialog.show(Edit_Profile_Activity.this, "", "Wait While Loading Categories...");
            //return
        }

        @Override
        protected String doInBackground(String... params) {
            API_Layer.getBusinessCategories(Edit_Profile_Activity.this);
            return null;
        }



        @Override
        protected void onPostExecute(String result) {

            if(pd!=null && pd.isShowing()){
                pd.dismiss();
            }
            new MaterialDialog.Builder(Edit_Profile_Activity.this)
                    .title("Select a Category")
                    .items(Constants.storeBusinessCategories)
                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            category.setText(text);
                            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY,category.getText().toString());
                            //Util.changeDefaultBackgroundImage(text.toString());
                            return false;
                        }
                    })
                    .show();
            //return
        }
    }

}
