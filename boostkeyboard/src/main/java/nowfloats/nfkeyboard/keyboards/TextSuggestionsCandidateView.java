package nowfloats.nfkeyboard.keyboards;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import nowfloats.nfkeyboard.R;
import nowfloats.nfkeyboard.adapter.TextSuggestionAdapter;

/**
 * Created by Admin on 02-04-2018.
 */

public class TextSuggestionsCandidateView extends BaseCandidateView{

    private int currentView;
    private TextSuggestionAdapter textSuggestionAdapter;
    public TextSuggestionsCandidateView(Context context) {
        super(context);
    }

    public TextSuggestionsCandidateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextSuggestionsCandidateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void addCandidateView(ViewGroup parent, ImePresenterImpl.TabType tabType){

        switch (tabType){
            case KEYBOARD:
                currentView = R.id.img_nowfloats;
                break;
            case SETTINGS:
                currentView = R.id.img_settings;
                break;
            default:
                currentView = View.NO_ID;
                break;
        }
        parent.addView(this);
        findViewById(R.id.img_nowfloats).setBackgroundResource(android.R.color.transparent);
        findViewById(R.id.img_settings).setBackgroundResource(android.R.color.transparent);
        if (currentView != View.NO_ID) {
            findViewById(currentView).setBackgroundResource(R.drawable.round_414141);
        }
        findViewById(R.id.img_nowfloats).setOnClickListener(this);
        findViewById(R.id.img_settings).setOnClickListener(this);
    }

    @Override
    void setCandidateData(Bundle bundle) {

        if (textSuggestionAdapter == null){
            RecyclerView mRecyclerView = findViewById(R.id.rv_suggestions);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.HORIZONTAL));
            mRecyclerView.setHasFixedSize(true);
            textSuggestionAdapter = new TextSuggestionAdapter(mContext);
            mRecyclerView.setAdapter(textSuggestionAdapter);
        }

        textSuggestionAdapter.addNewSuggestions(bundle.getStringArrayList("data"));
    }

    @Override
    public void onClick(View view) {
        if (currentView != R.id.img_nowfloats && currentView == view.getId()){
            return;
        }
        currentView = view.getId();
        findViewById(R.id.img_settings).setBackgroundResource(view.getId() == R.id.img_settings ? R.drawable.round_414141:android.R.color.transparent);
        findViewById(R.id.img_nowfloats).setBackgroundResource(view.getId() == R.id.img_nowfloats ? R.drawable.round_414141:android.R.color.transparent);
        listener.onClick(view);
    }
}
