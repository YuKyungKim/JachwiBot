package com.soma.albert.jachwibot;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.roboid.robot.Device;
import org.smartrobot.android.RobotActivity;
import org.smartrobot.android.action.Action;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by josh on 15. 8. 15..
 */
public class Conversation  extends RobotActivity {

    private Action mAction;
    private SimsimiAPI simsimiAPI; // a class represents for requesting
    private String result = "Fail";
    private BufferedReader bufferReader = null;
    private InputStreamReader inputStreamReader = null;
    public Device mSpeakerDevice;
    private Context context;
    public String simsimi_response=null;
    public boolean oncheck=false;
    public Conversation(Context current){
        this.context=current;
    }
    public void start(Device speaker) {
        mSpeakerDevice = speaker;
        oncheck=false;
        mAction = Action.obtain(context, Action.VoiceRecognition.ID);//google_stt_action 만들기
        mAction.addDeviceDataChangedListener(this);
        mAction.activate();
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();

        mAction.deactivate();
        mAction.dispose();
    }
    @Override
    public void onDeviceDataChanged(Device device, Object values, long timestamp)
    {
        switch(device.getId())
        {
            case Action.VoiceRecognition.EVENT_TEXT://stt_action 발생
            {
                if(oncheck==false) {
                    String[] results = (String[]) values;
                    simsimiAPI = new SimsimiAPI(results[0]);//stt list 중 제일 높은 결과 값을 simsimi로 보냄
                    simsimiAPI.execute();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    while (true) {

                        if (simsimiAPI.getStatus() == AsyncTask.Status.PENDING) {
                            break;
                        }
                        if (simsimiAPI.getStatus() == AsyncTask.Status.FINISHED) {//simsimi에서 request값을 받아오는게 끝났을 시
                            this.simsimi_response = simsimiAPI.get_result();//simsimi의 대답
                            /////////////google에 tts request를 보내는 부분///////////
                            Get_Google_Voice get_google_voice = new Get_Google_Voice();
                            get_google_voice.execute(simsimi_response);
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            ByteArrayOutputStream sample = get_google_voice.get_result();
                            /////////////////////////////////////////////////////

                            ArrayList mPcmQueue = new ArrayList();//480개씩 pcm 데이터를 쪼개 보내야 됨
                            Decoder_pcm decoder_pcm = new Decoder_pcm();
                            try {
                                mPcmQueue = decoder_pcm.start(sample);//pcm으로 디코딩 된 데이터를 ArrayList에 저장
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            long ts, es;
                            int[] data = null;
                            int k = 0;
                            System.gc();
                            while (true) {
                                ts = System.currentTimeMillis();
                                data = null;
                                if (!mPcmQueue.isEmpty()) {
                                    data = (int[]) mPcmQueue.remove(0);
                                    if (data != null)
                                        mSpeakerDevice.write(data);//스피커 출력
                                    else {
                                        break;
                                    }
                                /*
                                20ms마다 정확히 보내는 방법이 없음..
                                또한 garbage collector가 작동시 멈추는 한계점이 있음..
                                Thread.sleep의 경우 오차가 큼
                                 */
                                    while (true) {//20ms마다 write해야되므로 20ms를 세어줌
                                        es = System.currentTimeMillis();
                                        if (es - ts >= 20) {
                                            break;
                                        }
                                    }
                                } else {
                                    break;
                                }
                            }
                            ////////////////////////////////////////////////////////
                            simsimi_response = null;
                            Log.d("ddd", results[0]);
                            break;
                        }
                    }
                    oncheck=true;
                }
                break;
            }
        }
    }

}
