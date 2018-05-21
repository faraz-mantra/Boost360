package nowfloats.nfkeyboard.interface_contracts;

import android.view.View;

import nowfloats.nfkeyboard.models.KeywordModel;

public interface CandidateViewItemClickListener {
    void onItemClick(KeywordModel word);

    void onKeyboardTabClick(View view);
}
