package com.example.ending.uisimple;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class TeacherACActivity extends AppCompatActivity {

    private ListView mListView;
    private String[]nums={"下课后返回学生们的学号"};
    private String[]names={"下课后返回学生们的姓名"};
    private String[]apps={"下课后返回学生们的使用时间最长的应用"};
    private String[]time={"下课后返回学生们的对应应用使用时间"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_ac);

        mListView=(ListView)findViewById(R.id.lv);
        TeacherACActivity.MyBaseAdapter mAdapter=new TeacherACActivity.MyBaseAdapter();
        mListView.setAdapter(mAdapter);
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
            View view = View.inflate(TeacherACActivity.this,R.layout.list_student,null);
            TextView mname =(TextView)view.findViewById(R.id.sname);
            TextView mnum =(TextView)view.findViewById(R.id.snum);
            TextView mapp =(TextView)view.findViewById(R.id.sapp);
            TextView mtime =(TextView)view.findViewById(R.id.stime);
            mname.setText("姓名:"+names[position]);
            mnum.setText("学号:"+nums[position]);
            mapp.setText("使用最久:"+apps[position]);
            mtime.setText("时长:"+time[position]+"min");
            return view;
        }
    }


    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.tacBack:{
                Intent intent = new Intent();
                intent.setClass(TeacherACActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//设置不要刷新将要跳到的界面
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//它可以关掉所要到的界面中间的activity
                startActivity(intent);
                break;
            }
        }
    }
}
