package com.nowfloats.NavigationDrawer.businessApps;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Key_Preferences;
import com.thinksity.R;

/**
 * Created by Admin on 12/27/2016.
 */

public class BusinessAppCompleteFragment extends Fragment implements View.OnClickListener {
    private String type;
    private UserSessionManager session;
    private Context context;

    public static Fragment getInstance(String type){
        Fragment frag=new BusinessAppCompleteFragment();
        Bundle b=new Bundle();
        b.putString("type",type);
        frag.setArguments(b);
        return  frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            type=getArguments().getString("type","android");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_business_app_complete,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if(!isAdded()) return;

        session = new UserSessionManager(context, getActivity());

        TextView appNameTextView= (TextView) view.findViewById(R.id.app_name);
        TextView firstCharText= (TextView) view.findViewById(R.id.textcharacter);
        ImageView logoImage = (ImageView) view.findViewById(R.id.app_logo);
        Button shareButton= (Button) view.findViewById(R.id.share_app);
        Button previewButton= (Button) view.findViewById(R.id.preview_app);
        Button openButton= (Button) view.findViewById(R.id.open_app);
        TextView appTextView= (TextView) view.findViewById(R.id.android_app);

        shareButton.setOnClickListener(this);
        previewButton.setOnClickListener(this);
        openButton.setOnClickListener(this);

        String logo = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl);

        String name=session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME);
        appNameTextView.setText(name);

        if(logo==null || logo.isEmpty()){
            firstCharText.setText(String.valueOf(name.charAt(0)));
            firstCharText.setVisibility(View.VISIBLE);
        }
        else if(!logo.contains("http")){
            logo = "https://"+logo;
        }
        Glide.with(context)
                .load(logo)
                .placeholder(getResources().getDrawable(R.drawable.studio_architecture))
                .into(logoImage);

        if(type.equals("android")){
            appTextView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.android_green), null, null, null );
            appTextView.setText(getResources().getString(R.string.android_app));
        }else{
            appTextView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ios_icon_black), null, null, null );
            appTextView.setText(getResources().getString(R.string.ios_app));
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.share_app:
                break;
            case R.id.open_app:
                break;
            case R.id.preview_app:
                break;
            default:
                break;
        }
    }
}
