package ly.leash.Leashly;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

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
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class WalkStarted extends ActionBarActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String REGISTER_URL = "http://leash.ly/webservice/register_walk_details.php";
    //ids
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    String user = null;
    Button walk_fin;
    ImageButton camera_btn;
    ProgressDialog dialog;
    String loc_of_image;
    LocationRequest mLocationRequest;
    GoogleApiClient mLocationClient;
    LatLng prev_latlng;
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    Date date = new Date();
    String dte = dateFormat.format(date);
    int REQUEST_IMAGE = 1;
    String save_loc = null;
    Integer camera_done = 0;
    long startTime;
    long endTime;
    long duration;  //divide by 1000000 to get milliseconds.
    String sender_id;
    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Uri fileUri;
    // Progress Dialog
    private ProgressDialog pDialog;

    /**
     * Create a file Uri for saving an image or video
     */
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Leashly");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Leashly", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
            Log.d("mediaDir", mediaFile.toString());
        } else {
            return null;
        }

        return mediaFile;
    }

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
            sender_id = extras.getString("sender_id");
            Log.d("walk start ID", user + "");
            Log.d("walk start sender_id", sender_id + "");
        }
        startTime = System.nanoTime();
        save_loc = Environment.getExternalStorageDirectory().toString() + "/MAP_" + dte + "_userId_" + user + ".png";
        walk_fin = (Button) findViewById(R.id.walk_finished_button);
        camera_btn = (ImageButton) findViewById(R.id.camera_btn);
        walk_fin.setOnClickListener(this);
        camera_btn.setOnClickListener(this);
        mLocationClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        //PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        //wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
        //        "MyWakelockTag");
        //wakeLock.acquire();
        setUpMapIfNeeded();
    }

    @Override
    public void onClick(View v) {
        Log.d("ID", v.getId() + "");
        switch (v.getId()) {
            case R.id.walk_finished_button:
                //wakeLock.release();
                //dialog = ProgressDialog.show(WalkStarted.this, "Uploading",
                //      "Please wait...");
                endTime = System.nanoTime();
                duration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds.
                LocationServices.FusedLocationApi.removeLocationUpdates(mLocationClient, this);
                new CaptureMapScreen().execute();
                //Log.d("At dismiss", "");
                //dialog.dismiss();

                break;
            case R.id.camera_btn:
                dispatchTakePictureIntent();
                walk_fin.setVisibility(View.INVISIBLE);
                camera_btn.setVisibility(View.VISIBLE);
                // do stuff;

                break;
        }
    }

    protected void dispatchTakePictureIntent() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        // start the image capture Intent
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

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
        //Bundle extras = getIntent().getExtras();
        String data = null;
        try {
            data = new MapPosition().execute(user).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.d("data", data + "");
        double lat, longitude;
        try {
            JSONObject jobj = new JSONObject(data);
            lat = jobj.getDouble("lat");
            longitude = jobj.getDouble("long");

        } catch (JSONException e) {
            lat = 0.0;
            longitude = 0.0;
            e.printStackTrace();
            // do something
        }
        mMap.setMyLocationEnabled(true); // false to disable
        prev_latlng = new LatLng(lat, longitude);
        MarkerOptions marker = new MarkerOptions().position(prev_latlng);
        final Marker marker_new = mMap.addMarker(marker);
        marker_new.showInfoWindow();
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setAllGesturesEnabled(false);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                prev_latlng).zoom(15).build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
            // Image captured and saved to fileUri specified in the Intent
            Log.d("requestCode", requestCode + "");
            loc_of_image = fileUri.getPath();
            Log.d("Image_Loc", loc_of_image);
            try {
                UploadToServer uts;
                uts = new UploadToServer();
                uts.execute(loc_of_image);
                camera_done = 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.d("Image capture failed", "fail");
            // Image capture failed, advise user
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // Report to the UI that the location was
        double lat = location.getLatitude();
        double longitude = location.getLongitude();
        mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(lat, longitude), prev_latlng)
                .width(30)
                .color(R.color.primaryColor));
        prev_latlng = new LatLng(lat, longitude);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mLocationClient.connect();
    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000);

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mLocationClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("WalkStarted", "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("walkstarted", "GoogleApiClient connection has failed");
    }


    class finishActivity extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(WalkStarted.this);
            pDialog.setMessage("Finishing Up...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            if (camera_done == 1) {
                //Finish up
                try {
                    // Building Parameters
                    //:walker_id, :requester_id, :dogs_walked, :duration_sec, :dog_1s, :dog_2s, :route_taken, :map_loc, :img_loc
                    String dogs_walked = "0";
                    String dog_1s = "0";
                    String dog_2s = "0";

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("walker_id", user));
                    params.add(new BasicNameValuePair("requester_id", sender_id));
                    params.add(new BasicNameValuePair("duration_sec", duration + ""));
                    params.add(new BasicNameValuePair("dogs_walked", dogs_walked));
                    params.add(new BasicNameValuePair("dog_1s", dog_1s));
                    params.add(new BasicNameValuePair("dog_2s", dog_2s));
                    params.add(new BasicNameValuePair("route_taken", "0"));
                    params.add(new BasicNameValuePair("map_loc", save_loc));
                    params.add(new BasicNameValuePair("img_loc", loc_of_image));
                    Log.d("request!", "starting");
                    Log.d("params", params.toString());
                    JSONObject json2 = jsonParser.makeHttpRequest(
                            REGISTER_URL, "POST", params);
                    //Log.d("completed",json.toString());
                    // full json response
                    System.out.println(json2);
                    Log.d("Updating Walk Details", json2.toString());

                    // json success element
                    success = json2.getInt(TAG_SUCCESS);
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
            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null) {
                Toast.makeText(WalkStarted.this, file_url, Toast.LENGTH_LONG).show();
            }
            if (camera_done == 1) {
                //Finish up
                POST2GCM.post(sender_id, "WalkDone", user);
            }

        }


    }

    public class CaptureMapScreen extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            Log.d("pre", "pre-execute");
            dialog = ProgressDialog.show(WalkStarted.this, "Uploading",
                    "Please wait...");

        }

        @Override
        protected void onPostExecute(String args) {
            Log.d("post", "post-execute2");

        }

        @Override
        protected String doInBackground(Void... params) {
            GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
                Bitmap bitmap;

                @Override
                public void onSnapshotReady(Bitmap snapshot) {
                    // TODO Auto-generated method stub
                    bitmap = snapshot;
                    try {
                        Log.d("storage", Environment.getExternalStorageDirectory().toString());
                        FileOutputStream out = new FileOutputStream(save_loc);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                        UploadImageClass uim;
                        uim = new UploadImageClass();
                        uim.execute(save_loc);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            mMap.snapshot(callback);
            return null;
        }
    }

    public class MapPosition extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... args) {
            String user = args[0];
            String result = "";
            InputStream is = null;
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("user", user));

//http post
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://leash.ly/webservice/get_data_id.php");
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
                JSONObject json_data = null;
                for (int i = 0; i < jArray.length(); i++) {
                    json_data = jArray.getJSONObject(i);
                    Log.i("log_tag", "id: " + json_data.getInt("id") +
                                    ", name: " + json_data.getString("first_name") +
                                    ", address_1: " + json_data.getString("address_1") +
                                    ", address_2: " + json_data.getString("address_2")
                    );
                }
                return json_data.toString();
            } catch (JSONException e) {
                Log.e("log_tag", "Error parsing data " + e.toString());
            }


            return null;
        }
    }

    public class UploadImageClass extends AsyncTask<String, String, Integer> {

        int serverResponseCode = 0;

        public Integer doInBackground(String... args) {
            final String fileName = args[0];
            Log.d("fileName", fileName);
            mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                public void onSnapshotReady(Bitmap bitmap) {
                    // Write image to disk
                    try {
                        FileOutputStream out = new FileOutputStream(fileName);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    new UploadToServer().execute(fileName);
                    //dialog.dismiss();
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            Log.i("result", "" + result);
            Log.d("At dismiss2", "");
            dialog.dismiss();
            if (walk_fin.getVisibility() == walk_fin.VISIBLE) {
                //Animation fadeout = AnimationUtils.loadAnimation(WalkStarted.this, R.anim.fadeout);
                //walk_fin.startAnimation(fadeout);
                walk_fin.setVisibility(View.GONE);
                walk_fin.setEnabled(false);
                Animation fadein = AnimationUtils.loadAnimation(WalkStarted.this, R.anim.fadein);
                camera_btn.startAnimation(fadein);
                camera_btn.setVisibility(View.VISIBLE);
            }
        }

    }

    public class UploadToServer extends AsyncTask<String, String, Integer> {

        String upLoadServerUri = "http://leash.ly/webservice/upload_to_serv.php";
        int serverResponseCode = 0;

        @Override
        public void onPostExecute(Integer serverResponse) {
            new finishActivity().execute();
        }

        public Integer doInBackground(String... args) {
            String fileName = args[0];
            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File sourceFile = new File(args[0]);
            while (sourceFile.exists() != true) {
                System.out.println("WAITING...");
            }

            try { // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);
                conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);
                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();
            } catch (MalformedURLException ex) {
                //dialog.dismiss();
                ex.printStackTrace();
                //Toast.makeText(UploadImage.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                //dialog.dismiss();
                e.printStackTrace();
                //Toast.makeText(UploadImage.this, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e);
            }
            //dialog.dismiss();
            return serverResponseCode;
        }
    }
}