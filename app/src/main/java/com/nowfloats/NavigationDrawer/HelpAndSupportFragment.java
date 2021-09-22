package com.nowfloats.NavigationDrawer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.nowfloats.NavigationDrawer.API.RiaNetworkInterface;
import com.nowfloats.NavigationDrawer.model.RiaSupportModel;
import com.nowfloats.network.MyOkHttpClient;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.Utils;
import com.nowfloats.util.WebEngageController;
import com.rd.PageIndicatorView;
import com.thinksity.R;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.framework.webengageconstant.EventLabelKt.HELP_AND_SUPPORT;
import static com.framework.webengageconstant.EventNameKt.CLICKED_ON_HELP_AND_SUPPORT;
import static com.framework.webengageconstant.EventValueKt.NULL;
import static com.nowfloats.NavigationDrawer.HomeActivity.headerText;

/**
 * Created by Admin on 28-12-2017.
 */

public class HelpAndSupportFragment extends Fragment {
    private Context mContext;
    private List<RiaSupportModel> mRiaSupportModelList;
    private ProgressDialog dialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        WebEngageController.trackEvent(CLICKED_ON_HELP_AND_SUPPORT, HELP_AND_SUPPORT, NULL);
        return inflater.inflate(R.layout.fragment_new_help_and_support, container, false);
    }

    private void showProgress() {
        if (dialog == null) {
            dialog = new ProgressDialog(mContext);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setIndeterminate(true);
            dialog.setMessage(getString(R.string.please_wait));
        }
        if (!dialog.isShowing())
            dialog.show();
    }

    private void hideProgress() {
        if (dialog != null && dialog.isShowing() && getActivity() != null && isAdded()) {
            dialog.dismiss();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isAdded() || isDetached()) return;
        MixPanelController.track(MixPanelController.HELP_AND_SUPPORT_CLICK, null);
        mRiaSupportModelList = new ArrayList<>(2);

        addDefaultRiaData();
        setAdapterWithPager(view);
//        TextView queryMessageText = view.findViewById(R.id.textView9);
//        makeLinkClickable(queryMessageText);
//        CharSequence charSequence = Methods.fromHtml("If your query is still unanswered, please contact us at <a href=\"mailto:" + getString(R.string.settings_feedback_link) + "\">" + getString(R.string.settings_feedback_link) + "</a> " +
//                "or call at <a href=\"tel:"+ getString(R.string.contact_us_number)+"\">"+getString(R.string.contact_us_number)+"</a>."+
//                "or <a href=\"" + CHAT_INTENT_URI + "\"><u>CHAT</u></a>.");
//
//        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
//        makeLinkClickable(spannableStringBuilder, charSequence);
//        tvTextRia.setText(spannableStringBuilder);
//        tvTextRia.setMovementMethod(LinkMovementMethod.getInstance());

    }

    private void getRiaMembers(HashMap<String, String> map, final View view) {
        showProgress();
        RiaNetworkInterface riaNetworkInterface = Constants.riaRestAdapter.create(RiaNetworkInterface.class);
        riaNetworkInterface.getAllMemberForFp(map, new Callback<List<RiaSupportModel>>() {
            @Override
            public void success(List<RiaSupportModel> list, Response response) {
                if (list == null || list.size() == 0 ||
                        response.getStatus() < 200 || response.getStatus() > 300) {
                    addDefaultRiaData();
                } else {
                    for (RiaSupportModel model : list) {
                        if (TextUtils.isEmpty(model.getType())) {
                            model.setType(MemberType.WEB.toString());
                            mRiaSupportModelList.add(model);
                        } else if (MemberType.CHC.name().equals(model.getType())) {
                            mRiaSupportModelList.add(0, model);
                        } else if (MemberType.TA.name().equals(model.getType())) {
                            model.setType(MemberType.TA.toString());
                            mRiaSupportModelList.add(model);
                        }
                    }
                }
                setAdapterWithPager(view);
            }

            @Override
            public void failure(RetrofitError error) {
                addDefaultRiaData();
                setAdapterWithPager(view);
                Methods.showSnackBarNegative(getActivity(), getString(R.string.something_went_wrong));

            }
        });
    }

//    protected void makeLinkClickable(TextView view) {
//
//        SpannableStringBuilder spanTxt = new SpannableStringBuilder("If your query is still unanswered, please check ");
//        spanTxt.append(Methods.fromHtml("<u><b>FAQs</b></u>"));
//        spanTxt.setSpan(new ClickableSpan() {
//            @Override
//            public void onClick(View widget) {
//                Intent i = new Intent(mContext, Mobile_Site_Activity.class);
//                i.putExtra("WEBSITE_NAME", getString(R.string.setting_faq_url));
//                startActivity(i);
//            }
//        }, spanTxt.length() - Methods.fromHtml("<u><b>FAQs</b></u>").length(), spanTxt.length(), 0);
//        spanTxt.append(" ");
//        spanTxt.append(Methods.fromHtml(" or contact us at <a href=\"mailto:" + getString(R.string.settings_feedback_link) + "\"><b>" + getString(R.string.settings_feedback_link) + "</b></a> or call our toll number <a href=\"tel:" + getString(R.string.contact_us_number) + "\"><b>" + getString(R.string.contact_us_number) + "</b></a>."));
//        view.setMovementMethod(LinkMovementMethod.getInstance());
//        view.setText(spanTxt, TextView.BufferType.SPANNABLE);
//    }

    @Override
    public void onResume() {
        super.onResume();
        if (headerText != null) {
            headerText.setText(getString(R.string.help_and_support));
        }
    }

    private void setAdapterWithPager(View view) {
        hideProgress();
        if (getActivity() == null || !isAdded()) {
            return;
        }
        ViewPager mPager = view.findViewById(R.id.ps_pager);
        mPager.setClipToPadding(false);
        // set padding manually, the more you set the padding the more you see of prev & next page
        int padding = Methods.dpToPx(25, mContext);
        mPager.setPadding(padding, padding, padding, padding);
        // sets a margin b/w individual pages to ensure that there is a gap b/w them
        mPager.setPageMargin(padding / 2);
        mPager.setAdapter(new viewPagerAdapter(getChildFragmentManager()));
        PageIndicatorView pageIndicatorView = view.findViewById(R.id.ps_indicator);
        pageIndicatorView.setCount(mRiaSupportModelList.size());
        pageIndicatorView.setViewPager(mPager);
    }

    private void addDefaultRiaData() {
        RiaSupportModel model = new RiaSupportModel();
        Activity activity = getActivity();
        if (activity != null && isAdded()) {
            model.setName(getString(R.string.support_name));
            model.setEmail(getString(R.string.settings_feedback_link));
            model.setPhoneNumber(getString(R.string.contact_us_number));
        }
        model.setGender(1);
        model.setType(MemberType.DEFAULT.toString());
        mRiaSupportModelList.add(model);
    }

    enum MemberType {
        CHC, WEB, TA, DEFAULT, WC
    }

    class GetMembers extends AsyncTask<String, String, List<RiaSupportModel>> {
        private int responseCode;
        private View view;
        private String url = "https://ria.withfloats.com/api/RIASupportTeam/GetAllMembersForFP?fpTag=";

        private OkHttpClient client = MyOkHttpClient.getOkHttpClient();

        private GetMembers(String clientId, String fpTag, final View view) {
            this.view = view;
            this.url = this.url.concat(fpTag).concat("&clientId=").concat(clientId);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }


        @Override
        protected List<RiaSupportModel> doInBackground(String... strings) {
            List<RiaSupportModel> models = new ArrayList<>();

            okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
            builder.addHeader("Authorization", Utils.getAuthToken());
            builder.url(url);
            builder.get();

            okhttp3.Request request = builder.build();

            try {
                okhttp3.Response response = client.newCall(request).execute();
                responseCode = response.code();

                JSONArray array = new JSONArray(response.body().string());

                for (int i = 0; i < array.length(); i++) {
                    RiaSupportModel model = new Gson().fromJson(array.get(i).toString(), RiaSupportModel.class);
                    models.add(model);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return models;
        }

        @Override
        protected void onPostExecute(List<RiaSupportModel> response) {
            super.onPostExecute(response);

            if (responseCode >= 200 && responseCode <= 300) {
                for (RiaSupportModel model : response) {
                    if (TextUtils.isEmpty(model.getType())) {
                        model.setType(MemberType.WEB.toString());
                        mRiaSupportModelList.add(model);
                    } else if (MemberType.CHC.name().equals(model.getType())) {
                        mRiaSupportModelList.add(0, model);
                    } else if (MemberType.TA.name().equals(model.getType())) {
                        model.setType(MemberType.TA.toString());
                        mRiaSupportModelList.add(model);
                    } else if (MemberType.WC.name().equals(model.getType())) {
                        model.setType(MemberType.WC.toString());
                        mRiaSupportModelList.add(model);
                    }
                }

                if (mRiaSupportModelList.size() == 0) {
                    addDefaultRiaData();
                }
            } else {
                addDefaultRiaData();
                Methods.showSnackBarNegative(getActivity(), getString(R.string.something_went_wrong));
            }

            setAdapterWithPager(view);
        }
    }

    private class viewPagerAdapter extends FragmentStatePagerAdapter {

        viewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle b = new Bundle();
            b.putString(HelpAndSupportCardItemFragment.RIA_MODEL_DATA, new Gson().toJson(mRiaSupportModelList.get(position)));
            return HelpAndSupportCardItemFragment.getInstance(b);
        }

        @Override
        public int getCount() {
            return mRiaSupportModelList.size();
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {

        }
    }

}