package nowfloats.nfkeyboard.models;

import android.text.TextUtils;

import nowfloats.nfkeyboard.adapter.BaseAdapterManager;

/**
 * Created by Admin on 23-02-2018.
 */

public class AllSuggestionModel {
    private String text, imageUrl, id;
    private BaseAdapterManager.SectionTypeEnum typeEnum = BaseAdapterManager.SectionTypeEnum.Text;



    public AllSuggestionModel(String text, String imageUrl) {
        this.text = text;
        this.imageUrl = imageUrl;
        if(!TextUtils.isEmpty(imageUrl)) {
            typeEnum = BaseAdapterManager.SectionTypeEnum.ImageAndText;
        }
    }

    private AllSuggestionModel() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTypeEnum(BaseAdapterManager.SectionTypeEnum type){
        typeEnum = type;
    }
    public String getText() {
        return text;
    }

    public String price;
    public String description;
    public String discount;

    public void setText(String text) {
        this.text = text;
    }

    public void setImageUrl(String imageUrl) {
        if(!TextUtils.isEmpty(imageUrl)) {
            typeEnum = BaseAdapterManager.SectionTypeEnum.ImageAndText;
        }
        this.imageUrl = imageUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public BaseAdapterManager.SectionTypeEnum getTypeEnum() {
        return typeEnum;
    }
}
