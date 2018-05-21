package nowfloats.nfkeyboard.interface_contracts;

import java.util.List;

import nowfloats.nfkeyboard.keyboards.ImePresenterImpl;
import nowfloats.nfkeyboard.models.AllSuggestionModel;

/**
 * Created by Admin on 28-03-2018.
 */

public interface ApiCallToKeyboardViewInterface {
    void onLoadMore(ImePresenterImpl.TabType type, List<AllSuggestionModel> models);
    void onError(ImePresenterImpl.TabType type);
    void onCompleted(ImePresenterImpl.TabType type);
}
