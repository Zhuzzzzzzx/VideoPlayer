package zxzhu.videoplayer.Activity;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import zxzhu.videoplayer.R;
import zxzhu.videoplayer.adapter.RecyclerAdapter;
import zxzhu.videoplayer.beans.Data;
import zxzhu.videoplayer.units.HttpUnit;
import zxzhu.videoplayer.units.SQLite;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RecyclerAdapter adapter;
    private ImageView recently;
    private List<String> urls;
    private List<String> ids;
    private List<String> texts;
    private SQLite sqLite;
    private Toolbar toolbar;
    private List<String> share_urls;
    private List<Data.ShowapiResBodyBean.PagebeanBean.ContentlistBean> contentlistBeanList;
    private String url = "http://route.showapi.com/255-1?showapi_appid=38518&showapi_sign=f00376d530ae4e799eae6fc301811c5f&type=41&title=&page=&";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        getDatas(0);
        setRecyclerView();
        setRecently();
    }

    private void init(){
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        layoutManager = new GridLayoutManager(this,1);
        adapter = new RecyclerAdapter(this,layoutManager);
        toolbar = (Toolbar)findViewById(R.id.bar_main);
        recently = (ImageView)findViewById(R.id.recently);
        sqLite = new SQLite(this);
        urls = new ArrayList<>();
        ids = new ArrayList<>();
        texts = new ArrayList<>();
        share_urls = new ArrayList<>();
        contentlistBeanList = new ArrayList<>();
    }

    private void setRecyclerView(){
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void getDatas(final int mode){
        HttpUnit.sendHttpRequest(url, new HttpUnit.CallBack() {
            @Override
            public void onFinish(String response) {
                //showLog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject showapi_res_body = jsonObject.getJSONObject("showapi_res_body");
                    JSONObject pagebean = showapi_res_body.getJSONObject("pagebean");
                    JSONArray contentlist = pagebean.getJSONArray("contentlist");
                    for (int i = 0; i < contentlist.length(); i++) {
                        JSONObject content = contentlist.getJSONObject(i);
                        Data.ShowapiResBodyBean.PagebeanBean.ContentlistBean contentlistBean = new Data.ShowapiResBodyBean.PagebeanBean.ContentlistBean();
                        contentlistBean.setText(content.getString("text"));
                        texts.add(content.getString("text"));
                        contentlistBean.setHate(content.getString("hate"));
                        contentlistBean.setLove(content.getString("love"));
                        contentlistBean.setCreate_time(content.getString("create_time"));
                        contentlistBean.setProfile_image(content.getString("profile_image"));
                        contentlistBean.setName(content.getString("name"));
                        contentlistBean.setId(content.getString("id"));
                        ids.add(content.getString("id"));
                        contentlistBean.setVideo_uri(content.getString("video_uri"));
                        urls.add(content.getString("video_uri"));
                        contentlistBean.setWeixin_url(content.getString("weixin_url"));
                        share_urls.add(content.getString("weixin_url"));
                        contentlistBeanList.add(contentlistBean);

                        Log.d(TAG, "parseJSON: "+contentlistBean.getName());
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                //刷新recycler
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.addData(contentlistBeanList,ids,urls,texts,share_urls);
                    }
                });
            }
        });
    }

    private void setRecently(){
        recently.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,Recently.class);
                startActivity(intent);
            }
        });

    }
}
