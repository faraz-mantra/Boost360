package com.nowfloats.manufacturing.projectandteams;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.manufacturing.projectandteams.ui.projectandteams.ProjectAndTeamsFragment;
import com.nowfloats.manufacturing.projectandteams.ui.projectandteamsdetails.ProjectDetailsFragment;
import com.nowfloats.manufacturing.projectandteams.ui.projectandteamsdetails.TeamsDetailsFragment;
import com.thinksity.R;

import java.util.HashMap;

public class ProjectAndTermsActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private HashMap<String, Integer> hmPrices = new HashMap<>();
    public UserSessionManager session;

    private Fragment currentFragment = null;
    private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_and_teams);
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
                } else {
                    finish();
                }
            }
        });
    }

    private void initializeView() {

        session = new UserSessionManager(this, this);

        //testingPurpos
        addFragment(new ProjectAndTeamsFragment(), "PROJECT_AND_TEAMS_FRAGMENT");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_projectteams, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            onBackPressed();
//            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
//            return true;
//        }else if(item.getItemId() == R.id.action_setting){
//            showPopup(findViewById(item.getItemId()));
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    // custom method
//    private void showPopup(final View view) {
//        Context wrapper = new ContextThemeWrapper(this, R.style.MyPopupMenuStyleWhite);
//        PopupMenu popupMenu = new PopupMenu(wrapper, view);
//        popupMenu.getMenu().add(0, 0, Menu.NONE, "Add Project");
//        popupMenu.getMenu().add(0, 1, Menu.NONE, "Add Team");
//        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                if(item.getTitle().equals("Add Project")){
//                    addFragment(new ProjectDetailsFragment(),"PROJECT_DETAILS_FRAGMENT");
//                }else if(item.getTitle().equals("Add Team")){
//                    addFragment(new TeamsDetailsFragment(),"TEAM_DETAILS_FRAGMENT");
//                }
//                return true;
//            }
//        });
//        popupMenu.show();
//    }

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