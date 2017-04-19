package com.nowfloats.accessbility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.nowfloats.Product_Gallery.Product_Gallery_Fragment;
import com.nowfloats.util.Key_Preferences;
import com.thinksity.R;


/**
 * Created by NowFloats on 4/12/2017.
 */

public class BubbleDialog extends AppCompatActivity implements ProductItemClickCallback {
    public static final String ACTION_KILL_DIALOG = "nowfloats.bubblebutton.bubble.ACTION_KILL_DIALOG";

    private Product_Gallery_Fragment productGalleryFragment;
    private FrameLayout mainFrame;
    private Button btnShare;
    String className;
    SparseArray<String> sharedProductUrls = new SparseArray<>();


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
        initialize();
        bindControls();
        loadData();
    }

    private void initialize() {

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenHeight = (int) (metrics.heightPixels * 0.80);
        mainFrame = (FrameLayout) findViewById(R.id.mainFrame);
        btnShare = (Button) findViewById(R.id.btnShare);
        className = getIntent().getStringExtra(Key_Preferences.WHATSAPP_CLASS);
        if(DataAccessbilityService.CLASS_NAME_WHATSAPP_CONVERSATION.equalsIgnoreCase(className)){
            btnShare.setText("Copy Products");
        }
        killListener = new KillListener();
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, screenHeight);

    }

    private void bindControls() {
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urls = arrayToStringUrl();
                if(urls.length() == 0){
                    Toast.makeText(BubbleDialog.this, "Please select at least one product", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.v("ggg",urls);
                if(DataAccessbilityService.CLASS_NAME_WHATSAPP_HOMEACTIVITY.equalsIgnoreCase(className)){
                    Intent intent  = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.setPackage(DataAccessbilityService.PK_NAME_WHATSAAPP);
                    intent.putExtra(Intent.EXTRA_TEXT,urls);
                    startActivity(Intent.createChooser(intent,"Share with:"));
                }else{
                    copyToClipboard(urls);
                }

                finish();
            }
        });
    }

    private Product_Gallery_Fragment product_gallery_fragment;

    private void loadData() {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        productGalleryFragment = new Product_Gallery_Fragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Product_Gallery_Fragment.KEY_FROM, Product_Gallery_Fragment.FROM.BUBBLE);
        productGalleryFragment.setArguments(bundle);
        ft.replace(R.id.mainFrame, productGalleryFragment).
                commit();

    }

    private void copyToClipboard(String message) {

        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(message);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("text label", message);
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(killListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    String arrayToStringUrl(){
        int size = sharedProductUrls.size();
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i<size ;i++){
            builder.append(sharedProductUrls.valueAt(i));
            builder.append("\n");
        }
        sharedProductUrls.clear();
        return builder.toString();
    }
    @Override
    public void addItemUrl(int index,String url) {
        sharedProductUrls.put(index,url);
        Log.v("ggg","add "+index+" "+url);
    }

    @Override
    public void deleteItemUrl(int index) {
        sharedProductUrls.delete(index);
        Log.v("ggg","delete "+index);
    }
}
