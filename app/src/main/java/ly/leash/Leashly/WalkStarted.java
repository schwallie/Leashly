package ly.leash.Leashly;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static ly.leash.Leashly.R.layout.activity_maps;

/**
 * Created by schwallie on 12/7/2014.
 */
public class WalkStarted extends ActionBarActivity implements View.OnClickListener {


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    String user = null;
    Button walk_fin;
    ImageButton camera_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walk_started);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);

        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = extras.getString("user");
            Log.d("ID", user+"");
        }
        walk_fin = (Button) findViewById(R.id.walk_finished_button);
        camera_btn = (ImageButton) findViewById(R.id.camera_btn);
        walk_fin.setOnClickListener(this);
        camera_btn.setOnClickListener(this);
        setUpMapIfNeeded();
    }
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    public void onClick(View v) {
        Log.d("ID",v.getId()+"");
        switch (v.getId()) {
            case R.id.walk_finished_button:
                Animation fadeout = AnimationUtils.loadAnimation(this, R.anim.fadeout);
                walk_fin.startAnimation(fadeout);
                Animation fadein = AnimationUtils.loadAnimation(this, R.anim.fadein);
                camera_btn.startAnimation(fadein);

                break;
            case R.id.camera_btn:
                dispatchTakePictureIntent();
                // do stuff;
                break;
        }
    }

        @Override
        protected void onResume() {
            super.onResume();
            setUpMapIfNeeded();
        }

        /**
         * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
         * installed) and the map has not already been instantiated.. This will ensure that we only ever
         * call {@link #setUpMap()} once when {@link #mMap} is not null.
         * <p/>
         * If it isn't installed {@link com.google.android.gms.maps.SupportMapFragment} (and
         * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
         * install/update the Google Play services APK on their device.
         * <p/>
         * A user can return to this FragmentActivity after following the prompt and correctly
         * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
         * have been completely destroyed during this process (it is likely that it would only be
         * stopped or paused), {@link #onCreate(android.os.Bundle)} may not be called again so we should call this
         * method in {@link #onResume()} to guarantee that it will be called.
         */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */

    private void setUpMap() {
        Bundle extras = getIntent().getExtras();
        String data = null;
        try {
            data = new MapPosition().execute(user).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.d("data", data+"");
        double lat, longitude;
        String dog = "";
        try{
            JSONObject jobj = new JSONObject(data);
            lat = jobj.getDouble("lat");
            longitude = jobj.getDouble("long");
            dog += jobj.getString("dog_1");
            if(!jobj.getString("dog_2").equals("")) {
                dog += " & ";
            }
            dog += jobj.getString("dog_2");
            if(!jobj.getString("dog_3").equals("")) {
                dog += " & ";
            }
            dog += jobj.getString("dog_3");

        } catch(JSONException e){
            dog = "";
            lat = 0.0;
            longitude = 0.0;
            e.printStackTrace();
            // do something
        }
        Log.d("dog_1", dog.getClass().toString());
        mMap.setMyLocationEnabled(false); // false to disable
        MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, longitude)).title(dog);
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.doghouse));
        final Marker marker_new = mMap.addMarker(marker);
        marker_new.showInfoWindow();
        mMap.getUiSettings().setZoomControlsEnabled(false);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(lat, longitude)).zoom(15).build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }
    public class MapPosition extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... args) {
            String user = args[0];
            String result = "";
            InputStream is = null;
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("user", user));

//http post
            try{
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://leash.ly/webservice/get_data_id.php");
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
            }catch(Exception e){
                Log.e("log_tag", "Error in http connection "+e.toString());
            }
//convert response to string
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();

                result=sb.toString();
            }catch(Exception e){
                Log.e("log_tag", "Error converting result "+e.toString());
            }

//parse json data
            try{
                JSONArray jArray = new JSONArray(result);
                JSONObject json_data = null;
                for(int i=0;i<jArray.length();i++){
                    json_data = jArray.getJSONObject(i);
                    Log.i("log_tag","id: "+json_data.getInt("id")+
                                    ", name: "+json_data.getString("first_name")+
                                    ", address_1: "+json_data.getString("address_1")+
                                    ", address_2: "+json_data.getString("address_2")
                    );
                }
                return json_data.toString();
            }
            catch(JSONException e){
                Log.e("log_tag", "Error parsing data "+e.toString());
            }




            return null;
        }
    }

}



