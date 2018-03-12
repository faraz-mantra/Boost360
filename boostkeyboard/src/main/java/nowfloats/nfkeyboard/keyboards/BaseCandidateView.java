package nowfloats.nfkeyboard.keyboards;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Admin on 26-02-2018.
 */

public interface BaseCandidateView {

    View onCreateView();
    View onAddView(ViewGroup p);
}
