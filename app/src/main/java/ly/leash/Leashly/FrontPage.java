package ly.leash.Leashly;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

/**
 * Created by schwallie on 11/18/2014.
 */
public class FrontPage extends ActionBarActivity implements View.OnClickListener {
    private Button mSubmit, mRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.front_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        // setup buttons
        mSubmit = (Button) findViewById(R.id.signup_front);
        mRegister = (Button) findViewById(R.id.register_front);

        // register listeners
        mSubmit.setOnClickListener(this);
        mRegister.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.signup_front:
                Intent i = new Intent(this, Login.class);
                startActivity(i);
                break;
            case R.id.register_front:
                Intent i2 = new Intent(this, Register.class);
                startActivity(i2);
                break;

            default:
                break;
        }
    }
}
