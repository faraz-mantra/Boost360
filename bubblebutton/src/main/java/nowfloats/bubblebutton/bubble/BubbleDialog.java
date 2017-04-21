package nowfloats.bubblebutton.bubble;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;

import nowfloats.bubblebutton.R;
import nowfloats.bubblebutton.adapter.ProductSuggestionAdapter;


/**
 * Created by NowFloats on 4/12/2017.
 */

public class BubbleDialog extends AppCompatActivity {
    public static final String KILL_DIALOG = "KILL_DIALOG";
    private class KillListener extends BroadcastReceiver{

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
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenHeight = (int) (metrics.heightPixels * 0.80);

        setContentView(R.layout.dialog_products);
        RecyclerView recycler = (RecyclerView) findViewById(R.id.list);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        recycler.setAdapter(new ProductSuggestionAdapter(this));
        killListener = new KillListener();
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setWindowAnimations(R.animator.dialog_show);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, screenHeight);

    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(KILL_DIALOG);
        registerReceiver(killListener,intentFilter);
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


}
