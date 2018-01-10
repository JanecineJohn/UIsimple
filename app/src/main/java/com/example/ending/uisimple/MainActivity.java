package com.example.ending.uisimple;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ending.uisimple.javabean.Joinner;
import com.example.ending.uisimple.javabean.MidJoinner;
import com.example.ending.uisimple.javabean.User;
import com.example.ending.uisimple.utils.getAppInfo;
import com.example.ending.uisimple.utils.postJson;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.ending.uisimple.utils.clearPreferences.clearAppInfo;
import static com.example.ending.uisimple.utils.clearPreferences.clearUserInfo;


public class MainActivity extends AppCompatActivity {
    DrawerLayout MainUI;
    LinearLayout Usermenu;
    String flag="";
    private ListView mListView;
    private String[]names={"获取老师端历史记录的日期"};
    private String[][]students={{"一个日期对应一节课的所有学生的应用使用记录，记录就放在这里"}};

    String webAddress;//扫描结果,聊天室地址
    String uid;//学生id
    int classId;//课堂号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Toast.makeText(MainActivity.this,"onCreate方法启动",Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            LinearLayout linear_bar = (LinearLayout) findViewById(R.id.ll_bar);
            linear_bar.setVisibility(View.VISIBLE);
            //获取到状态栏的高度
            int statusHeight = getStatusBarHeight();
            //动态的设置隐藏布局的高度
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) linear_bar.getLayoutParams();
            params.height = statusHeight;
            linear_bar.setLayoutParams(params);


            mListView=(ListView)findViewById(R.id.lv);
            MyBaseAdapter mAdapter=new MyBaseAdapter();
            mListView.setAdapter(mAdapter);


        }
    }

    //当界面从不可见变为可见时，判断有无旧课堂信息
    @Override
    protected void onStart() {
        super.onStart();
        //Log.i("主界面","onStart方法");
        //Toast.makeText(MainActivity.this,"onStart方法启动",Toast.LENGTH_SHORT).show();
        SharedPreferences pref = getSharedPreferences("userInfo",MODE_PRIVATE);
        if (pref.getInt("classId",0) > 0){
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("旧课堂")
                    .setMessage("是否要回到上一个课堂")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedPreferences pref = getSharedPreferences("userInfo",MODE_PRIVATE);
                            webAddress = pref.getString("webAddress","");
                            classId = pref.getInt("classId",0);
                            uid = pref.getString("uid","");
                            if (uid.equals("")){
                                Toast.makeText(MainActivity.this,"信息不完整，无法加入课堂",Toast.LENGTH_SHORT).show();
                            }else {
                                MidJoinner midJoinner = new MidJoinner(pref.getString("trueName",""),pref.getString("schoolId",""));
                                String mid = new Gson().toJson(midJoinner);
                                Joinner joinner = new Joinner(uid,classId,mid);
                                sendJoinClassHttp(joinner);
                            }
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            clearAppInfo(MainActivity.this);
                            clearUserInfo(MainActivity.this);
                        }
                    })
                    .show();
        }
    }

    //每次重新回到主界面(主界面获得焦点)时回调此方法
    @Override
    protected void onResume() {
        super.onResume();
        //Toast.makeText(MainActivity.this,"onResume方法启动onResume方法启动",Toast.LENGTH_LONG).show();
        Button loginBt = (Button) findViewById(R.id.LoginButton);//初始化登录按钮
        //先检查用户信息，判断是否有登录
        SharedPreferences pref = getSharedPreferences("userInfo",MODE_PRIVATE);
        uid = pref.getString("uid","");
        String userName = pref.getString("userName","");
        if (uid.equals("") || userName.equals("")){
            loginBt.setText("立即登录");
            TextView nameinfact=(TextView)findViewById(R.id.nameinfact);
            TextView numinfact=(TextView)findViewById(R.id.numinfact);
            nameinfact.setEnabled(false);
            numinfact.setEnabled(false);
            nameinfact.setVisibility(View.VISIBLE);
            numinfact.setVisibility(View.VISIBLE);
            CircleImageView Head=(CircleImageView)findViewById(R.id.Head);
            Head.setVisibility(View.INVISIBLE);
            mListView.setVisibility(View.INVISIBLE);
            TextView temptv=(TextView)findViewById(R.id.checkhistory);
            temptv.setText("登录查看历史记录");
            Button RegisterButton=(Button)findViewById(R.id.RegisterButton);
            RegisterButton.setText("注册");
            loginBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                    startActivityForResult(intent,1);
                }
            });
        }else {
            loginBt.setText("注销");
            loginBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //弹出对话框与用户交互
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("登出")
                            .setMessage("你确定登出此账号吗？")
                            .setCancelable(false)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SharedPreferences.Editor editor = getSharedPreferences("userInfo",MODE_PRIVATE).edit();
                                    editor.clear();
                                    editor.apply();
                                    startActivity(new Intent(MainActivity.this,MainActivity.class));
                                    MainActivity.this.finish();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .show();
                }
            });
            TextView nameinfact=(TextView)findViewById(R.id.nameinfact);
            TextView numinfact=(TextView)findViewById(R.id.numinfact);
            TextView userNameTv = (TextView) findViewById(R.id.userNameTv);
            nameinfact.setEnabled(false);
            numinfact.setEnabled(false);
            nameinfact.setVisibility(View.INVISIBLE);
            numinfact.setVisibility(View.INVISIBLE);
            userNameTv.setText(userName);
            CircleImageView Head=(CircleImageView)findViewById(R.id.Head);
            Head.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.VISIBLE);
            TextView temptv=(TextView)findViewById(R.id.checkhistory);
            temptv.setText("历史记录");
            Button RegisterButton=(Button)findViewById(R.id.RegisterButton);
            RegisterButton.setText("");
        }

    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        IntentResult intentResult = IntentIntegrator.
                parseActivityResult(requestCode,resultCode,data);
        if (intentResult != null){
            if (intentResult.getContents() == null){
                Toast.makeText(MainActivity.this,"内容为空",Toast.LENGTH_SHORT).show();
            }else {
                //Toast.makeText(MainActivity.this,"扫描成功",Toast.LENGTH_SHORT).show();
                //ScanResult为获取到的字符串(聊天室地址)
                String scanResult = intentResult.getContents();
                String[] addresses = scanResult.split("&");
                if (System.currentTimeMillis()-Long.parseLong(addresses[1]) < 5000){
                    webAddress = addresses[0];
                    //Toast.makeText(MainActivity.this,webAddress,Toast.LENGTH_SHORT).show();
                    String regex = "(?<=\\bonClass/)\\d+\\b";//匹配出课堂id
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(webAddress);
                    if (matcher.find()){
                        classId = Integer.parseInt(matcher.group());
                        dialogView();
                    }else {
                        Toast.makeText(MainActivity.this,"无法进入指定场景",Toast.LENGTH_SHORT).show();
                    }
                    /**根据需要对扫描得到的字符串进行操作*/
                }else {
                    Toast.makeText(MainActivity.this,"此二维码已失效，请重新操作",Toast.LENGTH_SHORT).show();
                }
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
            if(requestCode==1)
            {
                if(resultCode==1)
                {
                }
            }
        }
    }
    class MyBaseAdapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return names.length;
        }
        @Override
        public Object getItem(int position)
        {
            return names[position];
        }
        @Override
        public long getItemId(int position)
        {
            return position;
        }
        @Override
        public  View getView(int position, View convertView, ViewGroup parent)
        {
            View view = View.inflate(MainActivity.this,R.layout.list_item,null);
            TextView mTextView =(TextView)view.findViewById(R.id.item_lv);
            mTextView.setText(names[position]);
            return view;
        }
    }

    private int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void onClick(View view)
    {
        MainUI = (DrawerLayout) findViewById(R.id.drawerlayout);
        Usermenu=(LinearLayout) findViewById(R.id.Menu);
        switch (view.getId())
        {
            case R.id.CreatClass1:
            case R.id.CreatClass2:
            {
                if (uid.equals("")){
                    Toast.makeText(MainActivity.this,"无用户信息，请登录",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent=new Intent(MainActivity.this,TeacherOTCActivity.class);
                    startActivity(intent);
                }
                break;
            }
            case R.id.JoinClass1:
            case R.id.JoinClass2:
            {
                //先检查用户信息，判断是否有登录
                SharedPreferences pref = getSharedPreferences("userInfo",MODE_PRIVATE);
                //uid = pref.getString("uid","");
                String userName = pref.getString("userName","");

                if (uid.equals("") || userName.equals("")){
                    //检测不到用户信息，先登录，跳转到登录界面
                    Toast.makeText(MainActivity.this,"无用户信息，请登录",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                }else {
                    //加入课堂按钮，扫码登录
                    customScan();
                }
                break;
            }
            case R.id.RegisterButton:
            {
                Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.MenuButton:
            {
                MainUI.openDrawer(Usermenu);
            }

        }
    }
    //扫码登录课堂的实现方法
    public void customScan(){
        if (ContextCompat.checkSelfPermission
                (MainActivity.this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED){
            //如果没有权限，动态申请
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA},100);
            return;
        }
        new IntentIntegrator(MainActivity.this).setOrientationLocked(false)
                .setPrompt("")
                .setCaptureActivity(CustomScanActivity.class)
                .setBeepEnabled(true)
                .initiateScan();//开始扫描
    }

    //弹出对话框，让用户填写姓名学号
    public void dialogView(){
        final View joinClassForm = getLayoutInflater().inflate(R.layout.dialog_layout,null);
        final EditText nameEdt = joinClassForm.findViewById(R.id.dialog_name_edt);
        final EditText studentIdEdt = joinClassForm.findViewById(R.id.dialog_studentId_edt);

        new AlertDialog.Builder(this)
                .setTitle("扫描成功")
                .setView(joinClassForm)
                .setPositiveButton("签到", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //把用户名存储到sharedPreference文件,并清除以前的应用使用时间
                        new getAppInfo().clearUsedTime(MainActivity.this);//扫码进入课堂前，先记录应用已经使用的时长
                        SharedPreferences.Editor editor = getSharedPreferences("userInfo",MODE_PRIVATE).edit();
                        editor.putString("trueName",nameEdt.getText().toString());//将姓名写进文件
                        editor.putString("schoolId",studentIdEdt.getText().toString());//将学号写进文件
                        editor.putString("webAddress",webAddress);//将课堂地址写进文件
                        editor.putInt("classId",classId);//将课堂号写进文件
                        editor.putLong("enterTime",System.currentTimeMillis()-10*1000);//将进入课堂的时间戳写入用户文件
                        editor.apply();
                        //Toast.makeText(MainActivity.this,classId+" IN TEST",Toast.LENGTH_SHORT).show();
                        //执行登录处理，发送两个信息
                        MidJoinner midJoinner = new MidJoinner(nameEdt.getText().toString(),studentIdEdt.getText().toString());
                        String mid = new Gson().toJson(midJoinner);
                        Joinner joinner = new Joinner(uid,classId,mid);
                        sendJoinClassHttp(joinner);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();
    }

    public void sendJoinClassHttp(Joinner joinner){
        String url = getResources().getString(R.string.joinClassAddress);//服务器地址
        postJson post = new postJson();//创建一个post对象，准备向服务器发送请求
        post.postJoinClassHttp(url,joinner).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                final String errorMessage = e.toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,errorMessage,Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseMessage = response.body().string();
                Log.i("主界面，joinner模块：",responseMessage);
                if (responseMessage.equals("successful")){

                    Intent intent=new Intent(MainActivity.this,StudentOTCActivity.class);
                    intent.putExtra("address",webAddress);
                    intent.putExtra("classId",classId);
                    startActivity(intent);
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,"请重新输入信息",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    //进行动态权限申请后的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //获得授权
                customScan();
            }else {
                //被禁止授权
                Toast.makeText(MainActivity.this,"拒绝权限将无法扫码加入课堂",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
