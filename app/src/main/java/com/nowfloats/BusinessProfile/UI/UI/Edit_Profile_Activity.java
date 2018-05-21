package com.nowfloats.BusinessProfile.UI.UI;

import android.Manifest;
import android.app.Dialog;
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
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.BusinessProfile.UI.API.SetBusinessCategoryAsyncTask;
import com.nowfloats.BusinessProfile.UI.API.UploadProfileAsyncTask;
import com.nowfloats.BusinessProfile.UI.API.uploadIMAGEURI;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.model.RiaNodeDataModel;
import com.nowfloats.NotificationCenter.AlertArchive;
import com.nowfloats.signup.UI.API.API_Layer;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.RiaEventLogger;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Edit_Profile_Activity extends AppCompatActivity {
    public static EditText yourname, category, buzzname, buzzdescription;
    public static ImageView featuredImage, businessCategoryImage;
    private Toolbar toolbar;
    private RadioGroup productCategory;

    Boolean flag4name = false, flag4category = false, flag4buzzname = false, flag4buzzdescriptn = false, allBoundaryCondtn = true;
    public static String msgtxt4_name, msgtxt4buzzname, msgtxt4buzzdescriptn, msgtxtcategory;
    String[] profilesattr = new String[20];
    private String[] businessCategoryList;
    public static ImageView saveTextView;
    ContentValues values;
    Uri imageUri;
    private static final int GALLERY_PHOTO = 2;
    private static final int CAMERA_PHOTO = 1;
    Bitmap CameraBitmap;
    String path = null;
    String imageUrl = "";
    public static ImageView editProfileImageView, select_pic;
    UserSessionManager session;
//    TextView yourName_textlineTextView,businessName_textlineTextView,businessDesciption_textlineTextView ;


    private final int media_req_id = 5;
    private final int gallery_req_id = 6;
    private RiaNodeDataModel mRiaNodeDataModel;
    private ArrayList<String> categories;
    private boolean isChangedProductCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__profile_);
        Methods.isOnline(Edit_Profile_Activity.this);

        final PorterDuffColorFilter whiteLabelFilter_pop_ip = new PorterDuffColorFilter(getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_IN);
        final PorterDuffColorFilter whitecolorFilter = new PorterDuffColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);

        session = new UserSessionManager(getApplicationContext(), Edit_Profile_Activity.this);
        editProfileImageView = (ImageView) findViewById(R.id.editbusinessprofileimage);
        select_pic = (ImageView) findViewById(R.id.select_businessprofileimage);

        yourname = (EditText) findViewById(R.id.profileName);
        buzzname = (EditText) findViewById(R.id.businessName);
        category = (EditText) findViewById(R.id.businessCategory);
        productCategory = (RadioGroup) findViewById(R.id.rbgroup);
        buzzdescription = (EditText) findViewById(R.id.businessDesciption);

        mRiaNodeDataModel = getIntent().getParcelableExtra(Constants.RIA_NODE_DATA);

        productCategory.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                isChangedProductCategory = true;
                saveTextView.setVisibility(View.VISIBLE);
            }
        });
//        yourName_textlineTextView = (TextView) findViewById(R.id.yourName_textline);
//        businessName_textlineTextView = (TextView) findViewById(R.id.businessName_textline);
//        businessDesciption_textlineTextView = (TextView) findViewById(R.id.businessDesciption_textline);

        //category_text = (EditText) findViewById(R.id.buss_address_select_buzz_category);

        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categories == null) {
                    final ProgressDialog pd = ProgressDialog.show(Edit_Profile_Activity.this, "", getResources().getString(R.string.wait_while_loading_category));
                    API_Layer api = Constants.restAdapter.create(API_Layer.class);

                    HashMap<String, String> map = new HashMap<>();
                    map.put("clientId", Constants.clientId);
                    api.getCategories(map, new Callback<ArrayList<String>>() {
                        @Override
                        public void success(ArrayList<String> strings, Response response) {
                            if (pd != null && pd.isShowing()) {
                                pd.dismiss();
                            }
                            categories = strings;
                            //showCategoryDialog(categories);
                            showCountryDialog(categories);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            if (pd != null && pd.isShowing()) {
                                pd.dismiss();
                            }

                            showCountryDialog(new ArrayList<String>(Arrays.asList(Constants.storeBusinessCategories)));
                            //showCategoryDialog(new ArrayList<String>(Arrays.asList(Constants.storeBusinessCategories)));
                            Toast.makeText(Edit_Profile_Activity.this, getString(R.string.something_went_wrong_try_again), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {

                    showCountryDialog(categories);
                    //showCategoryDialog(categories);
                }
            }
        });
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        saveTextView = (ImageView) toolbar.findViewById(R.id.saveTextView);
        saveTextView.setColorFilter(whitecolorFilter);
        TextView titleTextView = (TextView) toolbar.findViewById(R.id.titleTextView);
        titleTextView.setText(getResources().getString(R.string.basic_info));


        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Methods.isOnline(Edit_Profile_Activity.this)) {
                    uploadProfile();
                    if (mRiaNodeDataModel != null) {
                        RiaEventLogger.getInstance().logPostEvent(session.getFpTag(),
                                mRiaNodeDataModel.getNodeId(), mRiaNodeDataModel.getButtonId(),
                                mRiaNodeDataModel.getButtonLabel(), RiaEventLogger.EventStatus.COMPLETED.getValue());
                        mRiaNodeDataModel = null;
                    }
                } else {
                    Methods.snackbarNoInternet(Edit_Profile_Activity.this);
                }

                //UploadProfileAsyncTask upload = new UploadProfileAsyncTask(Edit_Profile_Activity.this,)
            }
        });


        select_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final MaterialDialog dialog = new MaterialDialog.Builder(Edit_Profile_Activity.this)
                        .customView(R.layout.featuredimage_popup, true)
                        .show();

                View view = dialog.getCustomView();

                LinearLayout takeCamera = (LinearLayout) view.findViewById(R.id.cameraimage);
                LinearLayout takeGallery = (LinearLayout) view.findViewById(R.id.galleryimage);
                ImageView cameraImg = (ImageView) view.findViewById(R.id.pop_up_camera_imag);
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


        initData();
        //selectCats();
    }

    private String getProductCategory() {
        int buttonId = productCategory.getCheckedRadioButtonId();
        switch (buttonId) {
            case R.id.rb_products:
                return "Products";
            case R.id.rb_services:
                return "Services";
            default:
                return "";
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mRiaNodeDataModel != null) {
            RiaEventLogger.getInstance().logPostEvent(session.getFpTag(),
                    mRiaNodeDataModel.getNodeId(), mRiaNodeDataModel.getButtonId(),
                    mRiaNodeDataModel.getButtonLabel(), RiaEventLogger.EventStatus.DROPPED.getValue());
            mRiaNodeDataModel = null;
        }
    }

    private void showCategoryDialog(ArrayList<String> categories) {
        new MaterialDialog.Builder(Edit_Profile_Activity.this)
                .title(getString(R.string.select_category))
                .items(categories)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        category.setText(text);
                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY, category.getText().toString());
                        //Util.changeDefaultBackgroundImage(text.toString());
                        return false;
                    }
                }).show();
    }

    private void showCountryDialog(ArrayList<String> countries) {

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(Edit_Profile_Activity.this,
                R.layout.search_list_item_layout, countries);

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(Edit_Profile_Activity.this);
        builderSingle.setTitle(R.string.select_category);

        View view = LayoutInflater.from(Edit_Profile_Activity.this).inflate(R.layout.search_list_layout, null);
        builderSingle.setView(view);

        EditText edtSearch = (EditText) view.findViewById(R.id.edtSearch);
        ListView lvItems = (ListView) view.findViewById(R.id.lvItems);

        lvItems.setAdapter(adapter);


        final Dialog dialog = builderSingle.show();

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String strVal = adapter.getItem(position);
                dialog.dismiss();
                category.setText(strVal);
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY, category.getText().toString());
                //countryEditText.setText(strVal);
                //updateCountry();
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.getFilter().filter(s.toString().toLowerCase());
            }
        });

        dialog.setCanceledOnTouchOutside(false);
    }

    public void uploadProfile() {
        int i = 0;
        JSONObject offerObj = new JSONObject();
        JSONArray ja = new JSONArray();
        JSONObject obj1 = new JSONObject();
        JSONObject obj2 = new JSONObject();
        JSONObject obj3 = new JSONObject();

        if (flag4name) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(yourname.getWindowToken(), 0);
            try {
                obj1.put("key", "CONTACTNAME");
                obj1.put("value", msgtxt4_name);
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_CONTACTNAME, msgtxt4_name);
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

            if (msgtxt4buzzname.length() <= 3) {

                //Util.toast("Business Name has to be more than 3 characters", this);
                Methods.showSnackBarNegative(Edit_Profile_Activity.this, getResources().getString(R.string.business_name_more_than_3char));
                allBoundaryCondtn = false;
            }
            try {
                obj2.put("key", "NAME");
                obj2.put("value", msgtxt4buzzname);
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME, msgtxt4buzzname);
            } catch (Exception ex) {
                System.out.println();
            }
            ja.put(obj2);
            profilesattr[i] = "NAME";
            i++;
        }
        if (isChangedProductCategory) {
            JSONObject productCategoryObj = new JSONObject();
            try {
                productCategoryObj.put("key", "PRODUCTCATEGORYVERB");
                productCategoryObj.put("value", getProductCategory());
                session.storeFPDetails(Key_Preferences.PRODUCT_CATEGORY, getProductCategory());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ja.put(productCategoryObj);
        }
        if (flag4buzzdescriptn) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(buzzdescription.getWindowToken(), 0);
            try {

                obj3.put("key", "DESCRIPTION");
                obj3.put("value", msgtxt4buzzdescriptn);
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION, msgtxt4buzzdescriptn);
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

        if (allBoundaryCondtn && flag4category) {
            SetBusinessCategoryAsyncTask buzcat = new SetBusinessCategoryAsyncTask(Edit_Profile_Activity.this, msgtxtcategory, flag4category, session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));
            buzcat.execute();
        }

        if (allBoundaryCondtn && !flag4category) {
            UploadProfileAsyncTask upa = new UploadProfileAsyncTask(this, offerObj, profilesattr);
            upa.execute();
        } else {
            allBoundaryCondtn = true;
        }
        //update alert archive
        new AlertArchive(Constants.alertInterface, "PROFILE", session.getFPID());
    }

    private void initData() {

        buzzdescription.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION));
        buzzname.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
        yourname.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CONTACTNAME));
        category.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY));
        if (session.getFPDetails(Key_Preferences.PRODUCT_CATEGORY).equals("Services")) {
            productCategory.check(R.id.rb_services);
        } else {
            productCategory.check(R.id.rb_products);
        }
        // String baseNameProfileImage = "https://api.withfloats.com/"+ Constants.storePrimaryImage;
        /*if(!Constants.IMAGEURIUPLOADED)
        {*/
        String iconUrl = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_IMAGE_URI);
        if (iconUrl.length() > 0 && iconUrl.contains("BizImages") && !iconUrl.contains("http")) {
            String baseNameProfileImage = Constants.BASE_IMAGE_URL + "" + iconUrl;
            Picasso.with(Edit_Profile_Activity.this).load(baseNameProfileImage).placeholder(R.drawable.featured_photo_default).into(editProfileImageView);
        } else {
            if (iconUrl != null && iconUrl.length() > 0) {
                Picasso.with(Edit_Profile_Activity.this).load(iconUrl).placeholder(R.drawable.featured_photo_default).into(editProfileImageView);
            } else {
                Picasso.with(Edit_Profile_Activity.this).load(R.drawable.featured_photo_default).into(editProfileImageView);
            }
        }
        //}

        if (session.getIsSignUpFromFacebook().contains("true") && !Util.isNullOrEmpty(session.getFacebookPageURL())) {
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
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
            if (ActivityCompat.checkSelfPermission(Edit_Profile_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(Edit_Profile_Activity.this, Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Edit_Profile_Activity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        media_req_id);
            } else {
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
            String errorMessage = getString(R.string.device_does_not_support_capturing_image);
            // Util.toast(errorMessage, FloatAnImage.this);
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    public void galleryIntent() {
        try {
            if (ActivityCompat.checkSelfPermission(Edit_Profile_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(Edit_Profile_Activity.this, Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Edit_Profile_Activity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        gallery_req_id);
            } else {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

                startActivityForResult(i, GALLERY_PHOTO);
            }
        } catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = getString(R.string.device_does_not_support_capturing_image);
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
                    if (imageUri != null) {
                        path = getRealPathFromURI(imageUri);
                        CameraBitmap = Util.getBitmap(path, Edit_Profile_Activity.this);
                        imageUrl = getRealPathFromURI(imageUri);
                        path = Util.saveBitmap(path, Edit_Profile_Activity.this, "ImageFloat" + System.currentTimeMillis());
                    } else {
                        if (data != null) {
                            imageUri = data.getData();
                            if (imageUri == null) {
                                CameraBitmap = (Bitmap) data.getExtras().get("data");
                                if (CameraBitmap != null) {
                                    path = Util.saveCameraBitmap(CameraBitmap, Edit_Profile_Activity.this, "ImageFloat" + System.currentTimeMillis());
                                    imageUri = Uri.parse(path);
                                }
                            } else {
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
                    Methods.showSnackBar(Edit_Profile_Activity.this, getResources().getString(R.string.try_again));
                }

                if (!Util.isNullOrEmpty(path)) {
                    uploadPrimaryPicture(path);
                } else
                    Methods.showSnackBar(Edit_Profile_Activity.this, getResources().getString(R.string.select_image_upload));
            } else if (resultCode == RESULT_OK && (GALLERY_PHOTO == requestCode)) {
                {
                    Uri picUri = data.getData();
                    if (picUri != null) {
                        path = getPath(picUri);
                        path = Util.saveBitmap(path, Edit_Profile_Activity.this, "ImageFloat" + System.currentTimeMillis());
                        if (!Util.isNullOrEmpty(path)) {
                            uploadPrimaryPicture(path);
                        } else
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.select_image_upload), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public String getPath(Uri uri) {
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
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
        uploadIMAGEURI uploadAsyncTask = new uploadIMAGEURI(Edit_Profile_Activity.this, path, session.getFPID());
        uploadAsyncTask.execute();
    }
}
