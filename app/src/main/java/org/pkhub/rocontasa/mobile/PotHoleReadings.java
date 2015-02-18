package org.pkhub.rocontasa.mobile;

import android.content.Context;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import android.speech.tts.TextToSpeech;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.location.Location;
import java.util.Locale;
/**
 * Created by ajith_v on 12/20/2014.
 */
public class PotHoleReadings {
    String getPotholesUrl = "http://ajithv.net/detektor/getclusters";
    private String key = "a";
    TextToSpeech tts;
    public static JSONArray allpotHoles = null;
    public Context ctx;
    private static String TAG = "PotHoleReadings";
    private static PotHoleReadings instance;
    public PotHoleReadings getInstance(Context ctx) {
        this.ctx = ctx;
        if(instance == null) {
            instance = new PotHoleReadings();
        }
        return instance;
    }
    public JSONArray getAllpotHoles() {
        if(allpotHoles==null){
            getPotholes();
        }
        return allpotHoles;
    }

    public void setAllpotHoles(JSONArray allpotHoles) {
        this.allpotHoles = allpotHoles;
    }

    private void getPotholes(){
        new Thread() {
            public void run() {
                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpGet get = new HttpGet(getPotholesUrl);
                    HttpResponse response = client.execute(get);
                    HttpEntity httpEntity = response.getEntity();
                    InputStream is = httpEntity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "n");
                    }
                    is.close();
                    String json = sb.toString();
                    Log.d(TAG,"Response : " + json);
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray jsonArray = jsonObject.getJSONArray(key);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Log.d(TAG,"x:" + jsonArray.getJSONObject(i).getDouble("x") + " y:" +jsonArray.getJSONObject(i).getDouble("y") );
//                        checkIfNearPothole(jsonArray.getJSONObject(i).getDouble("x"),jsonArray.getJSONObject(i).getDouble("y"),12.866864705745659,77.63371509563127);
                    }
                    setAllpotHoles(jsonObject.getJSONArray(key));
                }catch (Exception e){
                    e.printStackTrace();
                    Log.d(TAG, "getAllPotHoles Exception : " + e.getMessage());
                }
            }
        }.start();

    }
}