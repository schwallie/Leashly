package ly.leash.Leashly;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;

import ly.leash.Leashly.app.AppController;
import ly.leash.Leashly.model.viewer;
import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by schwallie on 11/25/2014.
 */
public class full_view_walker extends ActionBarActivity {
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_walker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        Bundle extras = getIntent().getExtras();
        String data = null;
        if (extras != null) {
            data = extras.getString("data");
        }
        Log.d("Data",data);
        try {
            JSONObject jsonObj = new JSONObject(getIntent().getStringExtra("data"));
            TextView bio = (TextView) findViewById(R.id.bio_selected);
            TextView fname = (TextView) findViewById(R.id.first_name_selected);
            bio.setText(jsonObj.getString("bio"));
            fname.setText(jsonObj.getString("first_name"));
            if (imageLoader == null)
                imageLoader = AppController.getInstance().getImageLoader();
            NetworkImageView thumbNail = (NetworkImageView) findViewById(R.id.pic_selected);


            // thumbnail image
            thumbNail.setImageUrl(jsonObj.getString("pic"), imageLoader);
        } catch(JSONException e) {
            e.printStackTrace();
        }

    }
}