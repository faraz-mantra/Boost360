package com.nowfloats.signup.UI.UI;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nowfloats.signup.UI.AnimationTool;
import com.nowfloats.signup.UI.AnimationType;
import com.thinksity.R;

import static com.nowfloats.signup.UI.AnimationType.RIA_ZOOM_IN;
import static com.nowfloats.signup.UI.AnimationType.RIA_ZOOM_OUT;
import static com.nowfloats.signup.UI.AnimationType.SLIDE_DOWN;
import static com.nowfloats.signup.UI.AnimationType.SLIDE_LEFT;
import static com.nowfloats.signup.UI.AnimationType.SLIDE_RIA_LEFT;
import static com.nowfloats.signup.UI.AnimationType.SLIDE_RIA_RIGHT;
import static com.nowfloats.signup.UI.AnimationType.SLIDE_RIGHT;
import static com.nowfloats.signup.UI.AnimationType.SLIDE_UP;
import static com.nowfloats.signup.UI.AnimationType.ZOOM_IN;


public class SignUpWithRiaFragmentCopy extends Fragment implements AnimationTool.AnimationListener {

    private LoginAndSignUpFragment.OnFragmentInteraction mFragmentInteraction;

    private ImageView ivBack;

    private LinearLayout llRia, llStart, llContent, ivStart, ivRia, ivRiaZoomLeft, ivRiaZoomRight, ivRiaZoomTop;

    private DisplayMetrics displayMetrics;

    private TextView tvRia, tvRiaMessage;

    private boolean isBackPress = false;

    private AnimationTool animationTool;

    private String navigateTo = "";
    private int stepCount = 0;
    private boolean isCalled = false;

    public SignUpWithRiaFragmentCopy() {
    }

    public static SignUpWithRiaFragment newInstance() {
        SignUpWithRiaFragment fragment = new SignUpWithRiaFragment();
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof LoginAndSignUpFragment.OnFragmentInteraction) {
            mFragmentInteraction = (LoginAndSignUpFragment.OnFragmentInteraction) activity;
        } else {
            throw new RuntimeException("Interface not implemented");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_ria_sign_up, container, false);
        initializeControls(v);
        bindListeners();
        setPositions();
        return v;
    }

    private void initializeControls(View v) {
        displayMetrics = getActivity().getResources().getDisplayMetrics();
        ivStart = (LinearLayout) v.findViewById(R.id.ivStart);
        ivRia = (LinearLayout) v.findViewById(R.id.ivRia);
        ivBack = (ImageView) v.findViewById(R.id.ivBack);
        llContent = (LinearLayout) v.findViewById(R.id.llContent);
        llRia = (LinearLayout) v.findViewById(R.id.llRia);
        llStart = (LinearLayout) v.findViewById(R.id.llStart);
        ivRiaZoomRight = (LinearLayout) v.findViewById(R.id.ivRiaZoomRight);
        ivRiaZoomLeft = (LinearLayout) v.findViewById(R.id.ivRiaZoomLeft);
        ivRiaZoomTop = (LinearLayout) v.findViewById(R.id.ivRiaZoomTop);
        tvRia = (TextView) v.findViewById(R.id.tvRia);
        tvRiaMessage = (TextView) v.findViewById(R.id.tvRiaMessage);

        /*
         * Separate class for applying animation
         * Mandatory Fields:
         *    Listener--> After applying animation if you want callback set listener
         *    VisbilityStatus--> This hides or visible view after/before applying animation
         *
         */
        createAnimation();
    }

    private void createAnimation() {
        animationTool = new AnimationTool(getActivity());
        animationTool.setVisbilityStatus(isBackPress);
        animationTool.setListener(this);
    }

    /*
     * ***************** Set positions ********************
     *
     * To support for multiple devices we need to set height,width dynamically
     *
     * 1.Ria Image:
     *
     *     % in terms of device width,height
     *     width,height : 40%
     *     left-margin  : 5%
     *     top-margin   : 10%
     *
     *     RiaImageCircles adjusted left,top,right margins with same width mentioned above for RiaImage.
     *
     */

    private void bindListeners() {


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo = "back";
                reverseAnimation();

            }
        });

        llStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo = "chatactivity";
                reverseAnimation();
            }
        });
    }

    /*
     * used this only for Ria Specific case as we need to apply same animation for multiple-views
     * used this variable in callback
     */

    private void reverseAnimation() {

        isBackPress = true;
        ivBack.setVisibility(View.GONE);
        animationTool.setVisbilityStatus(isBackPress);
        animationTool.setAnimationType(SLIDE_RIA_RIGHT)
                .duration(100)
                .repeat(0)
                .interpolate(new AccelerateInterpolator())
                .playOn(ivStart);
    }

    private void setPositions() {


        /*
         * Ria Image Configurations
         */
        int height = (displayMetrics.heightPixels * 40) / 100;
        int width = (displayMetrics.widthPixels * 40) / 100;
        int topMargin = (displayMetrics.heightPixels * 10) / 100;
        int leftMargin = (displayMetrics.widthPixels * 5) / 100;
        if (width > height) {
            width = height;
        }

        RelativeLayout.LayoutParams llParams = new RelativeLayout.LayoutParams(
                width, width);
        llParams.setMargins(leftMargin, topMargin, 0, 0);
        ivRia.setLayoutParams(llParams);


        RelativeLayout.LayoutParams llParams1 = new RelativeLayout.LayoutParams(
                width, width);
        llParams1.setMargins(0, topMargin, 0, 0);
        ivRiaZoomLeft.setLayoutParams(llParams1);

        RelativeLayout.LayoutParams llParams2 = new RelativeLayout.LayoutParams(
                width, width);
        llParams2.setMargins(leftMargin + 50, topMargin + 20, 0, 0);
        ivRiaZoomRight.setLayoutParams(llParams2);

        RelativeLayout.LayoutParams llParams3 = new RelativeLayout.LayoutParams(
                width, width);
        llParams3.setMargins(leftMargin, topMargin - 50, 0, 0);
        ivRiaZoomTop.setLayoutParams(llParams3);

        ivRia.bringToFront();


        /*
         * Ria Text Configurations
         */

        leftMargin = (displayMetrics.widthPixels * 10) / 100;
        height = (displayMetrics.heightPixels * 40) / 100;
        RelativeLayout.LayoutParams rlContentParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        llContent.setPadding(leftMargin, height, 0, 0);
        llContent.setClipToPadding(false);
        llContent.setLayoutParams(rlContentParams);


        /*
         * Ria yellow background Configurations
         */

        RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, (displayMetrics.heightPixels * 50) / 100);
        rlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rlParams.setMargins(40, 0, 0, 0);
        llStart.setLayoutParams(rlParams);


        /*
         * Ria start button Configurations
         */

        RelativeLayout.LayoutParams rlStartParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rlStartParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rlStartParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rlStartParams.setMargins(0, 0, (displayMetrics.heightPixels * 6) / 100, (displayMetrics.heightPixels * 6) / 100);
        ivStart.setLayoutParams(rlStartParams);


        /*
         * starting first animation
         */
        animationTool.setAnimationType(ZOOM_IN)
                .duration(100)
                .repeat(0)
                .interpolate(new AccelerateInterpolator())
                .playOn(llStart);
    }

    @Override
    public void onAnimationStart(AnimationType animationType) {

    }

    @Override
    public void onAnimationEnd(AnimationType animationType) {
        if (isBackPress) {

            switch (animationType) {
                case SLIDE_RIA_RIGHT:
                    stepCount = 0;
                    ivBack.setVisibility(View.GONE);
                    animationTool.setAnimationType(SLIDE_RIA_LEFT)
                            .duration(100)
                            .repeat(0)
                            .interpolate(new AccelerateInterpolator())
                            .playOn(tvRiaMessage);
                    break;
                case SLIDE_RIA_LEFT:
                    if (stepCount == 0) {
                        stepCount++;
                        animationTool.setAnimationType(SLIDE_RIA_LEFT)
                                .duration(100)
                                .repeat(0)
                                .interpolate(new AccelerateInterpolator())
                                .playOn(tvRia);
                    } else {
                        stepCount = 0;
                        animationTool.setAnimationType(SLIDE_UP)
                                .duration(100)
                                .repeat(0)
                                .interpolate(new AccelerateInterpolator())
                                .playOn(ivRia);
                    }
                    break;
                case SLIDE_UP:
                    if (stepCount == 0) {
                        stepCount++;

                        ivRiaZoomLeft.setVisibility(View.GONE);
                        ivRiaZoomTop.setVisibility(View.GONE);
                        ivRiaZoomRight.setVisibility(View.GONE);

                        animationTool.setAnimationType(SLIDE_UP)
                                .duration(100)
                                .repeat(0)
                                .interpolate(new AccelerateInterpolator())
                                .playOn(llRia);
                    } else {
                        stepCount = 0;
                        animationTool.setAnimationType(ZOOM_IN)
                                .duration(100)
                                .repeat(0)
                                .interpolate(new AccelerateInterpolator())
                                .playOn(llStart);
                    }
                    break;
                case ZOOM_IN:
                    if (!isCalled) {
                        isCalled = true;
                        mFragmentInteraction.OnInteraction(navigateTo);
                    }
                    break;
            }
        } else {

            switch (animationType) {
                case ZOOM_IN:
                    animationTool.setAnimationType(SLIDE_DOWN)
                            .duration(100)
                            .repeat(0)
                            .interpolate(new AccelerateInterpolator())
                            .playOn(llRia);
                    break;
                case SLIDE_DOWN:
                    if (stepCount == 0) {
                        stepCount++;
                        animationTool.setAnimationType(SLIDE_DOWN)
                                .duration(100)
                                .repeat(0)
                                .interpolate(new AccelerateInterpolator())
                                .playOn(ivRia);
                    } else {
                        stepCount = 0;
                        animationTool.setAnimationType(SLIDE_LEFT)
                                .duration(100)
                                .repeat(0)
                                .interpolate(new AccelerateInterpolator())
                                .playOn(tvRia);
                    }
                    break;
                case SLIDE_LEFT:
                    if (stepCount == 0) {
                        stepCount++;
                        animationTool.setAnimationType(SLIDE_LEFT)
                                .duration(100)
                                .repeat(0)
                                .interpolate(new AccelerateInterpolator())
                                .playOn(tvRiaMessage);
                    } else {
                        stepCount = 0;
                        animationTool.setAnimationType(SLIDE_RIGHT)
                                .duration(100)
                                .repeat(0)
                                .interpolate(new AccelerateInterpolator())
                                .playOn(ivStart);
                        ivBack.setVisibility(View.VISIBLE);
                    }
                    break;
                case SLIDE_RIGHT:
                case RIA_ZOOM_OUT:
                    if (!isBackPress) {

                        animationTool.setAnimationType(RIA_ZOOM_IN)
                                .duration(100)
                                .repeat(0)
                                .interpolate(new AccelerateInterpolator())
                                .playOn(ivRiaZoomLeft);
                        animationTool.setAnimationType(RIA_ZOOM_IN)
                                .duration(100)
                                .repeat(0)
                                .interpolate(new AccelerateInterpolator())
                                .playOn(ivRiaZoomRight);
                        animationTool.setAnimationType(RIA_ZOOM_IN)
                                .duration(100)
                                .repeat(0)
                                .interpolate(new AccelerateInterpolator())
                                .playOn(ivRiaZoomTop);
                    }
                    break;
                case RIA_ZOOM_IN:

                    if (!isBackPress) {

                        animationTool.setAnimationType(RIA_ZOOM_OUT)
                                .duration(100)
                                .repeat(0)
                                .interpolate(new AccelerateInterpolator())
                                .playOn(ivRiaZoomLeft);
                        animationTool.setAnimationType(RIA_ZOOM_OUT)
                                .duration(100)
                                .repeat(0)
                                .interpolate(new AccelerateInterpolator())
                                .playOn(ivRiaZoomRight);
                        animationTool.setAnimationType(RIA_ZOOM_OUT)
                                .duration(100)
                                .repeat(0)
                                .interpolate(new AccelerateInterpolator())
                                .playOn(ivRiaZoomTop);
                    }
                    break;
            }
        }
    }

    public void onDetach() {
        super.onDetach();
    }

    public void onBackPressed() {
        ivBack.performClick();
    }
}
