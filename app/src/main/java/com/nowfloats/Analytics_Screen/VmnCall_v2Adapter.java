package com.nowfloats.Analytics_Screen;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.Analytics_Screen.model.VmnCallModel;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Admin on 23-06-2017.
 */

public class VmnCall_v2Adapter extends RecyclerView.Adapter<VmnCall_v2Adapter.MyHolder> {

    private static final int DATE_HEADER_VIEW = 0, VMN_CALL_VIEW = 1;
    public ConnectToVmnPlayer connectToVmn;
    NotificationManagerCompat notificationManager;
    private ArrayList<VmnCallModel> mList;
    private int currentPlay = -1;
    private Handler handler;
    private Context mContext;
    private NotificationCompat.Builder mBuilder;

    VmnCall_v2Adapter(Context context, ArrayList<VmnCallModel> list) {
        mContext = context;
        mList = list;
        handler = new Handler();
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = null;
        switch (viewType) {
            case DATE_HEADER_VIEW:
                convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_vmn_call_header, parent, false);
                break;
            case VMN_CALL_VIEW:
                convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_vmn_call_item, parent, false);
                break;
        }
        return new MyHolder(convertView, viewType);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, int position) {
        if (holder == null) {
            return;
        }
        final VmnCallModel childModel = mList.get(position);
        if (holder.viewType == DATE_HEADER_VIEW) {
            holder.header.setText(mList.get(position).getCallDateTime());
        } else {
            holder.date.setText(getDate(Methods.getFormattedDate(childModel.getCallDateTime())));
            if (childModel.getCallStatus().equalsIgnoreCase("MISSED")) {
                holder.date.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(mContext, R.drawable.ic_call_missed), null, null, null);
                holder.play.setText("Missed\nCall");
                holder.play.setTextColor(ContextCompat.getColor(mContext, R.color.gray_transparent));
                holder.play.setPaintFlags(holder.play.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
            } else {
                if (currentPlay == position) {
                    holder.play.setTextColor(ContextCompat.getColor(mContext, R.color.gray_transparent));
                } else {
                    holder.play.setTextColor(ContextCompat.getColor(mContext, R.color.gray));
                }
                holder.date.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(mContext, R.drawable.ic_call_received), null, null, null);
                holder.play.setText(mContext.getString(R.string.play_with_underline));
                holder.play.setPaintFlags(holder.play.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                holder.play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (((TextView) v).getText().toString().equalsIgnoreCase("play")) {
                            currentPlay = holder.getAdapterPosition();
                            holder.play.setTextColor(ContextCompat.getColor(mContext, R.color.gray_transparent));
                            connectToVmn = new ConnectToVmnPlayer(childModel);
//                        connectToVmn.setData(childModel);
                        }
                    }
                });

            }
            holder.number.setText(mList.get(position).getCallerNumber());
        }
    }

    private String getDate(String date) {
      /*  int comaIndex = DATE.indexOf(",");
        String subYear = DATE.substring(comaIndex,DATE.lastIndexOf(" at"));*/
        return date.replaceAll(",.*?at", /*"'"+subYear.substring(subYear.length()-2)*/ "");
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getViewType();

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private void downloadFile(String fileurl, String filename, final ImageView downloadImage) {
        // Log.v("ggg",Environment.getExternalStoragePublicDirectory().getAbsolutePath()+" "+Environment.getExternalStorageDirectory().getAbsolutePath());
        File file = initProfilePicFolder(filename);
        int count = 0;
        try {
            URL url = new URL(fileurl);
            URLConnection conexion = url.openConnection();
            conexion.connect();
            // this will be useful so that you can show a tipical 0-100% progress bar
            int lenghtOfFile = conexion.getContentLength();

            // downlod the file
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(file);

            byte data[] = new byte[1024];

            long total = 0;
            showDownloadNotification(file.getName());
            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                //Log.v("ggg",(int)(total*100/lenghtOfFile)+" ");
                int progress = (int) (total * 100 / lenghtOfFile);
                publishHandler(downloadImage, progress, file.getAbsolutePath());
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(ContextCompat.getColor(mContext, R.color.primary), PorterDuff.Mode.SRC_IN);
                    downloadImage.setColorFilter(porterDuffColorFilter);
                    Toast.makeText(mContext, mContext.getString(R.string.something_went_wrong_try_again), Toast.LENGTH_SHORT).show();
                }
            }, 400);
        }
    }

    private void publishHandler(final ImageView downloadImage, final int progress, final String path) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (progress == 100) {
                    PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(ContextCompat.getColor(mContext, R.color.primaryColor), PorterDuff.Mode.SRC_IN);
                    downloadImage.setColorFilter(porterDuffColorFilter);
                    mBuilder.setProgress(0, 0, false);
                    mBuilder.setContentText(mContext.getString(R.string.successfully_downloaded));
                    Toast.makeText(mContext, path + " " + mContext.getString(R.string.successfully_downloaded), Toast.LENGTH_SHORT).show();
                } else {
                    mBuilder.setProgress(100, progress, false);
                }
                notificationManager.notify(0, mBuilder.build());
            }
        }, 500);
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private File initProfilePicFolder(String file) {
        String dateFile = file + "_" + new SimpleDateFormat("ddMMyyyy_HH:mm", Locale.ENGLISH).format(new Date());
        File ProfilePicFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Boost/");
        if (!ProfilePicFolder.exists()) {
            ProfilePicFolder.mkdirs();
            //Log.v("ggg",ProfilePicFolder.mkdirs()+" ");
        }
        File ProfilePicFile;
        for (int i = 1; ; i++) {
            ProfilePicFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Boost/" + dateFile + ".mp3");
            if (ProfilePicFile.exists()) {
                if (i > 1) {
                    dateFile = dateFile.replaceAll("\\(" + (i - 1) + "\\)", "");
                }

                dateFile += "(" + i + ")";

            } else {
                try {
                    if (ProfilePicFile.createNewFile()) {
                        Log.d("ggg", "Successfully created the parent dir:" + ProfilePicFile.getName());
                    } else {
                        Log.d("ggg", "Failed to create the parent dir:" + ProfilePicFile.getName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return ProfilePicFile;
    }

    public void showDownloadNotification(String title) {
        notificationManager = NotificationManagerCompat.from(mContext);
        mBuilder = new NotificationCompat.Builder(mContext)
                .setAutoCancel(false)
                .setContentTitle(title)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.app_launcher))
                .setContentText(mContext.getString(R.string.downloading))
                .setColor(ContextCompat.getColor(mContext, R.color.primaryColor))
                .setSmallIcon(R.drawable.app_launcher2);
        notificationManager.notify(0, mBuilder.build());
    }

    public interface RequestPermission {
        void requestStoragePermission();
    }

    public class ConnectToVmnPlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, View.OnClickListener {
        private VmnMediaPlayer vmnMediaPlayer;
        private MediaHolder childHolder;
        Runnable updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (vmnMediaPlayer == null) {
                    return;
                }

                int duration = vmnMediaPlayer.getCurrentPosition();
                //int seekBarPos = seekBarPos(duration);
                String time = getTimeFromMilliSeconds(duration);
                Log.v("ggg", duration + " " + time);
                childHolder.seekBar.setProgress(duration);
                childHolder.recCurrPoint.setText(time);
                if (duration == vmnMediaPlayer.getDuration() || !isPlaying()) {
                    return;
                }

                handler.postDelayed(updateSeekBar, 1000);
            }
        };
        private VmnCallModel mediaData;

        private ConnectToVmnPlayer(VmnCallModel model) {
            mediaData = model;
            vmnMediaPlayer = VmnMediaPlayer.getInstance(mContext);
            vmnMediaPlayer.setUpPlayer(this);
            showDialog();
        }

        public void releaseResources() {
            if (vmnMediaPlayer != null) {
                vmnMediaPlayer.release();
                vmnMediaPlayer = null;
            }

        }

        private MaterialDialog setPlayerDialog() {
            View parent = LayoutInflater.from(mContext).inflate(R.layout.dialog_media_player, null, false);
            MaterialDialog mediaDialog = new MaterialDialog.Builder(mContext)
                    .customView(parent, false)
                    .cancelable(false)
                    .negativeColorRes(R.color.primary_color)
                    .negativeText(mContext.getString(R.string.call))
                    .positiveColorRes(R.color.gray_transparent)
                    .positiveText(mContext.getString(R.string.cancel))
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            dialog.dismiss();
                            vmnMediaPlayer.stop();
                            releaseResources();
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            super.onNegative(dialog);
                            pause();
                            Methods.makeCall(mContext, mediaData.getCallerNumber());
                        }
                    })
                    .build();
            childHolder = new MediaHolder(mediaDialog.getCustomView());

            childHolder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser)
                        vmnMediaPlayer.seekTo(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            childHolder.playButton.setOnClickListener(this);
            childHolder.downloadImage.setOnClickListener(this);

            mediaDialog.show();
            return mediaDialog;
        }

        private void showDialog() {
            if (!TextUtils.isEmpty(mediaData.getCallRecordingUri())) {
                setPlayerDialog();
                vmnMediaPlayer.setDataUrl(mediaData.getCallRecordingUri());
            } else {
                Toast.makeText(mContext, "Can't get recording url", Toast.LENGTH_SHORT).show();
            }
        }

        int seekBarPos(int duration) {
            return duration * 100 / vmnMediaPlayer.getDuration();
        }

        private boolean isPlaying() {
            return vmnMediaPlayer.isPlaying();
        }

        private String getTimeFromMilliSeconds(int pos) {
            int seconds = (pos / 1000) % 60;
            int minutes = ((pos / (1000 * 60)) % 60);
            return String.format(Locale.ENGLISH, "%d:%02d", minutes, seconds);
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            childHolder.playButton.setImageResource(R.drawable.ic_play_arrow);
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            childHolder.recEndPoint.setText(getTimeFromMilliSeconds(mp.getDuration()));
            childHolder.seekBar.setMax(mp.getDuration());
            start();
        }

        private void pause() {
            vmnMediaPlayer.pause();
        }

        private void start() {

            vmnMediaPlayer.start();
            vmnMediaPlayer.seekTo(childHolder.seekBar.getProgress());
            handler.postDelayed(updateSeekBar, 1000);
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return false;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgview_play:
                    if (isPlaying()) {
                        pause();
                        childHolder.playButton.setImageResource(R.drawable.ic_play_arrow);
                    } else {
                        start();
                        childHolder.playButton.setImageResource(R.drawable.ic_pause_gray);
                    }
                    break;
                case R.id.download:
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(ContextCompat.getColor(mContext, R.color.gray_transparent), PorterDuff.Mode.SRC_IN);
                        childHolder.downloadImage.setColorFilter(porterDuffColorFilter);
                        Toast.makeText(mContext, mContext.getString(R.string.downloading), Toast.LENGTH_SHORT).show();

                        if (isExternalStorageWritable()) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    downloadFile(mediaData.getCallRecordingUri(), mediaData.getCallerNumber(), childHolder.downloadImage);
                                }
                            }).start();
                        } else {
                            Toast.makeText(mContext, "External storage becomes unavailable", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        ((RequestPermission) mContext).requestStoragePermission();
                    }
                    break;

            }
        }
    }

    private class MediaHolder {

        TextView date, number, recEndPoint, recCurrPoint;
        SeekBar seekBar;
        ImageView downloadImage, playButton;

        MediaHolder(View itemView) {

            downloadImage = (ImageView) itemView.findViewById(R.id.download);
            date = (TextView) itemView.findViewById(R.id.date);
            number = (TextView) itemView.findViewById(R.id.number);
            playButton = (ImageView) itemView.findViewById(R.id.imgview_play);
            recEndPoint = (TextView) itemView.findViewById(R.id.tv_end_time);
            recCurrPoint = (TextView) itemView.findViewById(R.id.tv_current_time);
            seekBar = (SeekBar) itemView.findViewById(R.id.seekBar);
            number.setSelected(true);
        }
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView header, number, date, play;
        int viewType;

        public MyHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            switch (viewType) {
                case DATE_HEADER_VIEW:
                    header = (TextView) itemView.findViewById(R.id.header_text);
                    break;
                case VMN_CALL_VIEW:
                    itemView.findViewById(R.id.llayout_number).setOnClickListener(this);
                    itemView.findViewById(R.id.call_icon).setOnClickListener(this);
                    number = (TextView) itemView.findViewById(R.id.tv_number);
                    date = (TextView) itemView.findViewById(R.id.tv_date);
                    play = (TextView) itemView.findViewById(R.id.tv_play);
                    break;
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.llayout_number:
                case R.id.call_icon:
                    Methods.makeCall(mContext, mList.get(getAdapterPosition()).getCallerNumber());
                    break;
            }
        }
    }

}
