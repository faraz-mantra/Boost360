package com.nowfloats.CustomPage;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.boost.marketplace.ui.home.MarketPlaceActivity;
import com.framework.analytics.SentryController;
import com.framework.firebaseUtils.caplimit_feature.CapLimitFeatureResponseItem;
import com.framework.firebaseUtils.caplimit_feature.PropertiesItem;
import com.framework.firebaseUtils.firestore.FirestoreManager;
import com.google.firebase.FirebaseApp;
import com.google.gson.GsonBuilder;
import com.nowfloats.CustomPage.Model.CreatePageModel;
import com.nowfloats.CustomPage.Model.CustomPageLink;
import com.nowfloats.CustomPage.Model.CustomPageModel;
import com.nowfloats.CustomPage.Model.PageDetail;
import com.nowfloats.CustomPage.Model.UploadImageToS3Model;
import com.nowfloats.CustomPage.Model.UploadImageToS3ResponseModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.model.RiaNodeDataModel;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.RiaEventLogger;
import com.nowfloats.util.Utils;
import com.nowfloats.util.WebEngageController;
import com.thinksity.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import jp.wasabeef.richeditor.RichEditor;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;

import static com.framework.firebaseUtils.caplimit_feature.CapLimitFeatureResponseItemKt.filterFeature;
import static com.framework.firebaseUtils.caplimit_feature.CapLimitFeatureResponseItemKt.getCapData;
import static com.framework.utils.UtilKt.hideKeyBoard;
import static com.framework.webengageconstant.EventLabelKt.FAILED_TO_UPDATE_CUSTOMPAGE;
import static com.framework.webengageconstant.EventLabelKt.SOMETHING_WENT_WRONG_PLEASE_TRY_AGAIN;
import static com.framework.webengageconstant.EventLabelKt.SUCCESSFULLY_ADDED_CUSTOMPAGE;
import static com.framework.webengageconstant.EventLabelKt.UPDATE_A_CUSTOMPAGE;
import static com.framework.webengageconstant.EventNameKt.POST_ACUSTOMPAGE;
import static com.framework.webengageconstant.EventNameKt.UPDATE_CUSTOMPAGE;
import static com.nowfloats.util.Key_Preferences.GET_FP_DETAILS_CATEGORY;


/**
 * Created by guru on 09-06-2015.
 */
public class CreateCustomPageActivity extends AppCompatActivity {
  private final int gallery_req_id = 6;
  private final int media_req_id = 5;
  public Toolbar toolbar;
  public ImageView save;
  public UserSessionManager session;
  Activity activity;
  EditText titleTxt;
  RichEditor richText;
  String curName, curHtml, curPageid;
  String imageTagName = "CustomePage";
  boolean isNewDataAdded = false;
  private Uri picUri;
  private HorizontalScrollView editor;
  private boolean editCheck = false;
  private int curPos;
  private ImageView deletePage;
  private final int GALLERY_PHOTO = 5;
  private RiaNodeDataModel mRiaNodedata;
  public ArrayList<CustomPageModel> dataModel = new ArrayList();

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
  }

  private String getProcessName(Context context) {
    if (context == null) return null;
    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
      if (processInfo.pid == android.os.Process.myPid()) {
        return processInfo.processName;
      }
    }
    return null;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    FirebaseApp.initializeApp(CreateCustomPageActivity.this);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && !Constants.webViewInit) {
      String process = getProcessName(this);
      String packageName = this.getPackageName();
      if (!packageName.equals(process)) {
        WebView.setDataDirectorySuffix(process);
        Constants.webViewInit = true;
      }
    }
    setContentView(R.layout.create_custom_page);

    curPos = getIntent().getIntExtra("position", -1);
    String jsonData = getIntent().getStringExtra("data");
    if (jsonData != null) {
      dataModel = new ArrayList(Arrays.asList(new GsonBuilder().create().fromJson(jsonData, CustomPageModel[].class)));
    }

    mRiaNodedata = getIntent().getParcelableExtra(Constants.RIA_NODE_DATA);

    toolbar = findViewById(R.id.tool_bar_product_detail);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    activity = CreateCustomPageActivity.this;

    save = toolbar.findViewById(R.id.home_view_delete_card);
    deletePage = toolbar.findViewById(R.id.delete_page);
    deletePage.setVisibility(View.GONE);
    final TextView title = toolbar.findViewById(R.id.titleProduct);
    title.setVisibility(View.VISIBLE);
    title.setText("New Page");
    save.setImageResource(R.drawable.checkmark_icon);
    session = new UserSessionManager(getApplicationContext(), activity);
    capLimitCheck();

    editor = findViewById(R.id.rich_editer);
    titleTxt = findViewById(R.id.titleEdit);
    richText = findViewById(R.id.subtextEdit);
    richText.setPlaceholder(getString(R.string.custom_page_details));
    richText.setFontSize(13);

    if (getIntent().hasExtra("pageid")) {
      final MaterialDialog materialProgress = new MaterialDialog.Builder(this)
          .widgetColorRes(R.color.accentColor)
          .content(getString(R.string.loading))
          .progress(true, 0)
          .show();
      materialProgress.setCancelable(false);


      try {
        CustomPageInterface pageInterface = Constants.restAdapter.create(CustomPageInterface.class);
        pageInterface.getPageDetail(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG),
            getIntent().getStringExtra("pageid"), Constants.clientId, new Callback<List<PageDetail>>() {
              @Override
              public void success(List<PageDetail> pageDetail, Response response) {
                materialProgress.dismiss();
                //Intent intent = new Intent(CreateCustomPageActivity, CreateCustomPageActivity.class);
                if (pageDetail.size() > 0) {
                  curName = pageDetail.get(0).DisplayName;
                  curHtml = pageDetail.get(0).HtmlCode;
                  curPageid = pageDetail.get(0)._id;
                  titleTxt.setText(curName);
                  title.setText(curName);
                  richText.setHtml(curHtml);
                  editCheck = true;
                  deletePage.setVisibility(View.VISIBLE);
                } else {
                  Methods.showSnackBarNegative(CreateCustomPageActivity.this, getString(R.string.page_details_not_found));
                }
              }

              @Override
              public void failure(RetrofitError error) {
                materialProgress.dismiss();
                Log.d("page detail error-", "" + error.getMessage());
                Methods.showSnackBarNegative(CreateCustomPageActivity.this, getString(R.string.page_details_not_found));
              }
            });
      } catch (Exception e) {
        SentryController.INSTANCE.captureException(e);
        e.printStackTrace();
        Methods.showSnackBarNegative(this, getString(R.string.something_went_wrong_try_again));
        materialProgress.dismiss();
      }
    }


    titleTxt.setOnTouchListener((v, event) -> {
      titleTxt.post(() -> editor.setVisibility(View.GONE));
      return false;
    });
    richText.setOnTouchListener((v, event) -> {
      richText.post(() -> {
        editor.setVisibility(View.VISIBLE);
        richText.focusEditor();
        Utils.showKeyboard(activity, richText);
      });
      return false;
    });


    deletePage.setOnClickListener(v -> {
      String url = Constants.NOW_FLOATS_API_URL + "/Discover/v1/floatingpoint/custompage/delete";
      new SinglePageDeleteAsyncTask(url, CreateCustomPageActivity.this, session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG),
          dataModel, curPos).execute();
    });

    save.setOnClickListener(v -> {
      if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("-1")) {
        Methods.showFeatureNotAvailDialog(CreateCustomPageActivity.this);
      } else {
        if (mRiaNodedata != null) {
          RiaEventLogger.getInstance().logPostEvent(session.getFpTag(),
              mRiaNodedata.getNodeId(), mRiaNodedata.getButtonId(),
              mRiaNodedata.getButtonLabel(),
              RiaEventLogger.EventStatus.COMPLETED.getValue());
          mRiaNodedata = null;
        }
        boolean flag = true;
        final String name = titleTxt.getText().toString(), html = richText.getHtml();
        if (!(titleTxt.getText().toString().trim().length() > 0)) {
          flag = false;
          Methods.showSnackBarNegative(activity, getString(R.string.enter_the_title));
        } else if (!(html.trim().length() > 0)) {
          flag = false;
          Methods.showSnackBarNegative(activity, getString(R.string.enter_the_description));
        }
        CustomPageInterface anInterface = Constants.restAdapter.create(CustomPageInterface.class);
        if (flag) {
          final MaterialDialog materialProgress = new MaterialDialog.Builder(activity)
              .widgetColorRes(R.color.accentColor)
              .content(getString(R.string.loading))
              .progress(true, 0)
              .show();
          materialProgress.setCancelable(false);
          try {
            if (!editCheck) {
              CreatePageModel pageModel = new CreatePageModel(name, html,
                  session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG), Constants.clientId);
              anInterface.createPage(pageModel, new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                  Log.d("CUSTOM_PAGE_CHECK", "" + s);
                  materialProgress.dismiss();
                  if (response.getStatus() == 200 || response.getStatus() == 201 || response.getStatus() == 202) {
                    MixPanelController.track("CreateCustomPage", null);
                    long time = System.currentTimeMillis();
                    dataModel.add(new CustomPageModel("Date(" + time + ")", name, s));
                    WebEngageController.trackEvent(POST_ACUSTOMPAGE, SUCCESSFULLY_ADDED_CUSTOMPAGE, session.getFpTag());
                    Methods.showSnackBarPositive(activity, getString(R.string.page_successfully_created));
                    isNewDataAdded = true;
                    onCustomPageAddedOrUpdated();
                    onBackPressed();
                  } else {
                    Methods.showSnackBarNegative(activity, getString(R.string.something_went_wrong_try_again));
                    WebEngageController.trackEvent(POST_ACUSTOMPAGE, SOMETHING_WENT_WRONG_PLEASE_TRY_AGAIN, session.getFpTag());
                  }
                }

                @Override
                public void failure(RetrofitError error) {
                  materialProgress.dismiss();
                  Methods.showSnackBarNegative(activity, getString(R.string.something_went_wrong_try_again));
                  WebEngageController.trackEvent(POST_ACUSTOMPAGE, SOMETHING_WENT_WRONG_PLEASE_TRY_AGAIN, session.getFpTag());
                }
              });
            } else {
              HashMap<String, String> map = new HashMap<String, String>();
              map.put("DisplayName", name);
              map.put("HtmlCode", html);
              map.put("PageId", "" + curPageid);
              map.put("Tag", "" + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));
              map.put("clientId", Constants.clientId);
              anInterface.updatePage(map, new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                  materialProgress.dismiss();
                  MixPanelController.track("UpdateCustomPage", null);
                  WebEngageController.trackEvent(UPDATE_CUSTOMPAGE, UPDATE_A_CUSTOMPAGE, session.getFpTag());
                  //Log.d("Update page success", "");
                  dataModel.get(curPos).DisplayName = name;
                  Methods.showSnackBarPositive(activity, getString(R.string.page_updated));
                  onBackPressed();
                }

                @Override
                public void failure(RetrofitError error) {
                  materialProgress.dismiss();
                  Methods.showSnackBarNegative(activity, getString(R.string.something_went_wrong_try_again));
                  WebEngageController.trackEvent(UPDATE_CUSTOMPAGE, FAILED_TO_UPDATE_CUSTOMPAGE, session.getFpTag());
                  //Log.d("Update page Fail", "" + error.getMessage());
                }
              });
            }
          } catch (Exception e) {
            SentryController.INSTANCE.captureException(e);
            e.printStackTrace();
            Methods.showSnackBarNegative(activity, getString(R.string.something_went_wrong_try_again));
            materialProgress.dismiss();
          }
        }
      }
    });

    LinearLayout subtxt_layout = findViewById(R.id.subtxt_layout);
    subtxt_layout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        richText.performClick();
      }
    });

    findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        richText.undo();
      }
    });

    findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        richText.redo();
      }
    });

    findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        richText.setBold();
      }
    });

    findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        richText.setItalic();
      }
    });

    findViewById(R.id.action_subscript).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        richText.setSubscript();
      }
    });

    findViewById(R.id.action_superscript).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        richText.setSuperscript();
      }
    });

    findViewById(R.id.action_strikethrough).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        richText.setStrikeThrough();
      }
    });

    findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        richText.setUnderline();
      }
    });

    findViewById(R.id.action_heading1).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        richText.setHeading(1);
      }
    });

    findViewById(R.id.action_heading2).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        richText.setHeading(2);
      }
    });

    findViewById(R.id.action_heading3).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        richText.setHeading(3);
      }
    });

    findViewById(R.id.action_heading4).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        richText.setHeading(4);
      }
    });

    findViewById(R.id.action_heading5).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        richText.setHeading(5);
      }
    });

    findViewById(R.id.action_heading6).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        richText.setHeading(6);
      }
    });

    findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
      boolean isChanged;

      @Override
      public void onClick(View v) {
        richText.setTextColor(isChanged ? Color.BLACK : Color.RED);
        isChanged = !isChanged;
      }
    });

    findViewById(R.id.action_bg_color).setOnClickListener(new View.OnClickListener() {
      boolean isChanged;

      @Override
      public void onClick(View v) {
        richText.setTextBackgroundColor(isChanged ? Color.WHITE : Color.YELLOW);
        isChanged = !isChanged;
      }
    });

    findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        richText.setIndent();
      }
    });

    findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        richText.setOutdent();
      }
    });

    findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        richText.setAlignLeft();
      }
    });

    findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        richText.setAlignCenter();
      }
    });

    findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        richText.setAlignRight();
      }
    });

    findViewById(R.id.action_blockquote).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        richText.setBlockquote();
      }
    });
    findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // showUrlDialog(getString(R.string.enter_image_url), 1);
        choosePicture();
      }
    });

    findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showUrlDialog(getString(R.string.enter_hyperlink_url), 2);
      }
    });
  }

  private void capLimitCheck() {
    CustomPageInterface pageInterface2 = Constants.restAdapter.create(CustomPageInterface.class);
    pageInterface2.getPageUrl(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG), 0, 10, 1, new Callback<CustomPageLink>() {
      @Override
      public void success(CustomPageLink pageDetail, Response response) {
        CapLimitFeatureResponseItem data = filterFeature(getCapData(), CapLimitFeatureResponseItem.FeatureKey.CUSTOMPAGES);
        if (data != null && pageDetail != null) {
          PropertiesItem capLimitCustomPage = data.filterProperty(PropertiesItem.KeyType.LIMIT);
          Log.i("test_limit_cap", "Total: " + pageDetail.getTotal() + " capLimit: " + capLimitCustomPage.getValueN());
          if (pageDetail.getTotal() != null && capLimitCustomPage.getValueN() != null && pageDetail.getTotal() >= capLimitCustomPage.getValueN()) {
            hideKeyBoard(activity);
            showAlertCapLimit("Can't add the custom page, please activate your premium Add-ons plan.");
          }
        }
      }

      @Override
      public void failure(RetrofitError error) {
      }
    });
  }

  void showAlertCapLimit(String msg) {
    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlertDialogTheme));
    builder.setCancelable(false);
    builder.setTitle("You have exceeded limit!").setMessage(msg);
    builder.setPositiveButton("Explore Add-ons", (dialog, which) -> {
      dialog.dismiss();
      initiateBuyFromMarketplace();
      activity.finish();
    });
    builder.setNegativeButton("Close", (dialog, which) -> {
      dialog.dismiss();
      activity.finish();
    });
    builder.create().show();
  }

  private void onCustomPageAddedOrUpdated() {
    FirestoreManager instance = FirestoreManager.INSTANCE;
    if (instance.getDrScoreData() != null && instance.getDrScoreData().getMetricdetail() != null) {
      instance.getDrScoreData().getMetricdetail().setBoolean_create_custom_page(true);
      instance.updateDocument();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_card_product_detail, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      onBackPressed();
      overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed() {
    Intent data = new Intent();
    data.putExtra("IS_REFRESH", isNewDataAdded);
    setResult(RESULT_OK, data);
    super.onBackPressed();
    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
  }

  private void showUrlDialog(String msg, final int url_id) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    LayoutInflater inflater = getLayoutInflater();
    View v = inflater.inflate(R.layout.dialog_url, null);
    final EditText et_url = v.findViewById(R.id.et_url);
    final EditText et_tag = v.findViewById(R.id.et_tag);
    if (url_id == 2) {
      et_tag.setVisibility(View.VISIBLE);
    }
    et_url.setHint(msg);
    builder.setView(v).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        String url = et_url.getText().toString().trim();
        if (url_id == 1 && !url.isEmpty()) {
          richText.insertImage(url, getString(R.string.no_image));
        } else if (url_id == 2 && !url.isEmpty()) {
          String tag = et_tag.getText().toString().trim();
          if (!tag.isEmpty()) {
            richText.insertLink(url, tag);
          }
        }

      }
    })
        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        });
    Dialog d = builder.create();
    d.show();

  }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        try {
//             if (resultCode == RESULT_OK && (GALLERY_PHOTO == requestCode)) {
//                {
//                    Uri picUri = data.getData();
//                    if (picUri != null) {
//                        String path = getPath(picUri);
//                        //path = Util.saveBitmap(path, this, "ImageFloat" + System.currentTimeMillis());
//
//                        if (!Util.isNullOrEmpty(path)) {
//                            richText.insertImage(path, "dachshund");
//                        } else
//                            Toast.makeText(getApplicationContext(), "Please select an image to upload", Toast.LENGTH_SHORT).show();
//
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    public String getPath(Uri uri) {
//        try {
//            String[] projection = {MediaStore.Images.Media.DATA};
//            Cursor cursor = managedQuery(uri, projection, null, null, null);
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//            return cursor.getString(column_index);
//        } catch (Exception e) {
//        }
//        return null;
//    }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);


    if (resultCode == RESULT_OK && (Constants.GALLERY_PHOTO == requestCode)) {

      Bitmap CameraBitmap = null;
      String path = null;
      if (data != null) {
        picUri = data.getData();
        if (picUri == null) {
          CameraBitmap = (Bitmap) data.getExtras().get("data");
          path = Util.saveBitmap(CameraBitmap, activity, imageTagName + System.currentTimeMillis());
          picUri = Uri.parse(path);
        } else {
          path = Methods.getRealPathFromURI(picUri, this);
          CameraBitmap = Util.getBitmap(path, activity);

        }
      }
      if (CameraBitmap != null) {
        uploadImageToS3(new File(path).getName(), Methods.convertBitmapToString(Methods.scaleBitmap(CameraBitmap, .5f)));
      }

    } else if (resultCode == RESULT_OK && (Constants.CAMERA_PHOTO == requestCode)) {

      Bitmap CameraBitmap = null;
      String path = null;

      try {
        if (picUri == null) {
          if (data != null) {
            picUri = data.getData();
            if (picUri == null) {
              CameraBitmap = (Bitmap) data.getExtras().get("data");
              path = Util.saveCameraBitmap(CameraBitmap, activity, imageTagName + System.currentTimeMillis());
              picUri = Uri.parse(path);

            } else {
              path = Methods.getRealPathFromURI(picUri, this);
              CameraBitmap = Util.getBitmap(path, activity);

            }
          } else {
            Methods.showSnackBar(activity, getString(R.string.try_again));
          }
        } else {
          path = Methods.getRealPathFromURI(picUri, this);
          CameraBitmap = Util.getBitmap(path, activity);

        }

        if (CameraBitmap != null) {
          uploadImageToS3(new File(path).getName(), Methods.convertBitmapToString(Methods.scaleBitmap(CameraBitmap, .5f)));
        }

      } catch (Exception e) {
        SentryController.INSTANCE.captureException(e);
        e.printStackTrace();
      } catch (OutOfMemoryError E) {
        E.printStackTrace();
        CameraBitmap.recycle();
        System.gc();
        Methods.showSnackBar(activity, getString(R.string.try_again));
      }
    }
  }

  public void choosePicture() {
    final MaterialDialog dialog = new MaterialDialog.Builder(activity)
        .customView(R.layout.featuredimage_popup, true)
        .show();
    final PorterDuffColorFilter whiteLabelFilter_pop_ip = new PorterDuffColorFilter(getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_IN);

    View view = dialog.getCustomView();
    TextView header = view.findViewById(R.id.textview_heading);
    header.setText(R.string.upload_image);
    LinearLayout takeCamera = view.findViewById(R.id.cameraimage);
    LinearLayout takeGallery = view.findViewById(R.id.galleryimage);
    ImageView cameraImg = view.findViewById(R.id.pop_up_camera_imag);
    ImageView galleryImg = view.findViewById(R.id.pop_up_gallery_img);
    cameraImg.setColorFilter(whiteLabelFilter_pop_ip);
    galleryImg.setColorFilter(whiteLabelFilter_pop_ip);

    takeCamera.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        cameraIntent();
        dialog.dismiss();
      }
    });

    takeGallery.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        galleryIntent();
        dialog.dismiss();

      }
    });
  }

  public void cameraIntent() {
    try {
      // use standard intent to capture an image
      if (ActivityCompat.checkSelfPermission(CreateCustomPageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
          PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(CreateCustomPageActivity.this, Manifest.permission.CAMERA) !=
          PackageManager.PERMISSION_GRANTED) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
            ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
          Methods.showApplicationPermissions(getString(R.string.camera_and_storage_permission), getString(R.string.we_need_these_permissions_to_enable), CreateCustomPageActivity.this);
        } else {
          ActivityCompat.requestPermissions(CreateCustomPageActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, media_req_id);
        }

      } else {

        ContentValues Cvalues = new ContentValues();
        Intent captureIntent;
        Cvalues.put(MediaStore.Images.Media.TITLE, "New Picture");
        Cvalues.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        picUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Cvalues);
        captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
        startActivityForResult(captureIntent, Constants.CAMERA_PHOTO);
      }
    } catch (ActivityNotFoundException anfe) {
      SentryController.INSTANCE.captureException(anfe);
      // display an error message
      String errorMessage = getString(R.string.device_does_not_support_capturing_image);
      Methods.showSnackBarNegative(activity, errorMessage);
    }
  }

  public void galleryIntent() {
    try {
      if (ActivityCompat.checkSelfPermission(CreateCustomPageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
          PackageManager.PERMISSION_GRANTED) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
          Methods.showApplicationPermissions(getString(R.string.storage_permission), getString(R.string.we_need_this_permission_to_enable_image_upload), CreateCustomPageActivity.this);
        } else {
          ActivityCompat.requestPermissions(CreateCustomPageActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, gallery_req_id);
        }

      } else {

        Intent i = new Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, Constants.GALLERY_PHOTO);
      }
    } catch (ActivityNotFoundException anfe) {
      SentryController.INSTANCE.captureException(anfe);
      // display an error message
      String errorMessage = getString(R.string.device_does_not_support_capturing_image);
      Methods.showSnackBarNegative(activity, errorMessage);
    }
  }


  private void uploadImageToS3(String fileName, String fileContent) {

    final MaterialDialog materialProgress = new MaterialDialog.Builder(this)
        .widgetColorRes(R.color.accentColor)
        .content(getString(R.string.loading))
        .progress(true, 0)
        .show();

    final UploadImageToS3Model uploadImageToS3Model = new UploadImageToS3Model();
    uploadImageToS3Model.setFileName(fileName);
    uploadImageToS3Model.setFileData(fileContent);
    uploadImageToS3Model.setFileCategory(1);

    RestAdapter restAdapter = new RestAdapter.Builder()
        .setEndpoint(Constants.UPLOAD_TO_S3_ENDPOINT)
        //.setClient(Methods.getHttpclient(60))
        .setLog(new AndroidLog(CreateCustomPageActivity.class.getName()))
        .setLogLevel(RestAdapter.LogLevel.FULL)
        .build();

    Callback<UploadImageToS3ResponseModel> callback = new Callback<UploadImageToS3ResponseModel>() {
      @Override
      public void success(UploadImageToS3ResponseModel uploadImageToS3ResponseModel, Response response) {

        materialProgress.dismiss();

        if (response.getStatus() == 200 && uploadImageToS3ResponseModel != null) {
          if (uploadImageToS3ResponseModel.getBody() != null) {
            richText.insertImage(uploadImageToS3ResponseModel.getBody().getResult(), getString(R.string.no_image));
          } else {
            Methods.showSnackBarNegative(CreateCustomPageActivity.this, getString(R.string.upload_image_err));
          }
        } else {
          Methods.showSnackBarNegative(CreateCustomPageActivity.this, getString(R.string.upload_image_err));
        }
      }

      @Override
      public void failure(RetrofitError error) {
        materialProgress.dismiss();
        Methods.showSnackBarNegative(CreateCustomPageActivity.this, getString(R.string.upload_image_err));
      }
    };

    restAdapter.create(CustomPageInterface.class).uploadImageToS3(uploadImageToS3Model, callback);
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (mRiaNodedata != null) {
      RiaEventLogger.getInstance().logPostEvent(session.getFpTag(),
          mRiaNodedata.getNodeId(), mRiaNodedata.getButtonId(),
          mRiaNodedata.getButtonLabel(),
          RiaEventLogger.EventStatus.DROPPED.getValue());
      mRiaNodedata = null;
    }
  }

  private void initiateBuyFromMarketplace() {
    ProgressDialog progressDialog = new ProgressDialog(this);
    String status = "Loading. Please wait...";
    progressDialog.setMessage(status);
    progressDialog.setCancelable(false);
    progressDialog.show();
    Intent intent = new Intent(this, MarketPlaceActivity.class);
    intent.putExtra("expCode", session.getFP_AppExperienceCode());
    intent.putExtra("fpName", session.getFPName());
    intent.putExtra("fpid", session.getFPID());
    intent.putExtra("fpTag", session.getFpTag());
    intent.putExtra("accountType", session.getFPDetails(GET_FP_DETAILS_CATEGORY));
    intent.putStringArrayListExtra("userPurchsedWidgets", new ArrayList(session.getStoreWidgets()));
    if (session.getUserProfileEmail() != null) {
      intent.putExtra("email", session.getUserProfileEmail());
    } else {
      intent.putExtra("email", getString(R.string.ria_customer_mail));
    }
    if (session.getUserPrimaryMobile() != null) {
      intent.putExtra("mobileNo", session.getUserPrimaryMobile());
    } else {
      intent.putExtra("mobileNo", getString(R.string.ria_customer_number));
    }
    intent.putExtra("profileUrl", session.getFPLogo());
    intent.putExtra("buyItemKey", CapLimitFeatureResponseItem.FeatureKey.CUSTOMPAGES.name());
    startActivity(intent);
    new Handler().postDelayed(() -> progressDialog.dismiss(), 1000);
  }
}