package ly.leash.Leashly;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Criteria;
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
import android.widget.EditText;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WalkStarted extends ActionBarActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String REGISTER_URL = "http://leash.ly/webservice/register_walk_details.php";
    //ids
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    final ArrayList<Integer> selectedItems = new ArrayList<>();
    //CharSequence[] dog_items;
    List<String> dog_items = new ArrayList<>();
    String user = null;
    Button walk_fin;
    ImageButton camera_btn;
    ProgressDialog dialog;
    String loc_of_image, img_name;
    LocationRequest mLocationRequest;
    GoogleApiClient mLocationClient;
    LatLng prev_latlng;
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    Date date = new Date();
    String dte = dateFormat.format(date);
    int REQUEST_IMAGE = 1;
    String save_loc = null;
    String fileName;
    String dog_1, dog_2, dog_3, note_text;
    Integer camera_done = 0;
    long startTime;
    long endTime;
    long duration;  //divide by 1000000 to get milliseconds.
    double lat, longitude;
    String sender_id, walk_id, map_deets;
    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Uri fileUri;
    // Progress Dialog
    private ProgressDialog pDialog;

    /**
     * Create a file Uri for saving an image or video
     */
    private Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile(int type) {
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
            img_name = "IMG_" + timeStamp + ".jpg";
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    img_name);
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
            walk_id = extras.getString("walk_id");
            dog_1 = extras.getString("dog_1");
            dog_2 = extras.getString("dog_2");
            dog_3 = extras.getString("dog_3");
            lat = extras.getDouble("lat");
            longitude = extras.getDouble("lon");
            Log.d("walk start ID", user + "");
            Log.d("walk start sender_id", sender_id + "");
        }
        if (dog_1 != null) {
            dog_items.add(dog_1 + " #1");
            dog_items.add(dog_1 + " #2");
        }
        if (dog_2 != null) {
            dog_items.add(dog_2 + " #1");
            dog_items.add(dog_2 + " #2");
        }
        if (dog_3 != null) {
            dog_items.add(dog_3 + " #1");
            dog_items.add(dog_3 + " #2");
        }
        startTime = System.nanoTime();
        map_deets = "MAP_" + dte + "_userId_" + user + ".png";
        save_loc = Environment.getExternalStorageDirectory().toString() + "/" + map_deets;
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

    private void setUpMap() {

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
        Criteria myCriteria = new Criteria();
        myCriteria.setAccuracy(Criteria.ACCURACY_FINE);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
            // Image captured and saved to fileUri specified in the Intent
            Log.d("requestCode", requestCode + "");
            loc_of_image = fileUri.getPath();
            Log.d("Image_Loc", loc_of_image);
            try {
                camera_done = 1;
                UploadToServer uts;
                uts = new UploadToServer();
                Log.d("Executing", "1");
                uts.execute(loc_of_image);
                showDialog();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.d("Image capture failed", "fail");
            // Image capture failed, advise user
        }
    }

    public void showDialog() {

        AlertDialog dialog;
//following code will be in your activity.java file
        // arraylist to keep the selected items
        AlertDialog.Builder builder = new AlertDialog.Builder(WalkStarted.this);
        builder.setTitle("Who did what?");
        builder.setCancelable(false);
        final CharSequence[] charSequenceItems = dog_items.toArray(new CharSequence[dog_items.size()]);
        builder.setMultiChoiceItems(charSequenceItems, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    // indexSelected contains the index of item (of which checkbox checked)
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected,
                                        boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            // write your code when user checked the checkbox
                            selectedItems.add(indexSelected);
                        } else if (selectedItems.contains(indexSelected)) {
                            // Else, if the item is already in the array, remove it
                            // write your code when user Uchecked the checkbox
                            selectedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                })
                // Set the action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        showNoteDialog();
                        //  Your code when user clicked on OK
                        //  You can write the code  to save the selected item here

                    }

                });

        dialog = builder.create();//AlertDialog dialog; create like this outside onClick
        dialog.show();

    }

    public void showNoteDialog() {

        final AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(WalkStarted.this);
        builder.setTitle("Leave a note for the owner");
        builder.setCancelable(false);
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //  Your code when user clicked on OK
                //  You can write the code  to save the selected item here

            }

        });

        dialog = builder.create();//AlertDialog dialog; create like this outside onClick
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean wantToCloseDialog = false;
                //Do stuff, possibly set wantToCloseDialog to true then...
                if (input.getText().toString().matches("")) {
                    Toast.makeText(WalkStarted.this, "Please enter a note", Toast.LENGTH_LONG).show();
                } else {
                    note_text = input.getText().toString();
                    wantToCloseDialog = true;
                }
                if (wantToCloseDialog)
                    new finishActivity().execute();
                dialog.dismiss();
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            }
        });

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
            pDialog.setCancelable(false);
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
                    String dog_1s = "";
                    String dog_2s = "";
                    Log.d("dog_items", dog_items.toString());
                    for (int x_ = 0; x_ < selectedItems.size(); x_++) {
                        int temp = selectedItems.get(x_);
                        if (dog_items.get(temp).contains("#1")) {
                            if (dog_1s.matches("")) {
                                dog_1s += dog_items.get(temp).split(" ")[0];
                            } else {
                                dog_1s += "," + dog_items.get(temp).split(" ")[0];
                            }
                        } else {
                            if (dog_2s.matches("")) {
                                dog_2s += dog_items.get(temp).split(" ")[0];
                            } else {
                                dog_2s += "," + dog_items.get(temp).split(" ")[0];
                            }
                        }

                    }
                    Log.d("input", note_text);
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("walker_id", user));
                    params.add(new BasicNameValuePair("requester_id", sender_id));
                    params.add(new BasicNameValuePair("duration_sec", duration + ""));
                    params.add(new BasicNameValuePair("dogs_walked", dogs_walked));
                    params.add(new BasicNameValuePair("dog_1s", dog_1s));
                    params.add(new BasicNameValuePair("dog_2s", dog_2s));
                    params.add(new BasicNameValuePair("route_taken", "0"));
                    params.add(new BasicNameValuePair("map_loc", map_deets));
                    params.add(new BasicNameValuePair("img_loc", img_name));
                    params.add(new BasicNameValuePair("walk_id", walk_id));
                    params.add(new BasicNameValuePair("walk_note", note_text));
                    Log.d("request!", "starting");
                    Log.d("params", params.toString());
                    JSONObject json2 = jsonParser.makeHttpRequest(
                            REGISTER_URL, "POST", params);
                    //Log.d("completed",json.toString());
                    // full json response

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
            if (camera_done == 1) {
                //Finish up
                POST2GCM.post(sender_id, "WalkDone", walk_id);
                Intent i = new Intent(WalkStarted.this, WalkerMain.class);
                startActivity(i);
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

    public class UploadImageClass extends AsyncTask<String, String, Integer> {

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
                    Log.d("Executing", "2");
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
            if (walk_fin.getVisibility() == View.VISIBLE) {
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
            File file = new File(fileName);
            boolean deleted = file.delete();
        }


        public Integer doInBackground(String... args) {
            fileName = args[0];
            HttpURLConnection conn;
            DataOutputStream dos;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File sourceFile = new File(args[0]);
            while (!sourceFile.exists()) {
                System.out.println("WAITING...");
            }

            try { // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);
                conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setConnectTimeout(50000);
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