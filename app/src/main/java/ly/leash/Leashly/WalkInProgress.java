package ly.leash.Leashly;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.ViewSwitcher;

import java.util.Arrays;

/**
 * Created by schwallie on 12/21/2014.
 */
public class WalkInProgress extends ActionBarActivity implements ViewSwitcher.ViewFactory {
    private static final String TAG = "IntroActivity";
    private final int[] images = {R.drawable.paw_outline, R.drawable.paw_fill};
    private final int interval = 1000;
    String user;
    ListView leftDrawerList;
    ArrayAdapter<String> navigationDrawerAdapter;
    Animation aniIn;
    Animation aniOut;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private String[] leftSliderData = {"Logout", "Contact Us"};
    private int index = 0;
    private boolean isRunning = false;
    /* this handler will process the broadcast intent */
    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
            Integer message = intent.getIntExtra("message", 0);
            System.out.println(message + "");
            startAnimatedBackground(message);
            // you could start your animation here
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_walk_in_progress);
        nitView();
        if (toolbar != null) {
            toolbar.setTitleTextColor(Color.WHITE);
            setSupportActionBar(toolbar);
        }
        initDrawer();
        Integer starter = 0;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = extras.getString("user");
            Log.d("ID", user + "");
            starter = extras.getInt("animate");
        }
        startAnimatedBackground(starter);
    }

    private void nitView() {
        leftDrawerList = (ListView) findViewById(R.id.left_drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout_progress);
        navigationDrawerAdapter = new ArrayAdapter<String>(WalkInProgress.this, android.R.layout.simple_list_item_1, leftSliderData);
        Log.d("nsv", navigationDrawerAdapter + "");
        Log.d("leftSliderData", Arrays.toString(leftSliderData));
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

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(messageReceiver, new IntentFilter("Animate"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(messageReceiver);
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
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startAnimatedBackground(Integer num) {
        ImageSwitcher imageSwitcher = null;
        aniIn = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in);
        aniOut = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out);

        aniIn.setDuration(500);
        aniOut.setDuration(500);
        switch (num) {
            case 0:
                imageSwitcher = (ImageSwitcher) findViewById(R.id.switcher_accept);
                break;
            case 1:
                ImageView accept = (ImageView) findViewById(R.id.accept_finish);
                accept.setVisibility(View.VISIBLE);
                ImageView on_way_starter = (ImageView) findViewById(R.id.on_way_starter);
                on_way_starter.setVisibility(View.INVISIBLE);
                imageSwitcher = (ImageSwitcher) findViewById(R.id.switcher_on_way);
                break;
            case 2:
                ImageView on_way = (ImageView) findViewById(R.id.on_way_finish);
                on_way.setVisibility(View.VISIBLE);
                ImageView in_progress_starter = (ImageView) findViewById(R.id.in_progress_starter);
                in_progress_starter.setVisibility(View.INVISIBLE);
                imageSwitcher = (ImageSwitcher) findViewById(R.id.switcher_in_progress);
                break;
            case 3:
                Intent notificationIntent = new Intent(this, WalkDone.class);
                startActivity(notificationIntent);
            default:
                break;
        }
        imageSwitcher.setInAnimation(aniIn);
        imageSwitcher.setOutAnimation(aniOut);
        imageSwitcher.setFactory(this);
        imageSwitcher.setImageResource(images[0]);
        isRunning = true;
        final Handler handler = new Handler();
        final ImageSwitcher finalImageSwitcher = imageSwitcher;
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                if (isRunning) {
                    System.out.println("Running.." + finalImageSwitcher + index);
                    index++;
                    index = index % images.length;
                    finalImageSwitcher.setImageResource(images[index]);
                    handler.postDelayed(this, interval);
                }
            }
        };
        handler.postDelayed(runnable, interval);

    }

    public View makeView() {
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));
        return imageView;
    }

    @Override
    public void finish() {
        isRunning = false;
        super.finish();
    }
}


