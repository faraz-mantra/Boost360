package com.nowfloats.riachatsdk.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nowfloats.riachatsdk.R;

/**
 * Created by NowFloats on 27-03-2017 by Romio Ranjan Jena.
 */

public class CustomDialogFragment extends DialogFragment {

    private static final String ARG_FROM = "from";

    private DialogFrom from;

    private OnResultReceive mResultListener;

    public enum DialogFrom {
        CREATE_MY_SITE,
        SKIP,
        BACK_PRESS,
        BACK_PRESS_LOGIN,
        NO_INTERNET,
        COUNTRY_CODE,
        COUNTRY_CODE_SKIP,
        SKIP_LOGIN,
        DEFAULT
    }

    public static CustomDialogFragment newInstance(final DialogFrom dialogFrom) {

        CustomDialogFragment fragment = new CustomDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FROM, dialogFrom);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            from = (DialogFrom) getArguments().get(ARG_FROM);
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        getDialog().getWindow()
//                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.dialog_error, container, false);
        FrameLayout llRia = (FrameLayout) mView.findViewById(R.id.ivRia);
        llRia.bringToFront();

        LinearLayout llHorizontal = (LinearLayout) mView.findViewById(R.id.llHorizontal);
        LinearLayout llVertical = (LinearLayout) mView.findViewById(R.id.llVertical);

        LinearLayout llPositive = (LinearLayout) mView.findViewById(R.id.llPositive);
        LinearLayout llNegative = (LinearLayout) mView.findViewById(R.id.llNegative);

        LinearLayout llPos = (LinearLayout) mView.findViewById(R.id.llPos);
        LinearLayout llNeg = (LinearLayout) mView.findViewById(R.id.llNeg);

        TextView tvPositive = (TextView) mView.findViewById(R.id.tvPositive);
        TextView tvNegative = (TextView) mView.findViewById(R.id.tvNegative);

        TextView tvPos = (TextView) mView.findViewById(R.id.tvPos);
        TextView tvNeg = (TextView) mView.findViewById(R.id.tvNeg);

        TextView tvTitle = (TextView) mView.findViewById(R.id.tvTitle);
        TextView tvContent = (TextView) mView.findViewById(R.id.tvContent);

        ImageView ivHorizontalSep = (ImageView) mView.findViewById(R.id.ivHorizontalSep);
        ImageView ivHorizontal = (ImageView) mView.findViewById(R.id.ivHorizontal);

        switch (from) {
            case SKIP:
                tvTitle.setVisibility(View.GONE);

                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) tvContent
                        .getLayoutParams();

                mlp.setMargins(0, 130, 0, 0);
                setCancelable(false);
                tvContent.setText(getActivity().getString(R.string.server_error_content));
                tvPositive.setText(getActivity().getString(R.string.sure_lets_go));
                llNegative.setVisibility(View.GONE);

                llPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mResultListener == null) return;
                        mResultListener.createmysite();
                    }
                });

                break;
            case NO_INTERNET:

                setCancelable(false);
                tvTitle.setText(getActivity().getString(R.string.cannot_reach_network));
                tvContent.setText(getActivity().getString(R.string.cannot_reach_network_msg));
                tvPositive.setText(getActivity().getString(R.string.ok));
                llNegative.setVisibility(View.GONE);

                llPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().finish();
                    }
                });

                break;
            case CREATE_MY_SITE:

                tvTitle.setText(getActivity().getString(R.string.edit_title));
                tvContent.setText(getActivity().getString(R.string.edit_content));
                tvNegative.setText(getActivity().getString(R.string.yes_i_want_to_make_changes));
                tvPositive.setText(getActivity().getString(R.string.no_i_dont_want_to_change_details));
                setCancelable(false);

                llPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mResultListener == null) return;
                        mResultListener.dismissPopup();
                    }
                });

                llNegative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mResultListener == null) return;
                        mResultListener.createmysite();
                    }
                });


                break;
            case BACK_PRESS:

                llHorizontal.setVisibility(View.VISIBLE);
                llVertical.setVisibility(View.GONE);

                ivHorizontalSep.setVisibility(View.VISIBLE);

                tvTitle.setText(getActivity().getString(R.string.alert));
                tvContent.setText(getActivity().getString(R.string.you_will_lose_info));
                tvNeg.setText(getActivity().getString(R.string.exit));
                tvPos.setText(getActivity().getString(R.string.cancel));
                setCancelable(false);

                llPos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mResultListener == null) return;
                        mResultListener.dismissPopup();
                    }
                });

                llNeg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mResultListener == null) return;
                        mResultListener.finishActivity();

                    }
                });


                break;
            case BACK_PRESS_LOGIN:

                llHorizontal.setVisibility(View.VISIBLE);
                llVertical.setVisibility(View.GONE);

                ivHorizontalSep.setVisibility(View.VISIBLE);

                tvTitle.setText(getActivity().getString(R.string.leave_or_login));
                tvContent.setText(getActivity().getString(R.string.your_website_has_been_created_through));
                tvNeg.setText(getActivity().getString(R.string.leave));
                tvPos.setText(getActivity().getString(R.string.login));
                setCancelable(true);

                llPos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mResultListener == null ) return;
                        mResultListener.navigateToHome();
                    }
                });

                llNeg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mResultListener == null ) return;
                        mResultListener.finishActivity();

                    }
                });


                break;
            case SKIP_LOGIN:

                llHorizontal.setVisibility(View.VISIBLE);
                llVertical.setVisibility(View.GONE);

                ivHorizontalSep.setVisibility(View.VISIBLE);

                tvTitle.setText(getActivity().getString(R.string.cant_wait_to_see_your_dashboard));
                tvTitle.setVisibility(View.INVISIBLE);
                tvContent.setText(getActivity().getString(R.string.since_you_are_eager_to_manage));
                tvNeg.setText(getActivity().getString(R.string.skip_login));
                tvPos.setText(getActivity().getString(R.string.continue_chat));
                setCancelable(false);

                llPos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mResultListener == null ) return;
                        mResultListener.dismissPopup();
                    }
                });

                llNeg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mResultListener == null ) return;
                        mResultListener.navigateToHome();

                    }
                });


                break;
            case COUNTRY_CODE:

                llHorizontal.setVisibility(View.VISIBLE);
                llVertical.setVisibility(View.GONE);

                ivHorizontalSep.setVisibility(View.VISIBLE);

                tvTitle.setText(getActivity().getString(R.string.change_country_code));
                tvContent.setText(getActivity().getString(R.string.country_code_msg));
                tvNeg.setText(getActivity().getString(R.string.skip));

                llNeg.setVisibility(View.GONE);
                tvPos.setText(getActivity().getString(R.string.okay));
                setCancelable(false);

                llNegative.setVisibility(View.GONE);
                ivHorizontal.setVisibility(View.GONE);

                llPos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mResultListener == null ) return;
                        mResultListener.dismissPopup();
                    }
                });

                llNeg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mResultListener == null ) return;
                        mResultListener.dismissPopup();

                    }
                });


                break;
            case COUNTRY_CODE_SKIP:

                llHorizontal.setVisibility(View.VISIBLE);
                llVertical.setVisibility(View.GONE);

                ivHorizontalSep.setVisibility(View.VISIBLE);

                tvTitle.setText(getActivity().getString(R.string.change_country_code));
                tvContent.setText(getActivity().getString(R.string.country_code_msg));
                tvNeg.setText(getActivity().getString(R.string.skip));
                tvNeg.setAllCaps(false);
                tvPos.setText(getActivity().getString(R.string.continue_details));
                setCancelable(false);

                llPos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mResultListener == null ) return;
                        mResultListener.dismissPopup();
                    }
                });

                llNeg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mResultListener == null ) return;
                        mResultListener.skipNode();

                    }
                });


                break;
        }

        try {
            if(getDialog().getWindow() != null) {
                getDialog().getWindow().setBackgroundDrawableResource(R.drawable.place_pick_dialog_bg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return mView;
    }

    public void setResultListener(OnResultReceive onResultReceive) {
        mResultListener = onResultReceive;
    }

    public interface OnResultReceive {
        void createmysite();

        void navigateToHome();

        void navigateToSignup();

        void dismissPopup();

        void skipNode();

        void finishActivity();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }
}
