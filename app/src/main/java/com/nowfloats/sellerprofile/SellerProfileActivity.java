package com.nowfloats.sellerprofile;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.LinearLayout;

import com.nowfloats.managenotification.ExpandableCardView;
import com.thinksity.R;

public class SellerProfileActivity extends AppCompatActivity implements ExpandableCardView.OnExpandedListener {

//    private ExpandableCardView ecvSiteAppearance, ecvBankDetails;

    private LinearLayout llSignature;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_profile);
//        ecvSiteAppearance = findViewById(R.id.ecvSiteAppearance);
//        ecvBankDetails = findViewById(R.id.ecvBankDetails);

//        ecvSiteAppearance.setOnExpandedListener(this);
//        ecvBankDetails.setOnExpandedListener(this);

    }

    private void initViews() {
        llSignature = findViewById(R.id.llSignature);
    }

    @Override
    public void onExpandChanged(int id, View v, boolean isExpanded) {

    }

    private void bindSignature() {

//        mSignatureView = new SignatureView(SellerProfileActivity.this);
//        mSignatureView.setDrawingCacheEnabled(true);
//        mSignatureView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                (int) (180 * px)));
//        mSignatureView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
//        llSignature.addView(mSignatureView);
    }
}
