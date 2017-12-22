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
import com.example.ending.uisimple.utils.postJson;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    DrawerLayout MainUI;
    LinearLayout Usermenu;
    String flag="";
    private ListView mListView;
    private String[]names={"获取老师端历史记录的日期"};
    private String[][]students={{"一个日期对应一节课的所有学生的应用使用记录，记录就放在这里"}};

    String scanResult;//扫描结果,聊天室地址
    String uid;//学生id
    int classId;//课堂号
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    //每次重新回到主界面回调此方法
    @Override
    protected void onResume() {
        super.onResume();
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
                Toast.makeText(MainActivity.this,"扫描成功",Toast.LENGTH_SHORT).show();
                //ScanResult为获取到的字符串(聊天室地址)
                scanResult = intentResult.getContents();
                String regex = "(?<=\\bonClass/)\\d+\\b";//匹配出课堂id
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(scanResult);
                if (matcher.find()){
                    classId = Integer.parseInt(matcher.group());
                }
                dialogView();
//                Intent intent=new Intent(MainActivity.this,StudentOTCActivity.class);
//                intent.putExtra("address",scanResult);
//                startActivity(intent);
                /**根据需要对扫描得到的字符串进行操作*/
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
            if(requestCode==1)
            {
                if(resultCode==1)
                {
                    flag=data.getStringExtra("extra_data");
                    mListView.setVisibility(View.VISIBLE);
                    TextView temptv=(TextView)findViewById(R.id.checkhistory);
                    temptv.setText("历史记录");
                    TextView num=(TextView)findViewById(R.id.numinfact);
                    TextView name=(TextView)findViewById(R.id.nameinfact);
                    TextView num1=(TextView)findViewById(R.id.num);
                    TextView name1=(TextView)findViewById(R.id.name);
                    TextView num2=(TextView)findViewById(R.id.sentence);
                    TextView name2=(TextView)findViewById(R.id.sayit);
                    TextView wel=(TextView)findViewById(R.id.welcome);
                    wel.setVisibility(View.VISIBLE);
                    num.setVisibility(View.INVISIBLE);
                    name.setVisibility(View.INVISIBLE);
                    num1.setText("登录后返回一个学号");
                    name1.setText("登录后返回一个姓名");
                    num2.setText("为中华之崛起而读书");
                    name2.setText("——周恩来");
                    Button tempbt=(Button)findViewById(R.id.LoginButton);
                    tempbt.setVisibility(View.INVISIBLE);
                    tempbt.setEnabled(false);
                    Button tempbt1=(Button)findViewById(R.id.RegisterButton);
                    tempbt1.setVisibility(View.INVISIBLE);
                    tempbt1.setEnabled(false);

                }
            }
        }
        /*super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1)
        {
            if(resultCode==1)
            {
                flag=data.getStringExtra("extra_data");
                mListView.setVisibility(View.VISIBLE);
                TextView temptv=(TextView)findViewById(R.id.checkhistory);
                temptv.setText("历史记录");
                TextView num=(TextView)findViewById(R.id.numinfact);
                TextView name=(TextView)findViewById(R.id.nameinfact);
                TextView num1=(TextView)findViewById(R.id.num);
                TextView name1=(TextView)findViewById(R.id.name);
                TextView num2=(TextView)findViewById(R.id.sentence);
                TextView name2=(TextView)findViewById(R.id.sayit);
                TextView wel=(TextView)findViewById(R.id.welcome);
                wel.setVisibility(View.VISIBLE);
                num.setVisibility(View.INVISIBLE);
                name.setVisibility(View.INVISIBLE);
                num1.setText("登录后返回一个学号");
                name1.setText("登录后返回一个姓名");
                num2.setText("为中华之崛起而读书");
                name2.setText("——周恩来");
                Button tempbt=(Button)findViewById(R.id.LoginButton);
                tempbt.setVisibility(View.INVISIBLE);
                tempbt.setEnabled(false);
                Button tempbt1=(Button)findViewById(R.id.RegisterButton);
                tempbt1.setVisibility(View.INVISIBLE);
                tempbt1.setEnabled(false);

            }
        }*/
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
                Intent intent=new Intent(MainActivity.this,TeacherOTCActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.JoinClass1:
            case R.id.JoinClass2:
            {
                //先检查用户信息，判断是否有登录
                SharedPreferences pref = getSharedPreferences("userInfo",MODE_PRIVATE);
                uid = pref.getString("userId","");
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
            case R.id.LoginButton:
            {
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                startActivityForResult(intent,1);
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
                        //把用户名存储到sharedPreference文件
                        SharedPreferences.Editor editor =
                                getSharedPreferences("userInfo",MODE_PRIVATE).edit();
                        editor.putString("trueName",nameEdt.getText().toString());//将用户ID写进文件
                        editor.apply();

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
        String url = getResources().getString(R.string.joinClassAddress);//服务器注册地址
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
                    intent.putExtra("address",scanResult);
                    intent.putExtra("classId",classId);
//                    intent.putExtra("address","ws://10.243.6.27:8080/webSocket/onClass/117");
//                    intent.putExtra("classId",117);
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
