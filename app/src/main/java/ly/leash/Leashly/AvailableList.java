package ly.leash.Leashly;

/**
 * Created by schwallie on 11/23/2014.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ly.leash.Leashly.adapter.CustomListAdapter;
import ly.leash.Leashly.app.AppController;
import ly.leash.Leashly.model.viewer;


public class AvailableList extends ActionBarActivity {
    // Log tag
    private static final String TAG = AvailableList.class.getSimpleName();

    // Movies json url
    private static final String url = "http://leash.ly/webservice/get_active.php";
    ListView leftDrawerList;
    ArrayAdapter<String> navigationDrawerAdapter;
    String sender_id;
    private ProgressDialog pDialog;
    private List<viewer> movieList = new ArrayList<viewer>();
    private ListView listView;
    private CustomListAdapter adapter;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private String[] leftSliderData = {"Logout", "Contact Us"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_listview);
        nitView();
        if (toolbar != null) {
            toolbar.setTitle("Navigation Drawer");
            setSupportActionBar(toolbar);
        }
        initDrawer();
        Bundle extras = getIntent().getExtras();
        Double lat = null;
        Double lon = null;
        if (extras != null) {
            lat = extras.getDouble("lat");
            lon = extras.getDouble("lon");
            sender_id = extras.getString("user_id");
            Log.d("Avail_list sender_id", sender_id);
        }
        listView = (ListView) findViewById(R.id.list);
        adapter = new CustomListAdapter(this, movieList);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();
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
                                try {
                                    clicked_at = response.getString(position);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Intent i = new Intent(getApplicationContext(), full_view_walker.class);
                                i.putExtra("data", clicked_at);
                                i.putExtra("sender_id", sender_id);
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

    private void nitView() {
        leftDrawerList = (ListView) findViewById(R.id.left_drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationDrawerAdapter = new ArrayAdapter<String>(AvailableList.this, android.R.layout.simple_list_item_1, leftSliderData);
        Log.d("nsv", navigationDrawerAdapter + "");
        leftDrawerList.setAdapter(navigationDrawerAdapter);
        leftDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String clicked_at = leftSliderData[position];
                switch (clicked_at) {
                    case "Logout":
                        PreferenceData.setUserLoggedInStatus(getBaseContext(), false);
                        PreferenceData.clearLoggedInEmailAddress(getBaseContext());
                        Intent i = new Intent(getBaseContext(), Login.class);
                        startActivity(i);
                        break;
                    case "Contact Us":
                        Intent i2 = new Intent(getBaseContext(), Register.class);
                        startActivity(i2);
                        break;

                    default:
                        break;
                }


            }
        });
    }

    private void initDrawer() {

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
