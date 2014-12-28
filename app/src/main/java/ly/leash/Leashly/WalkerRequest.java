package ly.leash.Leashly;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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

/**
 * Created by schwallie on 12/4/2014.
 * Leashly!
 */
public class WalkerRequest extends ActionBarActivity implements View.OnClickListener {
    //FloatingActionButton acceptFabButton;
    Button start_walk_btn, accept_walk_btn;
    String dog_1_final_name, dog_2_final_name, dog_3_final_name;
    String id = null;
    double lat, lon;
    String json_data, sender_id, dogs, addtl_instruct, main_instruct;
    String dog_1_color, dog_2_color, dog_3_color, dog_1_breed, dog_2_breed, dog_3_breed;
    String dog_1_weight, dog_2_weight, dog_3_weight, dog_1_pic, dog_2_pic, dog_3_pic;
    String dog_1_name, dog_2_name, dog_3_name;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    String id_of_walk = null;
    String distance;
    String get_url = "http://leash.ly/webservice/get_walk_deets_id.php";
    String get_main_details = "http://leash.ly/webservice/get_data_id.php";
    JSONParser jsonParser = new JSONParser();
    NetworkImageView dog_1_pic_view, dog_2_pic_view, dog_3_pic_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("WalkerRequest", "walk_request");
        setContentView(R.layout.walker_request);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getString("id");
            id_of_walk = extras.getString("sender_id");
            Log.d("walk request ID", id + "");
            Log.d("walk request ID of walk", id_of_walk);
        }
        dog_1_pic_view = (NetworkImageView) findViewById(R.id.dog_1_pic);
        dog_2_pic_view = (NetworkImageView) findViewById(R.id.dog_2_pic);
        dog_3_pic_view = (NetworkImageView) findViewById(R.id.dog_3_pic);
        dog_1_pic_view.setOnClickListener(this);
        dog_2_pic_view.setOnClickListener(this);
        dog_3_pic_view.setOnClickListener(this);
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
                    dogs = jobj_get.getString("dogs_walked");
                    addtl_instruct = jobj_get.getString("addtl_instruct");
                    distance = String.valueOf(jobj_get.getDouble("distance"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    // do something
                }

                List<NameValuePair> id_params = new ArrayList<>();
                Log.d("sender_id", sender_id);
                id_params.add(new BasicNameValuePair("user", sender_id));
                JSONObject jobj_get_other = jsonParser.makeHttpRequest(get_main_details, "POST", id_params);
                System.out.println(jobj_get_other);
                try {
                    JSONArray jobj_other = jobj_get_other.getJSONArray(sender_id);
                    for (int i = 0; i < jobj_other.length(); i++) {
                        json_data = jobj_other.getJSONObject(i).toString();
                    }
                    JSONObject jobj_get_other_deets = new JSONObject(json_data);
                    main_instruct = jobj_get_other_deets.getString("key_details");
                    lat = jobj_get_other_deets.getDouble("lat");
                    lon = jobj_get_other_deets.getDouble("long");
                    dog_1_name = jobj_get_other_deets.getString("dog_1");
                    dog_2_name = jobj_get_other_deets.getString("dog_2");
                    dog_3_name = jobj_get_other_deets.getString("dog_3");
                    dog_1_breed = jobj_get_other_deets.getString("dog_1_type");
                    dog_1_color = jobj_get_other_deets.getString("dog_1_color");
                    dog_1_weight = jobj_get_other_deets.getString("dog_1_weight");
                    dog_1_pic = jobj_get_other_deets.getString("dog_1_pic");
                    dog_2_breed = jobj_get_other_deets.getString("dog_2_type");
                    dog_2_color = jobj_get_other_deets.getString("dog_2_color");
                    dog_2_weight = jobj_get_other_deets.getString("dog_2_weight");
                    dog_2_pic = jobj_get_other_deets.getString("dog_2_pic");
                    dog_3_breed = jobj_get_other_deets.getString("dog_3_type");
                    dog_3_color = jobj_get_other_deets.getString("dog_3_color");
                    dog_3_weight = jobj_get_other_deets.getString("dog_3_weight");
                    dog_3_pic = jobj_get_other_deets.getString("dog_3_pic");


                } catch (JSONException e) {
                    e.printStackTrace();
                    // do something
                }
                return null;
            }

            protected void onPostExecute(String id_of_insert) {
                TextView dist = (TextView) findViewById(R.id.distance_request);
                if (distance.length() > 4)
                    distance = distance.substring(0, 4);
                dist.setText(distance + " miles");
                TextView addtl = (TextView) findViewById(R.id.addtl_instruct);
                addtl.setText(addtl_instruct);
                TextView norm_instruct = (TextView) findViewById(R.id.norm_instruct);
                norm_instruct.setText(main_instruct);
                String[] parts = dogs.split(",");
                int x = parts.length;
                //Dog3 can obly be 3
                TextView dog_1_walk_deets = (TextView) findViewById(R.id.dog_1_walk_deets);
                TextView dog_2_walk_deets = (TextView) findViewById(R.id.dog_2_walk_deets);
                TextView dog_3_walk_deets = (TextView) findViewById(R.id.dog_3_walk_deets);

                if (imageLoader == null)
                    imageLoader = AppController.getInstance().getImageLoader();
                // thumbnail image
                if (!parts[0].equals(dog_1_name)) {
                    //Dog1 can be 1, 2, or 3
                    if (parts[0].equals(dog_2_name)) {

                        dog_1_walk_deets.setText("Name: " + parts[0] + " Color: " + dog_2_color + "  Weight: " + dog_2_weight + "  Breed:" + dog_2_breed);
                        dog_1_pic_view.setImageUrl("http://leash.ly/" + dog_2_pic, imageLoader);

                    } else if (parts[0].equals(dog_3_name)) {
                        dog_1_walk_deets.setText("Name: " + parts[0] + " Color: " + dog_3_color + "  Weight: " + dog_3_weight + "  Breed: " + dog_3_breed);
                        dog_1_pic_view.setImageUrl("Name: " + "http://leash.ly/" + dog_3_pic, imageLoader);
                    }
                } else if (x == 1) {
                    dog_1_walk_deets.setText("Name: " + parts[0] + " Color: " + dog_1_color + "  Weight: " + dog_1_weight + "  Breed: " + dog_1_breed);
                    dog_1_pic_view.setImageUrl("http://leash.ly/" + dog_1_pic, imageLoader);
                } else if (!parts[1].equals(dog_2_name)) {
                    dog_2_walk_deets.setText("Name: " + parts[1] + " Color:" + dog_3_color + "  Weight: " + dog_3_weight + "  Breed: " + dog_3_breed);
                    dog_2_pic_view.setImageUrl("http://leash.ly/" + dog_3_pic, imageLoader);

                    if (parts[0].equals(dog_1_name)) {
                        dog_1_walk_deets.setText("Name: " + parts[0] + " Color: " + dog_1_color + "  Weight: " + dog_1_weight + "  Breed: " + dog_1_breed);
                        dog_1_pic_view.setImageUrl("http://leash.ly/" + dog_1_pic, imageLoader);
                    }
                }
                if (x == 1) {
                    dog_1_final_name = parts[0];
                    dog_1_walk_deets.setVisibility(View.VISIBLE);
                } else if (x == 2) {
                    dog_1_final_name = parts[0];
                    dog_2_final_name = parts[1];
                    if (parts[1].equals(dog_2_name) && parts[0].equals(dog_1_name)) {
                        dog_1_walk_deets.setText("Name: " + parts[0] + " Color: " + dog_1_color + "  Weight: " + dog_1_weight + "  Breed: " + dog_1_breed);
                        dog_1_pic_view.setImageUrl("http://leash.ly/" + dog_1_pic, imageLoader);
                        dog_2_walk_deets.setText("Name: " + parts[1] + " Color: " + dog_2_color + "  Weight: " + dog_2_weight + "  Breed: " + dog_2_breed);
                        dog_2_pic_view.setImageUrl("http://leash.ly/" + dog_2_pic, imageLoader);
                    }

                    dog_1_walk_deets.setVisibility(View.VISIBLE);
                    dog_2_walk_deets.setVisibility(View.VISIBLE);
                } else {
                    dog_1_final_name = parts[0];
                    dog_2_final_name = parts[1];
                    dog_3_final_name = parts[2];
                    dog_1_pic_view.setImageUrl("http://leash.ly/" + dog_1_pic, imageLoader);
                    dog_2_pic_view.setImageUrl("http://leash.ly/" + dog_2_pic, imageLoader);
                    dog_3_pic_view.setImageUrl("http://leash.ly/" + dog_3_pic, imageLoader);
                    dog_1_walk_deets.setText("Name: " + parts[0] + "  Color: " + dog_1_color + "  Weight: " + dog_1_weight + "  Breed: " + dog_1_breed);
                    dog_2_walk_deets.setText("Name: " + parts[1] + "  Color: " + dog_2_color + "  Weight: " + dog_2_weight + "  Breed: " + dog_2_breed);
                    dog_3_walk_deets.setText("Name: " + parts[2] + "  Color: " + dog_3_color + "  Weight: " + dog_3_weight + "  Breed: " + dog_3_breed);
                    dog_1_walk_deets.setVisibility(View.VISIBLE);
                    dog_2_walk_deets.setVisibility(View.VISIBLE);
                    dog_3_walk_deets.setVisibility(View.VISIBLE);
                }

            }

        }.execute(null, null, null);

        /*fabButton = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.checkmark))
                .withButtonColor(R.color.primaryColor)
                .withGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL)
                .withMargins(0, 0, 16, 16)
                .create();*/
        start_walk_btn = (Button) findViewById(R.id.start_walk_btn);
        accept_walk_btn = (Button) findViewById(R.id.accept_walk_btn);
        //fabButton.setOnClickListener(this);
        start_walk_btn.setOnClickListener(this);
        accept_walk_btn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Log.d("ID", v.getId() + "");
        switch (v.getId()) {
            case R.id.accept_walk_btn:
                POST2GCM.post(sender_id, "AcceptWalk", id);
                Animation fadeout = AnimationUtils.loadAnimation(this, R.anim.fadeout);
                //fabButton.startAnimation(fadeout);
                //fabButton.setVisibility(View.GONE);
                //fabButton.clearAnimation();
                accept_walk_btn.startAnimation(fadeout);
                accept_walk_btn.clearAnimation();
                accept_walk_btn.setVisibility(View.GONE);
                Animation fadein = AnimationUtils.loadAnimation(this, R.anim.fadein);
                start_walk_btn.startAnimation(fadein);
                break;
            case R.id.start_walk_btn:
                Intent i = new Intent(getApplicationContext(), WalkStarted.class);
                i.putExtra("user", id);
                i.putExtra("sender_id", sender_id);
                i.putExtra("walk_id", id_of_walk);
                i.putExtra("lat", lat);
                i.putExtra("lon", lon);
                i.putExtra("dog_1", dog_1_final_name);
                i.putExtra("dog_2", dog_2_final_name);
                i.putExtra("dog_3", dog_3_final_name);
                startActivity(i);
            case R.id.dog_1_pic:
                /*View webViewLayout = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.web_view, null, false);
                WebView myWebView = (WebView) webViewLayout.findViewById(R.id.web_view);
                myWebView.getSettings().setBuiltInZoomControls(true); //to get zoom functionalities
                String url = "http://leash.ly/"+dog_1_pic; //url of your image
                String ht = "<html><head><meta name=\"viewport\" content=\"width=device-width, minimum-scale=1.0\"/><style type=\"text/css\">html, body {margin: 0;padding: 0;} img {border: none;}</style><head><body style=\"background: black;\"><table><tr><td align=\"center\"><img src=\"" + url + "\" /></td></tr></table></body></html>";
                myWebView.loadData(ht, "text/html", "UTF-8");*/
            case R.id.dog_2_pic:
                /*View webViewLayout_2 = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.web_view, null, false);
                WebView myWebView_2 = (WebView) webViewLayout_2.findViewById(R.id.web_view);
                myWebView_2.getSettings().setBuiltInZoomControls(true); //to get zoom functionalities
                String url_2 = "http://leash.ly/"+dog_2_pic; //url of your image
                String ht_2 = "<html><head><meta name=\"viewport\" content=\"width=device-width, minimum-scale=1.0\"/><style type=\"text/css\">html, body {margin: 0;padding: 0;} img {border: none;}</style><head><body style=\"background: black;\"><table><tr><td align=\"center\"><img src=\"" + url_2 + "\" /></td></tr></table></body></html>";
                myWebView_2.loadData(ht_2, "text/html", "UTF-8");*/
            case R.id.dog_3_pic:
                /*View webViewLayout_3 = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.web_view, null, false);
                WebView myWebView_3 = (WebView) webViewLayout_3.findViewById(R.id.web_view);
                myWebView_3.getSettings().setBuiltInZoomControls(true); //to get zoom functionalities
                String url_3 = "http://leash.ly/"+dog_3_pic; //url of your image
                String ht_3 = "<html><head><meta name=\"viewport\" content=\"width=device-width, minimum-scale=1.0\"/><style type=\"text/css\">html, body {margin: 0;padding: 0;} img {border: none;}</style><head><body style=\"background: black;\"><table><tr><td align=\"center\"><img src=\"" + url_3 + "\" /></td></tr></table></body></html>";
                myWebView_3.loadData(ht_3, "text/html", "UTF-8");*/
        }
    }

    /*public void addNewFab() {
        acceptFabButton = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.checkmark))
                .withButtonColor(R.color.primaryColor)
                .withGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL)

                .withButtonSize(70)
                .create();
        Log.d("user_intent", id + "");
        acceptFabButton.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   Intent i = new Intent(getApplicationContext(), WalkStarted.class);
                                                   i.putExtra("user", id);
                                                   i.putExtra("sender_id", sender_id);
                                                   i.putExtra("walk_id", id_of_walk);
                                                   startActivity(i);
                                               }
                                           }

        );
    }*/
}