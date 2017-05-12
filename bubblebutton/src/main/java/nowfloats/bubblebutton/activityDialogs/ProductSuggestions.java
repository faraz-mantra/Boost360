package nowfloats.bubblebutton.activityDialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.Window;

import nowfloats.bubblebutton.R;
import nowfloats.bubblebutton.adapter.ProductSuggestionAdapter;

/**
 * Created by Admin on 13-04-2017.
 */

public class ProductSuggestions extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        DisplayMetrics matrics = getResources().getDisplayMetrics();
        int screenWidth = (matrics.widthPixels );
        int screenHeight = (int) (matrics.heightPixels * 0.80);
        setContentView(R.layout.dialog_products);
        getWindow().setLayout(screenWidth, screenHeight);
        //View view = LayoutInflater.from(this).inflate(R.layout.dialog_products,null);
      /*  WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.x=0;
        lp.y=200;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height =WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity=Gravity.TOP | Gravity.END;
        lp.flags= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        lp.format = PixelFormat.RGBA_8888;

        getWindow().setContentView(view, lp);*/

        RecyclerView recycler = (RecyclerView) findViewById(R.id.list);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        recycler.setAdapter(new ProductSuggestionAdapter(this));
    }
}
