package com.example.ending.uisimple.services;

import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.example.ending.uisimple.MainActivity;
import com.example.ending.uisimple.R;
import com.example.ending.uisimple.javabean.AppInfo;
import com.example.ending.uisimple.javabean.Chatter;
import com.example.ending.uisimple.utils.getAppInfo;
import com.example.ending.uisimple.utils.mBase64;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.ending.uisimple.utils.clearPreferences.clearAppInfo;
import static com.example.ending.uisimple.utils.clearPreferences.clearUserInfo;
import static com.example.ending.uisimple.utils.mBase64.DrawableToString;

public class WebSocketClientService extends Service {
    WebSocketClient mWebSocketClient;
    //final String address = "ws://10.243.6.27:8080/websocket/onClass/84";
    String address;//服务器地址
    String trueName;//姓名
    String schoolId;//学号
    long startTime;//进入课堂的时间
    int classId;//课堂id


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("Service的onBind：","启动");
        address = intent.getStringExtra("address");//获得服务器地址
        classId = intent.getIntExtra("classId",0);//获得课堂id
        Log.i("Service的服务器地址：",address);
        Log.i("Service的课堂号：",classId + "");
        if (classId == 0){
            Toast.makeText(WebSocketClientService.this,"无法获得课堂Id",Toast.LENGTH_SHORT).show();
        }else {
            connect();//用得到的服务器地址进行后台连接
            Log.i("已经得到课堂号","准备进行webSocket连接");
        }
        return new MyBinder();
    }

    //内部类，里面提供方法，返回此service对象
    public class MyBinder extends Binder {
        public WebSocketClientService getService(){
            return WebSocketClientService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Service的onCreate：","启动");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeConnect();
    }

    //连接服务器
    private void connect(){
        new Thread(){
            @Override
            public void run() {
                try {
                    mWebSocketClient = new WebSocketClient(new URI(address)) {
                        @Override
                        public void onOpen(ServerHandshake serverHandshake) {
                            Log.d("webSocket相关信息---->","onOpen，建立webSocket连接");
                            SharedPreferences pref = getSharedPreferences("userInfo",MODE_PRIVATE);
                            trueName = pref.getString("trueName","");
                            schoolId = pref.getString("schoolId","无");
                            startTime = pref.getLong("enterTime",99999999);
                            Chatter chatter = new Chatter(trueName,schoolId,true,"进入课堂(系统信息)",classId);
                            String chatterJson = new Gson().toJson(chatter);
                            sendMsg(chatterJson);
                        }

                        @Override
                        public void onMessage(String s) {
                            //当有服务端发送来消息的时候，回调此函数
                            Log.d("webSocket相关信息---->","onMessage，服务端发送过来的信息为：" + s);
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                boolean isClose = jsonObject.getString("message").equals("close");
                                boolean isStudent = jsonObject.getBoolean("isStudent");
                                if (isClose && !isStudent){
                                    //如果isClose为真，isStudent为假，代表这条信息是老师发的关闭课堂消息
                                    sendInfomation();//发送数据到服务器，关闭课堂

                                    //发送关闭信息到聊天室，其实可以省略
                                    Chatter closeChat = new Chatter("系统信息","",false,"已断开连接",classId);//构建系统信息
                                    String closeJson = new Gson().toJson(closeChat);
                                    Intent intent = new Intent
                                            ("com.example.dell.broadcast.WebSocket_BROADCAST");
                                    intent.putExtra("newMessage",closeJson);
                                    sendBroadcast(intent);
                                    mWebSocketClient.onClose(404,"客户端主动断开",false);//主动断开连接
                                }else {
                                    Intent intent = new Intent
                                            ("com.example.dell.broadcast.WebSocket_BROADCAST");
                                    intent.putExtra("newMessage",s);
                                    sendBroadcast(intent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onClose(int i, String s, boolean b) {
                            //连接断开
                            Log.d("webSocket相关信息---->","onClose，连接断开"+ i + "/" + s + "/" + b);
                            closeConnect();
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d("webSocket相关信息---->","onError，出错：" + e);
                        }
                    };
                    mWebSocketClient.connect();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    //断开连接
    private void closeConnect(){
        try {
            mWebSocketClient.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            mWebSocketClient = null;
        }
    }
    //发送信息
    public void sendMsg(String msg){
        //断线重连
        if (mWebSocketClient == null){
            connect();
        }
        mWebSocketClient.send(msg);
    }

    //当课堂关闭时，发送学生手机使用情况到服务器
    /**思路：先把JsonArray创建好(里面涉及到图片转String)，
     * 然后这个JsonArray和课堂号等一起组成一个JsonObject*/
    public JSONObject endClassObject(){
        JSONObject endClassInfo = new JSONObject();
        try{
            JSONArray jsonArray = new JSONArray();
            List<AppInfo> infoList = new getAppInfo().getappinfo(WebSocketClientService.this,startTime);//获取进入课堂到结束课堂这段时间内手机使用情况集合
            for (int i=0;i<infoList.size();i++){
                JSONObject jsonObject = new JSONObject();
                AppInfo appInfo = infoList.get(i);//拿到每一个手机应用信息

                String appIcon = DrawableToString(appInfo.getAppIcon());//获得图标,并转成String,长度约3W
                String appLabel = appInfo.getAppLabel();//获得名称
                long appUsedTime = appInfo.getAppUsedTime();//获得使用时长

                jsonObject.put("appIcon",appIcon);
                jsonObject.put("appLabel",appLabel);
                jsonObject.put("appUsedTime",appUsedTime);
                jsonArray.put(jsonObject);//for循环结束后，jsonArray就创建好了
            }
            SharedPreferences pref = getSharedPreferences("userInfo",MODE_PRIVATE);
            endClassInfo.put("uid",pref.getString("uid",""));
            endClassInfo.put("classId",pref.getInt("classId",0));
            endClassInfo.put("trueName",pref.getString("trueName",""));
            endClassInfo.put("schoolId",pref.getString("schoolId",""));
            endClassInfo.put("appInfoList",jsonArray);//将手机应用信息放入JsonObject
        }catch (JSONException e){
            e.printStackTrace();
        }
        return endClassInfo;
    }

    public void sendInfomation(){
        String url = getResources().getString(R.string.endClassAddress);
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        RequestBody body = RequestBody.create(JSON,endClassObject().toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String errorMsg = e.toString();
                Log.i("无法接收返回信息",errorMsg);
                new AlertDialog.Builder(WebSocketClientService.this)
                        .setTitle("请检查网络")
                        .setMessage("无法上传信息，将被视为没有上课")
                        .setPositiveButton("重新上传", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                sendInfomation();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseMsg = response.body().string();
                //返回信息，说明上传信息成功
                if (responseMsg.equals("successful")){
                    clearAppInfo(WebSocketClientService.this);//上传成功，删除本次的APP信息
                    clearUserInfo(WebSocketClientService.this);//删除本次课堂号等信息
                    Intent intent = new Intent("com.example.dell.broadcast.mySituation_FINISH");
                    sendBroadcast(intent);
                }
            }
        });
    }
}
