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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import kr.robomation.physical.Albert;

public class MainActivity extends RobotActivity implements View.OnClickListener {

    Activity act = this;

    // 날씨 - 아이콘
    TextView currentTempText = null;
    TextView maxMinTempText = null;
    ImageView weatherIcon = null;
    TextView weatherText = null;
    ImageView isRain = null;
    private GpsInfo gps;

    // 날씨 - 통신
    APIRequest api;
    RequestBundle requestBundle;
    String URL = "http://apis.skplanetx.com/weather/current/minutely";
    Map<String, Object> weatherParam;
    String hndResult = "";
    Handler msgHandler;
    // TODO 예보 따로 따오기..

    // 집안일 패널과 알람 패널
    GridView  houseworkGridView, alarmGridView;
    private ArrayList<HouseworkComponent> houseCompList = new ArrayList();
    private ArrayList<AlarmComponent> alarmCompList = new ArrayList();
    private AlertDialog dialog = null;

    // bottom panel - conversation with albert
    private Conversation conversation;
    private Device mSpeakerDevice;
    public String simsimi_response = "";

    // gps
    private double lat;
    private double lon;

    private static final int ALARM_REQUEST = 10;

    @Override
    public void onInitialized(Robot robot) {
        mSpeakerDevice = robot.findDeviceById(Albert.EFFECTOR_SPEAKER);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.albertConnectBtn:
                Intent serviceIntent = new Intent(this, LuncherService.class);
                startService(serviceIntent);
                break;
            case R.id.Alarm_set:
                Intent intent = new Intent(this, AlarmActivity.class);
                intent.putExtra("Class", this.getClass());
                startActivityForResult(intent, ALARM_REQUEST);
                break;
            case R.id.Alarm_Cancel:
                AlarmActivity.alarm_cancel(this, AlarmActivity.MORNING_CALL);
                AlarmActivity.alarm_cancel(this, 1);
                break;
            case R.id.conversationBtn:
                conversation.start(mSpeakerDevice);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                check();
                break;

            // TODO 집안일 및 알람 추가 버튼 만들기

        }
    }

    public void check() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ALARM_REQUEST:
                if (resultCode == RESULT_OK) {
                    int Alarm_data = data.getIntExtra("Alarm_ID", -1);
                    Log.d("Alarm", "ID = " + String.valueOf(Alarm_data));
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // db
        final DBManager dbManager = new DBManager(getApplicationContext(), "jachwibot.db", null, 1);

        // gps lat, lon data communicate
        gps = new GpsInfo(MainActivity.this);
        lat = gps.getLatitude();
        lon = gps.getLongitude();
        //Log.i("gps", ""+lat+", "+lon);

        commWithOpenAPIServer();

        // weather panel
        weather();

        // housework panel
        houseworkGridView = (GridView) findViewById(R.id.houseworkGridView);
        final HouseworkGridAdapter houseworkGridAdapter = new HouseworkGridAdapter();
        houseworkGridView.setAdapter(houseworkGridAdapter);
        // db data call - housework
        final ArrayList<HouseworkComponent> houseworkList = dbManager.selectAllHoseworkData();
        houseCompList.clear();
        if(houseworkList != null) {
            for (int i = 0; i < houseworkList.size(); i++) {
                houseCompList.add(houseworkList.get(i));
            }
        }
        // 추가 버튼
        HouseworkComponent plus = new HouseworkComponent(0, 0, null);
        houseCompList.add(plus);

        houseworkGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getLastVisiblePosition() == position) {
                    Intent intent = new Intent(view.getContext(), HouseworkActivity.class);
                    startActivityForResult(intent, 0);
                } else {
                    dialog = createInflaterDialog(dbManager, houseCompList.get(position).getHouseworkId(), position);
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
        alarmGridView.setAdapter(new alarmGridAdapter());
        // db data call - alarm
        ArrayList<AlarmComponent> alarmList = dbManager.selectAllAlarmData();
        if(alarmList != null) {
            for (int i = 0; i < alarmList.size(); i++) {
                alarmCompList.add(alarmList.get(i));
            }
        }

        // TODO 추가 버튼 만들것.

    }

    private AlertDialog createInflaterDialog(final DBManager dbManager, final int houseCmpId, final int position) {
        final View innerView = getLayoutInflater().inflate(R.layout.housework_dialog, null);
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle("삭제 확인");
        ab.setView(innerView);
        ab.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbManager.delete("DELETE FROM HOUSEWORK_LIST WHERE housework_id = " + houseCmpId + ";");
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

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        setTitle("자취봇");

        //get Context to Decoder
        Decoder_pcm decoder = new Decoder_pcm(this);
        //get Context to Decoder
        conversation = new Conversation(this);

        currentTempText = (TextView) findViewById(R.id.curtemptext);
        maxMinTempText = (TextView) findViewById(R.id.maxMinTempText);
        weatherIcon = (ImageView) findViewById(R.id.weatherImageIcon);
        weatherText = (TextView) findViewById(R.id.weatherText);
        isRain = (ImageView) findViewById(R.id.unbrellaIcon);

        // bottom panel
        // albert connect button
        Button albertConnectBtn = (Button) findViewById(R.id.albertConnectBtn);
        albertConnectBtn.setOnClickListener(this);
        // 알버트랑 대화하기
        Button conversationBtn = (Button) findViewById(R.id.conversationBtn);
        conversationBtn.setOnClickListener(this);
        // 알람 추가 버튼
        findViewById(R.id.Alarm_set).setOnClickListener(this);
        findViewById(R.id.Alarm_Cancel).setOnClickListener(this);
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
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.housework_component, parent, false);
            }

            ImageView cmpIcon = (ImageView) convertView.findViewById(R.id.houseCmpIcon);
            TextView cmpText = (TextView) convertView.findViewById(R.id.houseCmpText);

            HouseworkComponent cmp = houseCompList.get(position);

            if (cmp.getHouseworkType() == 1) {
                cmpIcon.setImageResource(R.drawable.trash);
                cmpText.setText(calcDay(cmp.getLastDay()));
            } else if (cmp.getHouseworkType() == 2) {
                cmpIcon.setImageResource(R.drawable.washing_machine);
                cmpText.setText(calcDay(cmp.getLastDay()));
            } else if (cmp.getHouseworkType() == 0) {
                cmpIcon.setImageResource(R.drawable.plus);
                cmpText.setText("추가");
            }
            return convertView;
        }
    }

    public String calcDay(String lastDate) {
        Calendar todayCal = Calendar.getInstance();
        Calendar lastdayCal = Calendar.getInstance();

        StringTokenizer strTk = new StringTokenizer(lastDate, ".");
        int lastYear = Integer.parseInt(strTk.nextToken());
        int lastMonth = Integer.parseInt(strTk.nextToken());
        int lastDay = Integer.parseInt(strTk.nextToken());

        lastdayCal.set(lastYear, lastMonth - 1, lastDay);

        long delta = todayCal.getTimeInMillis() - lastdayCal.getTimeInMillis() ;

        lastdayCal.setTimeInMillis(delta);

        int date = lastdayCal.get(Calendar.DAY_OF_YEAR) + 1;

        long gap = (delta / 86400000);

        if (gap == 0) {
            return "오늘";
        } else if (gap == 1) {
            return "어제";
        } else {
            return "" + gap  + "일 전";
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
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.alarm_component, parent, false);
            }

            ImageView cmpIcon = (ImageView) convertView.findViewById(R.id.cmpIcon);
            TextView cmpText = (TextView) convertView.findViewById(R.id.cmpText);

            AlarmComponent cmp = alarmCompList.get(position);

            if (cmp.getAlarmId() == 1) {
                cmpIcon.setImageResource(R.drawable.alarm_clock);
                cmpText.setText(cmp.getAlarmText());
            } else if (cmp.getAlarmId() == 2) {
                cmpIcon.setImageResource(R.drawable.clock);
                cmpText.setText(cmp.getAlarmText());
            } else if (cmp.getAlarmId() == 3) {
                cmpIcon.setImageResource(R.drawable.calendar);
                cmpText.setText(cmp.getAlarmText());
            }
            return convertView;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("Alarm", "ID = " + String.valueOf(intent.getIntExtra("ID", -1)) +
                " Repeat = " + String.valueOf(intent.getBooleanExtra("Repeat", false)));
    }

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

    public void commWithOpenAPIServer() {
        api = new APIRequest();
        APIRequest.setAppKey("da0c8662-a2d8-3bc5-83f0-189948f4959e");

        weatherParam = new HashMap<String, Object>();
        // ?version=1&lat="+gpsInfo.lat+"&lon="+gpsInfo.lon
        weatherParam.put("version", "1");
        weatherParam.put("lat", "" + lat);
        weatherParam.put("lon", "" + lon);

        requestBundle = new RequestBundle();
        requestBundle.setUrl(URL);
        requestBundle.setParameters(weatherParam);
        requestBundle.setHttpMethod(PlanetXSDKConstants.HttpMethod.GET);
        requestBundle.setResponseType(PlanetXSDKConstants.CONTENT_TYPE.JSON);

        try {
            api.request(requestBundle, reqListener);
        } catch (PlanetXSDKException e) {
            e.printStackTrace();
        }
    }

    // Opean API 통신시 사용하는 비동기 Listener
    RequestListener reqListener = new RequestListener() {
        @Override
        public void onPlanetSDKException(PlanetXSDKException e) {
            hndResult = e.toString();
            msgHandler.sendEmptyMessage(0);
        }

        @Override
        public void onComplete(ResponseMessage result) {
            // 응답을 받아 메시지 핸들러에 알려준다.
            hndResult = result.toString();
            msgHandler.sendEmptyMessage(0);
        }

    };

    public void weather() {
        msgHandler = new Handler() {
            public void dispatchMessage(Message msg) {
                String tmax = "0", tmin = "0", tcurent = "0", precipType = null;
                String skyName = null, skyCode = "";
                try {
                    JSONParser jsonParser = new JSONParser();
                    JSONObject data = (JSONObject) jsonParser.parse(hndResult);
                    JSONObject weather = (JSONObject) data.get("weather");
                    //Log.i("weather", ""+weather);
                    JSONArray minutely = (JSONArray) weather.get("minutely");
                    //Log.i("minutely", ""+minutely);
                    JSONObject minutely1 = (JSONObject) minutely.get(0);
                    //Log.i("minutely1", ""+minutely);
                    // 강수정보
                    // 0: 현상없음, 1: 비, 2: 비/눈, 3: 눈
                    JSONObject precipitation = (JSONObject) minutely1.get("precipitation");
                    precipType = (String) precipitation.get("type");
                    if (precipType.equals("1") || precipType.equals("2") || precipType.equals("3")) {
                        isRain.setImageResource(R.drawable.umbrella);
                    }
                    // 기온정보
                    JSONObject temperature = (JSONObject) minutely1.get("temperature");
                    //Log.i("temperature", ""+temperature);
                    tmax = (String) temperature.get("tmax");
                    tmin = (String) temperature.get("tmin");
                    tcurent = (String) temperature.get("tc");
                    // 하늘상태정보
                    JSONObject sky = (JSONObject) minutely1.get("sky");
                    //Log.i("sky", ""+sky);
                    skyName = (String) sky.get("name");
                    skyCode = (String) sky.get("code");
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                currentTempText.setText("" + tcurent + "℃");
                maxMinTempText.setText("최고" + tmax.substring(0, 2) + "℃/최저" + tmin.substring(0, 2) + "℃");
                weatherText.setText("" + skyName);
                skyIconMatch(skyCode);
            }

            ;
        };
    }

    public void skyIconMatch(String skyCode) {
        if (skyCode.equals("SKY_A00")) {
            // 상태없음
            weatherIcon.setImageResource(R.drawable.sky_a00);
        } else if (skyCode.equals("SKY_A01")) {
            // 맑음
            weatherIcon.setImageResource(R.drawable.sky_a01);
        } else if (skyCode.equals("SKY_A02")) {
            // 구름조금
            weatherIcon.setImageResource(R.drawable.sky_a02);
        } else if (skyCode.equals("SKY_A03")) {
            // 구름많음
            weatherIcon.setImageResource(R.drawable.sky_a03);
        } else if (skyCode.equals("SKY_A04")) {
            // 구름많고 비
            weatherIcon.setImageResource(R.drawable.sky_a04);
        } else if (skyCode.equals("SKY_A05")) {
            // 구름많고 눈
            weatherIcon.setImageResource(R.drawable.sky_a05);
        } else if (skyCode.equals("SKY_A06")) {
            // 구름많고 비 또는 눈
            weatherIcon.setImageResource(R.drawable.sky_a06);
        } else if (skyCode.equals("SKY_A07")) {
            // 흐림
            weatherIcon.setImageResource(R.drawable.sky_a07);
        } else if (skyCode.equals("SKY_A08")) {
            // 흐리고 비
            weatherIcon.setImageResource(R.drawable.sky_a08);
        } else if (skyCode.equals("SKY_A09")) {
            // 흐리고 눈
            weatherIcon.setImageResource(R.drawable.sky_a09);
        } else if (skyCode.equals("SKY_A10")) {
            // 흐리고 비 또는 눈
            weatherIcon.setImageResource(R.drawable.sky_a10);
        } else if (skyCode.equals("SKY_A11")) {
            // 흐리고 낙뢰
            weatherIcon.setImageResource(R.drawable.sky_a11);
        } else if (skyCode.equals("SKY_A12")) {
            // 뇌우, 비
            weatherIcon.setImageResource(R.drawable.sky_a12);
        } else if (skyCode.equals("SKY_A13")) {
            // 뇌우, 눈
            weatherIcon.setImageResource(R.drawable.sky_a13);
        } else if (skyCode.equals("SKY_A14")) {
            // 뇌우, 비 또는 눈
            weatherIcon.setImageResource(R.drawable.sky_a14);
        } else {
            weatherIcon.setImageResource(R.drawable.sky_a00);
        }
    }
}
