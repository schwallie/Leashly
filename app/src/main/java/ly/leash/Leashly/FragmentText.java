package ly.leash.Leashly;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class FragmentText extends Fragment {
    // Log tag
    private static final String TAG = AvailableList.class.getSimpleName();
    private static final String url = "http://leash.ly/webservice/get_active.php";
    String sender_id;
    double distanceInMiles;
    Double lat = null;
    Double lon = null;
    Integer sort;
    private List<viewer> movieList = new ArrayList<>();
    private ListView listView;
    private CustomListAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lon = getArguments().getDouble("lon");
        lat = getArguments().getDouble("lat");
        sort = getArguments().getInt("sort");
        Log.d("sort", sort + "");

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "Started");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Resumed");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View V = inflater.inflate(R.layout.front_list, container, false);
        listView = (ListView) V.findViewById(R.id.list);
        adapter = new CustomListAdapter(getActivity(), movieList);
        Log.d("Adapter", adapter.toString() + "");
        listView.setAdapter(adapter);
        final Double finalLat = lat;
        final Double finalLon = lon;
        final Double finalLat1 = lat;
        final Double finalLon1 = lon;
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(final JSONArray response) {
                        Log.d(TAG, response.toString());
                        //hidePDialog();
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                viewer movie = new viewer();
                                LatLng point1 = new LatLng(obj.getDouble("lat"), obj.getDouble("long"));
                                LatLng point2 = new LatLng(finalLat, finalLon);
                                distanceInMiles = LatLngTool.distance(point1, point2, LengthUnit.MILE);
                                movie.setTitle(obj.getString("first_name"));
                                movie.setThumbnailUrl(obj.getString("pic"));
                                movie.setYear(Double.parseDouble(String.format("%.2f", distanceInMiles)));
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
                                Intent i = new Intent(getActivity(), full_view_walker.class);
                                i.putExtra("data", clicked_at);
                                i.putExtra("lat", finalLat1);
                                i.putExtra("lon", finalLon1);
                                i.putExtra("sender_id", sender_id);
                                startActivity(i);

                            }
                        });

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                //hidePDialog();

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);
        return V;
    }
}