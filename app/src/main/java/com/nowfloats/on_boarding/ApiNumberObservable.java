package com.nowfloats.on_boarding;

import java.util.Observable;

/**
 * Created by Admin on 20-03-2018.
 */

public class ApiNumberObservable extends Observable {

    private int n = 0;
    private String fpTag, fpId;
    public void setValue(int n)
    {
        this.n = n;
        setChanged();
        notifyObservers();
    }
    public int getValue()
    {
        return n;
    }

    public String getFpTag() {
        return fpTag;
    }

    public void setFpTag(String fpTag) {
        this.fpTag = fpTag;
    }

    public String getFpId() {
        return fpId;
    }

    public void setFpId(String fpId) {
        this.fpId = fpId;
    }
}
