package com.nowfloats.BusinessProfile.UI.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nowfloats.NavigationDrawer.Mobile_Site_Activity;
import com.thinksity.R;

/**
 * Created by Admin on 07-04-2017.
 */

public class ShowArrayFragment extends Fragment implements AdapterView.OnItemClickListener {

    String [] array;
    private Context mContext;

    public static Fragment getInstance(Bundle b){
        Fragment frag = new ShowArrayFragment();
        frag.setArguments(b);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_array_list,container,false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments()!=null){
            Bundle b = getArguments();
            array = b.getStringArray("array");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        if(!isAdded()) return;
        ((ChangeTitle)mContext).setTitle("Quikr Guidelines");
        ListView mListView = (ListView) view.findViewById(R.id.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext,R.layout.faq_row_layout,array);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(position ==6){
            openWebView("http://www.quikr.com/html/termsandconditions.php");
        }else if(position == 7){
            openWebView("http://bangalore.quikr.com/html/privacy.php");
        }else if(position == 8){
            openWebView("http://hyderabad.quikr.com/html/policies.php");
        }
    }


    public interface ChangeTitle{
        void setTitle(String title);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((ChangeTitle)mContext).setTitle("Social Sharing");
    }
    private void openWebView(String url){

        ;//"http://prostinnovation.com/";
        Intent showWebSiteIntent = new Intent(mContext, Mobile_Site_Activity.class);
        // showWebSiteIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        showWebSiteIntent.putExtra("WEBSITE_NAME", url);
        mContext.startActivity(showWebSiteIntent);
        ((Social_Sharing_Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
