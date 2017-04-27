package com.nowfloats.accessbility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.nowfloats.Product_Gallery.Product_Gallery_Fragment;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;


/**
 * Created by NowFloats on 4/12/2017.
 */

public class BubbleDialog extends AppCompatActivity {
    public static final String ACTION_KILL_DIALOG = "nowfloats.bubblebutton.bubble.ACTION_KILL_DIALOG";

    private Product_Gallery_Fragment productGalleryFragment;
    private FrameLayout mainFrame;
    private Button btnShare;
    private MaterialSearchView searchView;

    private class KillListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }

    private KillListener killListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_products);
        MixPanelController.track(MixPanelController.BUBBLE_DIALOG,null);
        initialize();
        bindControls();
        loadData();
    }

    private void initialize() {

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenHeight = (int) (metrics.heightPixels * 0.80);
        mainFrame = (FrameLayout) findViewById(R.id.mainFrame);
        btnShare = (Button) findViewById(R.id.btnShare);
        searchView = (MaterialSearchView) findViewById(R.id.searchView);
        killListener = new KillListener();
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, screenHeight);
        searchView.setVoiceSearch(false);
        searchView.setEllipsize(true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.select_a_product_to_share));
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.white));
    }

    private void bindControls() {
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ArrayList<Uri> arrUri = productGalleryFragment.getSelectedProducts();
//                if (arrUri != null && arrUri.size() > 0) {
//                    navigateToWhatsApp(arrUri);
//                } else {
//
//                }
                String selectedProducts = productGalleryFragment.getSelectedProducts();
                if (!TextUtils.isEmpty(selectedProducts)) {
                    navigateToWhatsApp(selectedProducts);
                } else {

                }

            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (productGalleryFragment != null) {
                    productGalleryFragment.filterProducts(s);
                }
                return false;
            }
        });

    }

    private void loadData() {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        productGalleryFragment = new Product_Gallery_Fragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Product_Gallery_Fragment.KEY_FROM, Product_Gallery_Fragment.FROM.BUBBLE);
        productGalleryFragment.setArguments(bundle);
        ft.replace(R.id.mainFrame, productGalleryFragment).
                commit();

    }

//    private void navigateToWhatsApp(ArrayList<Uri> localArrayList) {
//        Intent sendIntent = new Intent();
//        sendIntent.setAction(Intent.ACTION_SEND);
//        sendIntent.setType("image/jpeg");
//        sendIntent.setPackage(DataAccessbilityServiceV2.PK_NAME_WHATSAPP);
//        sendIntent.putExtra(Intent.EXTRA_TEXT,"");
//        sendIntent.putParcelableArrayListExtra("android.intent.extra.STREAM", localArrayList);
//        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        startActivity(sendIntent);
//    }

    private void navigateToWhatsApp(String message) {
        MixPanelController.track(MixPanelController.BUBBLE_DIALOG_SHARE,null);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
        sendIntent.setPackage(DataAccessbilityServiceV2.PK_NAME_WHATSAPP);
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(sendIntent);
    }

    private void copyToClipboard(String message) {

        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText("text to clip");
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("text label", "text to clip");
            clipboard.setPrimaryClip(clip);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_KILL_DIALOG);
        registerReceiver(killListener, intentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bubble, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(killListener);
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
        overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
