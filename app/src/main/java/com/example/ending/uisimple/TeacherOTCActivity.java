package com.example.ending.uisimple;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ending.uisimple.javabean.Chatter;
import com.example.ending.uisimple.javabean.User;
import com.example.ending.uisimple.services.WebSocketClientService;
import com.example.ending.uisimple.services.WebSocketTeacher;
import com.example.ending.uisimple.utils.postJson;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TeacherOTCActivity extends AppCompatActivity {

    String uid;//创建课堂所需的uid
    String classMessage;//存储返回的课堂信息
    int classId;//课堂号

    ImageView qrCordImv;//显示二维码的imageview
    Button endClass;//结束课堂的按钮

    boolean isBinding = false;//记录是否有绑定service，有：退出时解绑；无：直接退出

    WebSocketTeacher service;//service对象，需要里面的方法发送信息
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_otc);

        initView();//初始化组件
        SharedPreferences pref = getSharedPreferences("userInfo",MODE_PRIVATE);
        uid = pref.getString("uid","");
        getClassAddress(uid);//获取课堂信息，存到全局变量中，此方法一般情况下只调用一次
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBinding==true){
            unbindService(mServiceConnection);
        }
    }

    //匿名内部类，用于获取service对象
    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = ((WebSocketTeacher.MyBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            service = null;
        }
    };
    private void initView(){
        qrCordImv = (ImageView) findViewById(R.id.qrCord_imv);
        endClass = (Button) findViewById(R.id.EndTheClass);

        endClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Chatter chatter = new Chatter("","",false,"close",classId);
                String chatterJson = new Gson().toJson(chatter);
                service.sendMsg(chatterJson);//发布结束课堂指令,后台开始收集所有学生的应用信息
                Intent intent = new Intent(TeacherOTCActivity.this,TeacherACActivity.class);
                intent.putExtra("uid",uid);
                intent.putExtra("classId",classId);
                startActivity(intent);
                TeacherOTCActivity.this.finish();
            }
        });
    }
    //获取课堂地址，以二维码形式显示
    public void getClassAddress(String uid){
        String url = getResources().getString(R.string.createClassAddress);//服务器登陆接口
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");//设置发送格式为json
        RequestBody body = RequestBody.create(JSON,uid);//将uid作为请求体
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Log.d("发送创建课堂请求","正在接收返回信息");
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                final String errorMessage = e.toString();
                Log.d("无法接收返回信息","某方面出错了");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TeacherOTCActivity.this,"无法接收课堂信息，请检查网络",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            //格式：ws://10.243.6.27:8080/webSocket/onClass/177&1515503747740
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //Log.d("","");
                //返回信息格式：ws://10.243.6.27:8080/webSocket/onClass/177
                classMessage = response.body().string();
                String regex = "(?<=\\bonClass/)\\d+\\b";//匹配出课堂id
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(classMessage);
                if (matcher.find()){
                    classId = Integer.parseInt(matcher.group());
                    //开启服务,并向服务传递服务器地址
                    Intent intent = new Intent(TeacherOTCActivity.this,WebSocketTeacher.class);
                    intent.putExtra("address",classMessage);
                    intent.putExtra("classId",classId);
                    TeacherOTCActivity.this.bindService(intent,mServiceConnection, Context.BIND_AUTO_CREATE);
                    isBinding = true;
                }else {
                    Log.i("教师界面","匹配不到课堂号");
                }

                Log.d("创建课堂接收到的信息",classMessage);
                //responseMsg = responseMsg + "&" + System.currentTimeMillis();//返回课堂信息后，在末尾加上时间戳
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadQrCord(classMessage);//将构件好的字符串转成二维码
                        //正确返回课堂信息后，才设置Imageview点击事件，并且将结束课堂设为可点击
                        qrCordImv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                loadQrCord(classMessage);
                            }
                        });
                        endClass.setEnabled(true);
                    }
                });
            }
        });
    }

    //将传入的字符串转成二维码图片
    public void loadQrCord(String str){
        if (str == null){
            Toast.makeText(TeacherOTCActivity.this,"无法获得场景信息，创建课堂失败",Toast.LENGTH_LONG).show();
            return;
        }
        str = str + "&" + System.currentTimeMillis();//将传进来的课堂信息加上时间戳
        try{
            //使用第三方jar包，用传入的字符串生成一个二维码位图矩阵
            BitMatrix bitMatrix = new MultiFormatWriter()
                    .encode(str, BarcodeFormat.QR_CODE,1200,1200);
            //使用第三方jar包，用二维码位图矩阵创建一个位图
            Bitmap bitmap = new BarcodeEncoder().createBitmap(bitMatrix);
            //将构建的位图显示到屏幕
            qrCordImv.setImageBitmap(bitmap);
        }catch (WriterException e){
            e.printStackTrace();
        }
    }
}
