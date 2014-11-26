package ly.leash.Leashly;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by schwallie on 11/18/2014.
 */




public class WalkerMain extends ActionBarActivity implements View.OnClickListener{
    private Button mActive;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walker_app);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        mActive = (Button) findViewById(R.id.go_active);
        // register listeners
        mActive.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Bundle extras = getIntent().getExtras();
        String user = null;
        Integer user_id = 0;
        if (extras != null) {
            user = extras.getString("user");
            user_id = extras.getInt("user_id");
            Log.d("In WalkerMain!", user);
        }
        switch (v.getId()) {
            case R.id.go_active:
                Intent i = new Intent(this, WalkerActive.class);
                i.putExtra("user", user);
                i.putExtra("user_id", user_id);
                startActivity(i);
                break;

            default:
                break;
        }
    }
}


