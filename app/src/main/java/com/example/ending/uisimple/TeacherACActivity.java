package com.example.ending.uisimple;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ending.uisimple.adapters.DividerItemDecoration;
import com.example.ending.uisimple.adapters.onClassInfoAdapter;
import com.example.ending.uisimple.javabean.AppInfo;
import com.example.ending.uisimple.javabean.OnClassInfo;
import com.example.ending.uisimple.services.WebSocketTeacher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.ending.uisimple.utils.mBase64.StringToDrawable;

public class TeacherACActivity extends AppCompatActivity {

    String uid;
    int classId;
    int times = 6;//请求次数

    RecyclerView recyclerView;
    Handler handler = new Handler();
    List<OnClassInfo> onClassInfoList = new ArrayList<>();//存储所有学生的信息
    onClassInfoAdapter adapter = new onClassInfoAdapter(onClassInfoList);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_ac);
        initView();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (times > 0){
                    times--;
                    getStuAppList();
                    handler.postDelayed(this,5000);
                }
            }
        };
        handler.post(runnable);//立即开始
    }

    private void initView(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler);//初始化recyclerview组件
        LinearLayoutManager layoutManager = new LinearLayoutManager(TeacherACActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(TeacherACActivity.this,DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(adapter);
        uid = getIntent().getStringExtra("uid");
        classId = getIntent().getIntExtra("classId",0);
    }
    //获取参与者的手机信息
    public void getStuAppList(){
        String url = getResources().getString(R.string.endClassAddressTeacher);//服务器登陆接口
        url = url + "&uid=" + uid + "&classId=" + classId;
        Request request = new Request.Builder()
                .url(url)
                .build();
        Log.d("发送创建课堂请求",request.toString());
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                final String errorMessage = e.toString();
                Log.d("无法接收返回信息","某方面出错了");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TeacherACActivity.this,"无法请求数据，请检查网络",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            //格式：ws://10.243.6.27:8080/webSocket/onClass/177&1515503747740
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseMsg = response.body().string();
                Log.i("返回的数据：",responseMsg);
                try {
                    JSONArray stuInfoArray = new JSONArray(responseMsg);//获得各个学生的信息
                    for (int i=0;i<stuInfoArray.length();i++){
                        List<AppInfo> appInfoList = new ArrayList<>();//存储每个人的手机应用信息
                        JSONObject eachStuObj = stuInfoArray.getJSONObject(i);
                        String trueName = eachStuObj.getString("trueName");
                        String schoolId = eachStuObj.getString("schoolId");
                        JSONArray appInfoArray = eachStuObj.getJSONArray("appInfoList");
                        //这个for循环用来搞定其中一位学生的App信息
                        for (int j=0;j<appInfoArray.length();j++){
                            JSONObject eachAppObj = appInfoArray.getJSONObject(j);
                            Drawable appIcon = StringToDrawable(eachAppObj.getString("appIcon"));
                            String appLabel = eachAppObj.getString("appLabel");
                            Long appUsedTime = eachAppObj.getLong("appUsedTime");

                            AppInfo appInfo = new AppInfo(appIcon,appLabel,appUsedTime);
                            appInfoList.add(appInfo);
                        }

                        OnClassInfo onClassInfo = new OnClassInfo(trueName,schoolId,appInfoList);
                        onClassInfoList.add(onClassInfo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
