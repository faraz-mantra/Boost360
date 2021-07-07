package com.nowfloats.signup.UI.Service;

/**
 * Created by NowFloatsDev on 08/06/2015.
 */
public class Signup_Facebook {

    /*Activity activity ;
    String Facebook_APP_ID = Constants.FACEBOOK_API_KEY;
    final Facebook facebook = new Facebook(Constants.FACEBOOK_API_KEY);
    AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);
    UserSessionManager sessionManager ;
    String access_token ;


    public Signup_Facebook(Activity appContext)
    {
        activity = appContext;
        sessionManager = new UserSessionManager(appContext.getApplicationContext(),appContext);
    }

    public static boolean isNetworkAvailable(Context mContext) {

        ConnectivityManager cm = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.e("Network Testing", "***Available***");
            return true;
        }
        Log.e("Network Testing", "***Not Available***");
        return false;
    }

    public String accessTokenFacebook() {

//        mPrefs = getPreferences(MODE_PRIVATE);
//        access_token = mPrefs.getString("access_token", null);
       // long expires = mPrefs.getLong("access_expires", 0);
        // Log.e("Facebook token", access_token);

        if (access_token != null) {
            facebook.setAccessToken(access_token);
        }
//        if (expires != 0) {
//            facebook.setAccessExpires(expires);
//        }

        *//**
     * Only call authorize if the access_token has expired.
     *//*
        if (!facebook.isSessionValid()) {

            facebook.authorize(activity, new String[] {}, new Facebook.DialogListener() {
                public void onComplete(Bundle values) {
                    try {
                        JSONObject me = new JSONObject(facebook.request("me"));

                        String id = me.getString("id");



                        System.out.println("******facebook.getAccessToken()****"
                                        + facebook.getAccessToken());

                        access_token = facebook.getAccessToken();

                        com.nowfloats.signup.UI.API.getFacebookData getData = new com.nowfloats.signup.UI.API.getFacebookData(activity,access_token);
                        getData.execute();

                       // return;

                       // new getFacebookData().execute();
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }

                public void onFacebookError(FacebookError error) {
                }

                public void onError(DialogError e) {
                }

                public void onCancel() {
                }
            });
        }

        return access_token ;
//        else {
//            new getFacebookData().execute();
//        }

    }

    public class getFacebookData extends AsyncTask<String, Void, String> {

        ProgressDialog pd = null;
        Boolean Connectiontimeout = false;
        Activity facebookActivity;
        String accessToken;

        public getFacebookData(Activity appContext,String accessToken)
        {
            facebookActivity = appContext;
            this.accessToken = accessToken;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(facebookActivity, activity.getString(R.string.please_wait),activity.getString(R.string.loading_wait), true);
            pd.setCancelable(true);

        }

        @Override
        protected String doInBackground(String... params) {
            fbUserProfile();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            pd.dismiss();
            if (Connectiontimeout != true) {
//                textName.setText(name);
//                textUserName.setText(userName);
//                textGender.setText(gender);
//                userImage.setImageBitmap(profilePic);
            } else {
                Toast.makeText(facebookActivity, activity.getString(R.string.connection_time_out),Toast.LENGTH_SHORT).show();
            }
        }

    }

    *//**
     * getting user facebook data from facebook server
     *//*
    public void fbUserProfile() {

        try {
           // access_token = mPrefs.getString("access_token", null);
            JSONObject jsonObj = null;
            JSONObject jsonObjData = null;
            JSONObject jsonObjUrl = null;
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 50000);
            HttpConnectionParams.setSoTimeout(httpParameters, 50000);

            HttpClient client = new DefaultHttpClient(httpParameters);

            String requestURL = "https://graph.facebook.com/me?fields=picture,id,name,gender,username&access_token="
                    + access_token;
            Log.i("Request URL:", "---" + requestURL);
            HttpGet request = new HttpGet(requestURL);

            HttpResponse response = client.execute(request);
            BufferedReader rd = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent()));
            String webServiceInfo = "";

            while ((webServiceInfo = rd.readLine()) != null) {
                Log.i("Service Response:", "---" + webServiceInfo);
                jsonObj = new JSONObject(webServiceInfo);
                jsonObjData = jsonObj.getJSONObject("picture");
                jsonObjUrl = jsonObjData.getJSONObject("data");
//                name = jsonObj.getString("name");
//                userName = jsonObj.getString("username");
//                gender = jsonObj.getString("gender");
//                imageURL = jsonObjUrl.getString("url");
//                profilePic = BitmapFactory.decodeStream((InputStream) new URL(
//                        imageURL).getContent());
            }

        } catch (Exception e) {
           // Connectiontimeout = true;
        }
    }*/


}
