package com.soma.albert.jachwibot;

import org.roboid.robot.Device;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by josh on 15. 8. 23..
 */
public class RobotSpeaker
{
    private Device mSpeakerDevice;
    public RobotSpeaker(Device device){
        this.mSpeakerDevice =device;
    }
    public void start(ByteArrayOutputStream sample){

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
    }
}
