package fo.aivot.idem;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by napoleon on 02-03-2015.
 */
class RequestTask extends AsyncTask<String, String, String>{
    ListView search_list;
    TextView no_found;
    private Activity mContext;
    private JSONArray contacts;

    public RequestTask(Activity context){
        mContext = context;

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
        mContext.findViewById(R.id.progressBar1).setVisibility(View.GONE);
        search_list = (ListView)mContext.findViewById(R.id.search_list);
        no_found = (TextView)mContext.findViewById(R.id.no_find);
        if(result != null && result.length()>0){
            JSONObject jObject = null;
            try {
               jObject = new JSONObject(result);
               contacts = jObject.getJSONArray("contacts");
               String firstContact = contacts.getJSONObject(0).getString("firstName");
                Log.v("results",firstContact);
                if(firstContact.equals("Onki úrslit")){
                   //no results...
                   search_list.setVisibility(View.GONE);
                    no_found.setVisibility(View.VISIBLE);
                    Log.v("results","no results...");
               }else{
                   search_list.setVisibility(View.VISIBLE);
                   no_found.setVisibility(View.GONE);
                   List<String[]> dataList = new ArrayList<String[]>();
                   for (int i=0; i < contacts.length(); i++) {
                       JSONObject contact = contacts.getJSONObject(i);

                       int id = contact.getInt("id");

                       dataList.add(new String[]{
                               contact.getString("area"),
                               contact.getString("firstName"),
                               contact.getString("lastName"),
                               contact.getJSONArray("phoneNumbers").getJSONObject(0).getString("phoneNumber")
                       });
                   }
                   search_list.setAdapter(new ListAdapter(mContext, dataList));

                   search_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                       @Override
                       public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                           try {
                               Intent intent = new Intent(mContext,PersonView.class);
                               intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                               JSONObject contact = contacts.getJSONObject(position);
                               intent.putExtra("personObject", contact.toString());
                               mContext.startActivity(intent);
                           } catch (JSONException e) {
                               e.printStackTrace();
                           }
                       }
                   });
               }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else{
            Log.v("Main_act",result+"...");
        }


    }
}