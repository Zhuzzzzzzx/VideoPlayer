package zxzhu.videoplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zxzhu.videoplayer.Activity.Content;
import zxzhu.videoplayer.R;
import zxzhu.videoplayer.beans.Data;
import zxzhu.videoplayer.units.ImageLoader;
import zxzhu.videoplayer.units.SQLite;
import zxzhu.videoplayer.units.Video;

/**
 * Created by Zhuzzzzzzx on 2017/5/20.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    private Context context;
    private int count=0;
    private int HEADER = 0, NORMAL = 1;
    private Bitmap img = null;
    private LinearLayoutManager manager;
    private List<Data.ShowapiResBodyBean.PagebeanBean.ContentlistBean> contentlistBeen;
    private List<String> urls;
    private List<String> ids;
    private List<String> texts;
    private List<String> share_urls;
    private static final String TAG = "RecyclerAdapterTest";
    //private List<Comment> commentList = new ArrayList<>();


    public void addData(List<Data.ShowapiResBodyBean.PagebeanBean.ContentlistBean> contentlistBeen,List<String> ids,List<String> urls,List<String> texts,List<String> share_urls){
        this.contentlistBeen = contentlistBeen;
        this.urls = urls;
        this.ids = ids;
        this.texts = texts;
        this.share_urls = share_urls;
        notifyDataSetChanged();
    }

    public RecyclerAdapter(Context context, LinearLayoutManager manager){
        this.context = context;
        this.manager = manager;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (viewType == HEADER) {
//            View view = LayoutInflater.from(context).inflate(R.layout.classify_item, parent, false);
//            return new MyViewHolder(view,HEADER);
//        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.item,parent,false);
            return new MyViewHolder(view,NORMAL);
    }
//        }

    //不懂服务器返回代码..
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Handler handler = new Handler(){
            public void handleMessage(Message message){

                holder.video.setImageBitmap(img);
            }
        };
        if (holder.viewType == NORMAL){
            Log.d(TAG, "onBindViewHolder: "+position);
            final Data.ShowapiResBodyBean.PagebeanBean.ContentlistBean content = contentlistBeen.get(position);
            Log.d(TAG, "onBindViewHolder: "+content.getCreate_time());
            ImageLoader.build(context).setImagePlace(R.drawable.user)
                    .setBitmap(content.getProfile_image(),holder.head);
            holder.name.setText(content.getName());
            holder.text.setText(content.getText());
            holder.time.setText(content.getCreate_time());
            holder.love_num.setText(content.getLove());
            holder.hate_num.setText(content.getHate());
            holder.video.setImageResource(R.drawable.vidoe_error);
//            holder.video.setTag(position);
            Video.getFirstFrame(content.getVideo_uri(), new Video.CallBack_bitmap() {


                @Override
                    public void onFinish(Bitmap bitmap) {
                    Log.d(TAG, "onFinish: 回调");
                    img = bitmap;
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            });

//            video.start();
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra("ids",(ArrayList<String>) ids);
                    intent.putStringArrayListExtra("urls", (ArrayList<String>) urls);
                    intent.putStringArrayListExtra("texts", (ArrayList<String>) texts);
                    intent.putStringArrayListExtra("share_urls", (ArrayList<String>) share_urls);
                    intent.putExtra("position",position);
                    intent.setClass(context, Content.class);
                    SQLite sqLite = new SQLite(context);
                    sqLite.insert(content);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(contentlistBeen != null)
            return contentlistBeen.size();
        else  return 0;
    }

    @Override
    public int getItemViewType(int position) {
//        if ( position == 0 ){
//            return HEADER;
//        }else {
            return NORMAL;
//        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView head;
        ImageView video;
        TextView name,time,text;
        TextView love_num,hate_num,commend_num,share_num;
        int viewType;
        public MyViewHolder(View itemView,int viewType) {
            super(itemView);
            this.viewType = viewType;
            head = (ImageView) itemView.findViewById(R.id.head_item);
            name = (TextView) itemView.findViewById(R.id.name_item);
            text = (TextView) itemView.findViewById(R.id.text_item);
            time = (TextView)itemView.findViewById(R.id.time_item);
            commend_num = (TextView) itemView.findViewById(R.id.commend_num);
            love_num = (TextView) itemView.findViewById(R.id.love_num);
            hate_num = (TextView) itemView.findViewById(R.id.hate_num);
            share_num = (TextView) itemView.findViewById(R.id.share_num);
            video = (ImageView) itemView.findViewById(R.id.video_img);
        }
    }
    /**
     * 下拉加载时把数据添加在上面
     */


}

