package com.nowfloats.SellerProfileV2;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.nowfloats.SellerProfileV2.Fragment.OperationModeFragment;
import com.nowfloats.SellerProfileV2.Fragment.SellerPofileV2OtherDetailsFragment;
import com.thinksity.R;

public class SellerProfileV2Activity extends AppCompatActivity implements OperationModeFragment.OperationModeInterface , SellerPofileV2OtherDetailsFragment.OnAddressSelectedListener {

    private int operationModeIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_profile_v2);

        askToSelectOperationMode();

    }

    private void askToSelectOperationMode() {
        OperationModeFragment operationModeFragment = OperationModeFragment.newInstance(this);
        getSupportFragmentManager().beginTransaction().add(R.id.fl_sellerProfile , operationModeFragment).commit();
    }

    @Override
    public void onInterfaceSelected(int operationIndex) {
        operationModeIndex = operationIndex;

    }

    @Override
    public void onAddressSelected(Uri uri) {

    }
}
