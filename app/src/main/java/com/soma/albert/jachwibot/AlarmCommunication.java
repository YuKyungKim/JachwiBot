package com.soma.albert.jachwibot;

import android.content.Context;

import org.roboid.robot.Device;
import org.smartrobot.android.RobotActivity;
import org.smartrobot.android.action.Action;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by josh on 15. 8. 23..
 */
public class AlarmCommunication extends RobotActivity {

    private Action mAction;
    private BufferedReader bufferReader = null;
    private InputStreamReader inputStreamReader = null;
    public Device mSpeakerDevice;
    private Context context;
    public int checker;
    public AlarmCommunication(Context current){
        this.context=current;
    }
    public void speaker_output(ByteArrayOutputStream sample){
        ArrayList mPcmQueue = new ArrayList();//480개씩 pcm 데이터를 쪼개 보내야 됨
        Decoder_pcm decoder_pcm = new Decoder_pcm();
        try {
            mPcmQueue = decoder_pcm.start(sample);//pcm으로 디코딩 된 데이터를 Arraylist에 저장
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
            if(!mPcmQueue.isEmpty()) {
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
                while(true) {//20ms마다 write해야되므로 20ms를 세어줌
                    es = System.currentTimeMillis();
                    if (es - ts >= 20) {
                        break;
                    }
                }
            }
            else{
                break;
            }
        }
    }
    public void start(Device speaker,String Question) {

        mSpeakerDevice = speaker;

        Get_Google_Voice get_google_voice = new Get_Google_Voice();
        get_google_voice.execute(Question);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        speaker_output(get_google_voice.get_result());

        mAction = Action.obtain(context, Action.VoiceRecognition.ID);//google_stt_action 만들기
        mAction.addDeviceDataChangedListener(this);
        mAction.activate();
    }
    public int response_checker(){
        return checker;
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
                String[] strinfo = (String[])values;
                String result=strinfo[0];
                CharSequence[] positives={"응","그래","맞아","했어","어","네","예","예스"};
                CharSequence[] negatives={"아니","아뇨","안했어","아직","노","놉"};
                checker=0;
                for(int i=0;positives.length>i;i++){
                    if (result.contains(positives[i])) {
                        checker=1;
                        break;
                    }
                }
                for(int i=0;negatives.length>i;i++){
                    if (result.contains(negatives[i])) {
                        checker=-1;
                        break;
                    }
                }

            }
        }
    }

}

