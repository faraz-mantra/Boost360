package com.nowfloats.Image_Gallery;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.squareup.picasso.Picasso;
import com.thinksity.R;
import com.thinksity.databinding.ActivityImageViewerBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ImageViewerActivity extends AppCompatActivity
{

    ActivityImageViewerBinding binding;
    private MyPagerAdapter adapter;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_viewer);

        position = getIntent().getIntExtra("POSITION", 0);

        List<String> images = new ArrayList<>(Arrays.asList(getIntent().getStringArrayExtra("IMAGES")));
        binding.maxCount.setText(String.valueOf(images.size()));
        binding.current.setText(String.valueOf(position + 1));

        adapter = new MyPagerAdapter(ImageViewerActivity.this, images);

        binding.pager.setAdapter(adapter);
        binding.pager.setCurrentItem(position);
        binding.pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

                position = i;
                binding.current.setText(String.valueOf(position + 1));
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        makeActivityAppearOnLockScreen();
    }


    public class MyPagerAdapter extends PagerAdapter
    {
        private Context context;
        private List<String> images;
        private LayoutInflater layoutInflater;


        public MyPagerAdapter(Context context, List<String> images)
        {
            this.context = context;
            this.images = images;
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount()
        {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object)
        {
            return view == object;
        }

        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int i)
        {
            View itemView = layoutInflater.inflate(R.layout.image_adapter_item, container, false);

            final ImageView imageView = itemView.findViewById(R.id.imgFullScreen);

            String url = images.get(i);

            try
            {
                if(!TextUtils.isEmpty(url))
                {
                    Picasso.with(context).load(url).into(imageView);
                }
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }

            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object)
        {
            container.removeView((RelativeLayout) object);
            notifyDataSetChanged();
        }
    }


    public void onNextClick(View view)
    {
        if(position < adapter.getCount())
        {
            position++;
            binding.pager.setCurrentItem(position);
        }
    }

    public void onPreviousClick(View view)
    {
        if(position > 0)
        {
            position--;
            binding.pager.setCurrentItem(position);
        }
    }

    public void onClose(View view)
    {
        finish();
    }

    public void onDelete(View view)
    {
        new AlertDialog.Builder(ImageViewerActivity.this)
                .setMessage("Are you sure, you want to delete?")
                .setPositiveButton("Yes", (dialog, which)-> {

                    deleteApiCall(adapter.images.get(position));
                    dialog.dismiss();
                })
                .setNegativeButton("No", (dialog, which)-> dialog.dismiss())
                .show();
    }

    private void makeActivityAppearOnLockScreen()
    {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }


    private void deleteApiCall(String url)
    {
        UserSessionManager session = new UserSessionManager(getApplicationContext(), ImageViewerActivity.this);

        ImageApi imageApi = Constants.restAdapter.create(ImageApi.class);

        Map<String, String> map = new HashMap<>();
        map.put("clientId", Constants.clientId);
        map.put("fpId", session.getFPID());
        map.put("ExistingBackgroundImageUri", url);
        map.put("identifierType", "SINGLE");

        imageApi.deleteBackgroundImages(map, new Callback<Object>() {

            @Override
            public void success(Object strings, Response response) {

                if(response.getStatus() == 200)
                {
                    Toast.makeText(getApplicationContext(), "Image Deleted Successfully", Toast.LENGTH_SHORT).show();

                    runOnUiThread(()-> {

                        adapter.images.remove(position);
                        adapter.notifyDataSetChanged();

                        if (position > 0)
                        {
                            position--;
                            binding.pager.setCurrentItem(position);
                            binding.current.setText(String.valueOf(position + 1));
                        }

                        binding.maxCount.setText(String.valueOf(adapter.getCount()));
                    });
                }
            }

            @Override
            public void failure(RetrofitError error) {

                Toast.makeText(getApplicationContext(), "Fail to Delete Image", Toast.LENGTH_SHORT).show();
            }
        });
    }
}