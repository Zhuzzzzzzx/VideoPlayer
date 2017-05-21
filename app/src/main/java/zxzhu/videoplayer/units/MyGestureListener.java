package zxzhu.videoplayer.units;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

import zxzhu.videoplayer.Activity.Content;
import zxzhu.videoplayer.R;

import static zxzhu.videoplayer.Activity.Content.ISPLAY;

/**
 * Created by Zhuzzzzzzx on 2017/5/21.
 */

public class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

    private static float MIN_DISTANCE = 300 ;
    private static float MAX_DISTANCE = 200;
    private static float MIN_VELOCITY = 350;
    private Video video;
    private ImageView play;
    private SeekBar seekBar;
    private static String TAG = "MyGestureListener";
    private Context context = null;
    private ContentResolver contentResolver;
    private int brightPre,voicePre;
    private int bright,voice;
    private AudioManager audioMgr;

    public MyGestureListener(Context context, Video video,
                             ImageView play, SeekBar seekBar,
                             int brightPre,int voicePre){
        this.context = context;
        this.video = video;
        this.play = play;
        this.seekBar = seekBar;
        this.brightPre = brightPre;
        this.voicePre = voicePre;
        bright = brightPre;
        contentResolver = context.getContentResolver();
        audioMgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        voice = 0;
    }
    float disX = 0;
    float disY = 0;
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        Log.d("e1.getX():","e1.getX(): "+String.valueOf(e1.getX()));
        Log.d("e2.getX():","e2.getX(): "+String.valueOf(e2.getX()));
        Log.d("velocityX","velocityX: "+distanceX);
        Log.d("velocityY","velocityY: "+distanceY);
        disY = distanceY + disY;
        disX = distanceX + disX;
//        Log.d(TAG, "onScroll: "+disX);
//        Log.d(TAG, "onScroll: "+disY);
        //左右滑动调节进度
        if (disX<-100||disX>100) {

                video.start();
                video.pause();
                seekBar.setProgress(seekBar.getProgress() - (int) disX / 100);
        }
        //屏幕左侧滑动调节亮度
        else if ((int)(e1.getX())<500){
            if (bright>-1&&bright<256) {
                bright = bright + (int) (disY*255/1500);
            }
            if (bright>255){
                bright = 255;
            }
            if (bright<0){
                bright = 0;
            }
            Log.d(TAG, "onScroll: "+bright);

            Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS,bright );
        }//屏幕右侧滑动调节音量
            else if((int)(e1.getX())>500){
            if (voice>-1&&bright<41) {
                voice = voice + (int) (disY*50/1500);
            }
            if (voice>40){
                voice = 40;
            }
            if (voice<0){
                voice = 0;
            }

            audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, voice,
                    AudioManager.FLAG_PLAY_SOUND);
        }
        return super.onScroll(e1, e2, distanceX, distanceY);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (disX<-100||disX>100) {
            if (disX > 0) {
                Toast.makeText(context, "回退", Toast.LENGTH_SHORT).show();
            } else if (disX < 0) {
                Toast.makeText(context, "快进", Toast.LENGTH_SHORT).show();
            }
        }
        disX = 0;
        disY = 0;
        return super.onFling(e1, e2, velocityX, velocityY);
    }

}
