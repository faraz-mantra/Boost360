package com.nowfloats.Analytics_Screen.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nowfloats.NavigationDrawer.Create_Message_Activity;
import com.thinksity.R;


/**
 * Created by Abhi on 11/28/2016.
 */

public class PostFacebookUpdateFragment extends Fragment {
    Button postToFacebook;
    Context context;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root =inflater.inflate(R.layout.fragment_facebook_create_update,container,false);
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        postToFacebook= (Button) view.findViewById(R.id.create_facebook_update_button);
        postToFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, Create_Message_Activity.class);
                startActivity(i);
            }
        });
    }
}
