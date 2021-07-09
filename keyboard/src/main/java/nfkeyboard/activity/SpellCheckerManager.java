package nfkeyboard.activity;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.Log;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextInfo;
import android.view.textservice.TextServicesManager;


/**
 * Created by Admin on 30-03-2018.
 */

public class SpellCheckerManager implements LifecycleObserver, SpellCheckerSession.SpellCheckerSessionListener {
    private SpellCheckerSession mSpellChecker;
//    private SpellCheckerInterface spellListener;
    private Lifecycle mLifeCycle;
    private Context mContext;
    public SpellCheckerManager(Context context, Lifecycle lifeCycle){
        mLifeCycle = lifeCycle;
        mContext = context;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onResume(){

        TextServicesManager mTextManager = (TextServicesManager) mContext.getSystemService(Context.TEXT_SERVICES_MANAGER_SERVICE);
        if (mTextManager != null) {
            mSpellChecker = mTextManager.newSpellCheckerSession(null, null, this, true);
        }
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onPause(){
        if (mSpellChecker != null){
            mSpellChecker.close();
        }
    }

    public void getSuggestions(String[] text){
        TextInfo[] textInfo = new TextInfo[text.length];
        for (int i = 0; i<text.length;i++){
            textInfo[i] = new TextInfo(text[i]);
        }
        mSpellChecker.getSuggestions(textInfo,5,false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            //mSpellChecker.getSentenceSuggestions(textInfo,5);
        }else{
            mSpellChecker.getSuggestions(textInfo,5,true);
            // suggestion cant show
        }
    }

    public void getSuggestions(String text){

        mSpellChecker.getSuggestions(new TextInfo(text),5);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            //mSpellChecker.getSentenceSuggestions(textInfo,5);
        }else{
            mSpellChecker.getSuggestions(new TextInfo(text),5);
            // suggestion cant show
        }
    }
    @Override
    public void onGetSuggestions(SuggestionsInfo[] results) {

        if (mLifeCycle.getCurrentState().isAtLeast(Lifecycle.State.STARTED)){
            //spellListener.onSuggestion(results[0].);
        }
        Log.v("ggg",results.toString());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] results) {

        StringBuffer sb = new StringBuffer("");
        for(SentenceSuggestionsInfo result:results){
            int n = result.getSuggestionsCount();
            for(int i=0; i < n; i++){
                int m = result.getSuggestionsInfoAt(i).getSuggestionsCount();

                for(int k=0; k < m; k++) {
                    sb.append(result.getSuggestionsInfoAt(i).getSuggestionAt(k))
                            .append("\n");
                }
                sb.append("\n");
            }
        }
        Log.v("ggg",sb.toString());
        if (mLifeCycle.getCurrentState().isAtLeast(Lifecycle.State.STARTED)){
            //spellListener.onSuggestion(results[0].);

        }
    }
}
