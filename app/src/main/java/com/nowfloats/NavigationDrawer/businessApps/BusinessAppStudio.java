package com.nowfloats.NavigationDrawer.businessApps;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.nowfloats.CustomWidget.MaterialProgressBar;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.BusinessAppApis;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Admin on 12/28/2016.
 */
public class BusinessAppStudio extends Fragment implements View.OnClickListener {

    private String type;
    private Context context;
    UserSessionManager session;
    public static Fragment getInstance(String type){
        Fragment frag=new BusinessAppStudio();
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
        return inflater.inflate(R.layout.fragment_business_app_madman_studio,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if(!isAdded()){
            return;
        }

        session=new UserSessionManager(context,getActivity());

        Button previewButton= (Button) view.findViewById(R.id.preview_button);
        Button getAppButton= (Button) view.findViewById(R.id.get_app_button);
        ImageView iconImage= (ImageView) view.findViewById(R.id.imgview_icon_type);
        TextView freeText= (TextView) view.findViewById(R.id.tv_free);
        TextView nameTextView= (TextView) view.findViewById(R.id.app_name);
        LinearLayout comming_soon= (LinearLayout) view.findViewById(R.id.comming_soon);
        LinearLayout buttonLayout= (LinearLayout) view.findViewById(R.id.button_layout);

        previewButton.setOnClickListener(this);
        getAppButton.setOnClickListener(this);

        String name=session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME);
        nameTextView.setText(name);

        if(type.equals("android")){
            getAppButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.android_icon_white), null, null, null );
            getAppButton.setText(getResources().getString(R.string.get_android_app));
            previewButton.setText(getResources().getString(R.string.android_app_preview));
            iconImage.setImageDrawable(getResources().getDrawable(R.drawable.android_green_padding));
        }
        else {
            freeText.setVisibility(View.GONE);
            buttonLayout.setVisibility(View.GONE);
            comming_soon.setVisibility(View.VISIBLE);
            getAppButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ios_icon_white), null, null, null );
            getAppButton.setText(getResources().getString(R.string.get_ios_app));
            previewButton.setText(getResources().getString(R.string.ios_app_preview));
            iconImage.setImageDrawable(getResources().getDrawable(R.drawable.ios_icon_black));
        }
    }

    @Override
    public void onClick(View view) {
        final BusinessAppPreview frag= (BusinessAppPreview) getParentFragment();
        switch (view.getId()){
            case R.id.preview_button:
                frag.showScreenShots();
                break;
            case R.id.get_app_button:

                if(frag!=null) {
                    if (type.equals("android")) {
                        MaterialProgressBar.startProgressBar(getActivity(),"requesting for Business App",false);
                        BusinessAppApis.AppApis apis=BusinessAppApis.getRestAdapter();
                        apis.getGenerate(Constants.clientId, session.getFPID(), new Callback<JsonObject>() {
                            @Override
                            public void success(JsonObject s, Response response) {
                                MaterialProgressBar.dismissProgressBar();
                                if(s==null || response.getStatus()!=200){
                                    return;
                                }
                                Log.v("ggg",s.toString());
                                String status = s.get("Status").getAsString();
                                if(status!= null && status.equals("1")){
                                    BusinessAppPreview frag= (BusinessAppPreview) getParentFragment();
                                    frag.addAndroidFragment(BusinessAppPreview.SHOW_DEVELOPMENT,"");
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Log.v("ggg",error+"");
                                MaterialProgressBar.dismissProgressBar();
                                Methods.showSnackBarNegative(getActivity(),"Problem to start build");
                            }
                        });
                    }
                }
                break;
            default:
                break;
        }
    }
}
