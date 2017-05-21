package zxzhu.videoplayer.Activity;

import android.app.DownloadManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import zxzhu.videoplayer.R;
import zxzhu.videoplayer.beans.Data;
import zxzhu.videoplayer.units.MyGestureListener;
import zxzhu.videoplayer.units.Video;

public class Content extends AppCompatActivity {
    private String url;
    private List<String> urls;
    private List<String> texts;
    private List<String> ids;
    private List<String> share_urls;
    private final static String TAG = "Content";
    private ImageView play, next, last, direction, download, share, back;
    private Toolbar toolbar;
    private TextView time_current, time_total, title;
    private SurfaceView surfaceView;
    private Content context = Content.this;
    public static int ISPLAY = 0;
    private Video video;
    private SeekBar seekBar;
    private int position;
    private Animation alpha_in,alpha_out;
    private LinearLayout buttons;
    private GestureDetector detector;
    private boolean isImgClick = true;
    private long mLastTime = 0;
    private long mCurTime = 0;
    private int screenModePre = 1;
    private int screenBrightnessPre = 255;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!Settings.System.canWrite(this)){
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent,0);
        }
        setContentView(R.layout.activity_content);
        screenBrightnessPre = getBright();
        screenModePre = getScreenMode();
        init();
        setVideo();
        setOthers();
        setClick();

    }

    private void init() {
        Intent intent = getIntent();
        ids = intent.getStringArrayListExtra("ids");
        urls = intent.getStringArrayListExtra("urls");
        texts = intent.getStringArrayListExtra("texts");
        share_urls = intent.getStringArrayListExtra("share_urls");
        position = intent.getIntExtra("position", 0);
        url = urls.get(position);
        Log.d(TAG, "onCreate: " + texts.get(0));
        play = (ImageView) findViewById(R.id.play);
        next = (ImageView) findViewById(R.id.next);
        last = (ImageView) findViewById(R.id.last);
        direction = (ImageView) findViewById(R.id.direction);
        download = (ImageView) findViewById(R.id.download);
        share = (ImageView) findViewById(R.id.share);
        back = (ImageView) findViewById(R.id.back);
        time_current = (TextView) findViewById(R.id.time_current);
        time_total = (TextView) findViewById(R.id.time_total);
        title = (TextView) findViewById(R.id.title);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        buttons = (LinearLayout) findViewById(R.id.buttons);
        alpha_in = AnimationUtils.loadAnimation(this,R.anim.alpha_in);
        alpha_out = AnimationUtils.loadAnimation(this,R.anim.alpha_out);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        video = new Video(context);
        detector = new GestureDetector(context,new MyGestureListener(Content.this,video,play,seekBar,screenBrightnessPre,0));
    }


    /**
     * 设置视频播放相关
     * 开始、暂停、下⼀一个 & 上⼀一个视频
     */
    private void setVideo() {

        video.load(url,ids.get(position));
        video.into(surfaceView);
        video.setCurrentTime(seekBar, time_current, time_total);
        //设置控件长宽
//        LinearLayout parent = (LinearLayout)findViewById(R.id.surfaceView_parent);
//        ViewGroup.LayoutParams lp = parent.getLayoutParams();
//        float i = video.getWidth()/surfaceView.getWidth();
//        Log.d(TAG, "setVideo: "+video.getWidth());
//        lp.height = (int) (video.getHight()/i);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //上一个
                if (ISPLAY == 0) {
//                    video.prepare();
//                    time_total.setText(video.getDuration());
                    video.start();
                    ISPLAY = 1;
                    play.setImageResource(R.drawable.pause);
                } else if (ISPLAY == 1) {
                    video.pause();
                    ISPLAY = 0;
                    play.setImageResource(R.drawable.start);
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //下一个
                if (position < urls.size() - 1) {
                    if (video.isPlay()) {
                        video.pause();
                    }
                    video.stop();
                    video.reset();
                    position += 1;
                    url = urls.get(position);
                    video.setUrl(url,ids.get(position));
                    video.prepare();
                    video.start();
                    ISPLAY = 1;
                    play.setImageResource(R.drawable.pause);
//                    time_total.setText(video.getDuration());
                    title.setText(texts.get(position));
                } else {
                    Toast.makeText(context, "已经是最后一个", Toast.LENGTH_SHORT).show();
                }
            }
        });
        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //下一个
                if (position > 0) {
                    if (video.isPlay()) {
                        video.pause();
                    }
                    video.stop();
                    video.reset();
                    position -= 1;
                    url = urls.get(position);
                    video.setUrl(url, ids.get(position));
                    video.prepare();
                    video.start();
                    ISPLAY = 1;
                    play.setImageResource(R.drawable.pause);
//                    time_total.setText(video.getDuration());
                    title.setText(texts.get(position));
                } else {
                    Toast.makeText(context, "已经是第一个", Toast.LENGTH_SHORT).show();
                }
            }
        });

        title.setText(texts.get(position));
    }

    /**
     * 保存到本地
     */
    private void setDownload() {
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        String videoUrl = urls.get(position);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(videoUrl));
        request.setDestinationInExternalFilesDir(context,"zxZhu's_VideoPlayer", ids.get(position) + ".mp4");
        request.setTitle(ids.get(position) + ".mp4");
        request.setVisibleInDownloadsUi( true ) ;
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        long downloadId = downloadManager.enqueue(request);
        Toast.makeText(context,"开始下载",Toast.LENGTH_SHORT).show();

    }

    /**
     * 其他功能
     */
    private void setOthers() {
        //返回按钮
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        //下载
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDownload();
            }
        });
        //更改方向
        direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDriection();
            }
        });
        //分享
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"分享链接已复制到剪贴板",Toast.LENGTH_SHORT).show();
                ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(share_urls.get(position));
            }
        });
    }

    /**
     * 设置屏幕方向
     */
    private void setDriection(){
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            if (video.isPlay()){
                video.pause();
            }
            //设置竖屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        else if (context.getResources().getConfiguration().orientation ==Configuration.ORIENTATION_PORTRAIT) {
            if (video.isPlay()){
                video.pause();
            }
            //设置横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

    }

    /**
     * 防止按键遮挡视频 单击隐藏与显示
     */
    private void setClick(){

        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mLastTime = mCurTime;
                mCurTime = System.currentTimeMillis();
                if(mCurTime - mLastTime <300){//双击事件
                    if (ISPLAY == 0) {
//                    video.prepare();
//                    time_total.setText(video.getDuration());
                        video.start();
                        ISPLAY = 1;
                        play.setImageResource(R.drawable.pause);
                    } else if (ISPLAY == 1) {
                        video.pause();
                        ISPLAY = 0;
                        play.setImageResource(R.drawable.start);
                    }
                    mCurTime =0;
                    mLastTime = 0;
                }else{//单击事件
                    Log.d("000", "onClick: 单击");
                    if (isImgClick) {
                        toolbar.startAnimation(alpha_out);
                        buttons.startAnimation(alpha_out);
                        isImgClick = false;
                    }else {
                        toolbar.startAnimation(alpha_in);
                        buttons.startAnimation(alpha_in);
                        isImgClick = true;
                    }
                }
            }
        });
    }

    private int getScreenMode(){
        int screenMode = 0;
        try {
            screenMode = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);

            if(screenMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC){
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return screenMode;
    }

    private int getBright(){
        int screenBrightness = 255;
        try {
            screenBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return screenBrightness;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    //Gesture与scrollView不兼容问题解决
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //先让GestureDetector响应触碰事件
        detector.onTouchEvent(ev);
        //让Activity响应触碰事件
        super.dispatchTouchEvent(ev);
        return false;
    }

    @Override
    protected void onDestroy() {
        video.pause();
        //还原初始亮度
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,screenBrightnessPre );
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,screenModePre );
        super.onDestroy();
    }

    @Override//重写此方法防止旋转屏幕时销毁和重建
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
