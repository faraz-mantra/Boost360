package nowfloats.bubblebutton;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

/**
 * Created by Admin on 12-04-2017.
 */

public class ReadMessagesService extends Service implements Loader.OnLoadCompleteListener<Cursor> {


    private CursorLoader mCursorLoader;
    private static final int LOADER_ID_NETWORK = 204;
    private Uri contentUri = Uri.parse("content://sms/");
    public String selections;
    private String[] projections=new String[]{"date","address","body","seen","type"};
    private String order="date DESC";
    private int selectionLength=Constants.selections.length;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        StringBuilder builder = new StringBuilder();

        for(int i=0;i<selectionLength;i++){

            if(i == selectionLength-1){
                builder.append(" address Like \"%"+Constants.selections[i]+"%\"");
            }
            else{
                builder.append(" address Like \"%"+Constants.selections[i]+"%\" or");
            }

        }
        selections =builder.toString();
        mCursorLoader = new CursorLoader(this, contentUri, projections, null, null, order);
        mCursorLoader.registerListener(LOADER_ID_NETWORK, this);
        mCursorLoader.startLoading();
    }

    @Override
    public void onLoadComplete(Loader<Cursor> loader, Cursor cursor) {
        if(cursor!=null && cursor.moveToFirst()){

            //NfMessage message;
            do{
                Log.v("ggg",cursor.getLong(0)+" "+cursor.getString(3));
               /* message = new NfMessage()
                        .setDate(cursor.getLong(0))
                        .setSubject(cursor.getString(1))
                        .setBody(cursor.getString(2))
                        .setSeen(cursor.getString(3));*/
            }while(cursor.moveToNext());
            cursor.close();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCursorLoader != null) {
            mCursorLoader.unregisterListener(this);
            mCursorLoader.cancelLoad();
            mCursorLoader.stopLoading();
        }
    }
}
