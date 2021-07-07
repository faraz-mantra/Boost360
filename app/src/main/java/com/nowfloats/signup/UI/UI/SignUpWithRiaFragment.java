package com.nowfloats.signup.UI.UI;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nowfloats.signup.UI.AnimationTool;
import com.nowfloats.signup.UI.AnimationType;
import com.thinksity.R;

import static com.nowfloats.signup.UI.AnimationType.SLIDE_DOWN;
import static com.nowfloats.signup.UI.AnimationType.SLIDE_UP;


public class SignUpWithRiaFragment extends Fragment implements AnimationTool.AnimationListener {

    private LoginAndSignUpFragment.OnFragmentInteraction mFragmentInteraction;

    private ImageView ivBack;

    private LinearLayout llRia, llStart, llContent, ivStart, ivRia, ivRiaZoomLeft, ivRiaZoomRight, ivRiaZoomTop;

    private DisplayMetrics displayMetrics;

    private TextView tvRia, tvRiaMessage;

    private boolean isBackPress = false;

    private AnimationTool animationTool;

    private String navigateTo = "";
    private AnimationSet animationSet = null;

    public SignUpWithRiaFragment() {
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
        animationTool = new AnimationTool(requireActivity());
        animationTool.setVisbilityStatus(isBackPress);
        animationTool.setListener(this);
    }

    private void bindListeners() {


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo = "back";
                reverseAnimation();

            }
        });

        ivStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo = "chatactivity";
                mFragmentInteraction.OnInteraction(navigateTo);
            }
        });
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

    private void reverseAnimation() {

        isBackPress = true;
        ivBack.setVisibility(View.INVISIBLE);
        llStart.setVisibility(View.GONE);
        ivStart.setVisibility(View.GONE);
        animationTool.setVisbilityStatus(isBackPress);
        ivRiaZoomLeft.setVisibility(View.INVISIBLE);
        ivRiaZoomRight.setVisibility(View.INVISIBLE);
        ivRiaZoomTop.setVisibility(View.INVISIBLE);
        animationTool.setAnimationType(SLIDE_UP)
                .duration(100)
                .repeat(0)
                .interpolate(new AccelerateInterpolator())
                .playOn(llRia);
    }

    /*
     * used this only for Ria Specific case as we need to apply same animation for multiple-views
     * used this variable in callback
     */

    private void setPositions() {


        /*
         * Ria Image Configurations
         */
        int height = (displayMetrics.heightPixels * 40) / 100;
        int width = (displayMetrics.widthPixels * 40) / 100;
        int topMargin = (displayMetrics.heightPixels * 2) / 100;
        int leftMargin = (displayMetrics.widthPixels * 5) / 100;
        int bottomMargin = (displayMetrics.widthPixels * 5) / 100;
        if (width > height) {
            width = height;
        }

        RelativeLayout.LayoutParams llParams = new RelativeLayout.LayoutParams(
                width, width);
        llParams.setMargins(leftMargin, topMargin, 0, bottomMargin);
        ivRia.setLayoutParams(llParams);


        RelativeLayout.LayoutParams llParams1 = new RelativeLayout.LayoutParams(
                width, width);
        llParams1.setMargins(leftMargin, topMargin, 0, 0);
        ivRiaZoomLeft.setLayoutParams(llParams1);

        RelativeLayout.LayoutParams llParams2 = new RelativeLayout.LayoutParams(
                width, width);
        llParams2.setMargins(leftMargin, topMargin, leftMargin, 0);
        ivRiaZoomRight.setLayoutParams(llParams2);


        RelativeLayout.LayoutParams llParams3 = new RelativeLayout.LayoutParams(
                width, width);
        llParams3.setMargins(leftMargin, topMargin, 0, 0);
        ivRiaZoomTop.setLayoutParams(llParams3);

        ivRia.bringToFront();


//        /*
//         * Ria Text Configurations
//         */
//
        leftMargin = (displayMetrics.widthPixels * 10) / 100;
//        height = (displayMetrics.heightPixels * 40) / 100;
        LinearLayout.LayoutParams rlContentParams = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llContent.setPadding(leftMargin, 20, 0, 0);
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
        rlStartParams.setMargins((displayMetrics.heightPixels * 82) / 100, 0, (displayMetrics.heightPixels * 8) / 100, (displayMetrics.heightPixels * 8) / 100);
        ivStart.setLayoutParams(rlStartParams);


        /*
         * starting first animation
         */

        animationTool.setAnimationType(SLIDE_DOWN)
                .duration(200)
                .repeat(0)
                .interpolate(new AccelerateInterpolator())
                .playOn(llRia);
    }

    @Override
    public void onAnimationStart(AnimationType animationType) {
        switch (animationType) {
            case SLIDE_DOWN:
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        llStart.setVisibility(View.VISIBLE);
                    }
                }, 350);
        }

    }

    @Override
    public void onAnimationEnd(AnimationType animationType) {
        if (isBackPress) {

            switch (animationType) {
                case SLIDE_RIA_RIGHT:
                    ivRiaZoomLeft.setVisibility(View.INVISIBLE);
                    ivRiaZoomRight.setVisibility(View.INVISIBLE);
                    ivRiaZoomTop.setVisibility(View.INVISIBLE);
                    animationTool.setAnimationType(SLIDE_UP)
                            .duration(100)
                            .repeat(0)
                            .interpolate(new DecelerateInterpolator())
                            .playOn(llRia);
                    break;
                case SLIDE_UP:
                    mFragmentInteraction.OnInteraction(navigateTo);
                    break;
            }
        } else {

            switch (animationType) {
                case ZOOM_IN:
//                    animationTool.setAnimationType(SLIDE_RIGHT)
//                            .duration(100)
//                            .repeat(0)
//                            .interpolate(new AccelerateInterpolator())
//                            .playOn(ivStart);

                case SLIDE_DOWN:
                    animationSet = new AnimationSet(true);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
                            animation.setDuration(200);
                            ivStart.startAnimation(animation);

                            ivStart.setVisibility(View.VISIBLE);
                            leftAnimation();
                            rightAnimation();
                            topAnimation();
                            animationSet.start();
                        }
                    }, 200);
//                    llStart.setVisibility(View.VISIBLE);
                    break;
                    /*animationTool.setAnimationType(ZOOM_IN)
                            .duration(100)
                            .repeat(0)
                            .interpolate(new AccelerateInterpolator())
                            .playOn(llStart);*/

                case SLIDE_RIGHT:
                    break;

            }
        }
    }

    private void leftAnimation() {

        ivRiaZoomLeft.setVisibility(View.VISIBLE);
        Animation leftAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.ria_circle_left_out);
        ivRiaZoomLeft.setAnimation(leftAnimation);
        leftAnimation.setAnimationListener(new Animation.AnimationListener() {
            int i = 0;

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                if (isBackPress) {
                    animation.cancel();
                } else {
                    if (i == 0) {
                        i = 1;
                        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.ria_circle_left_in);
                    } else {
                        i = 0;
                        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.ria_circle_left_out);
                    }
                    ivRiaZoomLeft.setAnimation(animation);
                    animation.setAnimationListener(this);
                    animation.start();
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animationSet.addAnimation(leftAnimation);
//        leftAnimation.start();

    }

    private void rightAnimation() {

        ivRiaZoomRight.setVisibility(View.VISIBLE);
        Animation rightAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.ria_circle_right_in);
        ivRiaZoomRight.setAnimation(rightAnimation);
        rightAnimation.setAnimationListener(new Animation.AnimationListener() {
            int i = 0;

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                if (isBackPress) {
                    animation.cancel();
                } else {

                    if (i == 0) {
                        i = 1;
                        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.ria_circle_right_out);
                    } else {
                        i = 0;
                        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.ria_circle_right_in);
                    }
                    ivRiaZoomRight.setAnimation(animation);
                    animation.setAnimationListener(this);
                    animation.start();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animationSet.addAnimation(rightAnimation);

    }

    private void topAnimation() {

        ivRiaZoomTop.setVisibility(View.VISIBLE);
        Animation topAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.ria_circle_top_in);
        ivRiaZoomTop.setAnimation(topAnimation);
        topAnimation.setAnimationListener(new Animation.AnimationListener() {
            int i = 0;

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                if (isBackPress) {
                    animation.cancel();
                } else {
                    if (i == 0) {
                        i = 1;
                        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.ria_circle_top_out);
                    } else {
                        i = 0;
                        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.ria_circle_top_in);
                    }
                    ivRiaZoomTop.setAnimation(animation);
                    animation.setAnimationListener(this);
                    animation.start();
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
//        topAnimation.start();
        animationSet.addAnimation(topAnimation);
    }

    public void onDetach() {
        super.onDetach();
    }

    public void onBackPressed() {
        ivBack.performClick();
    }
}