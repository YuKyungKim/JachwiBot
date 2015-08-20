package com.soma.albert.jachwibot;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

/**
 * Created by josh on 15. 8. 18..
 */
public class Decoder_pcm {
    private Context context;
    public Decoder_pcm(Context current){
        this.context=current;
    }
    public Decoder_pcm(){
    }
    public ArrayList start(ByteArrayOutputStream resource) throws IOException {
        InputStream in = new ByteArrayInputStream(resource.toByteArray());
        byte[] result = decode(in, 0, 30000);//inputdata, 시작하는 시간, 끝나는 시간

        final ArrayList mPcmQueue = new ArrayList();
        int i=0;
        int k=0;
        int[] data = new int[479];
        byte[] deby = new byte[5];
        while(result.length>=k+4){
            data[i]=byte2short(result[k],result[k+1]);
            k+=2;
            i++;
            if(i==479){
                mPcmQueue.add(data);
                data = new int[479];
                i=0;
            }
        }
        return mPcmQueue;
    }


    public static int byte2short(byte src1, byte src2) {
        int s1 = src1 & 0xFF;
        int s2 = src2 & 0xFF;
//        int s3 = src[2] & 0xFF;
//        int s4 = src[3] & 0xFF;

        return ((s2 << 8) + (s1 << 0));

//        return ((s1 << 24) + (s2 << 16) + (s3 << 8) + (s4 << 0));
    }
    private InputStream open(int resid)
    {
        try
        {
            return new BufferedInputStream(context.getResources().openRawResource(resid));
        } catch (Resources.NotFoundException e)
        {
            return null;
        }
    }
    public byte[] decode(InputStream inputStream, int startMs, int maxMs) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(1024);

        float totalMs = 0;
        boolean seeking = true;

        try {
            Bitstream bitstream = new Bitstream(inputStream);
            Decoder decoder = new Decoder();
            int mono=0;
            int dragger=0;
            boolean done = false;
            while (!done) {
                Header frameHeader = bitstream.readFrame();
                if (frameHeader == null) {
                    done = true;
                } else {
                    totalMs += frameHeader.ms_per_frame();
                    if (totalMs >= startMs) {
                        seeking = false;
                    }

                    if (!seeking) {
                        // logger.debug("Handling header: " + frameHeader.layer_string());
                        SampleBuffer output = (SampleBuffer) decoder.decodeFrame(frameHeader, bitstream);
                        int Channels=output.getChannelCount();
                        int Sample_rate=output.getSampleFrequency();
                        int divider=1;
                        if(Sample_rate<44100){//Sampling_rate에 따라 frame당 얻어지는 데이터량이 다름(pcm.length를 divider로 조정)
                            divider*=2;
                        }
                        if(Channels==1){
                            divider*=2;
                        }
                        else if(Channels==2){
                            mono=2;
                        }
                        short[] pcm = output.getBuffer();
                        if(Sample_rate<=16000) {//sampling_rate가 16000(google_tts)의 경우 16000->24000의 조정이 필요 dragger를 추가하여 2 pcm 데이터당 1pcm 데이터를 복사하여 맞춤
                            for (int i = 0; i < pcm.length / divider; i++) {
                                outStream.write(pcm[i] & 0xff);
                                outStream.write((pcm[i] / 256) & 0xff);
                                dragger++;
                                if (dragger >= 2) {
                                    outStream.write(pcm[i] & 0xff);
                                    outStream.write((pcm[i] / 256) & 0xff);
                                    dragger = 0;
                                }
                            }
                        }
                        else if(mono==1) {//channel이 1인경우 => 정상 진행
                            for (int i = 0; i < pcm.length / divider; i++) {
                                outStream.write(pcm[i] & 0xff);
                                outStream.write((pcm[i] / 256) & 0xff);
                            }
                        }
                        else if(mono==2){//channel이 2인 경우 => 한쪽 채널만 따옴(안하면 노래가 느려짐)
                            for (int i = 0; i < pcm.length / divider; i=i+2) {
                                outStream.write(pcm[i] & 0xff);
                                outStream.write((pcm[i] / 256) & 0xff);
                            }
                        }
                        else if(Sample_rate>=44100){//sampling_rate가 44100인 경우 24000으로 맞추기 위해 데이터를 4개당 하나만 받음
                            for (int i = 0; i < pcm.length / divider; i=i+4) {
                                outStream.write(pcm[i] & 0xff);
                                outStream.write((pcm[i] / 256) & 0xff);
                                outStream.write(pcm[i+1] & 0xff);
                                outStream.write((pcm[i+2] / 256) & 0xff);
                            }
                        }
                    }

                    if (totalMs >= (startMs + maxMs)) {//mp3가 끝나는 경우
                        done = true;
                    }
                }
                bitstream.closeFrame();
            }

            return outStream.toByteArray();
        } catch (BitstreamException e) {
            throw new IOException("Bitstream error: " + e);
        } catch (DecoderException e) {
            throw new IOException("Decoder error: " + e);
        }
    }


}