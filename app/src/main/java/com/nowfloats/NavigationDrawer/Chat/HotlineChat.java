package com.nowfloats.NavigationDrawer.Chat;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.freshdesk.hotline.ConversationOptions;
import com.freshdesk.hotline.Hotline;
import com.freshdesk.hotline.HotlineCallbackStatus;
import com.freshdesk.hotline.HotlineConfig;
import com.freshdesk.hotline.HotlineUser;
import com.freshdesk.hotline.UnreadCountCallback;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by Admin on 1/11/2017.
 */

public class HotlineChat extends Fragment implements View.OnClickListener {
    private Context context;
    ConversationOptions convOptions;
    private ArrayList<String> tags;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hotline_chat,container,false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Hotline.getInstance(context).getUnreadCountAsync(new UnreadCountCallback() {
            @Override
            public void onResult(HotlineCallbackStatus hotlineCallbackStatus, int i) {
                Methods.showSnackBarPositive(getActivity(),"alter new messages "+String.valueOf(i));
            }
        });
        if(!isAdded()) return;
        Button conversation= (Button) view.findViewById(R.id.conversation);
        Button faq= (Button) view.findViewById(R.id.faq);
        EditText search= (EditText) view.findViewById(R.id.search);
        conversation.setOnClickListener(this);
        faq.setOnClickListener(this);
        search.setOnClickListener(this);
        HotlineConfig hlConfig=new HotlineConfig("4be3ee16-b8ab-400a-bcdd-2f33103c36f8","f843d239-0077-4acd-ab8c-9988c6c863bc");

        hlConfig.setVoiceMessagingEnabled(true);
        hlConfig.setCameraCaptureEnabled(true);
        hlConfig.setPictureMessagingEnabled(true);

        Hotline.getInstance(context).init(hlConfig);

        HotlineUser hlUser=Hotline.getInstance(context).getUser();
        hlUser.setName("John Doe");
        hlUser.setEmail("john.doe.1982@mail.com");
        hlUser.setExternalId("john.doe");
        hlUser.setPhone("+91", "9790987495");
        Hotline.getInstance(context).updateUser(hlUser);

        convOptions = new ConversationOptions();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.conversation:
                Hotline.showConversations(context);
                break;
            case R.id.faq:
                Hotline.showFAQs(context);
                break;
            case R.id.search:
                tags=new ArrayList<>();
                tags.add(((EditText)view).getText().toString());
                convOptions.filterByTags(tags,"Abhishek");
                Hotline.showConversations(context, convOptions);
                break;
            default:
                break;
        }
    }
}
