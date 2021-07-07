package com.nowfloats.NavigationDrawer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by NowFloats on 10-02-2017.
 */

public class RiaNodeDataModel implements Parcelable {

    public static final Creator<RiaNodeDataModel> CREATOR = new Creator<RiaNodeDataModel>() {
        @Override
        public RiaNodeDataModel createFromParcel(Parcel in) {
            return new RiaNodeDataModel(in);
        }

        @Override
        public RiaNodeDataModel[] newArray(int size) {
            return new RiaNodeDataModel[size];
        }
    };
    private String nodeId;
    private String buttonId;
    private String buttonLabel;

    public RiaNodeDataModel(String nodeId, String buttonId, String buttonLabel) {
        this.nodeId = nodeId;
        this.buttonId = buttonId;
        this.buttonLabel = buttonLabel;
    }

    private RiaNodeDataModel(Parcel in) {
        nodeId = in.readString();
        buttonId = in.readString();
        buttonLabel = in.readString();
    }

    public String getNodeId() {
        return nodeId;
    }

    public RiaNodeDataModel setNodeId(String nodeId) {
        this.nodeId = nodeId;
        return this;
    }

    public String getButtonId() {
        return buttonId;
    }

    public RiaNodeDataModel setButtonId(String buttonId) {
        this.buttonId = buttonId;
        return this;
    }

    public String getButtonLabel() {
        return buttonLabel;
    }

    public RiaNodeDataModel setButtonLabel(String buttonLabel) {
        this.buttonLabel = buttonLabel;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nodeId);
        dest.writeString(buttonId);
        dest.writeString(buttonLabel);
    }
}
