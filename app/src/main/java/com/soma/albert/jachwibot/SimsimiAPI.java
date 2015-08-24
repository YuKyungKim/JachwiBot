package com.soma.albert.jachwibot;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by josh on 15. 8. 18..
 */


public class SimsimiAPI extends AsyncTask<Void, Void, String> {

    //        private TextView response;
    private String req_msg;
    public String result;
    private BufferedReader bufferReader = null;
    private InputStreamReader inputStreamReader = null;
    public String simsimi_response;
    // process background work
    @Override
    protected String doInBackground(Void... voids) {
        return makeHttpRequest();
    }

    public SimsimiAPI(String strValue) {
        this.req_msg = strValue;
    }

    // request information to Simsimi Server
    public String makeHttpRequest() {
        String key = "dcca7d58-3a29-4e39-88ec-9dba80b0ecc9";
        String lc = "ko";
        double ft = 1.0;

        try {

            String text = URLEncoder.encode(req_msg, "UTF-8");
            String url = "http://api.simsimi.com/request.p?key="
                    + key + "&lc=" + lc + "&ft=" + ft + "&text=" + text;

            // Request
            URL simsimi = new URL(url);

            // Response
            BufferedReader input = null;
            input = new BufferedReader(new InputStreamReader(simsimi.openStream()));
            String line = "";
            while ((line = input.readLine()) != null) {
                result = line;
            }
            result = result.substring(result.indexOf("response") + 11, result.indexOf("\","));
            result = result.replace("\\n", " ");
            result = result.replaceAll("\\W", "");
            input.close();
/*                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpRequest = new HttpGet(url);

                HttpResponse response = httpClient.execute(httpRequest);

                inputStreamReader = new InputStreamReader(response.getEntity()
                        .getContent());
                bufferReader = new BufferedReader(inputStreamReader);

                while ((buffer = bufferReader.readLine()) != null) {
                    if (buffer.length() > 1) {
                        result = buffer;
                    }
                }*/
        } catch (Exception e) {
        } finally {

            // InputStreamReader is closed
            if (inputStreamReader != null)
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    System.out.println("InputStreamReader is not closed.");
                }

            // BufferedReader is closed.
            if (bufferReader != null)
                try {
                    bufferReader.close();
                } catch (IOException e) {
                    System.out.println("BufferedReader is not closed.");
                }
        }
        simsimi_response=result;
        return result; // return Server's response information which
        // consists of JSON Format.

    } // end of makeHttpRequest method


    protected void onPostExecute(String page) {
//            Device mTextDevice = mAction2.findDeviceById(Action.Tts.COMMAND_TEXT);
//            mTextDevice.writeString(page.toString());
//            mAction2.activate();
//            response.setText(page);
    }
    public String get_result(){
        return simsimi_response;
    }
}
