package com.soma.albert.jachwibot;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.skp.openplatform.android.sdk.api.APIRequest;
import com.skp.openplatform.android.sdk.common.PlanetXSDKConstants;
import com.skp.openplatform.android.sdk.common.PlanetXSDKException;
import com.skp.openplatform.android.sdk.common.RequestBundle;
import com.skp.openplatform.android.sdk.common.RequestListener;
import com.skp.openplatform.android.sdk.common.ResponseMessage;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.roboid.robot.Device;
import org.roboid.robot.Robot;
import org.smartrobot.android.RobotActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.StringTokenizer;

import kr.robomation.physical.Albert;

public class MainActivity extends RobotActivity implements View.OnClickListener {

    Activity act = this;

    Weather weather = new Weather();

    // setting panel
    GridView settingGridView;
    private Device mBatteryDevice;
    private ArrayList<SettingComponent> settingList = new ArrayList();
    private boolean isConnectedWithAlbert;
    int[] imageID = new int[4];
    String[] text = {
            "", // 알버트 연결 상태
            "", // 알버트 배터리 상태
            "날씨 알림", // 날씨 읽어주기 enable
            "일정 알림", // 일정 읽어주기 enable
    };
    int battery = 0;

    // 집안일 패널과 알람 패널
    GridView houseworkGridView, alarmGridView;
    private ArrayList<HouseworkComponent> houseCompList = new ArrayList();
    private ArrayList<AlarmComponent> alarmCompList = new ArrayList();
    private AlertDialog dialog = null;

    // bottom panel - conversation with albert
    private Conversation conversation;
    private AlarmCommunication alarmcommute;
    private CalendarAlarm calendaralarm;
    //private Get_Google_Voice get_google_voice = new Get_Google_Voice();
    private Device mSpeakerDevice;
    private Decoder_pcm decoder;
    private RobotSpeaker robotspeaker;
    public String simsimi_response = "";

    boolean readCalendar = true;
    boolean readWeather = true;

    private static final int ALARM_REQUEST = 10;
    private Robot_Alarm robot_alarm;

    final Handler handler = new Handler();

    private int count = 0;

    @Override
    public void onInitialized(Robot robot) {
        mBatteryDevice = robot.findDeviceById(Albert.SENSOR_BATTERY);
        mSpeakerDevice = robot.findDeviceById(Albert.EFFECTOR_SPEAKER);
        robot_alarm = new Robot_Alarm(robot);
        robot_alarm.setCallBackEvent(callBackEvent);
        robot.addDeviceDataChangedListener(this);
    }

    private Robot_Alarm.CallBackEvent callBackEvent = new Robot_Alarm.CallBackEvent(){
        @Override
        public void Robot_Alarm_Stop(int alarm_type) {
            Log.d("Robot_Alarm", "stop");

            if (alarm_type != 0) {
                String result = "";
                if (readCalendar) {
                    result += calendaralarm.get_first_schedule();
                    if (result == null) {
                        result = "오늘 일정이 없습니다.";
                    }
                }
                if (readWeather) {
                    result += weather.readMessage();
                }
                Get_Google_Voice get_google_voice = new Get_Google_Voice();
                get_google_voice.execute(result);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }
                robotspeaker = new RobotSpeaker(mSpeakerDevice);
                robotspeaker.start(get_google_voice.get_result());
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        setTitle("자취봇");

        // weather

        weather.findWeather(this);

        // weather panel
        weather.currentTempText = (TextView) findViewById(R.id.curtemptext);
        weather.maxMinTempText = (TextView) findViewById(R.id.maxMinTempText);
        weather.weatherIcon = (ImageView) findViewById(R.id.weatherImageIcon);
        weather.weatherText = (TextView) findViewById(R.id.weatherText);
        weather.isRain = (ImageView) findViewById(R.id.unbrellaIcon);

        //get Context to CalendarAlarm
        calendaralarm = new CalendarAlarm(this);

        //get Context to Decoder
        decoder = new Decoder_pcm(this);
        //get Context to Decoder
        conversation = new Conversation(this);
        alarmcommute = new AlarmCommunication(this);

        // setting panel
        settingGridView = (GridView) findViewById(R.id.settingGridView);

        albertCondition();

        // bottom panel
        // 알버트랑 대화하기
        Button conversationBtn = (Button) findViewById(R.id.conversationBtn);
        conversationBtn.setOnClickListener(this);
    }

    void albertCondition() {
        Log.i("isConnect", ""+isConnectedWithAlbert);
        if(isConnectedWithAlbert) {
            imageID[0] = R.drawable.albert_color;
            text[0] = "연결 됨";
        } else {
            imageID[0] = R.drawable.albert_bw;
            text[0] = "연결 안 됨";
        }

        if(mBatteryDevice != null) {
            battery = mBatteryDevice.read();
        }
        imageID[1] = R.drawable.battery;
        text[1] = ""+battery+"%";

        if(readWeather) {
            imageID[2] = R.drawable.weather;
        } else {
            imageID[2] = R.drawable.weather_disable;
        }

        if(readCalendar) {
            imageID[3] = R.drawable.calendar;
        } else {
            imageID[3] = R.drawable.calendar_disable;

        }


        handler.post(new Runnable() {
            @Override
            public void run() {
                SettingGridAdapter settingGridAdapter = new SettingGridAdapter(MainActivity.this, imageID, text);
                settingGridView.setAdapter(settingGridAdapter);
                settingGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:
                                Intent serviceIntent = new Intent(view.getContext(), LauncherService.class);
                                startService(serviceIntent);
                                break;
                            case 2:
                                readWeather = !readWeather;
                                break;
                            case 3:
                                readCalendar = !readCalendar;
                                break;
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ALARM_REQUEST:
                if (resultCode == RESULT_OK) {
                    int Alarm_data = data.getIntExtra("alarm_type", -1);
                    Log.d("Alarm", "alarm_type = " + String.valueOf(Alarm_data));
                }
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("Alarm", "alarm_type = " + String.valueOf(intent.getIntExtra("alarm_type", -1)) +
                " alarm_name = " + intent.getStringExtra("alarm_name") +
                " isRepeat = " + String.valueOf(intent.getBooleanExtra("isRepeat", false)));
        if(robot_alarm != null){
            robot_alarm.set_Alram_status(true, intent.getIntExtra("alarm_type", -1));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // db
        final DBManager dbManager = new DBManager(getApplicationContext(), "jachwibot.db", null, 1);

        // setting panel

        // housework panel
        houseworkGridView = (GridView) findViewById(R.id.houseworkGridView);
        final HouseworkGridAdapter houseworkGridAdapter = new HouseworkGridAdapter();
        houseworkGridView.setAdapter(houseworkGridAdapter);
        // db data call - housework
        ArrayList<HouseworkComponent> houseworkList = dbManager.selectAllHoseworkData();
        houseCompList.clear();
        for(int i = 0; i < houseworkList.size(); i++) {
            houseCompList.add(houseworkList.get(i));
        }

        // 추가 버튼
        HouseworkComponent plus = new HouseworkComponent(0, 0, null);
        houseCompList.add(plus);
        houseworkGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getLastVisiblePosition() == position) {
                    Intent intent = new Intent(view.getContext(), HouseworkActivity.class);
                    startActivityForResult(intent, 0);
                } else {
                    dialog = createInflaterDialogHouse(dbManager, houseCompList.get(position).getHouseworkId(), position);
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            //Log.i("dismiss", "call");
                            houseworkGridAdapter.notifyDataSetChanged();
                        }
                    });
                    dialog.show();

                }
            }
        });

        // alarm panel
        alarmGridView = (GridView) findViewById(R.id.alarmGridView);
        final AlarmGridAdapter alarmGridAdapter = new AlarmGridAdapter();
        alarmGridView.setAdapter(alarmGridAdapter);
        // db data call - alarm
        ArrayList<AlarmComponent> alarmList = dbManager.selectAllAlarmData();
        alarmCompList.clear();
        for (int i = 0; i < alarmList.size(); i++) {
            alarmCompList.add(alarmList.get(i));
        }


        // 추가 버튼
        AlarmComponent plusAlarm = new AlarmComponent(-1, null, null, null, 0, 0);
        alarmCompList.add(plusAlarm);
        alarmGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getLastVisiblePosition() == position) {
                    //추가 버튼 클릭
                    Intent intent = new Intent(view.getContext(), AlarmActivity.class);
                    intent.putExtra("Class", view.getContext().getClass());
                    startActivityForResult(intent, ALARM_REQUEST);
                } else {
                    //삭제
                    String Message = String.valueOf(alarmCompList.get(position).getHour()) + "시 " +
                    String.valueOf(alarmCompList.get(position).getMin()) + "분 ";
                    if(alarmCompList.get(position).getisRepeat()) {
                        Message += "\n";

                        String Week_str[] = {"false", "일", "월", "화", "수", "목", "금", "토"};
                        String Week[] = alarmCompList.get(position).getWeek().split("/");
                        String temp_msg = "/";
                        for(int i = 1; i < Week.length; i++) {
                            if(Week[i].equals("true")){
                                temp_msg += ", " + Week_str[i];
                            }
                        }
                        Message += temp_msg.replace("/,", "");
                        Message += String.valueOf("요일에 반복");
                    }
                    dialog = createInflaterDialogAlarm(dbManager, alarmCompList.get(position).getAlarmtype(), position, Message);
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            //Log.i("dismiss", "call");
                            alarmGridAdapter.notifyDataSetChanged();
                        }
                    });
                    dialog.show();
                }
            }
        });

    }

    @Override
    public void onExecute() {
        if(count++ == 50) {
            albertCondition();
            isConnectedWithAlbert = false;
            battery = 0;
            count = 0;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.conversationBtn:
                conversation.start(mSpeakerDevice);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onDeviceDataChanged(Device device, Object values, long timestamp) {
        switch(device.getId()) {
            case Albert.SENSOR_ACCELERATION:
                isConnectedWithAlbert = true;
                imageID[0] = R.drawable.albert_color;
                text[0] = "연결 됨";
                battery = mBatteryDevice.read();
                text[1] = ""+battery+"%";
                break;
        }
    }

    private AlertDialog createInflaterDialogHouse(final DBManager dbManager, final int houseCmpId, final int position) {
        final View innerView = getLayoutInflater().inflate(R.layout.dialog, null);
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle("삭제 확인");
        ab.setView(innerView);
        ab.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbManager.delete("HOUSEWORK_LIST", "housework_id", new String[]{String.valueOf(houseCmpId)});
                houseCompList.remove(position);
            }
        });

        ab.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return ab.create();
    }
    private AlertDialog createInflaterDialogAlarm(final DBManager dbManager, final int alarmtype, final int position, String Message) {
        final View innerView1 = getLayoutInflater().inflate(R.layout.dialog, null);
        TextView textView = (TextView)innerView1.findViewById(R.id.msg);
        textView.setText(Message);

        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle("삭제 확인");
        ab.setView(innerView1);
        ab.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alarmCompList.remove(position);
                AlarmActivity.alarm_cancel(getApplicationContext(), alarmtype);
            }
        });

        ab.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return ab.create();
    }

    public class SettingGridAdapter extends BaseAdapter {
        private Context context;
        private int[] imageId;
        private String[] text;

        public SettingGridAdapter(Context c, int[] imageId, String[] text) {
            context = c;
            this.imageId = imageId;
            this.text = text;
        }

        @Override
        public int getCount() { return text.length; }

        @Override
        public Object getItem(int position) { return null; }

        @Override
        public long getItemId(int position) { return 0; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View grid;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(convertView == null) {
                grid = new View(context);
                grid = inflater.inflate(R.layout.setting_component, null);
                ImageView imageView = (ImageView) grid.findViewById(R.id.settingCmpIcon);
                TextView textView = (TextView) grid.findViewById(R.id.settingCmpText);
                imageView.setImageResource(imageId[position]);
                textView.setText(text[position]);
            } else {
                grid = (View) convertView;
            }

            return grid;
        }
    }

    public class HouseworkGridAdapter extends BaseAdapter {
        LayoutInflater inflater;

        public HouseworkGridAdapter() {
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
                convertView = inflater.inflate(R.layout.housework_component, parent, false);
            }

            ImageView cmpIcon = (ImageView) convertView.findViewById(R.id.houseCmpIcon);
            TextView cmpText = (TextView) convertView.findViewById(R.id.houseCmpText);

            HouseworkComponent cmp = houseCompList.get(position);

            if(cmp.getHouseworkType() == 1) {
                cmpIcon.setImageResource(R.drawable.trash);
                cmpText.setText(HouseworkComponent.CalcDay(cmp.getLastDay()));
            } else if(cmp.getHouseworkType() == 2) {
                cmpIcon.setImageResource(R.drawable.washing_machine);
                cmpText.setText(HouseworkComponent.CalcDay(cmp.getLastDay()));
            } else if(cmp.getHouseworkType() == 3) {
                cmpIcon.setImageResource(R.drawable.shoppingcart);
                cmpText.setText(HouseworkComponent.CalcDay(cmp.getLastDay()));
            } else if(cmp.getHouseworkType() == 0) {
                cmpIcon.setImageResource(R.drawable.plus);
                cmpText.setText("추가");
            }
            return convertView;
        }
    }
    public class AlarmGridAdapter extends BaseAdapter {
        LayoutInflater inflater;

        public AlarmGridAdapter() {
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
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.alarm_component, parent, false);
            }

            ImageView cmpIcon = (ImageView) convertView.findViewById(R.id.cmpIcon);
            TextView cmpText = (TextView) convertView.findViewById(R.id.cmpText);

            AlarmComponent cmp = alarmCompList.get(position);

            if (cmp.getAlarmtype() == -1) {
                cmpIcon.setImageResource(R.drawable.plus);
                cmpText.setText("추가");
            } else if (cmp.getAlarmtype() == 0) {
                cmpIcon.setImageResource(R.drawable.alarm_clock);
                cmpText.setText("모닝콜");
            } else {
                cmpIcon.setImageResource(R.drawable.clock);
                cmpText.setText(cmp.getAlarmname());
            }

            return convertView;
        }
    }
}
