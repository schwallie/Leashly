package ly.leash.Leashly;

/**
 * Created by schwallie on 11/23/2014.
 */
import android.app.ProgressDialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.app.Activity;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import ly.leash.Leashly.adapter.CustomListAdapter;
import ly.leash.Leashly.app.AppController;
import ly.leash.Leashly.model.viewer;
import ly.leash.Leashly.util.JSONFunctions;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.toolbox.JsonArrayRequest;
import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

public class AvailableList extends ActionBarActivity {
    // Log tag
    private static final String TAG = AvailableList.class.getSimpleName();

    // Movies json url
    private static final String url = "http://leash.ly/webservice/get_active.php";
    private ProgressDialog pDialog;
    private List<viewer> movieList = new ArrayList<viewer>();
    private ListView listView;
    private CustomListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_listview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        Bundle extras = getIntent().getExtras();
        Double lat = null;
        Double lon = null;
        if (extras != null) {
            lat = extras.getDouble("lat");
            lon = extras.getDouble("lon");
        }
        listView = (ListView) findViewById(R.id.list);
        adapter = new CustomListAdapter(this, movieList);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        // changing action bar color
        // Creating volley request obj
        final Double finalLat = lat;
        final Double finalLon = lon;
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(final JSONArray response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                viewer movie = new viewer();
                                LatLng point1 = new LatLng(obj.getDouble("lat"), obj.getDouble("long"));
                                LatLng point2 = new LatLng(finalLat, finalLon);
                                double distanceInMiles = LatLngTool.distance(point1, point2, LengthUnit.MILE);
                                movie.setTitle(obj.getString("first_name"));
                                movie.setThumbnailUrl(obj.getString("pic"));
                                movie.setRating(((String) obj.getString("experience")));
                                movie.setYear(Double.parseDouble(String.format("%.2f", distanceInMiles)));
                                // Genre is json array
                                /*JSONArray genreArry = obj.getJSONArray("walks");
                                ArrayList<String> genre = new ArrayList<String>();
                                for (int j = 0; j < genreArry.length(); j++) {
                                    genre.add((String) genreArry.get(j));
                                }
                                movie.setGenre(genre);*/

                                // adding movie to movies array
                                movieList.add(movie);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {
                                String clicked_at = null;
                                try{
                                    clicked_at = response.getString(position);
                                } catch(JSONException e) {
                                    e.printStackTrace();
                                }
                                Intent i = new Intent(getApplicationContext(),full_view_walker.class);
                                i.putExtra("data", clicked_at);
                                startActivity(i);

                            }
                        });

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }



}

/*
public class AvailableList extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .build();

        ImageLoader.getInstance().init(config);
        // END - UNIVERSAL IMAGE LOADER SETUP
        setContentView(R.layout.user_listview);
        CustomList adapter = new
                CustomList(AvailableList.this, web, imageId);
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(AvailableList.this, "You Clicked at " +web[+ position], Toast.LENGTH_SHORT).show();
            }
        });
    }

    ListView list;
    String[] web = {
            "Google Plus",
            "Twitter",
            "Windows",
            "Bing",
            "Itunes",
            "Wordpress",
            "Drupal"
    } ;
    String url = "http://leash.ly/images/cody.png";
    DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).build();
    //initialize image view
    ImageView imageView = (ImageView) findViewById(R.id.img);
    ImageLoader.getInstance().displayImage(url, imageView, options);
    String[] imageId = {
            "http://leash.ly/images/cody.png",
            "http://leash.ly/images/chase.png",
            "http://leash.ly/images/zach.png",
            "http://leash.ly/images/cody.png",
            "http://leash.ly/images/chase.png",
            "http://leash.ly/images/zach.png",
            "http://leash.ly/images/cody.png"
    };


}
*/
