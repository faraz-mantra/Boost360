package com.nowfloats.managecustomers;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.nowfloats.Analytics_Screen.SubscribersActivity;
import com.nowfloats.Business_Enquiries.API_Business_enquiries;
import com.nowfloats.Business_Enquiries.BusinessEnquiryActivity;
import com.nowfloats.Business_Enquiries.Model.Business_Enquiry_Model;
import com.nowfloats.Business_Enquiries.Model.BzQueryEvent;
import com.nowfloats.Image_Gallery.FullScreenImage;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.riachatsdk.CustomWidget.FirstItemSpacesDecoration;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageCustomerFragmentV1 extends Fragment {
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

    private boolean isSelected = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    API_Business_enquiries businessEnquiries = new API_Business_enquiries(bus, session);
                    businessEnquiries.getMessages();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

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
        if (HomeActivity.headerText != null)
            HomeActivity.headerText.setText(getResources().getString(R.string.manage_customers));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        HomeActivity.headerText.setText(Constants.StoreName);
    }

    private void showGif() {

        Intent fullImage = new Intent(activity, FullScreenImage.class);
        fullImage.putExtra("currentPositon", 0);
        startActivity(fullImage);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }


    int lastTopValue = 0;

    private class ManageCustomerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_manage_customers_v1_item, parent, false);

            final ManageCustomerHolder manageCustomerHolder = new ManageCustomerHolder(v);

            if (viewType == 0) {
                lastTopValue = 0;
                manageCustomerHolder.llUpdates.setVisibility(View.VISIBLE);
                DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
                int rightspace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25,
                        getActivity().getResources().getDisplayMetrics());
                manageCustomerHolder.rv_carousel.addItemDecoration(new FirstItemSpacesDecoration(0, rightspace, false));

                final LinearLayoutManager manager = new LinearLayoutManager(getActivity()
                        , LinearLayoutManager.HORIZONTAL, false);
                ManageUpdatesAdapter manageUpdatesAdapter = new ManageUpdatesAdapter();
                manageCustomerHolder.rv_carousel.setLayoutManager(manager);
                manageCustomerHolder.rv_carousel.setAdapter(manageUpdatesAdapter);
                manageUpdatesAdapter.notifyItemChanged(0);
                SnapHelper snapHelper = new PagerSnapHelper();
                snapHelper.attachToRecyclerView(manageCustomerHolder.rv_carousel);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        manageCustomerHolder.llImage.setTranslationY(-100f);
//                    }
//                }, 2000);
                manageCustomerHolder.rv_carousel.setOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        Rect rect = new Rect();
                        manageCustomerHolder.llImage.getLocalVisibleRect(rect);
                        if (lastTopValue != rect.right) {
//                            if (lastTopValue != 0) {
//                                manageCustomerHolder.llImage.setX(-100f);
//                            }
                            lastTopValue = rect.right;
                        }
                    }
                });
            } else {
                manageCustomerHolder.llUpdates.setVisibility(View.GONE);
            }

            return manageCustomerHolder;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            if (holder instanceof ManageCustomerHolder) {
                ManageCustomerHolder manageCustomerHolder = (ManageCustomerHolder) holder;

                manageCustomerHolder.tvOne.setVisibility(View.VISIBLE);
                manageCustomerHolder.tvOne.setGravity(Gravity.CENTER);
                manageCustomerHolder.tvTwo.setVisibility(View.VISIBLE);
                manageCustomerHolder.tvTwo.setGravity(Gravity.CENTER);
                manageCustomerHolder.swCustomerAssistant.setVisibility(View.GONE);
                manageCustomerHolder.pbEnquiries.setVisibility(View.GONE);


                switch (customerList[position]) {
                    case CI_WEBSITE:


                        manageCustomerHolder.tvTitle.setText("Customer Interactions on your website");
                        manageCustomerHolder.tvOne.setText(getActivity().getString(R.string.business_enquiries_title));
                        manageCustomerHolder.tvTwo.setText(getActivity().getString(R.string.subscribers));

                        manageCustomerHolder.tvOne.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MixPanelController.track(EventKeysWL.SIDE_PANEL_BUSINESS_ENQUIRIES, null);
                                Intent i = new Intent(getActivity(), BusinessEnquiryActivity.class);
                                startActivity(i);
                                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        });

                        manageCustomerHolder.tvTwo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MixPanelController.track(EventKeysWL.SIDE_PANEL_SUBSCRIBERS, null);
                                Intent i = new Intent(getActivity(), SubscribersActivity.class);
                                startActivity(i);
                                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        });
                        break;

                    case FB_CHATS:

                        manageCustomerHolder.tvTitle.setText("Manage Social Interactions on channels like facebook");
                        manageCustomerHolder.tvOne.setText("Open My Facebook Chats");
                        manageCustomerHolder.tvTwo.setVisibility(View.GONE);

                        manageCustomerHolder.tvOne.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(getActivity(), FacebookChatActivity.class);
                                startActivity(i);
                                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        });

                        break;

                    case MULTI_CHANNEL_CUSTOMERS:
                        manageCustomerHolder.swCustomerAssistant.setVisibility(View.VISIBLE);

                        manageCustomerHolder.tvTitle.setText("Manage multi-channels customers of whatsapp,sms,phonecalls");
                        manageCustomerHolder.tvOne.setText("Enable Customer Assistant");
                        manageCustomerHolder.tvOne.setGravity(Gravity.LEFT);
                        manageCustomerHolder.tvTwo.setVisibility(View.GONE);

                        manageCustomerHolder.swCustomerAssistant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                MixPanelController.track(EventKeysWL.SIDE_PANEL_WHATSAPP_BUBBLE, null);

                                if (!isChecked) {
                                    session.setBubbleStatus(isChecked);
                                } else {

                                    if ((android.os.Build.VERSION.SDK_INT >= 23 && getActivity() != null && !Settings.canDrawOverlays(getActivity()))
                                            || (!Methods.isAccessibilitySettingsOn(getActivity()))) {
                                        session.setBubbleTime(-1);
                                    } else {
                                        session.setBubbleStatus(isChecked);
                                    }

                                }
                            }
                        });
                        break;

                }

            }
        }

        @Override
        public int getItemCount() {
            return customerList.length;
        }

        private class ManageCustomerHolder extends RecyclerView.ViewHolder {

            TextView tvOne, tvTwo, tvTitle;
            CardView cvManageCustomer;
            ImageView llImage;
            Switch swCustomerAssistant;
            ProgressBar pbEnquiries;
            RecyclerView rv_carousel;
            LinearLayout llUpdates;

            public ManageCustomerHolder(View itemView) {
                super(itemView);
                llImage = (ImageView) itemView.findViewById(R.id.llImage);
                swCustomerAssistant = (Switch) itemView.findViewById(R.id.swCustomerAssistant);
                rv_carousel = (RecyclerView) itemView.findViewById(R.id.rv_carousel);
                llUpdates = (LinearLayout) itemView.findViewById(R.id.llUpdates);
                cvManageCustomer = (CardView) itemView.findViewById(R.id.cvManageCustomer);
                pbEnquiries = (ProgressBar) itemView.findViewById(R.id.pbEnquiries);
                tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
                tvOne = (TextView) itemView.findViewById(R.id.tvOne);
                tvTwo = (TextView) itemView.findViewById(R.id.tvTwo);

            }


        }
    }

    private class ManageUpdatesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_manage_updates_v1_item, parent, false);
            return new ManageUpdatesHolder(v);
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ManageUpdatesHolder manageUpdatesHolder = (ManageUpdatesHolder) holder;
            if (position == 0) {
                holder.itemView.setVisibility(View.INVISIBLE);
            } else {
                holder.itemView.setVisibility(View.VISIBLE);

                Business_Enquiry_Model data = Constants.StorebizQueries.get(position - 1);
                manageUpdatesHolder.tvTitle.setText(data.message);

                String email = data.contact;
                Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
                Matcher m = p.matcher(email);
                boolean matchFound = m.matches();
                if (matchFound) {
                    manageUpdatesHolder.tvSubmit.setText(getActivity().getResources().getString(R.string.email));
                } else {
                    manageUpdatesHolder.tvSubmit.setText(getActivity().getResources().getString(R.string.call));
                }

            }


        }

        @Override
        public int getItemCount() {
            return 4;
        }

        private class ManageUpdatesHolder extends RecyclerView.ViewHolder {

            TextView tvSubmit, tvTitle;

            public ManageUpdatesHolder(View itemView) {
                super(itemView);
                tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
                tvSubmit = (TextView) itemView.findViewById(R.id.tvSubmit);

            }
        }
    }

    @Subscribe
    public void getValues(final BzQueryEvent event) {
        BoostLog.i("BZ ENQ", "event-" + event.StorebizEnterpriseQueries + "\n" + event.StorebizQueries);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final LinearLayoutManager manager = new LinearLayoutManager(getActivity()
                        , LinearLayoutManager.VERTICAL, false);
                rvManageCustomers.setLayoutManager(manager);
                rvManageCustomers.setAdapter(manageCustomerAdapter = new ManageCustomerAdapter());

            }
        });
    }
}
