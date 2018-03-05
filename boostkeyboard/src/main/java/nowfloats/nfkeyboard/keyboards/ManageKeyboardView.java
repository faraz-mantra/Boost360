package nowfloats.nfkeyboard.keyboards;

import android.content.Context;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import nowfloats.nfkeyboard.R;
import nowfloats.nfkeyboard.activity.SpeechRecognitionManager;
import nowfloats.nfkeyboard.adapter.MainAdapter;
import nowfloats.nfkeyboard.interface_contracts.CandidateToPresenterInterface;
import nowfloats.nfkeyboard.interface_contracts.ItemClickListener;
import nowfloats.nfkeyboard.interface_contracts.SpeechRecognitionResultInterface;
import nowfloats.nfkeyboard.models.AllSuggestionModel;

/**
 * Created by Admin on 27-02-2018.
 */

public class ManageKeyboardView extends FrameLayout implements ItemClickListener, SpeechRecognitionResultInterface {
    private KeyboardViewBaseImpl mKeyboardView;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private MainAdapter shareAdapter;
    private SpeechRecognitionManager mSpeechRecognitionManager;
    private CandidateToPresenterInterface presenterListener;
    private TextView mSpeechMessageTv;
    private ConstraintLayout speechLayout, shareLayout;

    public ManageKeyboardView(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public ManageKeyboardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public ManageKeyboardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void setPresenterListener(CandidateToPresenterInterface listener){
        presenterListener = listener;
        init();
    }
    private void init(){
        speechLayout= findViewById(R.id.speech_layout);
        shareLayout= findViewById(R.id.sharelayout);
        mSpeechMessageTv = findViewById(R.id.tv_message);
    }
    public void showShareLayout(ArrayList<AllSuggestionModel> models){
        mKeyboardView.setVisibility(INVISIBLE);
        stopListening();
        shareLayout.setVisibility(VISIBLE);
        if (mRecyclerView == null) {
            mRecyclerView = shareLayout.findViewById(R.id.rv_list);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            shareAdapter = new MainAdapter(mContext, this);
            shareAdapter.setSuggestionModels(models);
            mRecyclerView.setAdapter(shareAdapter);
            SnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(mRecyclerView);
        }else {
            shareAdapter.setSuggestionModels(models);
            shareAdapter.notifyDataSetChanged();
        }
    }

    public void onSetSuggestions(ArrayList<AllSuggestionModel> models){
        shareAdapter.setSuggestionModels(models);
        shareAdapter.notifyDataSetChanged();
    }
    public void showKeyboardLayout(){
        shareLayout.setVisibility(GONE);
        mKeyboardView.setVisibility(VISIBLE);
        stopListening();
    }

    public void showSpeechInput(){
        if (mSpeechRecognitionManager == null) {
            mSpeechRecognitionManager = new SpeechRecognitionManager(mContext,this);
        }
        shareLayout.setVisibility(GONE);
        mKeyboardView.setVisibility(INVISIBLE);
        mSpeechMessageTv.setText("Connecting...");
        speechLayout.setVisibility(VISIBLE);
        mSpeechRecognitionManager.startListening();
    }

    public void stopListening(){
        speechLayout.setVisibility(GONE);
        if (mSpeechRecognitionManager != null){
            mSpeechRecognitionManager.stopListening();
        }

    }
    public KeyboardViewBaseImpl getKeyboard(){
        return mKeyboardView = findViewById(R.id.keyboard_view);
    }

    @Override
    public void onItemClick(AllSuggestionModel model) {
        if (presenterListener!= null){
            presenterListener.onItemClick(model);
        }
    }

    @Override
    public void onResult(Bundle b) {
        ArrayList<String> matches = b.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (matches == null) return;
        Toast.makeText(mContext, matches.size()>0?matches.get(0):"null", Toast.LENGTH_SHORT).show();
//        for (String speech : matches){
//            result.append(speech+"\n");
//        }
        //result.delete(result.lastIndexOf("\n"),result.length());
        presenterListener.onSpeechResult(matches.get(0));

    }

    @Override
    public void onReadyToSpeech(Bundle b) {
        mSpeechMessageTv.setText("Listening...");
        //Toast.makeText(mContext, "ready to speech", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(int i) {
        String message = null;
        switch (i) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                mSpeechMessageTv.setText(message);
                return;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                if(speechLayout.getVisibility() == VISIBLE) {
                    presenterListener.onSpeechResult(null);
                }
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
            case SpeechRecognizer.ERROR_SERVER:
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "Recognizer Busy";
                //showSpeechInput();
                //mSpeechRecognitionManager.startListening();
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        mSpeechMessageTv.setText(message);
        //Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }
}
