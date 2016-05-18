package com.nowfloats.NavigationDrawer.API.twitter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Twitter.TwitterConstants;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.thinksity.R;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.net.ssl.HostnameVerifier;

import oauth.signpost.OAuth;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;


public final class PostImageTweetInBackgroundAsyncTask extends
        AsyncTask<Void, String, String> {

    private Activity appContext = null;
    ProgressDialog pd = null;
    String Username = null, Password = null;
    private SharedPreferences prefs = null;
    String responseMessage = "";
    Boolean success = false;
    String clientIdConcatedWithQoutes = "\"" + Constants.clientId + "\"";
    int size = 0;
    SharedPreferences.Editor prefsEditor;
    private String token, secretToken;
    String shareText = "";
    String id = null;
    String mesgUrl = null;
    String path = null;
    UserSessionManager session;

    public PostImageTweetInBackgroundAsyncTask(Activity context, String shareText, String id, String path, UserSessionManager session) {
        this.appContext = context;
        this.shareText = shareText;
        this.id = id;
        this.path = path;
        this.session = session;
        if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG) != null && path != null)
            mesgUrl = "http://" + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG) + ".nowfloats.com/bizFloat/" + id;
        prefs = appContext.getSharedPreferences(TwitterConstants.PREF_NAME, Activity.MODE_PRIVATE);

    }

    public PostImageTweetInBackgroundAsyncTask(Activity context, UserSessionManager session) {
        this.appContext = context;
        prefs = appContext.getSharedPreferences(TwitterConstants.PREF_NAME, Activity.MODE_PRIVATE);
        this.session = session;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(String result) {

    }

    @Override
    protected String doInBackground(Void... params) {
        String response = "", tweetMessage = "";

        int len = shareText.length();
        int mlen = 140 - (20 + 8);
        int tlen = Math.min(len, mlen);
        String separator = " ... ";

        String tag = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG);
        String rootAliasUri = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI);
        String siteUrl = (rootAliasUri!=null && rootAliasUri!="" && rootAliasUri.length()>0)?rootAliasUri:("http://"+tag+".nowfloats.com");
        String messageUrl = siteUrl + "/bizFloat/" + id;
        String shortUrl = shortUrl(messageUrl);

        if(shortUrl==null || shortUrl=="" || shortUrl.length()==0){
            shortUrl = messageUrl;
        }

        len = shareText.length();
        //  shortUrl = "10" ;
        mlen = 140 - ((shortUrl!=null)?shortUrl.length() + 8:0);
        tlen = Math.min(len, mlen);
        separator = " ... ";
        if (tlen != mlen) {
            separator = " - ";
        }

        if (!Util.isNullOrEmpty(path) && shareText.length() > 30) {
            shareText = shortUrl(shareText);
         }
        //tweetMessage = shareText.substring(0, tlen)+separator+shortUrl;
        tweetMessage = shareText + separator + shortUrl;


        if (prefs.getBoolean(TwitterConstants.PREF_KEY_TWITTER_LOGIN,false)) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(appContext.getResources().getString(R.string.twitter_consumer_key));
            builder.setOAuthConsumerSecret(appContext.getResources().getString(R.string.twitter_consumer_secret));

            //SharedPreferences mSharedPreferences = null;
            String access_token = prefs.getString(TwitterConstants.PREF_KEY_OAUTH_TOKEN, "");
            String access_token_secret = prefs.getString(TwitterConstants.PREF_KEY_OAUTH_SECRET, "");

            AccessToken accessToken = new AccessToken(access_token, access_token_secret);
            Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
            twitter.setOAuthAccessToken(accessToken);

            //twitter.updateStatus(tweetMessage);
            StatusUpdate update = new StatusUpdate(tweetMessage);
            if(path!=null&& path!="" && path.length()>0) {
                File file = new File(path);
                update.setMedia(file);
            }
            try {
                twitter.updateStatus(update);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    public String shortUrl(String serverDataFetchUri) {
        String shortUrl = "";
        String serverResponse = "";
        try {
            // Set connection timeout to 5 secs and socket timeout to 10 secs
            HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = 5000;
            HttpConnectionParams.setConnectionTimeout(httpParameters,
                    timeoutConnection);
            int timeoutSocket = 10000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
            SchemeRegistry registry = new SchemeRegistry();
            SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
            socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
            registry.register(new Scheme("https", socketFactory, 443));
            SingleClientConnManager mgr = new SingleClientConnManager(httpParameters, registry);

            HttpClient hc = new DefaultHttpClient(mgr, httpParameters);

            HttpPost request = new HttpPost(
                    "https://www.googleapis.com/urlshortener/v1/url?key=AIzaSyCo9zb7OlpbObma7PEPwJv189qOtw-FtGM");
            request.setHeader("Content-type", "application/json");
            request.setHeader("Accept", "application/json");

            JSONObject obj = new JSONObject();
            obj.put("longUrl", serverDataFetchUri);
            request.setEntity(new StringEntity(obj.toString(), "UTF-8"));

            HttpResponse response = hc.execute(request);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                serverResponse = out.toString();
            } else {
                return null;
            }

            if (!Util.isNullOrEmpty(serverResponse)) {
                JSONObject data = new JSONObject(serverResponse);
                if (data != null) {
                    Constants.shortUrl = shortUrl = data.getString("id");
                }
            } else {
                Constants.shortUrl = shortUrl = serverDataFetchUri;
            }
        } catch (Exception e) {
            Constants.shortUrl = shortUrl = serverDataFetchUri;
        }
        return shortUrl;

    }

}
