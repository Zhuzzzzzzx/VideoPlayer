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
import zxzhu.videoplayer.units.Video;

/**
 * Created by Zhuzzzzzzx on 2017/5/20.
 */

public class RecentlyAdapter extends RecyclerView.Adapter<RecentlyAdapter.MyViewHolder> {
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
    private static final String TAG = "RecentlyAdapter";
    //private List<Comment> commentList = new ArrayList<>();


    public void addData(List<Data.ShowapiResBodyBean.PagebeanBean.ContentlistBean> contentlistBeen,List<String> ids,List<String> urls,List<String> texts,List<String> share_urls){
        this.contentlistBeen = contentlistBeen;
        this.urls = urls;
        this.ids = ids;
        this.texts = texts;
        this.share_urls = share_urls;
        notifyDataSetChanged();
    }

    public void refresh(){
        notifyDataSetChanged();
    }

    public RecentlyAdapter(Context context, LinearLayoutManager manager){
        this.context = context;
        this.manager = manager;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (viewType == HEADER) {
//            View view = LayoutInflater.from(context).inflate(R.layout.classify_item, parent, false);
//            return new MyViewHolder(view,HEADER);
//        }else {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recently,parent,false);
        return new MyViewHolder(view,NORMAL);
    }
//        }

    //不懂服务器返回代码..
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (holder.viewType == NORMAL){
            Log.d(TAG, "onBindViewHolder: "+position);
            final Data.ShowapiResBodyBean.PagebeanBean.ContentlistBean content = contentlistBeen.get(position);
            Log.d(TAG, "onBindViewHolder: "+content.getCreate_time());
            holder.text.setText(content.getText());
            holder.time.setText(content.getCreate_time());
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
        TextView time,text;
        int viewType;
        public MyViewHolder(View itemView,int viewType) {
            super(itemView);
            this.viewType = viewType;
            text = (TextView) itemView.findViewById(R.id.item_title_recently);
            time = (TextView)itemView.findViewById(R.id.item_time_recently);
        }
    }


}

