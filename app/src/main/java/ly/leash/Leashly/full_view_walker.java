package ly.leash.Leashly;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;

import ly.leash.Leashly.app.AppController;
import ly.leash.Leashly.model.viewer;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * Created by schwallie on 11/25/2014.
 */
public class full_view_walker extends ActionBarActivity implements View.OnClickListener {
    Button btnRegId;
    EditText etRegId;
    GoogleCloudMessaging gcm;
    String regid;
    String gcm_id;
    String PROJECT_NUMBER = "621850944390";
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_walker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        FloatingActionButton fabButton = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.checkmark))
                .withButtonColor(R.color.primaryColor)
                .withGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL)
                .withMargins(0, 0, 16, 16)
                .create();
        fabButton.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        String data = null;
        if (extras != null) {
            data = extras.getString("data");
            Log.d("Data", data);


            try {
                JSONObject jsonObj = new JSONObject(getIntent().getStringExtra("data"));
                TextView bio = (TextView) findViewById(R.id.bio_selected);
                TextView fname = (TextView) findViewById(R.id.first_name_selected);
                bio.setText(jsonObj.getString("bio"));
                fname.setText(jsonObj.getString("first_name"));
                gcm_id = jsonObj.getString("user_id");
                if (imageLoader == null)
                    imageLoader = AppController.getInstance().getImageLoader();
                NetworkImageView thumbNail = (NetworkImageView) findViewById(R.id.pic_selected);


                // thumbnail image
                thumbNail.setImageUrl(jsonObj.getString("pic"), imageLoader);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void getRegId(){
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
                    POST2GCM.post(gcm_id, "NewWalk");

                } catch (Exception ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(full_view_walker.this, "Requesting Walker!", Toast.LENGTH_SHORT).show();
            }
        }.execute(null, null, null);
    }
    @Override
    public void onClick(View v) {
        getRegId();
    } }
