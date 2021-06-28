package com.newfloats.staffs.ui;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.newfloats.staffs.R;

public class BaseFragmentStaff extends Fragment implements BaseStaffActivity.ToolBarAction {
    protected void launchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackButtonClick() {
        getActivity().onBackPressed();
    }

    @Override
    public void onAddButtonClick() {

    }
}
