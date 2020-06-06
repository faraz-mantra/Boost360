package com.nowfloats.NavigationDrawer;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.BusinessProfile.UI.UI.changePasswordAsyncTask;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Store.Model.OnItemClickCallback;
import com.nowfloats.Store.NewPricingPlansActivity;
import com.nowfloats.Store.SimpleImageTextListAdapter;
import com.nowfloats.Store.YourPurchasedPlansActivity;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Admin on 29-01-2018.
 */

public class BoostExtensionsFragment extends Fragment {

    private Context mContext;
    private SharedPreferences pref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upgrade, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        pref = mContext.getSharedPreferences(Constants.PREF_NAME, Activity.MODE_PRIVATE);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isAdded() || isDetached()) return;

        final String[] adapterTexts = getResources().getStringArray(R.array.boost_extensions);
        final int[] adapterImages = {R.drawable.ic_web_foreground, R.drawable.ic_web_foreground};

        RecyclerView mRecyclerView = view.findViewById(R.id.rv_upgrade);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        SimpleImageTextListAdapter adapter = new SimpleImageTextListAdapter(mContext, new OnItemClickCallback() {
            @Override
            public void onItemClick(int pos) {
                switch (adapterTexts[pos]) {
                    case "Wordpress":
                        showWordpressScript();
                        break;
                    case "Static Website":

                        break;
                }
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        adapter.setItems(adapterImages, adapterTexts);
        mRecyclerView.setLayoutManager(new

                LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(adapter);
     }

    private void showWordpressScript() {
        if (getActivity() == null) return;
        UserSessionManager session = new UserSessionManager(mContext, getActivity());
        MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                .customView(R.layout.boostx_script_panel, true)
                .positiveText("COPY & SHARE")
                .negativeText("LATER")
                .positiveColorRes(R.color.primaryColor)
                .negativeColorRes(R.color.gray_40)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        dialog.dismiss();
                    }

                    @Override
                    public void onPositive(MaterialDialog dialog) {

                        String script = Constants.boostx_script.replace("[[FPTAG]]", session.getFpTag());
                        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("boostx_script", script);
                        clipboard.setPrimaryClip(clip);

                        Methods.showSnackBarPositive(getActivity(),
                                "The script has been copied to clipboard. Also you can share the script with your developer using the most appropriate option.");

                        /*Create an ACTION_SEND Intent*/
                        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                        /*This will be the actual content you wish you share.*/
                        String shareBody = "Here is the share content body";
                        /*The type of the content is text, obviously.*/
                        intent.setType("text/plain");
                        /*Applying information Subject and Body.*/
                        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Boost360 wordpress-plugin script");
                        intent.putExtra(android.content.Intent.EXTRA_TEXT, script);
                        /*Fire!*/
                        startActivity(Intent.createChooser(intent, "Shared from Boost360"));
                    }
                }).build();

        EditText scriptText = (EditText)dialog.getCustomView().findViewById(R.id.boostx_script_body);
        scriptText.setText(Constants.boostx_script.replace("[[FPTAG]]", session.getFpTag()));

        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mContext instanceof HomeActivity && HomeActivity.headerText != null) {
            HomeActivity.headerText.setText(getString(R.string.subscriptions));
        }
    }
}
