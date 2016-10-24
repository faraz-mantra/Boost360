package com.nowfloats.Twitter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.nowfloats.Login.UserSessionManager;
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

import javax.net.ssl.HostnameVerifier;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Boost Android on 07/05/2016.
 */
public class PostTweet extends AsyncTask<String, Integer, Void> {
    private final SharedPreferences mSharedPreferences;
    private Context mContext= null;
    private ProgressDialog mPostProgress;
    private UserSessionManager session;

    public PostTweet(Context context){
        mContext =context;
        mSharedPreferences= mContext.getSharedPreferences(TwitterConstants.PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
       /* mPostProgress = new ProgressDialog(mContext);
        mPostProgress.setMessage("Loading...");
        mPostProgress.setCancelable(false);
        mPostProgress.show();*/
    }

    @Override
    protected Void doInBackground(String[] params) {
        String status = params[0];
        String messageId = null;
        if(params.length>1) {
            messageId = params[1];
        }

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(mContext.getResources().getString(R.string.twitter_consumer_key));
        builder.setOAuthConsumerSecret(mContext.getResources().getString(R.string.twitter_consumer_secret));

        //SharedPreferences mSharedPreferences = null;
        String access_token = mSharedPreferences.getString(TwitterConstants.PREF_KEY_OAUTH_TOKEN, "");
        String access_token_secret = mSharedPreferences.getString(TwitterConstants.PREF_KEY_OAUTH_SECRET, "");

        AccessToken accessToken = new AccessToken(access_token, access_token_secret);
        Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
        try {
            if(messageId!=null) {
                String tag = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG);
                String rootAliasUri = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI);
                String shortUrl = shortUrl("http://" + tag + ".nowfloats.com/bizFloat/" + messageId);
                String siteUrl = (rootAliasUri != null) ? rootAliasUri : ("http://" + tag + ".nowfloats.com");
                String messageUrl = siteUrl + "/bizFloat/" + messageId;

                if (shortUrl == null) {
                    shortUrl = messageUrl;
                }

                int len = status.length();
                //  shortUrl = "10" ;
                int mlen = 140 - ((shortUrl != null) ? shortUrl.length() + 8 : 0);
                int tlen = Math.min(len, mlen);
                String separator = " ... ";
                if (tlen != mlen) {
                    separator = " - ";
                }

                //tweetMessage = shareText.substring(0, tlen)+separator+shortUrl;
                String tweetMessage = status + separator + shortUrl;

                StatusUpdate statusUpdate = new StatusUpdate(tweetMessage);

                twitter4j.Status response = twitter.updateStatus(statusUpdate);
                Log.d("Status", response.getText());
                //Toast.makeText(mContext, "Your tweet is posted", Toast.LENGTH_SHORT).show();
            }
        } catch (TwitterException e) {
           // Toast.makeText(mContext, "Posting probolm", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void Result) {
        closeProgress();
    }
    private void closeProgress() {
        if (mPostProgress != null && mPostProgress.isShowing()) {
            mPostProgress.dismiss();
            mPostProgress = null;
        }
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