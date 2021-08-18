package com.nowfloats.managecustomers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.Analytics_Screen.SubscribersActivity;
import com.nowfloats.Analytics_Screen.VmnCallCardsActivity;
import com.nowfloats.Analytics_Screen.VmnNumberRequestActivity;
import com.nowfloats.Business_Enquiries.BusinessEnquiryActivity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.customerassistant.ThirdPartyQueriesActivity;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.squareup.otto.Bus;
import com.thinksity.R;

import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;

import static com.nowfloats.NavigationDrawer.HomeActivity.headerText;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageCustomerFragmentV1 extends Fragment {

    public static final String ACTION_KILL_DIALOG = "nowfloats.bubblebutton.bubble.ACTION_KILL_DIALOG";
    private static final int CI_WEBSITE = 0, FB_CHATS = 1,
            MULTI_CHANNEL_CUSTOMERS = 2;
    private static final int PERM_REQUEST_CODE_DRAW_OVERLAYS = 122;
    private SharedPreferences pref = null;
    private UserSessionManager session;
    private Activity activity;
    private RecyclerView rvManageCustomers;
    private ManageCustomerAdapter manageCustomerAdapter;
    private int customerList[] = {CI_WEBSITE, MULTI_CHANNEL_CUSTOMERS};
    private Bus bus;
    private MaterialDialog overLayDialog;
    private LinearLayout bubbleOverlay;
    private IntentFilter clickIntentFilters = new IntentFilter(ACTION_KILL_DIALOG);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_manage_customers_v1, container, false);
        pref = activity.getSharedPreferences(Constants.PREF_NAME, Activity.MODE_PRIVATE);
        bus = BusProvider.getInstance().getBus();
        session = new UserSessionManager(activity.getApplicationContext(), activity);
        customerList[0] = CI_WEBSITE;
        return mainView;
    }

    @Override
    public void onViewCreated(final View mainView, Bundle savedInstanceState) {
        super.onViewCreated(mainView, savedInstanceState);
        if (!isAdded()) return;
        rvManageCustomers = (RecyclerView) mainView.findViewById(R.id.rvManageCustomers);
        bubbleOverlay = (LinearLayout) mainView.findViewById(R.id.floating_bubble_overlay);

        final LinearLayoutManager manager = new LinearLayoutManager(getActivity()
                , LinearLayoutManager.VERTICAL, false);
        rvManageCustomers.setLayoutManager(manager);
        rvManageCustomers.setAdapter(manageCustomerAdapter = new ManageCustomerAdapter());


    }

    @Override
    public void onStop() {
        super.onStop();
        bus.unregister(this);

        /*if (getActivity() == null) return;
        if (Constants.PACKAGE_NAME.equals("com.biz2.nowfloats")) {
            getActivity().stopService(new Intent(getActivity(), BubblesService.class));
            getActivity().unregisterReceiver(clickReceiver);
        }*/
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
       /* if (Constants.PACKAGE_NAME.equals("com.biz2.nowfloats")) {
            getActivity().registerReceiver(clickReceiver, clickIntentFilters);
        }
*/
        if (manageCustomerAdapter != null)
            manageCustomerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (headerText != null)
            headerText.setText(getResources().getString(R.string.manage_customers));
    }

    private void showOverlay(ManageCustomerAdapter.ManageCustomerHolder manageCustomerHolder) {

        int cx = (manageCustomerHolder.rlRevealLayout.getLeft() + manageCustomerHolder.rlRevealLayout.getRight());
        int cy = manageCustomerHolder.rlRevealLayout.getTop();
        int radius = Math.max(manageCustomerHolder.rlRevealLayout.getWidth(), manageCustomerHolder.rlRevealLayout.getHeight());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Animator animator =
                    ViewAnimationUtils.createCircularReveal(manageCustomerHolder.rlRevealLayout, cx, cy, 0, radius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(700);

            manageCustomerHolder.rlRevealLayout.setVisibility(View.VISIBLE);
            manageCustomerHolder.rflOverLay.setBackgroundColor(Color.parseColor("#66000000"));
            animator.start();
        } else {
            Animator anim = android.view.ViewAnimationUtils.
                    createCircularReveal(manageCustomerHolder.rlRevealLayout, cx, cy, 0, radius);
            manageCustomerHolder.rlRevealLayout.setVisibility(View.VISIBLE);
            manageCustomerHolder.rflOverLay.setBackgroundColor(Color.parseColor("#66000000"));
            anim.start();
        }
    }

    private void closeOverlay(final ManageCustomerAdapter.ManageCustomerHolder manageCustomerHolder) {

        int cx = (manageCustomerHolder.rlRevealLayout.getLeft() + manageCustomerHolder.rlRevealLayout.getRight());
        int cy = manageCustomerHolder.rlRevealLayout.getTop();
        int radius = Math.max(manageCustomerHolder.rlRevealLayout.getWidth(), manageCustomerHolder.rlRevealLayout.getHeight());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            Animator anim = ViewAnimationUtils.
                    createCircularReveal(manageCustomerHolder.rlRevealLayout, cx, cy, radius, 0);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    manageCustomerHolder.rlRevealLayout.setVisibility(View.INVISIBLE);
                    manageCustomerHolder.rflOverLay.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.transparent));
                }
            });
            anim.start();

        } else {
            Animator anim = android.view.ViewAnimationUtils.
                    createCircularReveal(manageCustomerHolder.rlRevealLayout, cx, cy, radius, 0);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    manageCustomerHolder.rlRevealLayout.setVisibility(View.INVISIBLE);
                    manageCustomerHolder.rflOverLay.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.transparent));
                }
            });
            anim.start();

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PERM_REQUEST_CODE_DRAW_OVERLAYS) {
//
//            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (android.os.Build.VERSION.SDK_INT >= 23) {
//
//                        if (getActivity() != null && Settings.canDrawOverlays(getActivity())) {
//
//                            if (pref.getBoolean(Key_Preferences.HAS_SUGGESTIONS, false)) {
//                                checkCustomerAssistantService();
//                            }
//
//                            checkForAccessibility();
//                        }
//                    }
//                }
//            }, 1000);
//        }
    }

    /*
     *  Enable Customer Assistant Steps
     */

   /* private void enableCustomerAssistant() {

        MixPanelController.track(EventKeysWL.SIDE_PANEL_WHATSAPP_BUBBLE, null);
        MixPanelController.track(EventKeysWL.SIDE_PANEL_CUSTOMER_ASSISTANT, null);

        if (pref.getBoolean(Key_Preferences.HAS_SUGGESTIONS, false)) {
            if (!Methods.isMyServiceRunning(activity, CustomerAssistantService.class)) {
                activity.startService(new Intent(activity, CustomerAssistantService.class));
            }
            session.setCustomerAssistantStatus(true);
        }

        getProducts();

        if ((android.os.Build.VERSION.SDK_INT >= 23 && getActivity() != null && !Settings.canDrawOverlays(getActivity()))
                || (!Methods.isAccessibilitySettingsOn(getActivity()))) {
            session.setBubbleTime(-1);

            if (Constants.PACKAGE_NAME.equals("com.biz2.nowfloats") *//*&& "1".equals(paymentState)*//*) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkOverlay(Home_Fragment_Tab.DrawOverLay.FromHome);
                    }
                }, 1000);

            }

        } else {

            session.setBubbleStatus(true);
            if (Methods.hasOverlayPerm(getActivity())) {

                if (!Methods.isMyServiceRunning(getActivity(), CustomerAssistantService.class)) {
                    Intent bubbleIntent = new Intent(getActivity(), CustomerAssistantService.class);
                    getActivity().startService(bubbleIntent);
                }
            }

            manageCustomerAdapter.notifyDataSetChanged();

            MaterialDialog.Builder builder = new MaterialDialog.Builder(activity)
                    //.title(getString(R.string.book_a_new_domain))
                    .customView(R.layout.dialog_ca_enable, false)
//                    .positiveText(getString(R.string.ok))
                    .positiveColorRes(R.color.primaryColor);
            if (!activity.isFinishing()) {
                final MaterialDialog materialDialog = builder.show();
                materialDialog.setCanceledOnTouchOutside(false);

                View caView = materialDialog.getCustomView();
                TextView tvCrossPlatform = (TextView) caView.findViewById(R.id.tvCrossPlatform);
                LinearLayout tvBubble = (LinearLayout) caView.findViewById(R.id.llBubble);
                LinearLayout tvCustomerAssistant = (LinearLayout) caView.findViewById(R.id.llCustomerAssistant);
                View vwSeperator = (View) caView.findViewById(R.id.vwSeperator);

                if (!pref.getBoolean(Key_Preferences.HAS_SUGGESTIONS, false)) {
                    tvCustomerAssistant.setVisibility(View.GONE);
                    vwSeperator.setVisibility(View.GONE);
                }

                tvCrossPlatform.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                    }
                });
                tvBubble.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                        if (!pref.getBoolean(Key_Preferences.HAS_BUBBLE_SHARE_PRODUCTS, false)) {
                            startActivity(new Intent(getActivity(), BubbleInAppDialog.class));
                        } else {
                            MixPanelController.track(MixPanelController.WHATS_APP_DIALOG_CLICKED, null);
                            try {
                                Intent launchIntent = getActivity().getPackageManager().
                                        getLaunchIntentForPackage(DataAccessibilityServiceV8.PK_NAME_WHATSAPP);
                                startActivity(launchIntent);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(), "Problem to open WhatsApp", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                tvCustomerAssistant.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                        Intent intent = new Intent(getActivity(), CustomerAssistantActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                    }
                });

            }
        }


    }

    private void disableCustomerAssistant() {
        MixPanelController.track(EventKeysWL.SIDE_PANEL_WHATSAPP_BUBBLE_OFF, null);
        MixPanelController.track(EventKeysWL.SIDE_PANEL_CUSTOMER_ASSISTANT_OFF, null);

        session.setBubbleStatus(false);
        session.setCustomerAssistantStatus(false);
        activity.stopService(new Intent(activity, CustomerAssistantService.class));
    }

    private void getProducts() {
        HashMap<String, String> values = new HashMap<>();
        values.put("clientId", Constants.clientId);
        values.put("skipBy", "0");
        values.put("fpTag", session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));
        //invoke getProduct api
        ProductGalleryInterface productInterface = Constants.restAdapter.create(ProductGalleryInterface.class);
        productInterface.getProducts(values, new Callback<ArrayList<ProductListModel>>() {
            @Override
            public void success(ArrayList<ProductListModel> productListModels, Response response) {


                if (productListModels == null || productListModels.size() == 0 || response.getStatus() != 200) {
                    pref.edit().putBoolean(Key_Preferences.HAS_BUBBLE_SHARE_PRODUCTS, false).apply();

                    return;
                }
                pref.edit().putBoolean(Key_Preferences.HAS_BUBBLE_SHARE_PRODUCTS, true).apply();

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void checkOverlay(Home_Fragment_Tab.DrawOverLay from) {
        if (!isAdded() || getActivity() == null) {
            return;
        }

        boolean checkAccessibility = true;

//        Calendar calendar = Calendar.getInstance();
//        long oldTime = pref.getLong(Key_Preferences.SHOW_BUBBLE_TIME, -1);
//        long newTime = calendar.getTimeInMillis();
//        long diff = 3 * 24 * 60 * 60 * 1000;
//
//        if (oldTime != -1 && ((newTime - oldTime) < diff)) {
//            return;
//        } else {
        if (!Methods.hasOverlayPerm(getActivity())) {
            checkAccessibility = false;
            dialogForOverlayPath(from);
        }
//        }

        if (checkAccessibility)
            checkForAccessibility();
    }

    private void checkForAccessibility() {
        if (getActivity() == null) return;
        if (!Methods.isAccessibilitySettingsOn(getActivity())) {
            showBubble();
        }
    }

    private void showBubble() {

        pref.edit().putLong(Key_Preferences.SHOW_BUBBLE_TIME, Calendar.getInstance().getTimeInMillis()).apply();

//        if (!pref.getBoolean(Key_Preferences.SHOW_BUBBLE_COACH_MARK, false)) {
        addOverlay();
        pref.edit().putBoolean(Key_Preferences.SHOW_BUBBLE_COACH_MARK, true).apply();
//        }

        int px = Methods.dpToPx(80, getActivity());
        Intent intent = new Intent(getActivity(), BubblesService.class);
        intent.putExtra(Key_Preferences.BUBBLE_POS_Y, px);
        intent.putExtra(Key_Preferences.DIALOG_FROM, BubblesService.FROM.HOME_ACTIVITY);
        activity.startService(intent);

    }

    private void addOverlay() {
        bubbleOverlay.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.bubble_pointing_sign, bubbleOverlay);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bubbleOverlay.removeAllViews();
                bubbleOverlay.setVisibility(View.GONE);
                //layout.setOnClickListener(null);
            }
        });
    }

    private void dialogForOverlayPath(Home_Fragment_Tab.DrawOverLay from) {
        if (getActivity() == null || !isAdded()) return;
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_bubble_overlay_permission, null);
        ImageView image = (ImageView) view.findViewById(R.id.gif_image);
        try {
            GlideDrawableImageViewTarget target = new GlideDrawableImageViewTarget(image);
            Glide.with(getContext()).load(R.drawable.overlay_gif).into(target);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (overLayDialog == null) {
            overLayDialog = new MaterialDialog.Builder(getContext())
                    .customView(view, false)
                    .positiveColorRes(R.color.primary)
                    .positiveText(getString(R.string.open_setting))
                    .callback(new MaterialDialog.ButtonCallback() {

                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            try {
                                requestOverlayPermission();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                        }
                    }).show();
        } else if (from == Home_Fragment_Tab.DrawOverLay.FromHome) {
            overLayDialog.show();
        }
    }

    private void requestOverlayPermission() {

        if (getActivity() == null) {
            return;
        }

        MixPanelController.track(MixPanelController.BUBBLE_OVERLAY_PERM, null);
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getActivity().getPackageName()));
            startActivityForResult(intent, PERM_REQUEST_CODE_DRAW_OVERLAYS);
        } else {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }*/

    private class ManageCustomerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_manage_customers_v1_item, parent, false);
            final ManageCustomerHolder manageCustomerHolder = new ManageCustomerHolder(v);
            manageCustomerHolder.llUpdates.setVisibility(View.GONE);

            return manageCustomerHolder;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            if (holder instanceof ManageCustomerHolder) {
                final ManageCustomerHolder manageCustomerHolder = (ManageCustomerHolder) holder;

                manageCustomerHolder.tvOne.setVisibility(View.VISIBLE);
                manageCustomerHolder.tvOne.setGravity(Gravity.CENTER);
                manageCustomerHolder.tvTwo.setVisibility(View.VISIBLE);
                manageCustomerHolder.tvThree.setVisibility(View.GONE);
                manageCustomerHolder.iconImage.setVisibility(View.VISIBLE);
                manageCustomerHolder.tvTwo.setGravity(Gravity.CENTER);
                manageCustomerHolder.tvThree.setGravity(Gravity.CENTER);

                LinearLayout.LayoutParams params = null;

                switch (customerList[position]) {
                    case CI_WEBSITE:

                        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(Methods.dpToPx(30, getActivity()), 0, 0, 0);

                        manageCustomerHolder.tvThree.setVisibility(View.VISIBLE);
                        manageCustomerHolder.tvTitle.setText("Website\nCustomers");
                        manageCustomerHolder.tvTwo.setText(getActivity().getString(R.string.enquiries_title));
                        manageCustomerHolder.tvOne.setText(getActivity().getString(R.string.subscriptions));
                        manageCustomerHolder.tvThree.setText("Calls");

                        manageCustomerHolder.tvThree.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openCallLog();

                            }
                        });
                        manageCustomerHolder.tvInfoTitle.setText(getString(R.string.website_customers));
                        manageCustomerHolder.tvInfo.setText(getString(R.string.manage_website_customers));
                        manageCustomerHolder.tvTwo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MixPanelController.track(EventKeysWL.SIDE_PANEL_BUSINESS_ENQUIRIES, null);
                                Intent i = new Intent(getActivity(), BusinessEnquiryActivity.class);
                                startActivity(i);
                                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        });

                        manageCustomerHolder.tvOne.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MixPanelController.track(EventKeysWL.SIDE_PANEL_SUBSCRIBERS, null);
                                Intent i = new Intent(getActivity(), SubscribersActivity.class);
                                startActivity(i);
                                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        });


                        params.gravity = Gravity.BOTTOM;
                        manageCustomerHolder.iconImage.setLayoutParams(params);
                        manageCustomerHolder.iconImage.setImageResource(R.drawable.ic_manage_website);
                        manageCustomerHolder.llBackground.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openCallLog();
                            }
                        });
                        break;

                    case FB_CHATS:

                        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(Methods.dpToPx(30, getActivity()), 0, 0, 0);
                        manageCustomerHolder.tvTitle.setText("Social\nInteractions");
                        manageCustomerHolder.tvOne.setText(getString(R.string.my_facebook_chats));
                        manageCustomerHolder.tvTwo.setVisibility(View.GONE);
                        manageCustomerHolder.tvInfo.setText(getString(R.string.manage_social_customers));
                        manageCustomerHolder.tvInfoTitle.setText(getString(R.string.social_interactions));

                        manageCustomerHolder.tvOne.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(getActivity(), FacebookChatActivity.class);
                                startActivity(i);
                                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        });

                        params.gravity = Gravity.BOTTOM;
                        manageCustomerHolder.iconImage.setLayoutParams(params);
                        manageCustomerHolder.iconImage.setImageResource(R.drawable.ic_social);
                        manageCustomerHolder.llBackground.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(getActivity(), FacebookChatActivity.class);
                                startActivity(i);
                                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                            }
                        });
                        break;

                    case MULTI_CHANNEL_CUSTOMERS:


                        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(Methods.dpToPx(30, getActivity()), 0, 0, 0);

                        manageCustomerHolder.tvTitle.setText("Cross\nPlatform");
                        //manageCustomerHolder.tvOne.setText(getString(R.string.enable_customer_assistant));
                        manageCustomerHolder.tvInfoTitle.setText(getString(R.string.cross_platform));
                        manageCustomerHolder.tvOne.setGravity(Gravity.LEFT);
                        manageCustomerHolder.tvOne.setVisibility(View.VISIBLE);
                        manageCustomerHolder.tvOne.setText("WhatsApp Bubble");
                        manageCustomerHolder.tvInfo.setText(getString(R.string.manage_multichannel_customers));

//                        manageCustomerHolder.llBackground.setBackgroundResource(R.drawable.mcp_bg);

                        params.gravity = Gravity.CENTER;
                        manageCustomerHolder.iconImage.setLayoutParams(params);
                        manageCustomerHolder.iconImage.setImageResource(R.drawable.ic_cross_platform);
                        manageCustomerHolder.tvTwo.setText("Third Party Queries");
                        manageCustomerHolder.tvTwo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getActivity(), ThirdPartyQueriesActivity.class));
                                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                //enableCustomerAssistant();
                            }
                        });
                        manageCustomerHolder.tvOne.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogThirdPartyMessage();
                            }
                        });
                        manageCustomerHolder.llBackground.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getActivity(), ThirdPartyQueriesActivity.class));
                                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        });
                       /* if (session.isBoostBubbleEnabled() || Methods.hasOverlayPerm(getActivity())) {

                            manageCustomerHolder.tvOne.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    openCrossPlatform();
                                }
                            });
                            manageCustomerHolder.llBackground.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    openCrossPlatform();
                                }
                            });
                        } else {
                            manageCustomerHolder.tvOne.setText(getString(R.string.enable_customer_assistant));
                            manageCustomerHolder.tvOne.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(getActivity(), ThirdPartyQueriesActivity.class));
                                    //enableCustomerAssistant();
                                }
                            });
                            manageCustomerHolder.llBackground.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // show dialog for message coming soon
                                    //enableCustomerAssistant();
                                    dialogThirdPartyMessage();
                                }
                            });
                        }*/

                        break;

                }

                manageCustomerHolder.ivInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        showOverlay(manageCustomerHolder);
                    }
                });

                manageCustomerHolder.rlRevealLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeOverlay(manageCustomerHolder);
                    }
                });

                manageCustomerHolder.ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeOverlay(manageCustomerHolder);
                    }
                });

            }
        }

        private void dialogThirdPartyMessage() {
            new MaterialDialog.Builder(getContext())
                    .content(R.string.we_are_currently_reworking_this_feature_to_enhance)
                    .title(R.string.whats_app_bubble)
                    .positiveText(getString(R.string.ok_))
                    .show();
        }

        private void openCallLog() {

            final boolean isVmnEnable = "VMN".equals(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_1)) ||
                    "VMN".equals(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_3)) ||
                    "VMN".equals(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NAME));
            final String paymentState = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE);

            if (isVmnEnable) {
                Intent i = new Intent(getActivity(), VmnCallCardsActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } else if ((TextUtils.isDigitsOnly(paymentState) && "1".equalsIgnoreCase(paymentState))) {
                Intent i = new Intent(getActivity(), VmnNumberRequestActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } else {
                Methods.showFeatureNotAvailDialog(getActivity());
                // show first buy lighthouse
            }
        }
//
//        private void openCrossPlatform() {
//            MaterialDialog.Builder builder = new MaterialDialog.Builder(activity)
//                    .title(getString(R.string.are_you_sure))
//                    .content(getString(R.string.ca_disable_msg))
//                    .positiveText(getString(R.string.ok))
//                    .negativeText(getString(R.string.cancel))
//                    .positiveColorRes(R.color.primaryColor)
//                    .negativeColorRes(R.color.primaryColor)
//                    .callback(new MaterialDialog.ButtonCallback() {
//                        @Override
//                        public void onPositive(MaterialDialog dialog) {
//                            super.onPositive(dialog);
//                            disableCustomerAssistant();
//                            notifyDataSetChanged();
//                        }
//
//                        @Override
//                        public void onNegative(MaterialDialog dialog) {
//                            super.onNegative(dialog);
//
//                        }
//                    });
//
//            if (!activity.isFinishing()) {
//                final MaterialDialog materialDialog = builder.show();
//                materialDialog.setCancelable(false);
//            }
//        }

        @Override
        public int getItemCount() {
            return customerList.length;
        }

        private class ManageCustomerHolder extends RecyclerView.ViewHolder {

            TextView tvOne, tvTwo, tvThree, tvTitle, tvInfo, tvInfoTitle;
            CardView cvManageCustomer;
            RecyclerView rv_carousel;
            LinearLayout llUpdates, llBackground;
            RelativeLayout rlRevealLayout;
            ImageView iconImage, ivInfo, ivClose;
            RevealFrameLayout rflOverLay;

            public ManageCustomerHolder(View itemView) {
                super(itemView);
                rv_carousel = (RecyclerView) itemView.findViewById(R.id.rv_carousel);
                llBackground = (LinearLayout) itemView.findViewById(R.id.llBackground);
                llUpdates = (LinearLayout) itemView.findViewById(R.id.llUpdates);
                cvManageCustomer = (CardView) itemView.findViewById(R.id.cvManageCustomer);
                iconImage = (ImageView) itemView.findViewById(R.id.iconImage);
                ivInfo = (ImageView) itemView.findViewById(R.id.ivInfo);
                ivClose = (ImageView) itemView.findViewById(R.id.ivClose);
                tvInfo = (TextView) itemView.findViewById(R.id.tvInfo);
                tvInfoTitle = (TextView) itemView.findViewById(R.id.tvInfoTitle);
                tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
                tvOne = (TextView) itemView.findViewById(R.id.tvOne);
                tvTwo = (TextView) itemView.findViewById(R.id.tvTwo);
                tvThree = (TextView) itemView.findViewById(R.id.tvThree);
                rlRevealLayout = (RelativeLayout) itemView.findViewById(R.id.ll_reveal_layout);
                rflOverLay = (RevealFrameLayout) itemView.findViewById(R.id.rfl_overlay);
                rlRevealLayout.setVisibility(View.INVISIBLE);

            }


        }
    }

    /*private void checkCustomerAssistantService() {

        if (!Methods.isMyServiceRunning(getActivity(), CustomerAssistantService.class)) {
            Intent bubbleIntent = new Intent(getActivity(), CustomerAssistantService.class);
            getActivity().startService(bubbleIntent);
        }
        getActivity().sendBroadcast(new Intent(CustomerAssistantService.ACTION_REMOVE_BUBBLE));
    }

    BroadcastReceiver clickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("ggg", "clicked");
            bubbleOverlay.removeAllViews();
            bubbleOverlay.setVisibility(View.GONE);
            //bubbleOverlay.setOnClickListener(null);
        }

    };*/


}
