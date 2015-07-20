package com.nowfloats.BusinessProfile.UI.Model;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.android.DialogError;
import com.facebook.android.FacebookError;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.CustomFilterableAdapter;
import com.nowfloats.util.Methods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by NowFloatsDev on 27/05/2015.
 */
public class Facebook {

    Activity activity ;
    int size = 0;
    boolean[] checkedPages;
    private String page_access_token;
    UserSessionManager session;
    ArrayList<String> items;

    static final com.facebook.android.Facebook facebook = new com.facebook.android.Facebook(Constants.FACEBOOK_API_KEY);




    public static void fbData(Activity activity) {
        final String[] PERMISSIONS = new String[] { "photo_upload",
                "user_photos", "publish_stream", "read_stream",
                "offline_access", "publish_actions" };

        facebook.authorize(activity, PERMISSIONS,  new com.facebook.android.Facebook.DialogListener() {

            public void onComplete(Bundle values) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject me;
                        try {
                            me = new JSONObject(facebook.request("me"));
//                            Constants.FACEBOOK_USER_ACCESS_ID = facebook.getAccessToken();
//                            Constants.FACEBOOK_ACCESS_TOKEN = facebook.getAccessToken();
//                            Constants.FACEBOOK_USER_ID = (String) me.getString("id");
//                            Constants.FACEBOOK_USER_NAME = (String) me.getString("name");
////                            session.storeFacebookName(Constants.FACEBOOK_USER_NAME);
////                            DataBase dataBase = new DataBase(activity);
////                            dataBase.updateFacebookNameandToken(Constants.FACEBOOK_USER_NAME,Constants.FACEBOOK_ACCESS_TOKEN);
//
//                            Constants.fbShareEnabled = true;
////                            prefsEditor.putBoolean("fbShareEnabled", true);
////                            prefsEditor.putString("fbId", Constants.FACEBOOK_USER_ID);
////                            prefsEditor.putString("fbAccessId",
////                                    Constants.FACEBOOK_USER_ACCESS_ID);
////                            prefsEditor.putString("fbUserName",
////                                    Constants.FACEBOOK_USER_NAME);
////                            prefsEditor.commit();

                        } catch (Exception e1) {
                            e1.printStackTrace();

                        }

//                        try {
//
//                                // code runs in a thread
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        facebookHome.setImageDrawable(getResources().getDrawable(
//                                                R.drawable.facebook_icon));
//                                        facebookHomeCheckBox.setChecked(true);
//                                        facebookHomeStatus.setText(Constants.FACEBOOK_USER_NAME);
//                                    }
//                                });
//                            } catch (final Exception ex) {
//                                Log.i("---", "Exception in thread");
//                            }
//

                    }

                }).start();

//                facebookHome.setImageDrawable(getResources().getDrawable(
//                        R.drawable.facebook_icon));
//                facebookHomeCheckBox.setChecked(true);
//                facebookHomeStatus.setText(Constants.FACEBOOK_USER_NAME);
            }

            @Override
            public void onCancel() {
                onFBError();
            }

            @Override
            public void onFacebookError(FacebookError e) {
                onFBError();
            }

            @Override
            public void onError(DialogError e) {
                onFBError();
            }

        });



//        facebookHome.setImageDrawable(getResources().getDrawable(
//                R.drawable.facebook_icon));
//        facebookHomeCheckBox.setChecked(true);
//        facebookHomeStatus.setText(Constants.FACEBOOK_USER_NAME);
    }

    public void fbPageData() {
        final String[] PERMISSIONS = new String[] { "photo_upload",
                "user_photos", "publish_stream", "read_stream",
                "offline_access", "manage_pages", "publish_actions" };

        facebook.authorize(activity, PERMISSIONS, new com.facebook.android.Facebook.DialogListener() {
            public void onComplete(Bundle values) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject pageMe = new JSONObject(facebook
                                    .request("me/accounts"));
                            Constants.FbPageList = pageMe.getJSONArray("data");
                            if (Constants.FbPageList != null) {
                                size = Constants.FbPageList.length();

                                checkedPages = new boolean[size];
                                if (size > 0) {
                                    items = new ArrayList<String>();
                                    for (int i = 0; i < size; i++) {
//                                        Constants.FACEBOOK_PAGE_ID = (String) ((JSONObject) Constants.FbPageList.get(i)).get("id");
                                        items.add(i,(String) ((JSONObject) Constants.FbPageList
                                                .get(i)).get("name"));
                                    }

                                    for (int i = 0; i < size; i++) {
                                        checkedPages[i] = false;
                                    }
//                                    facebookPage.setImageDrawable(getResources().getDrawable(
//                                            R.drawable.facebook_page));
                               }
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        finally {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    if (items != null && items.size() > 0) {
                                        final ArrayList<String> stringList = items;
                                        final CustomFilterableAdapter adapter = new CustomFilterableAdapter(
                                                stringList, activity);
                                        final MaterialDialog dialog = new MaterialDialog.Builder(activity)
                                                .title("Select a Page")
//                                                .adapter(adapter)
                                                .build();

                                        ListView listView = dialog.getListView();
                                        if (listView != null) {
                                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    //Toast.makeText(PreSignUpActivity.this, "Clicked item " + position, Toast.LENGTH_SHORT).show();
                                                    String strName = adapter.getItem(position);
                                                    try {
                                                        page_access_token = ((String) ((JSONObject) Constants.FbPageList.get(position))
                                                                .get("access_token"));

                                                        session.storePageAccessToken(page_access_token);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
//                                                    pageSeleted(position, adapter.getItem(position), Constants.FACEBOOK_PAGE_ID, page_access_token);
                                                    dialog.dismiss();
                                                }
                                            });
                                        }
                                        dialog.show();
                                    } else {
                                        Methods.materialDialog(activity, "Prompt", "There is no Facebook page\nlinked to this account");
                                    }
                                }
                            });
                        }
                    }
                }).start();
            }

            @Override
            public void onCancel() {
                onFBPageError();
            }

            @Override
            public void onFacebookError(FacebookError e) {
                onFBPageError();
            }

            @Override
            public void onError(DialogError e) {
                onFBPageError();
            }



        });
    }

    public void pageSeleted(int id,String pageName, String pageID, String pageAccessToken) {
        String s = "";
        JSONObject obj;
        session.storeFacebookPage(pageName);
        JSONArray data = new JSONArray();
//        for (int i = 0; i < size; i++) {
//            if (checkedPages[i]) {
//                s = s + "," + items.get(i);

//        facebookPageStatus.setText("" + pageName);
//        facebookPageCheckBox.setChecked(true);

//        DataBase dataBase =new DataBase(activity);
//        dataBase.updateFacebookPage(pageName ,pageID,pageAccessToken);

        obj = new JSONObject();
        try {
            obj.put("id", (String) ((JSONObject) Constants.FbPageList
                    .get(id)).get("id"));
//            page_access_token = (String) ((JSONObject) Constants.FbPageList.get(id))
//                    .get("access_token"));
            obj.put("access_token",
                    (String) ((JSONObject) Constants.FbPageList.get(id))
                            .get("access_token"));



            data.put(obj);
//
//                    String id = (String) ((JSONObject) Constants.FbPageList
//                            .get(i)).get("id");
//                    String pageName = (String) ((JSONObject) Constants.FbPageList
//                            .get(i)).get("name");

            Constants.fbPageFullUrl = "https://www.facebook.com/pages/"
                    + pageName + "/" + pageID;
            Constants.fbFromWhichPage = pageName;
//            prefsEditor.putString("fbPageFullUrl",
//                    Constants.fbPageFullUrl);
//            prefsEditor.putString("fbFromWhichPage",
//                    Constants.fbFromWhichPage);
//            prefsEditor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        obj = new JSONObject();
        try {
            obj.put("data", data);
            Constants.FbPageList = data;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String fbPageData = obj.toString();
        if (!Util.isNullOrEmpty(fbPageData)) {
            if (fbPageData.equals("{\"data\":[]}")) {
              //  prefsEditor.putString("fbPageData", "");
                Constants.fbPageShareEnabled = false;
             //   prefsEditor.putBoolean("fbPageShareEnabled",
              //          Constants.fbPageShareEnabled);
             //   prefsEditor.commit();
                Constants.FbPageList = null;
                //InitShareResources();

            } else {
                Constants.fbPageShareEnabled = true;
            }
        }

    }


    static void onFBError() {
//        Constants.fbShareEnabled = false;
//        prefsEditor.putBoolean("fbShareEnabled", false);
//        prefsEditor.commit();
    }

    void onFBPageError() {
//        Constants.fbPageShareEnabled = false;
//        prefsEditor.putBoolean("fbPageShareEnabled", false);
//        prefsEditor.commit();
    }
}
