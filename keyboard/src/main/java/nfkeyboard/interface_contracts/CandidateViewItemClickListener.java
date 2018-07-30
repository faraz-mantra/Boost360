package nfkeyboard.interface_contracts;

import android.view.View;

import nfkeyboard.models.KeywordModel;

public interface CandidateViewItemClickListener {
    void onItemClick(KeywordModel word);

    void onKeyboardTabClick(View view);
}
