package com.nowfloats.NavigationDrawer.businessApps;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thinksity.R;


/**
 * Created by Abhi on 12/14/2016.
 */

public class BusinessAppScreenOneFragment extends Fragment {

    public final static int[] circleImages={R.drawable.businessapp_second,R.drawable.businessapp_second,
            R.drawable.businessapp_fourth,R.drawable.businessapp_fourth};
    int position=-1;
    private String[] boldTextsArray,smallTextsArray;

    public static Fragment getInstance(int position){
        Fragment frag=new BusinessAppScreenOneFragment();
        Bundle b=new Bundle();
        b.putInt("position",position);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            position=getArguments().getInt("position");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_business_screens,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        boldTextsArray=getResources().getStringArray(R.array.businessboldtext);
        smallTextsArray=getResources().getStringArray(R.array.businesssmalltext);

        TextView boldText= (TextView) view.findViewById(R.id.business_screen_bold_text);
        TextView smallText= (TextView) view.findViewById(R.id.business_screen_small_text);
        LinearLayout layout= (LinearLayout) view.findViewById(R.id.llayout);

        CardView cardView= (CardView) view.findViewById(R.id.business_screen_cardview);
        ImageView circleImage= (ImageView) view.findViewById(R.id.imageview_circle);

        //(cardView.getBackground()).setColorFilter(circleColors[position],PorterDuff.Mode.MULTIPLY);
        //circleImage.setImageDrawable(ContextCompat.getDrawable(getContext(),circleImages[position]));
        Glide.with(this).load(circleImages[position]).into(circleImage);
        boldText.setText(boldTextsArray[position]);
        smallText.setText(smallTextsArray[position]);
    }


}
