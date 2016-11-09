package fo.aivot.idem;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {
    Context context;
    EditText main_search;
    ListView search_list;
    TextView no_found;
    String FullSearchURL = "http://212.55.40.9:8080/ftonline/apps/contacts_grp.get_geo_result?p_query=%s&p_limit=30&p_offset=0&p_branch=&p_latitude=0.0&p_longitude=0.0&p_acc=0.0&p_key=n6u8m6a2r7f5o6";
    Activity activity;
    mTracker tracker;
    public static int OVERLAY_PERMISSION_REQ_CODE = 101;
    public static final int PHONESTATE_PERMISSION_REQ_CODE = 102;
    public static final int ALL_PERMISSION_REQ_CODE = 103;


    private Handler handler = new Handler();
    private Runnable postToServerRunnable = new Runnable() {
        @Override
        public void run() {
            if(isNetworkAvailable()){
                String searchURL = FullSearchURL.replace("%s", URLEncoder.encode(main_search.getText().toString()));
                findViewById(R.id.progressBar1).setVisibility(View.VISIBLE);
                new RequestTask(activity).execute(searchURL);
                tracker.trackEvent("Search", "Number", main_search.getText().toString());
            }else{
                Toast connectionToast = Toast.makeText(MainActivity.this, getString(R.string.no_internet), Toast.LENGTH_LONG);
                connectionToast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                connectionToast.show();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        activity = this;
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Locale locale = new Locale(sharedPrefs.getString("list_language", "en"));
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());


        setContentView(R.layout.activity_main);


        tracker = new mTracker(context);
        search_list = (ListView)findViewById(R.id.search_list);
        no_found = (TextView)findViewById(R.id.no_find);
        main_search = (EditText)findViewById(R.id.main_search);
        main_search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(postToServerRunnable);
                handler.postDelayed(postToServerRunnable, 500);
            }
        });
        Button clickButton = (Button) findViewById(R.id.calc_clear_txt_Prise);
        clickButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                main_search.setText("");
                handler.removeCallbacks(postToServerRunnable);
                search_list.setVisibility(View.GONE);
                no_found.setVisibility(View.GONE);
            }
        });
        tracker.trackScreenView("Home");


        Boolean turnedOn = sharedPrefs.getBoolean("prefOverlay",false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.v("Main", turnedOn + "");
            if(!android.provider.Settings.canDrawOverlays(context) && turnedOn){
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Hov!");
                alertDialog.setMessage(getString(R.string.permission_alert));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Alright",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:" + getPackageName()));
                                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Noy",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }

            RequestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS},ALL_PERMISSION_REQ_CODE);

        }else{
            Log.v("Main", Build.VERSION.SDK_INT + "");
        }



    }

    public void RequestPermissions(Activity activity, String[] androidPermissionName,int constant) {
        List<String> permissionRequests = new ArrayList<>();
        for(int i=0;i<androidPermissionName.length;i++){
            if(ContextCompat.checkSelfPermission(activity, androidPermissionName[i]) != PackageManager.PERMISSION_GRANTED) {
                if(!ActivityCompat.shouldShowRequestPermissionRationale(activity, androidPermissionName[i])){
                    Intent intent = new Intent(this, Settings.class);
                    startActivity(intent);
                }else{
                    permissionRequests.add(androidPermissionName[i]);
                }
            }
        }

        if(permissionRequests.size() == 0){
            Log.v("perm","all good..");
        }else{
            ActivityCompat.requestPermissions(MainActivity.this,
                    permissionRequests.toArray(new String[permissionRequests.size()]),
                    constant);
        }
    }

    public void RequestPermission(Activity activity, String androidPermissionName,int constant) {
        if(ContextCompat.checkSelfPermission(activity, androidPermissionName) != PackageManager.PERMISSION_GRANTED) {
            if(!ActivityCompat.shouldShowRequestPermissionRationale(activity, androidPermissionName)){
                Intent intent = new Intent(this, Settings.class);
                startActivity(intent);
            }
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{androidPermissionName},
                    constant);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PHONESTATE_PERMISSION_REQ_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!android.provider.Settings.canDrawOverlays(this)) {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Hov!");
                    alertDialog.setMessage(
                            "Tað sær út til at tú hevur Android M.\n" +
                                    "Ja altso, eg fái ikki víst tær hvør ringir í løtuni." +
                                    "Við Android M. so má eg spyrja teg um loyvi." +
                                    "So um tú vilt sleppa av við hesið boð, so mást tú gera eitt av tveimum" +
                                    "Antin, sløkkur tú tað inni á stillingar, ella loyvir tú appini tað hon vil"
                    );
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Alright",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                            Uri.parse("package:" + getPackageName()));
                                    startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Noy",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
        }else if(requestCode == PHONESTATE_PERMISSION_REQ_CODE){

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_about){
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }else if(id == R.id.menu_settings){
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
