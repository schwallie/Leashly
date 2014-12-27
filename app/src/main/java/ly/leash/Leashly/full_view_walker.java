package ly.leash.Leashly;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import org.json.JSONException;
import org.json.JSONObject;

import ly.leash.Leashly.app.AppController;


/**
 * Created by schwallie on 11/25/2014.
 */
public class full_view_walker extends ActionBarActivity implements View.OnClickListener {
    String gcm_id;
    String sender_id = null;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    TextView bio;
    TextView fname;
    double lat, lon, distance;
    NetworkImageView thumbNail;
    FloatingActionButton fabButton;
    String data = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_walker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        fabButton = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.checkmark))
                .withButtonColor(R.color.primaryColor)
                .withGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL)
                .withMargins(0, 0, 16, 16)
                .create();
        fabButton.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            data = extras.getString("data");
            sender_id = extras.getString("sender_id");
            lat = extras.getDouble("lat");
            lon = extras.getDouble("lon");
            Log.d("full_view Data", data);
            Log.d("full_view sender_id", sender_id + "");

            try {
                JSONObject jsonObj = new JSONObject(getIntent().getStringExtra("data"));
                bio = (TextView) findViewById(R.id.bio_selected);
                fname = (TextView) findViewById(R.id.first_name_selected);
                bio.setText(jsonObj.getString("bio"));
                fname.setText(jsonObj.getString("first_name"));
                gcm_id = jsonObj.getString("user_id");
                LatLng point1 = new LatLng(jsonObj.getDouble("lat"), jsonObj.getDouble("long"));
                LatLng point2 = new LatLng(lat, lon);
                distance = LatLngTool.distance(point1, point2, LengthUnit.MILE);
                if (imageLoader == null)
                    imageLoader = AppController.getInstance().getImageLoader();
                thumbNail = (NetworkImageView) findViewById(R.id.pic_selected);


                // thumbnail image
                thumbNail.setImageUrl(jsonObj.getString("pic"), imageLoader);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case -1:
                Log.d("full_view", "Clicked Fab");
                Animation fadeout = AnimationUtils.loadAnimation(full_view_walker.this, R.anim.fadeout);
                RelativeLayout first = (RelativeLayout) findViewById(R.id.rel_selected);
                first.startAnimation(fadeout);
                fabButton.startAnimation(fadeout);
                first.setVisibility(View.GONE);
                first.clearAnimation();
                Intent i = new Intent(getBaseContext(), MoreDetails.class);
                i.putExtra("sender_id", sender_id);
                i.putExtra("gcm_id", gcm_id);
                i.putExtra("distance", distance);
                i.putExtra("data", data);
                startActivity(i);
                //getRegId();
        }
    } }
