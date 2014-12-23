package ly.leash.Leashly;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by schwallie on 12/11/2014.
 */
public class LoginStartup extends AsyncTask<String, Void, Void> {
    private final Context context;
    String username;
    GoogleCloudMessaging gcm;
    String regid;
    String PROJECT_NUMBER = "621850944390";

    public LoginStartup(String username, Context context) {
        //super();
        Log.d("usernameInLogin", username + "");
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        String result = "";
        Integer walker = 0;
        Integer user_id = 0;
        Double lat = 0.0;
        Double lon = 0.0;
        String data = null;
        String json_data = null;
        InputStream is = null;
        username = params[0];
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("user", username));
        Log.d("getUserInfo", username + "");

//http post
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://leash.ly/webservice/get_data.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }
//convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();

            result = sb.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }

//parse json data
        try {
            JSONArray jArray = new JSONArray(result);
            for (int i = 0; i < jArray.length(); i++) {
                json_data = jArray.getJSONObject(i).toString();
            }
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }


        try {
            JSONObject jobj = new JSONObject(json_data);
            walker = jobj.getInt("walker");
            user_id = jobj.getInt("id");
            lat = jobj.getDouble("lat");
            lon = jobj.getDouble("long");
            registerId(user_id);


        } catch (JSONException e) {
            e.printStackTrace();
            // do something
        }
        try {
            if (walker.equals(0)) {
                Intent i = new Intent(context, AvailableList.class);
                //finish();
                i.putExtra("user", username);
                i.putExtra("user_id", user_id + "");
                i.putExtra("lat", lat);
                i.putExtra("lon", lon);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            } else {
                Intent i = new Intent(context, WalkerMain.class);
                //finish();
                i.putExtra("user", username);
                i.putExtra("user_id", user_id + "");
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // do something
        }
        return null;
    }


    public void registerId(final Integer user_id_to_use) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(PROJECT_NUMBER);
                    msg = "Device registered, registration ID=" + regid;
                    Log.i("GCM", msg);
                    //POST2GCM.post(regid);

                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("user_id", user_id_to_use.toString()));
                    nameValuePairs.add(new BasicNameValuePair("gcm", regid));

//http post
                    try {
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost httppost = new HttpPost("http://leash.ly/webservice/register_user_gcm.php");
                        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                        HttpResponse response = httpclient.execute(httppost);
                        HttpEntity entity = response.getEntity();
                        InputStream is = null;
                        is = entity.getContent();
                    } catch (Exception e) {
                        Log.e("log_tag", "Error in http connection " + e.toString());
                    }
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }
        }.execute(null, null, null);

    }
}



