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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by schwallie on 12/22/2014.
 */
public class MoreDetails extends ActionBarActivity implements View.OnClickListener {
    String id, sender_id, gcm_id;
    GoogleCloudMessaging gcm;
    Button start_walk;

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
            Log.d("walk request ID", id + "");
            Log.d("walk request sender", sender_id + "");
        }

        Animation fadein = AnimationUtils.loadAnimation(MoreDetails.this, R.anim.fadein);
        RelativeLayout second = (RelativeLayout) findViewById(R.id.rel_second);
        second.startAnimation(fadein);
        start_walk = (Button) findViewById(R.id.start_walk);
        start_walk.setOnClickListener(this);

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
                    POST2GCM.post(gcm_id, "NewWalk", sender_id);

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
        getRegId();
    }
}