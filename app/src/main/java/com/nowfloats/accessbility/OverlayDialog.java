package com.nowfloats.accessbility;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.thinksity.R;

/**
 * Created by Admin on 19-04-2017.
 */

public class OverlayDialog extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogForOverlayPath();
    }

    private MaterialDialog overLayDialog;
    private void dialogForOverlayPath(){

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_bubble_overlay_permission,null);
        ImageView image = (ImageView) view.findViewById(R.id.gif_image);
        TextView screenOverlay = (TextView) view.findViewById(R.id.overlay_title);
        screenOverlay.setVisibility(View.GONE);
        try {
            Glide.with(this).asGif().load(R.drawable.overlay_gif).into(image);
        }catch(Exception e){
            e.printStackTrace();
        }
        if(overLayDialog == null){
            overLayDialog = new MaterialDialog.Builder(this)
                    .title(getString(R.string.boost_bubble))
                    .customView(view,false)
                    .positiveColorRes(R.color.primary)
                    .positiveText(getString(R.string.open_setting))
                    .callback(new MaterialDialog.ButtonCallback() {

                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            try {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }catch(Exception e){
                                e.printStackTrace();
                            }finally {
                                dialog.dismiss();
                                finish();
                            }
                        }
                    }).show();
        }
    }
}
