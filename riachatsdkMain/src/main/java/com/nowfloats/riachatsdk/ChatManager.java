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

    private static ChatManager sChatManager;
    ChatConfig mChatConfig = new ChatConfig();
    private Activity mContext;

    private ChatManager(Activity context) {
        mContext = context;
    }

    public static ChatManager getInstance(Activity context) {
        if (sChatManager == null) {
            sChatManager = new ChatManager(context);
        }

        return sChatManager;
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

    public void startChat(ChatType chatType) {
        Intent i = new Intent(mContext, ChatViewActivity.class);
//        mContext.overridePendingTransition(R.anim.ria_fade_in, R.anim.slide_out_up);
        mContext.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        i.putExtra(Constants.PARCEL_NAME, mChatConfig);
        i.putExtra(Constants.CHAT_TYPE, chatType);
        mContext.startActivity(i);
    }

    public void endChat() {
        mChatConfig = null;
        mContext = null;
        sChatManager = null;
    }

    public enum ChatType {
        CREATE_WEBSITE,
        FEEDBACK
    }
}
