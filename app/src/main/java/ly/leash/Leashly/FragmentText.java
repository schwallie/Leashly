package ly.leash.Leashly;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import ly.leash.Leashly.adapter.CustomListAdapter;
import ly.leash.Leashly.app.AppController;
import ly.leash.Leashly.model.viewer;

public class FragmentText extends Fragment {
    // In the FragmentText
    public static final String EXTRA_POSITION = "extra_position";
    // Log tag
    private static final String TAG = AvailableList.class.getSimpleName();
    private static final String url = "http://leash.ly/webservice/get_active.php";
    private static final String REGISTER_URL = "http://leash.ly/webservice/register_walk_details.php";
    //ids
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String get_url = "http://leash.ly/webservice/get_data_id.php";
    double distanceInMiles;
    Double lat = null;
    Double lon = null;
    Integer sort;
    String id, sender_id, gcm_id, data, dog_1, dog_2, dog_3, first_name;
    String dog = "";
    String dogs_walked = "";
    String id_of_insert;
    GoogleCloudMessaging gcm;
    double distance;
    TextView deets;
    JSONParser jsonParser = new JSONParser();
    private EditText input;
    private View positiveAction;
    private List<viewer> movieList = new ArrayList<>();
    private ListView listView;
    private CustomListAdapter adapter;

    public static Fragment newInstance(int position, double lat, double lon, String dog_1, String dog_2, String dog_3, String sender_id,
                                       String first_name) {
        FragmentText ft = new FragmentText();
        Bundle args = new Bundle();
        args.putInt(EXTRA_POSITION, position);
        args.putDouble("lat", lat);
        args.putDouble("lon", lon);
        args.putString("dog_1", dog_1);
        args.putString("dog_2", dog_2);
        args.putString("dog_3", dog_3);
        args.putString("sender_id", sender_id);
        args.putString("first_name", first_name);
        ft.setArguments(args); // set the arguments with the tab position
        return ft;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sort = getArguments().getInt(EXTRA_POSITION);
        lon = getArguments().getDouble("lon");
        lat = getArguments().getDouble("lat");
        dog_1 = getArguments().getString("dog_1");
        dog_2 = getArguments().getString("dog_2");
        dog_3 = getArguments().getString("dog_3");
        sender_id = getArguments().getString("sender_id");
        first_name = getArguments().getString("first_name");
        Log.d("sort", sort + "");

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View V = inflater.inflate(R.layout.front_list, container, false);
        listView = (ListView) V.findViewById(R.id.list);
        adapter = new CustomListAdapter(getActivity(), movieList);
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
                        TreeMap dist_mp = new TreeMap<Double, HashMap<String, String>>();
                        TreeMap walks_mp = new TreeMap<Integer, HashMap<String, String>>(Collections.reverseOrder());
                        //hidePDialog();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                Map<String, String> mp1 = new HashMap<String, String>();
                                JSONObject obj = response.getJSONObject(i);
                                LatLng point1 = new LatLng(obj.getDouble("lat"), obj.getDouble("long"));
                                LatLng point2 = new LatLng(finalLat, finalLon);
                                distanceInMiles = LatLngTool.distance(point1, point2, LengthUnit.MILE);
                                mp1.put("dist", distanceInMiles + "");
                                mp1.put("name", obj.getString("first_name"));
                                mp1.put("pic", obj.getString("pic"));
                                mp1.put("walks", obj.getString("walks"));
                                mp1.put("lat", obj.getString("lat"));
                                mp1.put("long", obj.getString("long"));
                                while (dist_mp.containsKey(distanceInMiles)) {
                                    distanceInMiles += .0001;
                                }
                                dist_mp.put(distanceInMiles, mp1);
                                Integer walks = obj.getInt("walks");
                                while (walks_mp.containsKey(walks)) {
                                    walks += 1;
                                }
                                walks_mp.put(walks, mp1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        Set set;
                        if (sort == 1) {
                            // Get a set of the entries
                            set = walks_mp.entrySet();
                        } else if (sort == 0) {
                            set = dist_mp.entrySet();
                        } else {
                            set = dist_mp.entrySet();
                        }
                        // Get an iterator
                        Iterator i = set.iterator();
                        // Display elements
                        while (i.hasNext()) {
                            Map.Entry me = (Map.Entry) i.next();
                            System.out.print(me.getKey() + ": ");
                            System.out.println(me.getValue());
                            Map<String, String> mp = (Map<String, String>) me.getValue();
                            viewer movie = new viewer();
                            LatLng point1 = new LatLng(Double.parseDouble(mp.get("lat")), Double.parseDouble(mp.get("long")));
                            LatLng point2 = new LatLng(finalLat, finalLon);
                            distanceInMiles = LatLngTool.distance(point1, point2, LengthUnit.MILE);
                            movie.setTitle(mp.get("name"));
                            movie.setThumbnailUrl(mp.get("pic"));
                            movie.setYear(Double.parseDouble(String.format("%.2f", distanceInMiles)));
                            movie.setRating(mp.get("walks"));
                            movieList.add(movie);

                        }
                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {
                                String clicked_at = null;
                                try {
                                    clicked_at = response.getString(position);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                String fname = null;
                                try {
                                    JSONObject jsonObj = new JSONObject(clicked_at);
                                    fname = jsonObj.getString("first_name");
                                    gcm_id = jsonObj.getString("user_id");
                                    LatLng point1 = new LatLng(jsonObj.getDouble("lat"), jsonObj.getDouble("long"));
                                    LatLng point2 = new LatLng(lat, lon);
                                    distance = LatLngTool.distance(point1, point2, LengthUnit.MILE);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                                        .customView(R.layout.dialog_customview, true)
                                        .positiveText("Request")
                                        .negativeText(android.R.string.cancel)
                                        .callback(new MaterialDialog.ButtonCallback() {
                                            @Override
                                            public void onPositive(MaterialDialog dialog) {
                                                new finishActivity().execute();
                                            }

                                            @Override
                                            public void onNegative(MaterialDialog dialog) {
                                            }
                                        }).build();
                                deets = (TextView) dialog.getCustomView().findViewById(R.id.dialog_intro);

                                dog += dog_1;
                                dogs_walked += dog_1;
                                if (!(dog_2.equals(""))) {
                                    dogs_walked += ",";
                                    if (dog_3.equals("")) {
                                        dog += " and ";
                                    } else {
                                        dog += ", ";
                                    }
                                }
                                dog += dog_2;
                                dogs_walked += dog_2;
                                if (!(dog_3.equals(""))) {
                                    dogs_walked += ",";
                                    dog += " and ";
                                }
                                dog += dog_3;
                                dogs_walked += dog_3;
                                deets.setText(fname + " is ready to walk " + dog + "!");
                                positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
                                input = (EditText) dialog.getCustomView().findViewById(R.id.extra);
                                input.setSingleLine(false);
                                input.setLines(3);
                                input.setMaxLines(3);
                                input.setGravity(Gravity.TOP);
                                input.setBackgroundResource(R.drawable.backtext);
                                input.setHint("Anyone need extra belly rubs? Water?");
                                input.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        //positiveAction.setEnabled(s.toString().trim().length() > 0);
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {
                                    }
                                });

                                // Toggling the show password CheckBox will mask or unmask the password input EditText

                                dialog.show();
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

    public void getRegId() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getActivity());
                    }
                    //regid = gcm.register(PROJECT_NUMBER);
                    //msg = "Device registered, registration ID=" + regid;
                    //Log.i("GCM",  msg);
                    Log.d("newwalk", id_of_insert + "");
                    POST2GCM.post(gcm_id, "NewWalk", id_of_insert);

                } catch (Exception ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Intent notificationIntent;
                notificationIntent = new Intent(getActivity(), WalkInProgress.class);
                notificationIntent.putExtra("id", msg);
                notificationIntent.putExtra("sender_id", sender_id);

                notificationIntent.putExtra("animate", 0);
                startActivity(notificationIntent);
            }
        }.execute(null, null, null);

    }

    class finishActivity extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            try {
                // Building Parameters
                //:walker_id, :requester_id, :dogs_walked, :duration_sec, :dog_1s, :dog_2s, :route_taken, :map_loc, :img_loc
                String dog_1s = "0";
                String dog_2s = "0";

                List<NameValuePair> params = new ArrayList<>();
                Log.d("distance", distance + "");
                params.add(new BasicNameValuePair("walker_id", gcm_id));
                params.add(new BasicNameValuePair("requester_id", sender_id));
                params.add(new BasicNameValuePair("duration_sec", 0 + ""));
                params.add(new BasicNameValuePair("dogs_walked", dogs_walked));
                params.add(new BasicNameValuePair("dog_1s", dog_1s));
                params.add(new BasicNameValuePair("dog_2s", dog_2s));
                params.add(new BasicNameValuePair("route_taken", "0"));
                params.add(new BasicNameValuePair("addtl_instruct", input.getText().toString()));
                params.add(new BasicNameValuePair("distance", distance + ""));
                Log.d("params", params.toString());

                JSONObject json2 = jsonParser.makeHttpRequest(
                        REGISTER_URL, "POST", params);
                //Log.d("completed",json.toString());
                // full json response

                Log.d("Updating Walk Details", json2.toString());

                // json success element
                success = json2.getInt(TAG_SUCCESS);
                id_of_insert = json2.getString(TAG_MESSAGE);
                if (success == 1) {
                    Log.d("Success!", json2.getString(TAG_MESSAGE));
                    return json2.getString(TAG_MESSAGE);
                } else {
                    Log.d("Request Failure!", json2.getString(TAG_MESSAGE));
                    Log.d("Message", json2.getString(TAG_MESSAGE));
                    return json2.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;


        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(getActivity(), "Requesting Walker!", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(String id_of_insert) {
            getRegId();

        }
    }
}