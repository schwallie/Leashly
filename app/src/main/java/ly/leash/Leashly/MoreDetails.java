package ly.leash.Leashly;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by schwallie on 12/22/2014.
 Leashly!
 */
public class MoreDetails extends ActionBarActivity implements View.OnClickListener {
    private static final String REGISTER_URL = "http://leash.ly/webservice/register_walk_details.php";
    //ids
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String get_url = "http://leash.ly/webservice/get_data_id.php";
    String id, sender_id, gcm_id, data, dog_1, dog_2, dog_3;
    String json_data, id_of_insert;
    GoogleCloudMessaging gcm;
    Button start_walk;
    EditText addtl_instruct;
    double distance;
    CheckBox dog_1_chk, dog_2_chk, dog_3_chk;
    JSONParser jsonParser = new JSONParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("WalkerRequest", "walk_request");
        setContentView(R.layout.more_request_walk_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sender_id = extras.getString("sender_id");
            gcm_id = extras.getString("gcm_id");
            distance = extras.getDouble("distance");
            Log.d("more_details_walk request ID", gcm_id + "");
            Log.d("more_details_walk request sender", sender_id + "");
        }

        Animation fadein = AnimationUtils.loadAnimation(MoreDetails.this, R.anim.fadein);
        RelativeLayout second = (RelativeLayout) findViewById(R.id.rel_second);
        second.startAnimation(fadein);
        start_walk = (Button) findViewById(R.id.start_walk);
        start_walk.setOnClickListener(this);
        dog_1_chk = (CheckBox) findViewById(R.id.dog1_check);
        dog_2_chk = (CheckBox) findViewById(R.id.dog2_check);
        dog_3_chk = (CheckBox) findViewById(R.id.dog3_check);
        addtl_instruct = (EditText) findViewById(R.id.addtl_instructs);
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... args) {
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("user", sender_id));
                JSONObject jobj = jsonParser.makeHttpRequest(get_url, "POST", params);
                //jobj = jobj[sender_id+""];
                System.out.println(jobj);
                try {
                    JSONArray jobj_ = jobj.getJSONArray(sender_id);
                    for (int i = 0; i < jobj_.length(); i++) {
                        json_data = jobj_.getJSONObject(i).toString();
                    }
                    JSONObject jobj_get = new JSONObject(json_data);
                    dog_1 = jobj_get.getString("dog_1");
                    dog_2 = jobj_get.getString("dog_2");
                    dog_3 = jobj_get.getString("dog_3");
                } catch (JSONException e) {
                    e.printStackTrace();
                    // do something
                }
                return null;
            }

            @Override
            protected void onPostExecute(String nona) {

                dog_1_chk.setText(dog_1);
                if (dog_2.equals("") && dog_3.equals("")) {
                    dog_1_chk.setClickable(false);
                } else if (!dog_2.equals("")) {
                    dog_2_chk.setVisibility(View.VISIBLE);
                    dog_2_chk.setText(dog_2);

                }
                if (!dog_3.equals("")) {
                    dog_3_chk.setVisibility(View.VISIBLE);
                    dog_3_chk.setText(dog_3);
                }
            }
        }.execute(null, null, null);

    }

    public void getRegId() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    //regid = gcm.register(PROJECT_NUMBER);
                    //msg = "Device registered, registration ID=" + regid;
                    //Log.i("GCM",  msg);
                    Log.d("newwalk", id_of_insert + "");
                    POST2GCM.post(gcm_id, "NewWalk", id_of_insert);

                } catch (Exception ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(MoreDetails.this, "Requesting Walker!", Toast.LENGTH_SHORT).show();
            }
        }.execute(null, null, null);
    }

    @Override
    public void onClick(View v) {
        new finishActivity().execute();
    }

    class finishActivity extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            try {
                // Building Parameters
                //:walker_id, :requester_id, :dogs_walked, :duration_sec, :dog_1s, :dog_2s, :route_taken, :map_loc, :img_loc
                String dogs_walked = "";
                String dog_1s = "0";
                String dog_2s = "0";

                List<NameValuePair> params = new ArrayList<>();
                //dog_1_chk.setText(dog_1);
                if (dog_2.equals("") && dog_3.equals("")) {
                    //dog_1_chk.setClickable(false);
                    dogs_walked = dog_1;
                } else if (!dog_2.equals("")) {
                    //dog_2_chk.setVisibility(View.VISIBLE);
                    //dog_2_chk.setText(dog_2);
                    if (dog_1_chk.isChecked()) {
                        dogs_walked += dog_1;
                    }
                    if (dog_2_chk.isChecked()) {
                        if (!dogs_walked.equals("")) {
                            dogs_walked += ",";
                        }
                        dogs_walked += dog_2;
                    }

                }
                if (!dog_3.equals("")) {
                    if (dog_3_chk.isChecked()) {
                        if (!dogs_walked.equals("")) {
                            dogs_walked += ",";
                        }
                        dogs_walked += dog_3;
                    }
                }
                Log.d("distance", distance + "");
                params.add(new BasicNameValuePair("walker_id", gcm_id));
                params.add(new BasicNameValuePair("requester_id", sender_id));
                params.add(new BasicNameValuePair("duration_sec", 0 + ""));
                params.add(new BasicNameValuePair("dogs_walked", dogs_walked));
                params.add(new BasicNameValuePair("dog_1s", dog_1s));
                params.add(new BasicNameValuePair("dog_2s", dog_2s));
                params.add(new BasicNameValuePair("route_taken", "0"));
                params.add(new BasicNameValuePair("addtl_instruct", addtl_instruct.getText().toString()));
                params.add(new BasicNameValuePair("distance", distance + ""));
                Log.d("params", params.toString());

                JSONObject json2 = jsonParser.makeHttpRequest(
                        REGISTER_URL, "POST", params);
                //Log.d("completed",json.toString());
                // full json response

                Log.d("Updating Walk Details", json2.toString());

                // json success element
                success = json2.getInt(TAG_SUCCESS);
                id_of_insert = json2.getString(TAG_MESSAGE);
                if (success == 1) {
                    Log.d("Message", json2.getString(TAG_MESSAGE));
                    return json2.getString(TAG_MESSAGE);
                } else {
                    Log.d("Registering Failure!", json2.getString(TAG_MESSAGE));
                    Log.d("Message", json2.getString(TAG_MESSAGE));
                    return json2.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;


        }

        protected void onPostExecute(String id_of_insert) {
            getRegId();

        }


    }
}