package ly.leash.Leashly;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ly.leash.Leashly.app.AppController;

public class WalkDone extends ActionBarActivity {
    String user, id_of_walk, img_loc, map_loc, dog_1s, dog_2s, walk_notes;
    String json_data, sender_id, dogs_walked, duration_sec;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    String distance;
    String get_url = "http://leash.ly/webservice/get_walk_deets_id.php";
    JSONParser jsonParser = new JSONParser();
    NetworkImageView img_view, map_view;
    TextView walk_notes_txt, dog_1_deets, dog_2_deets, dog_3_deets, walk_len;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walk_done);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);

        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = extras.getString("id");
            id_of_walk = extras.getString("sender_id");
            Log.d("WalkDone ID", user + "");
            Log.d("WslkDone ID of walk", id_of_walk);
        }
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... args) {
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("id", id_of_walk));
                JSONObject jobj = jsonParser.makeHttpRequest(get_url, "POST", params);
                System.out.println(jobj);
                try {
                    JSONArray jobj_ = jobj.getJSONArray(id_of_walk);
                    for (int i = 0; i < jobj_.length(); i++) {
                        json_data = jobj_.getJSONObject(i).toString();
                    }
                    JSONObject jobj_get = new JSONObject(json_data);
                    sender_id = jobj_get.getString("requester_id");
                    duration_sec = jobj_get.getString("duration_sec");
                    img_loc = jobj_get.getString("img_loc");
                    map_loc = jobj_get.getString("map_loc");
                    dog_1s = jobj_get.getString("dog_1s");
                    dog_2s = jobj_get.getString("dog_2s");
                    dogs_walked = jobj_get.getString("dogs_walked");
                    walk_notes = jobj_get.getString("walk_note");
                    walk_notes_txt = (TextView) findViewById(R.id.walk_notes);
                    dog_1_deets = (TextView) findViewById(R.id.dog_1_who_did_what);
                    dog_2_deets = (TextView) findViewById(R.id.dog_2_who_did_what);
                    dog_3_deets = (TextView) findViewById(R.id.dog_3_who_did_what);
                    walk_len = (TextView) findViewById(R.id.walk_length);
                    map_view = (NetworkImageView) findViewById(R.id.map_of_walk);
                    img_view = (NetworkImageView) findViewById(R.id.img_after_walk);
                } catch (JSONException e) {
                    e.printStackTrace();
                    // do something
                }
                return null;
            }

            @Override
            protected void onPostExecute(String whatever) {

                if (imageLoader == null)
                    imageLoader = AppController.getInstance().getImageLoader();
                map_view.setImageUrl("http://leash.ly/images/saved_maps/" + map_loc, imageLoader);
                img_view.setImageUrl("http://leash.ly/images/saved_maps/" + img_loc, imageLoader);
                walk_notes_txt.setText(walk_notes);
                double walk_len_int = Integer.parseInt(duration_sec) / 60;
                System.out.println(Integer.parseInt(duration_sec) + "");
                System.out.println(walk_len_int + "");
                System.out.println((int) walk_len_int + "");
                walk_len.setText("Length of Walk: " + (int) walk_len_int + "m" + (int) (walk_len_int - (int) walk_len_int) * 60 + "s");
                String[] parts = dogs_walked.split(",");
                int x = parts.length;
                String dog_1 = parts[0];
                String dog_1_pee = " ";
                if (dog_1s.contains(dog_1)) {
                    dog_1_pee += "#1";
                }
                String dog_1_poo = " ";
                if (dog_2s.contains(dog_1)) {
                    dog_1_poo += "#2";
                }
                dog_1_deets.setText(dog_1 + ":" + dog_1_pee + dog_1_poo);
                if (x >= 2) {
                    String dog_2 = parts[1];
                    String dog_2_pee = " ";
                    if (dog_1s.contains(dog_2)) {
                        dog_2_pee += "#1";
                    }
                    String dog_2_poo = " ";
                    if (dog_2s.contains(dog_2)) {
                        dog_2_poo += "#2";
                    }
                    dog_2_deets.setText(dog_2 + ":" + dog_2_pee + dog_2_poo);
                    if (x == 3) {
                        String dog_3 = parts[2];
                        String dog_3_pee = " ";
                        if (dog_1s.contains(dog_3)) {
                            dog_3_pee += "#1";
                        }
                        String dog_3_poo = " ";
                        if (dog_2s.contains(dog_3)) {
                            dog_3_poo += "#2";
                        }
                        dog_3_deets.setText(dog_3 + ":" + dog_3_pee + dog_3_poo);
                    }
                }


            }

        }.execute(null, null, null);

    }
}

