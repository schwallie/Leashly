package ly.leash.Leashly;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by schwallie on 12/4/2014.
 */
public class WalkerRequest extends ActionBarActivity implements View.OnClickListener {
    FloatingActionButton fabButton, acceptFabButton;
    ImageButton deny;
    TextView thanks_txt, click_below;
    String id = null;
    String sender_id = null;
    private Handler mHandler = new Handler();
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
            sender_id = extras.getString("sender_id");
            Log.d("walk request ID", id + "");
            Log.d("walk request sender", sender_id + "");
        }
        fabButton = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.checkmark))
                .withButtonColor(R.color.primaryColor)
                .withGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL)
                .withMargins(0, 0, 16, 16)
                .create();
        fabButton.setOnClickListener(this);
        deny = (ImageButton) findViewById(R.id.deny_button);
        thanks_txt = (TextView) findViewById(R.id.thanks_accept);
        click_below = (TextView) findViewById(R.id.click_below_txt);
    }


    @Override
    public void onClick(View v) {
        Log.d("ID",v.getId()+"");
        switch (v.getId()) {
            case -1:
                POST2GCM.post(sender_id, "AcceptWalk", id);
                Animation fadeout = AnimationUtils.loadAnimation(this, R.anim.fadeout);
                fabButton.startAnimation(fadeout);
                deny.startAnimation(fadeout);
                Animation fadein = AnimationUtils.loadAnimation(this, R.anim.fadein);
                thanks_txt.startAnimation(fadein);
                click_below.startAnimation(fadein);

                mHandler.postDelayed(new Runnable() {
                    public void run() {
addNewFab();
                    }
                }, 1500);

                break;
            case R.id.deny_button:
                // do stuff;
                break;
        }
    }
    public void addNewFab() {
        acceptFabButton = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.checkmark))
                .withButtonColor(R.color.primaryColor)
                .withGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL)

                .withButtonSize(70)
                .create();
        Log.d("user_intent", id+"");
        acceptFabButton.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   //NEED TO SEND GCM TO USER
                                                   Intent i = new Intent(getApplicationContext(),WalkStarted.class);
                                                   i.putExtra("user", id);
                                                   i.putExtra("sender_id", sender_id);
                                                   startActivity(i);
                                               }
                                           }

        );
    }
}