package com.nowfloats.managecustomers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.nowfloats.Analytics_Screen.SubscribersActivity;
import com.nowfloats.Business_Enquiries.BusinessEnquiryActivity;
import com.nowfloats.Image_Gallery.FullScreenImage;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
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
public class ManageCustomerFragmentV2 extends Fragment {
    TextView tvBusinessEnquires, tvSubscribers, tvFacebookChat, tvBubbleInfo;
    Typeface robotoLight;
    private SharedPreferences pref = null;
    UserSessionManager session;
    SharedPreferences.Editor prefsEditor;
    private Activity activity;
    private Switch bubbleSwitch, customerAssistantSwitch;
    private TextView tvLearnMore;
    private LinearLayout llManageCustomers;

    private RecyclerView rvManageCustomers;

    private ManageCustomerAdapter manageCustomerAdapter;

    private static final int CI_WEBSITE = 0, FB_CHATS = 1,
            MULTI_CHANNEL_CUSTOMERS = 2, CAROUSEL = 3;

    private int customerList[] = {CI_WEBSITE, FB_CHATS, MULTI_CHANNEL_CUSTOMERS};

    private Bus bus;


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
        prefsEditor = pref.edit();
        session = new UserSessionManager(activity.getApplicationContext(), activity);
        customerList[0] = CI_WEBSITE;
        return mainView;
    }

    @Override
    public void onViewCreated(final View mainView, Bundle savedInstanceState) {
        super.onViewCreated(mainView, savedInstanceState);
        if (!isAdded()) return;
        rvManageCustomers = (RecyclerView) mainView.findViewById(R.id.rvManageCustomers);

        final LinearLayoutManager manager = new LinearLayoutManager(getActivity()
                , LinearLayoutManager.VERTICAL, false);
        rvManageCustomers.setLayoutManager(manager);
        rvManageCustomers.setAdapter(manageCustomerAdapter = new ManageCustomerAdapter());


    }

    @Override
    public void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (headerText != null)
            headerText.setText(getResources().getString(R.string.manage_customers));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (headerText != null)
        headerText.setText(Constants.StoreName);
    }

    private void showGif() {

        Intent fullImage = new Intent(activity, FullScreenImage.class);
        fullImage.putExtra("currentPositon", 0);
        startActivity(fullImage);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }


    private class ManageCustomerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_manage_customers_v1_item, parent, false);

            final ManageCustomerHolder manageCustomerHolder = new ManageCustomerHolder(v);

//            if (viewType == 0) {
//                lastTopValue = 0;
//                manageCustomerHolder.llUpdates.setVisibility(View.VISIBLE);
//                DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
//                int rightspace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25,
//                        getActivity().getResources().getDisplayMetrics());
//                manageCustomerHolder.rv_carousel.addItemDecoration(new FirstItemSpacesDecoration(0, rightspace, false));
//
//                final LinearLayoutManager manager = new LinearLayoutManager(getActivity()
//                        , LinearLayoutManager.HORIZONTAL, false);
//                ManageUpdatesAdapter manageUpdatesAdapter = new ManageUpdatesAdapter();
//                manageCustomerHolder.rv_carousel.setLayoutManager(manager);
//                manageCustomerHolder.rv_carousel.setAdapter(manageUpdatesAdapter);
//                manageUpdatesAdapter.notifyItemChanged(0);
//                SnapHelper snapHelper = new PagerSnapHelper();
//                snapHelper.attachToRecyclerView(manageCustomerHolder.rv_carousel);
////                new Handler().postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        manageCustomerHolder.llImage.setTranslationY(-100f);
////                    }
////                }, 2000);
//                manageCustomerHolder.rv_carousel.setOnScrollListener(new RecyclerView.OnScrollListener() {
//                    @Override
//                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                        super.onScrolled(recyclerView, dx, dy);
//                        Rect rect = new Rect();
//                        manageCustomerHolder.llImage.getLocalVisibleRect(rect);
//                        if (lastTopValue != rect.right) {
////                            if (lastTopValue != 0) {
////                                manageCustomerHolder.llImage.setX(-100f);
////                            }
//                            lastTopValue = rect.right;
//                        }
//                    }
//                });
//            } else {
            manageCustomerHolder.llUpdates.setVisibility(View.GONE);
//            }

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

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(Methods.dpToPx(30, getActivity()), 0, 0, 0);


                switch (customerList[position]) {
                    case CI_WEBSITE:

//                        manageCustomerHolder.tvThree.setVisibility(View.VISIBLE);
                        manageCustomerHolder.tvTitle.setText("Website\nInteractions");
                        manageCustomerHolder.tvTwo.setText(getActivity().getString(R.string.enquiries_title));
                        manageCustomerHolder.tvOne.setText(getActivity().getString(R.string.subscriptions));
                        manageCustomerHolder.tvThree.setText("Call");

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
                        manageCustomerHolder.llBackground.setBackgroundResource(R.drawable.mcw_bg);


                        params.gravity = Gravity.BOTTOM;
                        manageCustomerHolder.iconImage.setLayoutParams(params);
                        manageCustomerHolder.iconImage.setImageResource(R.drawable.ic_manage_website);


                        break;

                    case FB_CHATS:

                        manageCustomerHolder.tvTitle.setText(R.string.social_and_interaction);
                        manageCustomerHolder.tvOne.setText(R.string.my_facebook_chats);
                        manageCustomerHolder.tvTwo.setVisibility(View.GONE);
                        manageCustomerHolder.tvInfo.setText(getString(R.string.manage_social_customers));

                        manageCustomerHolder.tvOne.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(getActivity(), FacebookChatActivity.class);
                                startActivity(i);
                                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        });

                        manageCustomerHolder.llBackground.setBackgroundResource(R.drawable.mci_bg);
                        params.gravity = Gravity.BOTTOM;
                        manageCustomerHolder.iconImage.setLayoutParams(params);
                        manageCustomerHolder.iconImage.setImageResource(R.drawable.ic_social);
                        break;

                    case MULTI_CHANNEL_CUSTOMERS:


                        manageCustomerHolder.tvTitle.setText(R.string.cross_and_platform);
                        manageCustomerHolder.tvOne.setText(R.string.enable_customer_assistant);
                        manageCustomerHolder.tvOne.setGravity(Gravity.LEFT);
                        manageCustomerHolder.tvTwo.setVisibility(View.GONE);
                        manageCustomerHolder.tvInfo.setText(getString(R.string.manage_multichannel_customers));

                        manageCustomerHolder.llBackground.setBackgroundResource(R.drawable.mcp_bg);

                        params.gravity = Gravity.CENTER;
                        manageCustomerHolder.iconImage.setLayoutParams(params);
                        manageCustomerHolder.iconImage.setImageResource(R.drawable.ic_cross_platform);
                        break;

                }

                manageCustomerHolder.ivInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        showOverlay(manageCustomerHolder);
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

        @Override
        public int getItemCount() {
            return customerList.length;
        }

        private class ManageCustomerHolder extends RecyclerView.ViewHolder {

            TextView tvOne, tvTwo, tvThree, tvTitle, tvInfo;
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


//    private class ManageUpdatesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//        @Override
//        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//
//            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_manage_updates_v1_item, parent, false);
//            return new ManageUpdatesHolder(v);
//        }
//
//        @Override
//        public int getItemViewType(int position) {
//            return position;
//        }
//
//        @Override
//        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//            ManageUpdatesHolder manageUpdatesHolder = (ManageUpdatesHolder) holder;
//            if (position == 0) {
//                holder.itemView.setVisibility(View.INVISIBLE);
//            } else {
//                holder.itemView.setVisibility(View.VISIBLE);
//
//                Business_Enquiry_Model data = Constants.StorebizQueries.get(position - 1);
//                manageUpdatesHolder.tvTitle.setText(data.message);
//
//                String email = data.contact;
//                Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
//                Matcher m = p.matcher(email);
//                boolean matchFound = m.matches();
//                if (matchFound) {
//                    manageUpdatesHolder.tvSubmit.setText(getActivity().getResources().getString(R.string.email));
//                } else {
//                    manageUpdatesHolder.tvSubmit.setText(getActivity().getResources().getString(R.string.call));
//                }
//
//            }
//
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return 4;
//        }
//
//        private class ManageUpdatesHolder extends RecyclerView.ViewHolder {
//
//            TextView tvSubmit, tvTitle;
//
//            public ManageUpdatesHolder(View itemView) {
//                super(itemView);
//                tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
//                tvSubmit = (TextView) itemView.findViewById(R.id.tvSubmit);
//
//            }
//        }
//    }
//
//    @Subscribe
//    public void getValues(final BzQueryEvent event) {
//        BoostLog.i("BZ ENQ", "event-" + event.StorebizEnterpriseQueries + "\n" + event.StorebizQueries);
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                final LinearLayoutManager manager = new LinearLayoutManager(getActivity()
//                        , LinearLayoutManager.VERTICAL, false);
//                rvManageCustomers.setLayoutManager(manager);
//                rvManageCustomers.setAdapter(manageCustomerAdapter = new ManageCustomerAdapter());
//
//            }
//        });
//    }
}
