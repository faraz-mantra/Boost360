package com.nowfloats.CustomPage;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.CustomPage.Model.CreatePageModel;
import com.nowfloats.CustomPage.Model.CustomPageModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import java.util.HashMap;

import jp.wasabeef.richeditor.RichEditor;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


/**
 * Created by guru on 09-06-2015.
 */
public class CreateCustomPageActivity extends AppCompatActivity{
    public Toolbar toolbar;
    public ImageView save;
    public UserSessionManager session;
    Activity activity;
    EditText titleTxt;
    RichEditor richText;
    private String mHtmlFormat = "";
    private HorizontalScrollView editor;
    private boolean editCheck = false;
    String curName,curHtml,curPageid;
    int curPos;
    private ImageView deletePage;

    private int GALLERY_PHOTO =5;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_custom_page);

        toolbar = (Toolbar) findViewById(R.id.tool_bar_product_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity = CreateCustomPageActivity.this;

        save = (ImageView) toolbar.findViewById(R.id.home_view_delete_card);
        deletePage = (ImageView) toolbar.findViewById(R.id.delete_page);
        deletePage.setVisibility(View.GONE);
        TextView title = (TextView) toolbar.findViewById(R.id.titleProduct);
        title.setVisibility(View.VISIBLE);
        title.setText(getString(R.string.new_page));
        save.setImageResource(R.drawable.checkmark_icon);
        session = new UserSessionManager(getApplicationContext(),activity);

        editor = (HorizontalScrollView)findViewById(R.id.rich_editer);
        titleTxt = (EditText)findViewById(R.id.titleEdit);
        richText = (RichEditor)findViewById(R.id.subtextEdit);
        richText.setPlaceholder(getString(R.string.custom_page_details));

        if(getIntent().hasExtra("name")){
            curName = getIntent().getStringExtra("name");
            curHtml = getIntent().getStringExtra("html");
            curPageid = getIntent().getStringExtra("pageid");
            curPos = Integer.parseInt(getIntent().getStringExtra("pos"));
            titleTxt.setText(curName);
            title.setText(curName);
            richText.setHtml(curHtml);
            mHtmlFormat = curHtml;
            editCheck = true;
            deletePage.setVisibility(View.VISIBLE);
        }

        titleTxt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editor.setVisibility(View.GONE);
                return false;
            }
        });

        richText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.setVisibility(View.VISIBLE);
                richText.focusEditor();
                richText.requestFocus();
            }
        });

        richText.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                mHtmlFormat = text;
            }
        });

        deletePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = Constants.NOW_FLOATS_API_URL + "/Discover/v1/floatingpoint/custompage/delete";
                new SinglePageDeleteAsyncTask(url,CreateCustomPageActivity.this,session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG),
                        CustomPageActivity.dataModel.get(curPos).PageId,curPos).execute();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = true;
                final String name = titleTxt.getText().toString(),html = mHtmlFormat;
                if (!(titleTxt.getText().toString().trim().length()>0)){
                    flag = false;
                    Methods.showSnackBarNegative(activity,getString(R.string.enter_the_title));
                }else if(!(html.trim().length()>0)){
                    flag = false;
                    Methods.showSnackBarNegative(activity,getString(R.string.enter_the_description));
                }
                CustomPageInterface anInterface = Constants.restAdapter.create(CustomPageInterface.class);
                if (flag){
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
                                    materialProgress.dismiss();
                                    if (s!=null &&s.toString().trim().length()>0){
                                        //Log.d("Create page success", "");
                                        MixPanelController.track("CreateCustomPage", null);
                                        long time = System.currentTimeMillis();
                                        CustomPageActivity.dataModel.add(new CustomPageModel("Date(" + time + ")", name, s));
                                        Methods.showSnackBarPositive(activity, getString(R.string.page_successfully_created));
                                        finish();
                                    }else{
                                        Methods.showSnackBarNegative(activity, getString(R.string.enter_different_title_try_again));
                                        //Log.d("Create page Fail", "");
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    materialProgress.dismiss();
                                    Methods.showSnackBarNegative(activity, getString(R.string.something_went_wrong_try_again));
                                    //Log.d("Create page Fail", "" + error.getMessage());
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
                                    //Log.d("Update page success", "");
                                    CustomPageActivity.dataModel.get(curPos).DisplayName = name;
                                    Methods.showSnackBarPositive(activity, getString(R.string.page_updated));
                                    finish();
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    materialProgress.dismiss();
                                    Methods.showSnackBarNegative(activity, getString(R.string.something_went_wrong_try_again));
                                    //Log.d("Update page Fail", "" + error.getMessage());
                                }
                            });
                        }
                    }catch(Exception e){e.printStackTrace(); Methods.showSnackBarNegative(activity, getString(R.string.something_went_wrong_try_again)); materialProgress.dismiss();}
                }
            }
        });

        LinearLayout subtxt_layout = (LinearLayout)findViewById(R.id.subtxt_layout);
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
                showUrlDialog(getString(R.string.enter_image_url), 1);

            }
        });

       findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUrlDialog(getString(R.string.enter_hyperlink_url), 2);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_card_product_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
    private void showUrlDialog(String msg, final int url_id){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_url, null);
        final EditText et_url = (EditText) v.findViewById(R.id.et_url);
        final EditText et_tag = (EditText) v.findViewById(R.id.et_tag);
        if(url_id==2)
        {
            et_tag.setVisibility(View.VISIBLE);
        }
        et_url.setHint(msg);
        builder.setView(v).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String url = et_url.getText().toString().trim();
                if(url_id==1 && !url.isEmpty())
                {
                    richText.insertImage(url, getString(R.string.no_image));
                }
                else if(url_id==2 && !url.isEmpty())
                {
                   String tag = et_tag.getText().toString().trim();
                    if(!tag.isEmpty())
                    {
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
        Dialog d  = builder.create();
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

}