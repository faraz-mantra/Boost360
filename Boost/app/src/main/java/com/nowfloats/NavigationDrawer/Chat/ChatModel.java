package com.nowfloats.NavigationDrawer.Chat;

/**
 * Created by guru on 24/07/2015.
 */
public class ChatModel {
     public String message;
     public boolean incoming;
     public String time;

    public ChatModel(String message,boolean val,String time){
        this.message = message;
        this.incoming = val;
        this.time = time;
    }
}