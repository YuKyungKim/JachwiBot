package com.soma.albert.jachwibot;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by whee6409 on 15. 8. 24.
 */
public class Weather {
    // 날씨 - 통신
    APIRequest api;
    RequestBundle requestBundle;
    String URL = "http://apis.skplanetx.com/weather/current/minutely";
    Map<String, Object> weatherParam;
    String hndResult = "";
    Handler msgHandler;

    // 날씨 - 아이콘
    TextView currentTempText = null;
    TextView maxMinTempText = null;
    ImageView weatherIcon = null;
    TextView weatherText = null;
    ImageView isRain = null;

    // gps
    private GpsInfo gps;
    private double lat;
    private double lon;

    String tmax = "0", tmin = "0", tcurent = "0", precipType = null;
    String skyName = null, skyCode = "";

    public void Weather() {

    }

    public void findWeather(Context context) {
        // gps lat, lon data communicate
        gps = new GpsInfo(context);
        Log.i("gps", ""+gps.isGPSEnabled);
        if(gps.isGPSEnabled) {
            lat = gps.getLatitude();
            lon = gps.getLongitude();
            commWithOpenAPIServer();

            msgHandler = new Handler() {
                public void dispatchMessage(Message msg) {

                    try {
                        JSONParser jsonParser = new JSONParser();
                        JSONObject data = (JSONObject) jsonParser.parse(hndResult);
                        JSONObject weather = (JSONObject) data.get("weather");
                        //Log.i("weather", ""+weather);
                        JSONArray minutely = (JSONArray) weather.get("minutely");
                        //Log.i("minutely", ""+minutely);
                        JSONObject minutely1 = (JSONObject) minutely.get(0);
                        Log.i("minutely1", ""+minutely);
                        // 강수정보
                        // 0: 현상없음, 1: 비, 2: 비/눈, 3: 눈
                        JSONObject precipitation = (JSONObject) minutely1.get("precipitation");
                        precipType = (String) precipitation.get("type");
                        if(precipType.equals("1") || precipType.equals("2") || precipType.equals("3")) {
                            isRain.setImageResource(R.drawable.umbrella);
                        }
                        // 기온정보
                        JSONObject temperature = (JSONObject) minutely1.get("temperature");
                        //Log.i("temperature", "" + temperature);
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
            };
        }
    }

    public String readMessage() {
        String message = "오늘 최고 온도는 "+tmax+"도, 최저 온도는 "+tmin+"입니다. 현재 온도는 "+tcurent+"입니다. ";
        message+="현재 하늘상태는 "+skyName+" 입니다.";
        return message;
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
}

