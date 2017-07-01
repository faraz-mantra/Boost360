package com.nowfloats.riachatsdk.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
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

        LinearLayout llPositive = (LinearLayout) mView.findViewById(R.id.llPositive);
        LinearLayout llNegative = (LinearLayout) mView.findViewById(R.id.llNegative);

        TextView tvPositive = (TextView) mView.findViewById(R.id.tvPositive);
        TextView tvNegative = (TextView) mView.findViewById(R.id.tvNegative);
        TextView tvTitle = (TextView) mView.findViewById(R.id.tvTitle);
        TextView tvContent = (TextView) mView.findViewById(R.id.tvContent);

        switch (from) {
            case SKIP:
                tvTitle.setVisibility(View.GONE);

                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) tvContent
                        .getLayoutParams();

                mlp.setMargins(0, 130, 0, 0);

                tvContent.setText(getActivity().getString(R.string.server_error_content));
                tvPositive.setText(getActivity().getString(R.string.sure_lets_go));
                llNegative.setVisibility(View.GONE);

                llPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mResultListener.createmysite();
                    }
                });

                break;
            case CREATE_MY_SITE:

                tvTitle.setText(getActivity().getString(R.string.edit_title));
                tvContent.setText(getActivity().getString(R.string.edit_content));
                tvNegative.setText(getActivity().getString(R.string.yes_i_want_to_make_changes));
                tvPositive.setText(getActivity().getString(R.string.no_i_dont_want_to_change_details));

                llPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mResultListener.dismissPopup();
                    }
                });

                llNegative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mResultListener.createmysite();
                    }
                });


                break;
        }

        try {
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.place_pick_dialog_bg);
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

        void dismissPopup();
    }


}
