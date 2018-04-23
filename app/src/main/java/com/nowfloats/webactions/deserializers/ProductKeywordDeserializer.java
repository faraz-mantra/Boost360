package com.nowfloats.webactions.deserializers;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.nowfloats.Product_Gallery.Model.ProductKeywordResponseModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by NowFloats on 16-04-2018.
 */

public class ProductKeywordDeserializer implements JsonDeserializer<List<ProductKeywordResponseModel>> {
    @Override
    public List<ProductKeywordResponseModel> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonElement data = json.getAsJsonObject().getAsJsonArray("Data");

        return Arrays.asList(new Gson().fromJson(data, ProductKeywordResponseModel[].class));
    }
}
