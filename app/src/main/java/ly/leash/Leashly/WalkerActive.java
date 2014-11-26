package ly.leash.Leashly;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class WalkerActive extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    private ProgressDialog pDialog;

    //ids
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    public Double lat, lon;
    public Integer radius = 0;
    public String user = null;
    public Integer user_id = null;
    public JSONParser jsonParser = new JSONParser();

    private static final String UPDATE_URL = "http://leash.ly/webservice/active_walker.php";
    private final String TAG = "Leashly";

    private TextView mLocationView;

    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walker_active);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Display the connection status
        /*LocationRequest mLocationRequest;
        LocationClient mLocationClient;
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every second

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        mCurrentLocation = mLocationClient.getLastLocation();
        lat = mCurrentLocation.getLatitude();
        lon = mCurrentLocation.getLongitude();*/
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);

        /* Loop over the array backwards, and if you get an accurate location, then break out the loop*/
        Location l = null;

        for (int i=providers.size()-1; i>=0; i--) {
            l = lm.getLastKnownLocation(providers.get(i));
            if (l != null) break;
        }

        if (l != null) {
            lat = l.getLatitude();
            lon = l.getLongitude();
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = extras.getString("user");
            user_id = extras.getInt("user_id");
            Log.d("In WalkerActive", user);
        }
        String ret_value = null;
        try {
            ret_value = new UpdateActive().execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "GoogleApiClient connection has failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocationView.setText("Location received: " + location.toString());
    }



    class UpdateActive extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(WalkerActive.this);
            pDialog.setMessage("Going Active...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("user_id", user_id.toString()));
                params.add(new BasicNameValuePair("radius", radius.toString()));
                params.add(new BasicNameValuePair("lat", lat.toString()));
                params.add(new BasicNameValuePair("lon",lon.toString()));
                Log.d("request!", "starting");
                Log.d("params",params.toString());
                JSONObject json_update = jsonParser.makeHttpRequest(
                        UPDATE_URL, "POST", params);
                //Log.d("completed",json.toString());
                // full json_update response
                Log.d("Updated Activity", json_update.toString());

                // json_update success element
                success = json_update.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Updated Activity!", json_update.toString());
                    return json_update.getString(TAG_MESSAGE);
                }else{
                    Log.d("Registering Failure!", json_update.getString(TAG_MESSAGE));
                    return json_update.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();

        }

    }
}
