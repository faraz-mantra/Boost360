package nfkeyboard.interface_contracts;

import java.util.ArrayList;
import java.util.List;

import nfkeyboard.keyboards.ImePresenterImpl;
import nfkeyboard.models.AllSuggestionModel;
import nfkeyboard.models.networkmodels.Details;

/**
 * Created by Admin on 28-03-2018.
 */

public interface ApiCallToKeyboardViewInterface {
    void onLoadMore(ImePresenterImpl.TabType type, List<AllSuggestionModel> models);

    void onError(ImePresenterImpl.TabType type);

    void onCompleted(ImePresenterImpl.TabType type, ArrayList<AllSuggestionModel> modelList);

    void onDetailsLoaded(ArrayList<AllSuggestionModel> details);

}
