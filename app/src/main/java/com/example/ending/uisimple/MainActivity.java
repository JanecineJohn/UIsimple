package com.example.ending.uisimple;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Field;


public class MainActivity extends AppCompatActivity {
    DrawerLayout MainUI;
    LinearLayout Usermenu;
    String flag="";
    private ListView mListView;
    private String[]names={"获取老师端历史记录的日期"};
    private String[][]students={{"一个日期对应一节课的所有学生的应用使用记录，记录就放在这里"}};
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
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
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
                Intent intent=new Intent(MainActivity.this,StudentOTCActivity.class);
                startActivity(intent);
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



}
