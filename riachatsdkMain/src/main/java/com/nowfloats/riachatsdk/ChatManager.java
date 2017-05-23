package com.nowfloats.riachatsdk;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;

import com.nowfloats.riachatsdk.activities.ChatViewActivity;
import com.nowfloats.riachatsdk.models.ChatConfig;
import com.nowfloats.riachatsdk.utils.Constants;

/**
 * Created by NowFloats on 27-03-2017.
 */

public class ChatManager {

    ChatConfig mChatConfig = new ChatConfig();

    private Activity mContext;

    private static ChatManager sChatManager;

    public static ChatManager getInstance(Activity context) {
        if (sChatManager == null) {
            sChatManager = new ChatManager(context);
        }

        return sChatManager;
    }

    private ChatManager(Activity context) {
        mContext = context;
    }


    public void setBotImage(Bitmap bitmap) {
        mChatConfig.botImage = bitmap;
    }

    public void setBotName(String botName) {
        mChatConfig.botName = botName;
    }

    public void setChatBgColor(String color) {
        mChatConfig.chatBgColor = color;
    }

    public void setBotBubbleColor(String color) {
        mChatConfig.botBubbleColor = color;
    }

    public void setUserBubbleColor(String color) {
        mChatConfig.userBubbleColor = color;
    }

    public void setChatTextColor(String color) {
        mChatConfig.chatTextColor = color;
    }

    public void startChat() {
        Intent i = new Intent(mContext, ChatViewActivity.class);
        i.putExtra(Constants.PARCEL_NAME, mChatConfig);
        mContext.startActivity(i);
    }

    public void endChat() {
        mChatConfig = null;
        mContext = null;
        sChatManager = null;
    }
}
