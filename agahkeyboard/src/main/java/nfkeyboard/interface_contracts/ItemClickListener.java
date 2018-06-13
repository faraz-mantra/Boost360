package nfkeyboard.interface_contracts;

import nfkeyboard.models.AllSuggestionModel;

/**
 * Created by Admin on 23-02-2018.
 */

public interface ItemClickListener {
    void onItemClick(AllSuggestionModel model);

    String onCopyClick(AllSuggestionModel model);

    void onCreateProductOfferClick(AllSuggestionModel model);

    String onCreateProductOfferResponse(String name, double oldPrice, double newPrice, String createdOn, String expiresOn, String Url, String currency);

    boolean onClick(AllSuggestionModel model, boolean selected);

    void onDetailsClick(AllSuggestionModel model);

    void onError();
}
