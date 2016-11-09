package fo.aivot.idem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Debug;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by hamburger on 17-02-2016.
 */
public class IncomingCallService extends BroadcastReceiver {
    public static int CallStatus;
    @Override
    public void onReceive(final Context context, Intent intent) {
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new PhoneStateListener() {
            public Intent intent;
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);

                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                Boolean turnedOn = sharedPrefs.getBoolean("prefOverlay",false);
                CallStatus = state;
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        if(turnedOn && incomingNumber != null && incomingNumber.length() > 0 && isNetworkAvailable()){
                            intent = new Intent(context, phoneOverLay.class).putExtra("phoneNumber", incomingNumber.substring(incomingNumber.length() - 6));
                            context.startService(intent);
                            System.out.println("incomingNumber : " + incomingNumber.substring(incomingNumber.length() - 6));

                        }
                        break;
                    default:
                        //remove view;
                        context.stopService(new Intent(context, phoneOverLay.class));
                        break;
                }
                Log.v("IncomingCallService", "state:" + state);
            }


            public boolean isNetworkAvailable() {
                /*ConnectivityManager cm = (ConnectivityManager)
                        getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();*/
                return true;
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }
}