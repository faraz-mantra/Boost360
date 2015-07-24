package com.nowfloats.NavigationDrawer.Chat;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.nowfloats.Login.Login_Interface;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
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
    private RecyclerView recyclerView;
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
        recyclerView = (RecyclerView) view.findViewById(R.id.chat_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setItemAnimator(new FadeInUpAnimator());
        chatAdapter = new ChatAdapter(activity,chatModels);
        recyclerView.setAdapter(chatAdapter);

        Button sendBtn = (Button)view.findViewById(R.id.chat_send);
        final EditText chatMsg = (EditText)view.findViewById(R.id.chat_msg);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
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
                            chatModels.add(new ChatModel(message,false));
                            chatAdapter.notifyDataSetChanged();
                            chatMsg.setText("");
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