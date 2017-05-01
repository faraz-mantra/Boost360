package com.nowfloats.Analytics_Screen.Search_Query_Adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nowfloats.Analytics_Screen.VmnMediaPlayer;
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
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Admin on 27-04-2017.
 */

public class VmnCallAdapter extends BaseExpandableListAdapter {


    private final Handler handler;
    private ArrayList<ArrayList<VmnCallModel>> listData;
    private Context mContext;
    private ConnectToVmnPlayer connectToVmn;
    public VmnCallAdapter(Context context,ArrayList<ArrayList<VmnCallModel>> hashMap){
        mContext = context;
        listData = hashMap;
        handler = new Handler();
        connectToVmn = new ConnectToVmnPlayer();
    }

    @Override
    public int getGroupCount() {
        return listData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listData.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listData.get(groupPosition).get(0).getCallerNumber();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listData.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        MyParentHolder parentHolder;
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_vmn_call_item, parent, false);
            parentHolder = new MyParentHolder(convertView);
            convertView.setTag(parentHolder);

        }else{
            parentHolder = (MyParentHolder) convertView.getTag();
        }
        parentHolder.callerNumber.setText((String)getGroup(groupPosition));

        if (isExpanded) {
            parentHolder.arrowImage.setImageResource(R.drawable.ic_arrow_drop_up);
        }else {
            parentHolder.arrowImage.setImageResource(R.drawable.ic_arrow_drop_down);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final MyChildHolder childHolder;
        final VmnCallModel childModel = (VmnCallModel) getChild(groupPosition, childPosition);
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.vmn_call_child_item, parent, false);
            childHolder = new MyChildHolder(convertView);
            convertView.setTag(childHolder);

        }else{
            childHolder = (MyChildHolder) convertView.getTag();
        }
        childHolder.date.setText(Methods.getFormattedDate(childModel.getCallDateTime()));
        if(childModel.getCallStatus().equalsIgnoreCase("MISSED")){
            childHolder.callImage.setImageResource(R.drawable.ic_call_missed);
            childHolder.mediaImage.setVisibility(View.GONE);
            childHolder.downloadImage.setVisibility(View.GONE);
            childHolder.progressBar.setVisibility(View.GONE);
            childHolder.mediaLayout.setVisibility(View.GONE);
        }else{
            childHolder.mediaLayout.setVisibility(View.VISIBLE);
            childHolder.callImage.setImageResource(R.drawable.ic_call_received);
            childHolder.mediaImage.setVisibility(View.VISIBLE);
            childHolder.downloadImage.setVisibility(View.VISIBLE);
            childHolder.recEndPoint.setText("/ "+childModel.getCallDuration());
            childHolder.seekBar.setMax(100);
            if(connectToVmn.isSameUrl(childModel.getCallRecordingUri())){
                childHolder.seekBar.setProgress(connectToVmn.getSeekCurrentPos());
                childHolder.recCurrPoint.setText(connectToVmn.getrecCurrPoint());
                childHolder.playButton.setImageResource(connectToVmn.isPlaying() ? R.drawable.ic_pause_light : R.drawable.ic_play_light);
            }else{
                childHolder.seekBar.setProgress(0);
                childHolder.recCurrPoint.setText("0:00");
                childHolder.playButton.setImageResource(R.drawable.ic_play_light);
            }
            childHolder.downloadImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    childHolder.progressBar.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            downloadFile(childModel.getCallRecordingUri(),childModel.getId(),childHolder.progressBar);
                        }
                    }).start();
                }
            });
            childHolder.playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    connectToVmn.setDataUri(childModel.getCallRecordingUri(),childHolder);
                }
            });
        }

        return convertView;
    }

    public class ConnectToVmnPlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
        VmnMediaPlayer vmnMediaPlayer;
        MyChildHolder childHolder;
        ImageView prevPlayToPause;

        private ConnectToVmnPlayer(){
            vmnMediaPlayer = VmnMediaPlayer.getInstance(mContext);
            vmnMediaPlayer.setUpPlayer(this);
        }

        private void releaseResources(){
            vmnMediaPlayer.release();
        }
        private void setDataUri(String url,MyChildHolder holder){
            if(isSameUrl(url))
            {
                if(isPlaying())
                {
                    pause();
                    childHolder.playButton.setImageResource(R.drawable.ic_play_light);
                }else
                {
                    start();
                    childHolder.playButton.setImageResource(R.drawable.ic_pause_light);
                }
            }else
            {
                //stop prev start new
                setHolder(holder);
                vmnMediaPlayer.setDataUrl(url);
                childHolder.playButton.setImageResource(R.drawable.ic_pause_light);
            }
        }

        public void setHolder(MyChildHolder holder){
            setPrevPlayToPause();
            childHolder = holder;
            prevPlayToPause = childHolder.playButton;
        }

        public int getSeekCurrentPos(){
            return seekBarPos(vmnMediaPlayer.getCurrentPosition());
        }
        Runnable updateSeekBar = new Runnable() {
            @Override
            public void run() {
                int duration=vmnMediaPlayer.getCurrentPosition();
                int seekBarPos = seekBarPos(duration);
                String time=getTimeFromMilliSeconds(duration);
                Log.v("ggg",duration+" "+time);
                if(duration == vmnMediaPlayer.getDuration() || !isPlaying()){
                    return;
                }
                childHolder.seekBar.setProgress(seekBarPos);
                childHolder.recCurrPoint.setText(time);
                handler.postDelayed(updateSeekBar,1000);
            }
        };

        int seekBarPos(int duration){
            return duration*100 / vmnMediaPlayer.getDuration();
        }
        void setPrevPlayToPause(){
            if(prevPlayToPause == null){
                Log.v("ggg","first recording play");
                return;
            }
            prevPlayToPause.setImageResource(R.drawable.ic_play_light);
        }
        private boolean isSameUrl(String callRecordingUri) {
            return callRecordingUri.equals(vmnMediaPlayer.getUrl());
        }

        private boolean isPlaying(){
            return vmnMediaPlayer.isPlaying();
        }

        private String getTimeFromMilliSeconds(int pos) {
            int seconds = (pos / 1000) % 60 ;
            int minutes = ((pos / (1000*60)) % 60);
            return String.format(Locale.ENGLISH,"%d:%02d", minutes,seconds);
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            childHolder.recCurrPoint.setText("0:00");
            childHolder.playButton.setImageResource(R.drawable.ic_play_light);
            childHolder.seekBar.setProgress(0);
            vmnMediaPlayer.stop();
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            start();
        }

        private void pause(){
            vmnMediaPlayer.pause();
        }
        private void start() {
            vmnMediaPlayer.start();
            vmnMediaPlayer.seekTo(childHolder.seekBar.getProgress());
            handler.postDelayed(updateSeekBar,1000);
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return false;
        }

        String getrecCurrPoint() {
            int duration = vmnMediaPlayer.getCurrentPosition();
            return getTimeFromMilliSeconds(duration);
        }
    }
    public void releaseResources(){
        connectToVmn.releaseResources();
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }


    private class MyParentHolder {

        TextView callerNumber;
        ImageView arrowImage;
        MyParentHolder(View itemView) {
            callerNumber = (TextView) itemView.findViewById(R.id.caller_number);
            arrowImage = (ImageView) itemView.findViewById(R.id.arrowImage);
        }
    }

    public class MyChildHolder {

        public TextView date,recEndPoint,recCurrPoint;
        public ImageView mediaImage, callImage,downloadImage,playButton;
        public ProgressBar progressBar;
        LinearLayout mediaLayout;
        SeekBar seekBar;

        public MyChildHolder(View itemView) {
            date = (TextView) itemView.findViewById(R.id.tv_date);
            mediaImage = (ImageView) itemView.findViewById(R.id.media_img);
            callImage = (ImageView) itemView.findViewById(R.id.call_img);
            downloadImage = (ImageView) itemView.findViewById(R.id.download_img);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
            playButton = (ImageView) itemView.findViewById(R.id.imgview_play);
            recEndPoint = (TextView) itemView.findViewById(R.id.tv_end_time);
            recCurrPoint = (TextView) itemView.findViewById(R.id.tv_current_time);
            seekBar = (SeekBar) itemView.findViewById(R.id.seekBar);
            mediaLayout = (LinearLayout) itemView.findViewById(R.id.media_layout);
        }
    }

    private void downloadFile(String fileurl, String filename, final ProgressBar progress){
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

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                Log.v("ggg",(int)(total*100/lenghtOfFile)+" ");
                publishHandler(progress,total,lenghtOfFile);
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            Log.v("ggg",count+" "+e.getMessage());
        }
    }

    private void publishHandler(final ProgressBar progress, final long total, final int lengthOfFile){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progress.setProgress((int)(total*100/lengthOfFile));
                if(progress.getProgress() == 100){
                    progress.setProgress(0);
                    progress.setVisibility(View.GONE);
                }
            }
        },400);
    }
    private File initProfilePicFolder(String file) {
        File ProfilePicFolder = new File(Environment.getExternalStorageDirectory() + File.separator + "nowfloats/");
        if (!ProfilePicFolder.exists()) {
            ProfilePicFolder.mkdirs();
        }
        File ProfilePicFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "nowfloats/"+file+".mp3");
        if (!ProfilePicFile.exists()) {
            try {
                if (ProfilePicFile.createNewFile()) {
                    Log.d("ggg","Successfully created the parent dir:" + ProfilePicFile.getName());
                } else {
                    Log.d("ggg","Failed to create the parent dir:" + ProfilePicFile.getName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ProfilePicFile;
    }
}

