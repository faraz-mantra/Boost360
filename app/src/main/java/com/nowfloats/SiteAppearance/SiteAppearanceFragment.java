package com.nowfloats.SiteAppearance;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.KitsuneApi;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.biubiubiu.justifytext.library.JustifyTextView;

public class SiteAppearanceFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    UserSessionManager session;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Map<String, String> mfeedBack = new HashMap<>();

    //private CompoundButton.OnCheckedChangeListener checkedChangeListener;
    //private Switch svKitsune;
    private TextView tvHelpHeader, tvHelpFooter, tvKitsuneSwitch;
    private JustifyTextView tvHelpBody;
    private CardView cvKitsuneSwitch, cvRevertBack;
    private ImageView ivKitsuneSwitch;
    private LinearLayout linearLayout;
    private Context mContext;
    //private OnFragmentInteractionListener mListener;

    /*public SiteAppearanceFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static SiteAppearanceFragment newInstance(String param1, String param2) {
        SiteAppearanceFragment fragment = new SiteAppearanceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        session = new UserSessionManager(getContext(), getActivity());
        View view = inflater.inflate(R.layout.fragment_site_appearance, container, false);
        //svKitsune = (Switch)view.findViewById(R.id.sv_kitsune);
        cvKitsuneSwitch = view.findViewById(R.id.cv_kitsune_switch);
        cvRevertBack = view.findViewById(R.id.cv_revert_back);
        tvKitsuneSwitch = view.findViewById(R.id.tv_kitsune_switch);
        tvHelpHeader = view.findViewById(R.id.tv_help_header);
        tvHelpBody = view.findViewById(R.id.tv_help_body);
        tvHelpFooter = view.findViewById(R.id.tv_help_footer);
        linearLayout = view.findViewById(R.id.child_layout);
        //btnLearnMore = (Button)view.findViewById(R.id.btn_learn_kitsune);
        ivKitsuneSwitch = view.findViewById(R.id.iv_kitsune_switch);
      /*  if(session.getWebTemplateType().equals("6")){
            tvHelpHeader.setText(getResources().getString(R.string.conv_sa_title));
            tvHelpBody.setText(getResources().getString(R.string.conv_sa_body));
            tvHelpFooter.setVisibility(View.GONE);
            *//*svKitsune.setChecked(true);
            btnLearnMore.setVisibility(View.VISIBLE);
            tvKitsune.setText("REVERT TO OLD VERSION");*//*

            tvKitsuneSwitch.setText(getString(R.string.learn_more_about_version));
            tvKitsuneSwitch.setTextColor(Color.parseColor("#808080"));
            ivKitsuneSwitch.setBackgroundColor(Color.parseColor("#00000000"));
            ivKitsuneSwitch.setImageDrawable(getResources().getDrawable(R.drawable.ic_learn_more));

        }*/
        cvKitsuneSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   if(session.getWebTemplateType().equals("6")){
                    String url = "https://nowfloats.com/whychange";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }else {*/
                final ProgressDialog pg = ProgressDialog.show(getActivity(), "", getString(R.string.wait_for_new_look));
                new KitsuneApi(session.getFpTag()).setResultListener(new KitsuneApi.ResultListener() {
                    @Override
                    public void onResult(String response, boolean isError) {
                        pg.dismiss();
                        if (response.equals("true") && !isError) {
                            Methods.showSnackBarPositive(getActivity(), getString(R.string.your_website_appearance_changed));
                            tvHelpHeader.setText(getResources().getString(R.string.conv_sa_title));
                            tvHelpBody.setText(getResources().getString(R.string.conv_sa_body));
                            tvHelpFooter.setVisibility(View.GONE);
                            cvKitsuneSwitch.setVisibility(View.GONE);
                            tvKitsuneSwitch.setText(getString(R.string.learn_more_about_version));
                            tvKitsuneSwitch.setTextColor(Color.parseColor("#808080"));
                            ivKitsuneSwitch.setBackgroundColor(Color.parseColor("#00000000"));
                            ivKitsuneSwitch.setImageDrawable(getResources().getDrawable(R.drawable.ic_learn_more));
                            session.storeFpWebTempalteType("6");
                        } else {
                            Methods.showSnackBarNegative(getActivity(), getString(R.string.can_not_change_appearance));
                        }
                    }
                }).enableKitsune();
            }
        });
        cvRevertBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFeedBackDialog();
            }
        });
        //setOldDesignVisibility();
        return view;
    }

 /*   private void setOldDesignVisibility() {
        if(Long.parseLong(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CREATED_ON).split("\\(")[1].split("\\)")[0])/1000 > 1470614400){
            cvRevertBack.setVisibility(View.GONE);
        }else {
            cvRevertBack.setVisibility(View.VISIBLE);
        }
    }*/

    @Override
    public void onResume() {
        super.onResume();

        if (mContext instanceof HomeActivity && HomeActivity.headerText != null)
            HomeActivity.headerText.setText(getResources().getString(R.string.side_panel_site_appearance));
    }

    private void showFeedBackDialog() {
        //lfkvljgf
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = getActivity().getLayoutInflater().inflate(R.layout.feedback_kitsune_layout, null);
        final EditText et = v.findViewById(R.id.et_other_reason);
        final CheckBox cbOtherReasons = v.findViewById(R.id.cb_other_reasons);
        cbOtherReasons.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    et.setVisibility(View.VISIBLE);
                } else {
                    et.setVisibility(View.GONE);
                }
            }
        });

        final List<CheckBox> checkBoxList = new ArrayList<>();
        checkBoxList.add(v.findViewById(R.id.cb_old_theme));
        checkBoxList.add(v.findViewById(R.id.cb_customer_old_theme));
        checkBoxList.add(v.findViewById(R.id.cb_widgets_old_theme));
        checkBoxList.add(v.findViewById(R.id.cb_not_paid_for));
        checkBoxList.add(cbOtherReasons);
        final AlertDialog dialog = builder.setView(v).setCancelable(false).create();
        v.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Write Code for submitting the feedback and disabling kitsune
                //Also set the offline webtemplate to 4 in preference
                if (cbOtherReasons.isChecked() && Util.isNullOrEmpty(et.getText().toString().trim())) {
                    Methods.showSnackBarNegative(getActivity(), getString(R.string.fill_reasons));
                    return;
                }
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                JSONArray array = new JSONArray();
                String[] feedbackContent = getResources().getStringArray(R.array.kitsune_feedback);
                boolean checkBoxFlag = false;
                for (CheckBox cb : checkBoxList) {
                    if (cb.isChecked()) {
                        checkBoxFlag = true;
                        int index = checkBoxList.indexOf(cb);
                        if (index == 4) {
                            JSONObject obj = new JSONObject();
                            try {
                                obj.put("Key", "FEEDBACK_CONTENT");
                                obj.put("Value", et.getText().toString().trim());
                                array.put(obj);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            JSONObject obj = new JSONObject();
                            try {
                                obj.put("Key", "FEEDBACK_CONTENT");
                                obj.put("Value", feedbackContent[index]);
                                array.put(obj);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if (!checkBoxFlag) {
                    Methods.showSnackBarNegative(getActivity(), getString(R.string.check_any_checkbox));
                    return;
                }
                submitFeedBack(array);
            }
        });
        v.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void submitFeedBack(JSONArray array) {
        final ProgressDialog pd = ProgressDialog.show(getActivity(), "", getString(R.string.wait_while_reverting));
        new KitsuneApi(session.getFpTag()).setResultListener(new KitsuneApi.ResultListener() {
            @Override
            public void onResult(String response, boolean isError) {
                pd.dismiss();
                if (!isError) {
                    if (response.equals("true")) {
                        Methods.showSnackBarPositive(getActivity(), getString(R.string.successfully_revert));
                        tvKitsuneSwitch.setText(getString(R.string.upgrade_now));
                        tvKitsuneSwitch.setTextColor(getResources().getColor(R.color.primaryColor));
                        ivKitsuneSwitch.setImageDrawable(getResources().getDrawable(R.drawable.ic_enable_kitsune));
                        ivKitsuneSwitch.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                        session.storeFpWebTempalteType("4");
                        cvRevertBack.setVisibility(View.GONE);
                        tvHelpHeader.setText(getResources().getString(R.string.site_appearance_title));
                        tvHelpBody.setText(getResources().getString(R.string.site_appearance_text));
                        tvHelpFooter.setVisibility(View.VISIBLE);
                        tvHelpFooter.setText(getResources().getString(R.string.site_appearance_warn));
                        /*
                        tvHelpHeader.setText(getResources().getString(R.string.conv_sa_title));
                                    tvHelpBody.setText(getResources().getString(R.string.conv_sa_body));
                                    tvHelpFooter.setVisibility(View.GONE);
                                    btnLearnMore.setVisibility(View.VISIBLE);
                                    tvKitsune.setText("REVERT TO OLD VERSION");
                                    setOldDesignVisibility();
                                    session.storeFpWebTempalteType("6");
                         */
                    } else {
                        Methods.showSnackBarNegative(getActivity(), getString(R.string.failed_to_revert));
                    }
                } else {
                    Methods.showSnackBarNegative(getActivity(), getString(R.string.failed_to_revert));
                }
            }
        }).disablekitsune(array.toString());

    }

    // TODO: Rename method, update argument and hook method into UI event

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

}
