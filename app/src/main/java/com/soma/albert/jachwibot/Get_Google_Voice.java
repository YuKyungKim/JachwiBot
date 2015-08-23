package com.soma.albert.jachwibot;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by josh on 15. 8. 18..
 */
public class Get_Google_Voice extends AsyncTask<String, ByteArrayOutputStream, ByteArrayOutputStream> {
    public ByteArrayOutputStream result_voice;
    private String response;

    // process background work
    protected ByteArrayOutputStream doInBackground(String... params) {

        this.response = params[0];
        return makeHttpRequest();

    }

    // request information to Simsimi Server
    public ByteArrayOutputStream makeHttpRequest() {
        String TEXT_TO_SPEECH_SERVICE =
                "http://translate.google.com/translate_tts";
        String USER_AGENT =
                "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:11.0) " +
                        "Gecko/20100101 Firefox/11.0";
        String text=this.response;
        String language = "ko";
        try {
            text = URLEncoder.encode(text, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // Create url based on input params
        String strUrl = TEXT_TO_SPEECH_SERVICE + "?" +
                "tl=" + language + "&q=" + text+"&ie=UTF-8&total=1&idx=0&client=t";
        URL url = null;
        try {
            url = new URL(strUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Etablish connection
        HttpURLConnection connection = null;
        // Get method
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            connection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        // Set User-Agent to "mimic" the behavior of a web browser. In this
        // example, I used my browser's info
        try {
            connection.addRequestProperty("User-Agent", USER_AGENT);
        }
        catch (Exception e){
        }
        try {
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get content
        BufferedInputStream bufIn =
                null;
        try {
            bufIn = new BufferedInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] buffer = new byte[1024];
        int n;
        ByteArrayOutputStream bufOut = new ByteArrayOutputStream();
        try {
            while ((n = bufIn.read(buffer)) > 0) {
                bufOut.write(buffer, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Done");
        // consists of JSON Format.
        result_voice=bufOut;
        return bufOut;

    } // end of makeHttpRequest method
    public ByteArrayOutputStream get_result(){
        return result_voice;
    }
    /**
     * After background works finisheds, This method is called. Result of
     * doInBackground method is transmitted to onPostExecute's parameter
     */
    protected void onPostExecute(ByteArrayOutputStream bufOut) {
        result_voice=bufOut;
    }
}