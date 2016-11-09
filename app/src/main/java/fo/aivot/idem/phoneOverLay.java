package fo.aivot.idem;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.URLEncoder;


public class phoneOverLay extends Service {

    static WindowManager windowManager;
    public RelativeLayout chatHead ;
    Context context;
    WindowManager.LayoutParams params;

    @Override public IBinder onBind(Intent intent) {

        return null;
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null && intent.getStringExtra("phoneNumber").length() > 0 && !intent.getStringExtra("phoneNumber").equals("0")){
            String phoneNumber = intent.getStringExtra("phoneNumber");
            String FullSearchURL = "http://212.55.40.9:8080/ftonline/apps/contacts_grp.get_geo_result?p_query=%s&p_limit=30&p_offset=0&p_branch=&p_latitude=0.0&p_longitude=0.0&p_acc=0.0&p_key=n6u8m6a2r7f5o6";
            String searchURL = FullSearchURL.replace("%s", URLEncoder.encode(phoneNumber.toString()));

            if(isNetworkAvailable()){
                Log.v("MaIn","so far, so good");
                new minRequestTask(chatHead,windowManager,params).execute(searchURL);
            }else{
                JSONArray json = new JSONArray();
                json.put(isNetworkAvailable());
                Log.v("MaIn", json.toString());
            }

        }else{
            stopSelf();
        }
        return Service.START_STICKY;
    }


    @Override public void onCreate() {
        super.onCreate();
        context = this;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        chatHead = (RelativeLayout)View.inflate(this, R.layout.phone_overlay, null);
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
                );

        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = 100;

        chatHead.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(IncomingCallService.CallStatus != TelephonyManager.CALL_STATE_RINGING){
                    windowManager.removeView(chatHead);
                    stopSelf();
                }


                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(chatHead, params);
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatHead != null){
            try {
                windowManager.removeView(chatHead);
            }catch (Exception e){

            }
        }
    }
}