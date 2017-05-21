package zxzhu.videoplayer.units;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Zhuzzzzzzx on 2017/5/21.
 */

public class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

    private static float MIN_DISTANCE = 300 ;
    private static float MAX_DISTANCE = 200;
    private static float MIN_VELOCITY = 350;
    private static String TAG = "MyGestureListener";
    private Context context = null;
    private ContentResolver contentResolver;
    private List<String> list = null;

    public MyGestureListener(Context context,List<String> list){
        this.context = context;
        contentResolver = context.getContentResolver();
        this.list = list;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return super.onDown(e);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d(TAG,"e1.getX(): "+String.valueOf(e1.getX()));
        Log.d(TAG,"e2.getX(): "+String.valueOf(e2.getX()));
        Log.d(TAG,"velocityX: "+velocityX);
        Log.d(TAG,"velocityY: "+velocityY);
        //向右滑动返回
        if(e2.getX()-e1.getX() > MIN_DISTANCE
                && Math.abs(velocityX)>MIN_VELOCITY
                && Math.abs(e2.getY()-e1.getY()) < MAX_DISTANCE)
        {
            ((Activity)context).onBackPressed();
        }
        //向左滑动跳转
        if(e1.getX()-e2.getX() > MIN_DISTANCE
                && Math.abs(e2.getY()-e1.getY()) < MAX_DISTANCE)
        {
//            Intent intent = new Intent(context,DocImage.class);
//            intent.putExtra("list",(Serializable)list);
//            context.startActivity(intent);
        }
        return super.onFling(e1, e2, velocityX, velocityY);
    }

    //双击返回
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        ((Activity)context).onBackPressed();
        return super.onDoubleTap(e);
    }

}
