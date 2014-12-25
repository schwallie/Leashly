package ly.leash.Leashly;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Created by schwallie on 12/25/2014.
 */
public class FinalWalkDetails extends ActionBarActivity implements View.OnClickListener {

    String id, sender_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("WalkerRequest", "walk_request");
        setContentView(R.layout.final_walk_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getString("id");
            sender_id = extras.getString("sender_id");
            Log.d("walk request ID", id + "");
            Log.d("walk request sender", sender_id + "");
        }
    }


    @Override
    public void onClick(View v) {
        Log.d("ID", v.getId() + "");
        switch (v.getId()) {
            case -1:
                //POST2GCM.post(sender_id, "AcceptWalk", id);
                Animation fadeout = AnimationUtils.loadAnimation(this, R.anim.fadeout);
                Animation fadein = AnimationUtils.loadAnimation(this, R.anim.fadein);
                break;
            case R.id.deny_button:
                // do stuff;
                break;
        }
    }
}
