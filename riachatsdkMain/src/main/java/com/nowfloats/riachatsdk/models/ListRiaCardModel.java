package com.nowfloats.riachatsdk.models;

import java.io.Serializable;
import java.util.List;

public class ListRiaCardModel implements Serializable{


    private List<RiaCardModel> riaCardModels;

    public List<RiaCardModel> getRiaCardModels() {
        return riaCardModels;
    }

    public void setRiaCardModels(List<RiaCardModel> riaCardModels) {
        this.riaCardModels = riaCardModels;
    }
}