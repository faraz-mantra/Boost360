package com.nowfloats.education.batches;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.education.batches.ui.batchesfragment.BatchesFragment;
import com.nowfloats.education.faculty.ui.facultymanagement.FacultyManagementFragment;
import com.thinksity.R;

import java.util.HashMap;

public class BatchesActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private HashMap<String, Integer> hmPrices = new HashMap<>();
    public UserSessionManager session;
    public static TextView headerText;
    private Toolbar toolbar;

    private Fragment currentFragment = null;
    private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batches);
        initializeView();
        initView();
    }

    private void initView() {
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment currentFragment =
                        getSupportFragmentManager().findFragmentById(R.id.mainFrame);
                if (currentFragment != null) {
                    String tag = currentFragment.getTag();
                    Log.e("tag", ">>>$tag");
                } else {
                    finish();
                }
            }
        });
    }

    private void initializeView() {

        session = new UserSessionManager(this, this);

        toolbar = findViewById(R.id.app_bar);
        headerText = toolbar.findViewById(R.id.titleTextView);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }



        //testingPurpos
        getSupportFragmentManager().beginTransaction()
                .add(R.id.mainFrame, new BatchesFragment())
                // Add this transaction to the back stack
                .addToBackStack("Profile")
                .commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showLoader(final String message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(getApplicationContext());
                    progressDialog.setCanceledOnTouchOutside(false);
                }
                progressDialog.setMessage(message);
                progressDialog.show();
            }
        });
    }

    private void hideLoader() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    public void addFragment(Fragment fragment, String fragmentTag) {
        currentFragment = fragment;
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.mainFrame, fragment, fragmentTag);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.addToBackStack(fragmentTag);
        fragmentTransaction.commit();
    }

    public void replaceFragment(Fragment fragment, String fragmentTag) {
        popFragmentFromBackStack();
        addFragment(fragment, fragmentTag);
    }

    public void setHeaderText(String value){
        headerText.setText(value);
    }

    public void popFragmentFromBackStack() {
        fragmentManager.popBackStack();
    }

    @Override
    public void onBackPressed() {
        performBackPressed();
    }

    private void performBackPressed() {
        try {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                Fragment currentFragment =
                        getSupportFragmentManager().findFragmentById(R.id.mainFrame);
                String tag = currentFragment.getTag();
                Log.e("back pressed tag", ">>>$tag");
                popFragmentFromBackStack();
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}