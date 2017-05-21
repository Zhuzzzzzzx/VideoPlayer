package zxzhu.videoplayer.units;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import zxzhu.videoplayer.R;

import static android.R.id.message;
import static java.lang.Thread.sleep;

/**
 * Created by Zhuzzzzzzx on 2017/5/19.
 */

public class Video implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener {
    /**
     * Called when the activity is first created.
     */
    private static String TAG = "Video";
    private MediaPlayer player;
    private SurfaceView surface;
    private Context context;
    private SeekBar bar;
    private String url;
    private SurfaceHolder surfaceHolder;
    private int total = 0;

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

    }

    public interface CallBack {
        void onFinish(String time);
    }

    public interface CallBack_bitmap {
        void onFinish(Bitmap bitmap);
    }


    public Video(Context context) {
        this.context = context;
    }

    public int getHight() {
        return player.getVideoHeight();
    }

    public int getWidth() {
        return player.getVideoWidth();
    }

    public void into(SurfaceView surface) {
        this.surface = surface;
        initView();
    }

    public void start() {
        int memory = bar.getProgress() * total / 100;
        Log.d("memory", "start: "+memory);
        Log.d("memory", "start: "+total);
        player.start();
        player.seekTo(memory);
        Log.d(TAG, "start: 开始播放");
    }

    public void setCurrentTime(final SeekBar seekBar, final TextView time_current, final TextView time_total) {
        this.bar = seekBar;
        total = player.getDuration();
        final boolean[] ISTOUCHED = {false};
        final Handler handler = new Handler(){
            public void handleMessage(Message message){
                Log.d(TAG, "getMessageName: "+message.arg1 * 100 / message.arg2);
                if(!ISTOUCHED[0]) {
                    seekBar.setProgress(message.arg1 * 100 / message.arg2);
                }
                time_total.setText(""+secToTime(message.arg2/1000));
                time_current.setText(""+secToTime(message.arg1/1000));
            }
        };

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                time_current.setText(""+secToTime(seekBar.getProgress() * total / 100000));
                Log.d(TAG, "onProgressChanged: "+seekBar.getProgress() * total / 100000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                ISTOUCHED[0] = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (player != null) {
                    player.seekTo(seekBar.getProgress() * total / 100);
                    int current = player.getCurrentPosition();
                    int total = player.getDuration();
                    time_total.setText(""+secToTime(total/1000));
                    time_current.setText(""+secToTime(current/1000));
                    ISTOUCHED[0] = false;
                }
            }
        });
        Timer timer = new Timer();
        new Thread(new Runnable() {
            @Override
            public void run() {


                while(true) {
                    if (player.isPlaying()) {
                        Message message = new Message();
                        message.what = 1;
                        int current = player.getCurrentPosition();
                        int total = player.getDuration();
                        message.arg1 = current;
                        message.arg2 = total;
                        Log.d(TAG, "run: " + total);
                        handler.sendMessage(message);
                    }
                }
            }
        }).start();
    }

    public void setUrl(String url,String id) {
        try {
            File file = new File("/storage/emulated/0/Android/data/zxzhu.videoplayer/files/zxZhu's_VideoPlayer/"+id+".mp4");
            if (file.exists()){
                Toast.makeText(context,"从本地加载",Toast.LENGTH_SHORT).show();
                url = "/storage/emulated/0/Android/data/zxzhu.videoplayer/files/zxZhu's_VideoPlayer/"+id+".mp4";
            }
            player.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getDuration() {
        return player.getDuration();
    }

    public void reset() {
        player.reset();
    }

    public void prepare() {
        try {
            player.prepare();
            Log.d(TAG, "prepare: 准备完毕");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        if (player.isPlaying()) {
            player.pause();
            Log.d(TAG, "pause: 暂停");
        }
    }

    public void load(String url,String id) {
        File file = new File("/storage/emulated/0/Android/data/zxzhu.videoplayer/files/zxZhu's_VideoPlayer/"+id+".mp4");
        if (file.exists()){
            Toast.makeText(context,"从本地加载",Toast.LENGTH_SHORT).show();
            url = "/storage/emulated/0/Android/data/zxzhu.videoplayer/files/zxZhu's_VideoPlayer/"+id+".mp4";
        }
        this.url = url;
    }

    public void stop() {
        if (player.isPlaying()) {
            player.stop();
            player.release();
            Log.d(TAG, "stop: 停止释放");
        }
    }

    public void setPlay(int time) {
        player.seekTo(time);
    }

    public boolean isPlay() {
        return player.isPlaying();
    }

    private void initView() {
        surfaceHolder = surface.getHolder(); // SurfaceHolder是SurfaceView的控制接口
        surfaceHolder.addCallback(this); // 因为这个类实现了SurfaceHolder.Callback接口，所以回调参数直接this
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.setDataSource(url);
            player.prepare();
            player.seekTo(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        // 必须在surface创建后才能初始化MediaPlayer,否则不会显示图像

        player.setDisplay(surfaceHolder);
        // 设置显示视频显示在SurfaceView上

        //播放完后的回调
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Toast.makeText(context,"播放完毕",Toast.LENGTH_SHORT).show();
                player.seekTo(0);
            }
        });

    }


    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub
        if (player.isPlaying())

        {
            player.stop();
            player.release();
            player.reset();
        }
//     Activity销毁时停止播放，释放资源。不做这个操作，即使退出还是能听到视频播放的声音
    }


    public static void getFirstFrame(final String mUrl, final CallBack_bitmap callBack) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: ");
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(mUrl, new HashMap<String, String>());
                Bitmap bitmap;
                bitmap = mediaMetadataRetriever.getFrameAtTime(1000 * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

                if (callBack != null) {
                    callBack.onFinish(bitmap);

                }
            }
        }).start();
    }

    public static void getTotleTime(final String mUrl, final CallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(mUrl, new HashMap<String, String>());
                String time = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

                if (callBack != null) {
                    callBack.onFinish(time);
                }
            }
        });

    }

    /**
     * 将整形转化成时分秒格式字符串
     * @param time
     * @return
     */
    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }
}