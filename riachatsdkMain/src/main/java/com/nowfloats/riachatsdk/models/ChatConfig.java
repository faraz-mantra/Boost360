package com.nowfloats.riachatsdk.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by NowFloats on 27-03-2017.
 */

public class ChatConfig implements Parcelable {

    public Bitmap botImage;
    public String botName = "Ria";
    public String chatBgColor = "#ffffff";
    public String botBubbleColor = "#ffb900";
    public String userBubbleColor = "#FAFAFA";
    public String chatTextColor = "#808080";

    public ChatConfig(){

    }

    protected ChatConfig(Parcel in) {
        botImage = in.readParcelable(getClass().getClassLoader());
        botName = in.readString();
        chatBgColor = in.readString();
        botBubbleColor = in.readString();
        userBubbleColor = in.readString();
        chatTextColor = in.readString();
    }

    public static final Creator<ChatConfig> CREATOR = new Creator<ChatConfig>() {
        @Override
        public ChatConfig createFromParcel(Parcel in) {
            return new ChatConfig(in);
        }

        @Override
        public ChatConfig[] newArray(int size) {
            return new ChatConfig[size];
        }
    };

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(botImage, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeString(botName);
        dest.writeString(chatBgColor);
        dest.writeString(botBubbleColor);
        dest.writeString(userBubbleColor);
        dest.writeString(chatTextColor);
    }
}
