package fo.aivot.idem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.*;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hamburger on 17-02-2016.
 */
public class minRequestTask extends AsyncTask<String, String, String> {
    private JSONArray contacts;
    private RelativeLayout mContext;
    WindowManager windowManager;
    WindowManager.LayoutParams params;

    public minRequestTask(RelativeLayout context, WindowManager windowManager, WindowManager.LayoutParams params){
        mContext = context;
        this.windowManager =windowManager;
        this.params=params;


    }



    @Override
    protected String doInBackground(String... uri) {
        URL url;
        HttpURLConnection urlConnection = null;
        String response = null;

        try {
            url = new URL(uri[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            int responseCode = urlConnection.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK){
                String responseString = readStream(urlConnection.getInputStream());
                response = responseString;
            }else{
                Log.v("HTTP", "Response code:" + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(urlConnection != null)
                urlConnection.disconnect();
        }

        return response;
    }


    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result != null && result.length()>0){
            JSONObject jObject = null;
            try {
                jObject = new JSONObject(result);
                contacts = jObject.getJSONArray("contacts");
                String firstContact = contacts.getJSONObject(0).getString("firstName");
                Log.v("results", firstContact);
                if(firstContact.equals("Onki úrslit")){
                    //no results...
                    Log.v("results","no results...");
                }else{
                    //mTracker tracker = new mTracker(null);
                    //tracker.trackEvent("Call", "Incoming", "Found");
                    TextView name = (TextView) mContext.findViewById(R.id.name);
                    String Lastname = contacts.getJSONObject(0).getString("lastName");
                    name.setText(firstContact + " " + Lastname);
                    try {
                        windowManager.removeView(mContext);
                    }catch (Exception e){

                    }
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if(Settings.canDrawOverlays(mContext.getContext())){
                                windowManager.addView(mContext, params);
                            }
                        }else{
                            windowManager.addView(mContext, params);
                        }
                    }catch (Exception e){

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Log.v("Main_act",result+"...");
        }else{
            Log.v("Main_act",result+"...");
        }


    }
}
