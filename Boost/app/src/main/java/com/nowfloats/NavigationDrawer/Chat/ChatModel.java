package com.nowfloats.NavigationDrawer.Chat;

/**
 * Created by guru on 24/07/2015.
 */
public class ChatModel {
     public String message;
     public boolean incoming;
    public ChatModel(String message,boolean val){
        this.message = message;
        this.incoming = val;
    }
}
