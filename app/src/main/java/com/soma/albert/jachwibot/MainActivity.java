package com.soma.albert.jachwibot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.roboid.robot.Device;
import org.roboid.robot.Robot;
import org.smartrobot.android.RobotActivity;

import java.util.ArrayList;

import kr.robomation.physical.Albert;


public class MainActivity extends RobotActivity implements View.OnClickListener{

    Activity act = this;
    GridView houseworkGridView, alarmGridView;
    private ArrayList<HouseworkComponent> houseCompList = new ArrayList();
    private ArrayList<AlarmComponent> alarmCompList = new ArrayList();
    private Conversation conversation;
    private Device mSpeakerDevice;

    public String simsimi_response="";

    @Override
    public void onInitialized(Robot robot)
    {
        mSpeakerDevice = robot.findDeviceById(Albert.EFFECTOR_SPEAKER);
    }
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.albertConnectBtn) {
            Intent serviceIntent = new Intent(this, LuncherService.class);
            startService(serviceIntent);
        }
        else if(v.getId() == R.id.conversationBtn) {//알버트 대화 버튼
            conversation.start(mSpeakerDevice);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            check();
        }
    }
    public void check(){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get Context to Decoder
        Decoder_pcm decoder = new Decoder_pcm(this);
        //get Context to Decoder
        conversation= new Conversation(this);
        // test component
        houseCompList.add(new HouseworkComponent(1, "오늘"));
        houseCompList.add(new HouseworkComponent(2, "3일째"));

        alarmCompList.add(new AlarmComponent(1, "모닝콜"));
        alarmCompList.add(new AlarmComponent(2, "알람"));
        alarmCompList.add(new AlarmComponent(3, "스케줄 알람"));

        setContentView(R.layout.activity_main);

        houseworkGridView = (GridView) findViewById(R.id.houseworkGridView);
        houseworkGridView.setAdapter(new houseworkGridAdapter());

        alarmGridView = (GridView) findViewById(R.id.alarmGridView);
        alarmGridView.setAdapter(new alarmGridAdapter());

        // 알버트와 스마트폰을 연결하는 버튼
        Button albertConnectBtn = (Button) findViewById(R.id.albertConnectBtn);
        albertConnectBtn.setOnClickListener(this);

        // 알버트랑 대화하기
        Button conversationBtn = (Button) findViewById(R.id.conversationBtn);
        conversationBtn.setOnClickListener(this);

    }

    public class houseworkGridAdapter extends BaseAdapter {
        LayoutInflater inflater;

        public houseworkGridAdapter() {
            inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public final int getCount() {
            return houseCompList.size();
        }

        @Override
        public final Object getItem(int position) {
            return houseCompList.get(position);
        }

        @Override
        public final long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = inflater.inflate(R.layout.alarm_component, parent, false);
            }

            ImageView cmpIcon = (ImageView) convertView.findViewById(R.id.cmpIcon);
            TextView cmpText = (TextView) convertView.findViewById(R.id.cmpText);

            HouseworkComponent cmp = houseCompList.get(position);

            if(cmp.getHouseworkId() == 1) {
                cmpIcon.setImageResource(R.drawable.trash);
                cmpText.setText(cmp.getHouseworkText());
            } else if(cmp.getHouseworkId() == 2) {
                cmpIcon.setImageResource(R.drawable.washing_machine);
                cmpText.setText(cmp.getHouseworkText());
            }

            //Log.v("[HouseworkId, HouseworkText]", cmp.getHouseworkId()+", "+cmp.getHouseworkText());

            return convertView;
        }
    }

    public class alarmGridAdapter extends BaseAdapter {
        LayoutInflater inflater;

        public alarmGridAdapter() {
            inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public final int getCount() {
            return alarmCompList.size();
        }

        @Override
        public final Object getItem(int position) {
            return alarmCompList.get(position);
        }

        @Override
        public final long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = inflater.inflate(R.layout.alarm_component, parent, false);
            }

            ImageView cmpIcon = (ImageView) convertView.findViewById(R.id.cmpIcon);
            TextView cmpText = (TextView) convertView.findViewById(R.id.cmpText);

            AlarmComponent cmp = alarmCompList.get(position);

            if(cmp.getAlarmId() == 1) {
                cmpIcon.setImageResource(R.drawable.alarm_clock);
                cmpText.setText(cmp.getAlarmText());
            } else if(cmp.getAlarmId() == 2) {
                cmpIcon.setImageResource(R.drawable.clock);
                cmpText.setText(cmp.getAlarmText());
            } else if(cmp.getAlarmId() == 3) {
                cmpIcon.setImageResource(R.drawable.calendar);
                cmpText.setText(cmp.getAlarmText());
            }

            //Log.v("[AlarmId, AlarmText]", cmp.getAlarmId()+", "+cmp.getAlarmText());

            return convertView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
