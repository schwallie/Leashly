package ly.leash.Leashly;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class WalkDone extends ActionBarActivity {
    String user, id_of_walk;
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
    }
}

