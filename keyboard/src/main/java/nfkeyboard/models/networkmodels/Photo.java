package nfkeyboard.models.networkmodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import nfkeyboard.models.AllSuggestionModel;

/**
 * Created by Shimona on 02-06-2018.
 */

public class Photo {
    @SerializedName("ImageURL")
    @Expose
    private String imageUri;

    @SerializedName("selected")
    @Expose
    private boolean selected;

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public AllSuggestionModel toAllSuggestion() {
        AllSuggestionModel model = new AllSuggestionModel(imageUri);
        model.setImageUri(getImageUri());
        model.setSelected(getSelected());
        return model;
    }

}
