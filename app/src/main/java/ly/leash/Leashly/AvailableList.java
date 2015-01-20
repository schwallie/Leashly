package ly.leash.Leashly;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;
import ly.leash.Leashly.adapter.CustomListAdapter;
import ly.leash.Leashly.model.viewer;


public class AvailableList extends ActionBarActivity implements MaterialTabListener {
    private static final String TAG = AvailableList.class.getSimpleName();
    private static final String url = "http://leash.ly/webservice/get_active.php";
    ListView leftDrawerList;
    ArrayAdapter<String> navigationDrawerAdapter;
    MaterialTabHost tabHost;
    ViewPager pager;
    ViewPagerAdapter adapter_page;
    String sender_id;
    double distanceInMiles;
    Double lat = null;
    Double lon = null;
    Integer sort;
    private ProgressDialog pDialog;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private String[] leftSliderData = {"Logout", "Contact Us"};
    private List<viewer> movieList = new ArrayList<>();
    private ListView listView;
    private CustomListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_listview);
        nitView();
        if (toolbar != null) {
            toolbar.setTitleTextColor(Color.WHITE);
            setSupportActionBar(toolbar);
        }
        initDrawer();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            lat = extras.getDouble("lat");
            lon = extras.getDouble("lon");
            sender_id = extras.getString("user_id");
            Log.d("Avail_list sender_id", sender_id);
        }
        tabHost = (MaterialTabHost) this.findViewById(R.id.tabHost);
        pager = (ViewPager) this.findViewById(R.id.pager);

        // init view pager
        adapter_page = new ViewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter_page);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // when user do a swipe the selected tab change
                Log.d("1", "5");
                tabHost.setSelectedNavigationItem(position);

            }
        });

        // insert all tabs from pagerAdapter data
        for (int i = 0; i < adapter_page.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab()
                            .setText(adapter_page.getPageTitle(i))
                            .setTabListener(this)
            );

        }
    }

    private void nitView() {
        leftDrawerList = (ListView) findViewById(R.id.left_drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //FIND DRAWERLAYOUT
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationDrawerAdapter = new ArrayAdapter<>(AvailableList.this, android.R.layout.simple_list_item_1, leftSliderData);
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
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public void onTabSelected(MaterialTab tab) {
        Log.d("1", "1");
        Bundle args = new Bundle();
        args.putDouble("lat", lat);
        args.putDouble("lon", lon);
        args.putInt("sort", tab.getPosition());
        Log.d("Sort", tab.getPosition() + "");
        FragmentText frag = new FragmentText();
        frag.setArguments(args);
        pager.setCurrentItem(tab.getPosition());
    }


    @Override
    public void onTabReselected(MaterialTab tab) {
        Log.d("2", "2");
        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(MaterialTab tab) {

    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private String[] lst = {"Distance", "Experience", "History"};

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int num) {
            return FragmentText.newInstance(num);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Log.d("1", "4");
            return lst[position];
        }

    }


}
