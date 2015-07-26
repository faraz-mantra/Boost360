package com.nowfloats.NavigationDrawer.Chat;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nowfloats.Login.Login_Interface;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by guru on 24/07/2015.
 */
public class ChatFragment extends Fragment{
    public static RecyclerView chatRecyclerView;
    private Activity activity;
    private UserSessionManager session;
    public static ChatAdapter chatAdapter;
    public static ArrayList<ChatModel> chatModels = new ArrayList<>();
    public static String ChatFragmentPage = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        session = new UserSessionManager(activity.getApplicationContext(),activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        HomeActivity.headerText.setText("PRO CHAT");
        ChatFragmentPage = "";
    }

    @Override
    public void onPause() {
        super.onPause();
        ChatFragmentPage = null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
       super.onViewCreated(view, savedInstanceState);
       chatRecyclerView = (RecyclerView) view.findViewById(R.id.chat_recycler_view);
       chatRecyclerView.setHasFixedSize(true);
       chatRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
       chatRecyclerView.setItemAnimator(new FadeInUpAnimator());
       chatAdapter = new ChatAdapter(activity,chatModels);
       chatRecyclerView.setAdapter(chatAdapter);

       final ImageView sendBtn = (ImageView)view.findViewById(R.id.chat_send);
       PorterDuffColorFilter primaryLabelFilter = new PorterDuffColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
       sendBtn.setColorFilter(primaryLabelFilter);

       final FrameLayout chatSend_layout = (FrameLayout)view.findViewById(R.id.chat_send_layout);
       chatSend_layout.setVisibility(View.INVISIBLE);

        final EditText chatMsg = (EditText)view.findViewById(R.id.chat_msg);
       chatMsg.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length()>0){chatSend_layout.setVisibility(View.VISIBLE);}else{
                    chatSend_layout.setVisibility(View.INVISIBLE);
                }
           }
           @Override
           public void afterTextChanged(Editable s) {}
       });

        chatSend_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    chatMsg.setText("");
                    HashMap<String,String> map = new HashMap<String, String>();
                    final String message = chatMsg.getText().toString().trim();
                    map.put("message",message);
                    map.put("source","merchant");
                    Login_Interface chat = Constants.chatsendRestAdapter.create(Login_Interface.class);
                    chat.sendChat(session.getFPID(), map, new Callback<ChatRegResponse>() {
                        @Override
                        public void success(ChatRegResponse s, Response response) {
                            Log.i("Send GCM chat ", "reg success");
                            Log.d("Response", "Response : " + s.Status);
                            chatMsg.setText("");
                            chatModels.add(new ChatModel(message,false, Methods.getCurrentTime()));
                            chatAdapter.notifyDataSetChanged();
                            chatRecyclerView.scrollToPosition(chatModels.size() - 1);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.i("SEnd GCM chat ", "send FAILed");
                        }
                    });
                }catch(Exception e){
                    Log.i("Send GCM chat ","send exp");
                    e.printStackTrace();
                }
            }
        });
    }
}