package nfkeyboard.keyboards;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import io.separ.neural.inputmethod.indic.R;
import nfkeyboard.adapter.TextSuggestionAdapter;
import nfkeyboard.models.KeywordModel;

/**
 * Created by Admin on 02-04-2018.
 */

public class TextSuggestionsCandidateView extends BaseCandidateView {

    private int currentView;
    private TextSuggestionAdapter textSuggestionAdapter;
    private ArrayList<KeywordModel> suggestions;

    public TextSuggestionsCandidateView(Context context) {
        super(context);
    }

    public TextSuggestionsCandidateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextSuggestionsCandidateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addCandidateView(ViewGroup parent, ImePresenterImpl.TabType tabType) {

        switch (tabType) {
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
       /* if (textSuggestionAdapter == null) {
            RecyclerView mRecyclerView = findViewById(R.id.rv_suggestions);
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
            mRecyclerView.addItemDecoration(
                    new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));
            mRecyclerView.setHasFixedSize(true);
            textSuggestionAdapter = new TextSuggestionAdapter(mContext);
            mRecyclerView.setAdapter(textSuggestionAdapter);
            textSuggestionAdapter.setKeyboardItemClickListener(listener);
        }*/

        TextView suggestion1 = findViewById(R.id.tv_suggestion1);
        View view = findViewById(R.id.view);
        TextView suggestion2 = findViewById(R.id.tv_suggestion2);
        TextView suggestion3 = findViewById(R.id.tv_suggestion3);
        View view2 = findViewById(R.id.view2);
        suggestions = new ArrayList<>();
        if (bundle.getSerializable("data") != null && ((ArrayList<KeywordModel>) bundle.getSerializable("data")).size() > 0) {

            suggestions = (ArrayList<KeywordModel>) bundle.getSerializable("data");
            StringBuilder builder = new StringBuilder();
            for (KeywordModel model : suggestions) {
                builder.append(model.getWord() + ",");
            }
            Log.v(" adapter data - > ", builder.toString());
            // textSuggestionAdapter.addNewSuggestions(suggestions);
            if (suggestions != null && !suggestions.isEmpty()) {
                findViewById(R.id.suggestion_layout).setVisibility(VISIBLE);
            } else {
                findViewById(R.id.suggestion_layout).setVisibility(INVISIBLE);
            }
            if (suggestions != null && !suggestions.isEmpty()) {
                if (suggestions.size() == 1) {
                    suggestion1.setVisibility(VISIBLE);
                    suggestion2.setVisibility(GONE);
                    suggestion3.setVisibility(GONE);
                    view.setVisibility(GONE);
                    view2.setVisibility(GONE);
                    suggestion1.setText(suggestions.get(0).getWord());
                } else if (suggestions.size() == 2) {
                    suggestion1.setVisibility(VISIBLE);
                    suggestion2.setVisibility(VISIBLE);
                    suggestion3.setVisibility(GONE);
                    view.setVisibility(VISIBLE);
                    view2.setVisibility(GONE);
                    suggestion1.setText(suggestions.get(0).getWord());
                    suggestion2.setText(suggestions.get(1).getWord());
                } else if (suggestions.size() > 2) {
                    suggestion1.setVisibility(VISIBLE);
                    suggestion2.setVisibility(VISIBLE);
                    suggestion3.setVisibility(VISIBLE);
                    view.setVisibility(VISIBLE);
                    view2.setVisibility(VISIBLE);
                    suggestion1.setText(suggestions.get(0).getWord());
                    if (suggestions.size() > 1) {
                        suggestion2.setText(suggestions.get(1).getWord());
                    }
                    if (suggestions.size() > 2) {
                        suggestion3.setText(suggestions.get(2).getWord());
                    }
                }
            }

        }


        suggestion1.setOnClickListener(this);
        suggestion2.setOnClickListener(this);
        suggestion3.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (suggestions != null && !suggestions.isEmpty()) {
            if (view.getId() == R.id.tv_suggestion1) {
                listener.onItemClick(suggestions.get(0));
            } else if (view.getId() == R.id.tv_suggestion2) {
                listener.onItemClick(suggestions.get(1));
            } else if (view.getId() == R.id.tv_suggestion3) {
                listener.onItemClick(suggestions.get(2));
            }
        }
        if (currentView != R.id.img_nowfloats && currentView == view.getId()) {
            return;
        }
        currentView = view.getId();
        findViewById(R.id.img_settings).setBackgroundResource(view.getId() == R.id.img_settings ? R.drawable.round_414141 : android.R.color.transparent);
        //findViewById(R.id.img_nowfloats).setBackgroundResource(view.getId() == R.id.img_nowfloats ? R.drawable.round_414141 : android.R.color.transparent);
        listener.onKeyboardTabClick(view);
    }
}
