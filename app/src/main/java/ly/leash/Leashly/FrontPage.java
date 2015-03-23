package ly.leash.Leashly;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

/**
 * Created by schwallie on 11/18/2014.
 */
public class FrontPage extends ActionBarActivity implements View.OnClickListener {
    ListView leftDrawerList;
    ArrayAdapter<String> navigationDrawerAdapter;
    private Button mSubmit, mRegister;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private String[] leftSliderData = {"About Us", "Contact Us"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Boolean status = PreferenceData.getUserLoggedInStatus(this);
        Log.d("status", status + "");
        if(status) {
            LoginStartup userdata = new LoginStartup(PreferenceData.getLoggedInEmailUser(this), getApplicationContext());
            userdata.execute(PreferenceData.getLoggedInEmailUser(this));

        } else {
            setContentView(R.layout.front_page);
            nitView();
            if (toolbar != null) {
                toolbar.setTitleTextColor(Color.WHITE);
                setSupportActionBar(toolbar);
            }
            initDrawer();
            // setup buttons

            mSubmit = (Button) findViewById(R.id.signup_front);
            mRegister = (Button) findViewById(R.id.register_front);

            // register listeners
            mSubmit.setOnClickListener(this);
            mRegister.setOnClickListener(this);
        }
    }


    private void nitView() {
        leftDrawerList = (ListView) findViewById(R.id.left_drawer_front);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout_front);
        navigationDrawerAdapter = new ArrayAdapter<>(FrontPage.this, android.R.layout.simple_list_item_1, leftSliderData);
        leftDrawerList.setAdapter(navigationDrawerAdapter);
        leftDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String clicked_at = leftSliderData[position];
                switch (clicked_at) {
                    case "Logout":
                        PreferenceData.setUserLoggedInStatus(getBaseContext(), false);
                        PreferenceData.clearLoggedInEmailAddress(getBaseContext());
                        Intent i = new Intent(getBaseContext(), Login.class);
                        startActivity(i);
                        break;
                    case "Contact Us":
                        Intent i2 = new Intent(getBaseContext(), Register.class);
                        startActivity(i2);
                        break;

                    default:
                        break;
                }


            }
        });
    }

    private void initDrawer() {

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
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
