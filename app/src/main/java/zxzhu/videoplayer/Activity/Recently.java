package zxzhu.videoplayer.Activity;

import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import zxzhu.videoplayer.R;
import zxzhu.videoplayer.adapter.RecentlyAdapter;
import zxzhu.videoplayer.adapter.RecyclerAdapter;
import zxzhu.videoplayer.beans.Data;
import zxzhu.videoplayer.units.SQLite;

public class Recently extends AppCompatActivity {
    private SQLite sqLite;
    private final static String TAG = "Recently";
    private RecentlyAdapter adapter;
    private List<Data.ShowapiResBodyBean.PagebeanBean.ContentlistBean> recentlyList;
    private List<String> urls;
    private List<String> ids;
    private List<String> texts;
    private List<String> share_urls;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private ImageView clear,back;
    private Toolbar toolbar;
    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    adapter.addData(recentlyList, ids, urls, texts, share_urls);
                    break;
                case 1:
                    Toast.makeText(Recently.this, "数据为空", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "getFromHttp(1);");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recently);
        init();
        setRecyclerView();
        setClear();
        setBack();
        getFromSQL();
        setItemTouchHelper();
    }

    private void init() {
        sqLite = new SQLite(this);
        recentlyList = new ArrayList<>();
        urls = new ArrayList<>();
        share_urls = new ArrayList<>();
        ids = new ArrayList<>();
        texts = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recently_recycler);
        manager = new LinearLayoutManager(this);
        adapter = new RecentlyAdapter(this, manager);
        toolbar = (Toolbar) findViewById(R.id.bar_recently);
        clear = (ImageView) findViewById(R.id.clear);
        back = (ImageView) findViewById(R.id.back_recently);
    }

    private void setRecyclerView() {
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 启动时从数据库中得到最近数据
     */
    private void getFromSQL() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = sqLite.getDb().query("Recently", null, null, null, null, null, null);
                if (cursor != null && cursor.moveToLast()) {
                    Log.d(TAG, "cursor.moveToLast()");
                    do {
                        Data.ShowapiResBodyBean.PagebeanBean.ContentlistBean item = new Data.ShowapiResBodyBean.PagebeanBean.ContentlistBean();
                        item.setCreate_time(cursor.getString(cursor.getColumnIndex("time")));
                        item.setVideo_uri(cursor.getString(cursor.getColumnIndex("url")));
                        item.setText(cursor.getString(cursor.getColumnIndex("title")));
                        item.setWeixin_url((cursor.getString(cursor.getColumnIndex("share"))));
                        item.setId((cursor.getString(cursor.getColumnIndex("video_id"))));
                        recentlyList.add(item);
                        ids.add(cursor.getString(cursor.getColumnIndex("video_id")));
                        urls.add(cursor.getString(cursor.getColumnIndex("url")));
                        share_urls.add(cursor.getString(cursor.getColumnIndex("share")));
                        texts.add(cursor.getString(cursor.getColumnIndex("title")));
                        //Log.d(TAG,item.getUpdateTime());
                    } while (cursor.moveToPrevious());
                    cursor.close();
                    if (recentlyList != null) {
                        //实在不会写回调  用hanlder
                        Message message = new Message();
                        message.what = 0;
                        handler.sendMessage(message);
                    }
                } else {
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

    /**
     * 清空记录
     */
    private void setClear() {
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqLite.deleteAll();
                recentlyList.clear();
                urls.clear();
                share_urls.clear();
                texts.clear();
                ids.clear();
                adapter.refresh();
            }
        });
    }

    /**
     * item滑动删除 长按拖拽
     */
    private void setItemTouchHelper() {
        ItemTouchHelper.Callback mCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {
            /**
             * @param recyclerView
             * @param viewHolder 拖动的ViewHolder
             * @param target 目标位置的ViewHolder
             * @return
             */
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();//得到拖动ViewHolder的position
                int toPosition = target.getAdapterPosition();//得到目标ViewHolder的position
                if (fromPosition < toPosition) {
                    //分别把中间所有的item的位置重新交换
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(recentlyList, i, i + 1);
                        Collections.swap(urls, i, i + 1);
                        Collections.swap(ids, i, i + 1);
                        Collections.swap(share_urls, i, i + 1);
                        Collections.swap(texts, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(recentlyList, i, i - 1);
                        Collections.swap(urls, i, i - 1);
                        Collections.swap(ids, i, i - 1);
                        Collections.swap(share_urls, i, i - 1);
                        Collections.swap(texts, i, i - 1);
                    }
                }
                adapter.notifyItemMoved(fromPosition, toPosition);
                //返回true表示执行拖动
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                urls.remove(position);
                share_urls.remove(position);
                texts.remove(position);
                ids.remove(position);
                recentlyList.remove(position);
                adapter.notifyItemRemoved(position);
            }

        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void setBack(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
