package ly.leash.Leashly;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;


/**
 * Created by schwallie on 2/1/2015.
 * Leashly!
 */
public class ProfileView extends ActionBarActivity implements MaterialTabListener {
    ListView leftDrawerList;
    ArrayAdapter<String> navigationDrawerAdapter;
    Double lat, lon;
    String dog_1, dog_2, dog_3, first_name;
    MaterialTabHost tabHost;
    ViewPager pager;
    ViewPagerAdapter adapter_page;
    String sender_id;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private String[] leftSliderData = {"Logout", "Contact Us"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            System.out.println("in");
            toolbar.setTitleTextColor(Color.WHITE);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Edit Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sender_id = extras.getString("user_id");
            first_name = extras.getString("first_name");
        }

        tabHost = (MaterialTabHost) this.findViewById(R.id.tabHostProfile);
        pager = (ViewPager) this.findViewById(R.id.pagerProfile);

        // init view pager
        adapter_page = new ViewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter_page);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // when user do a swipe the selected tab change
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onTabSelected(MaterialTab tab) {
        pager.setCurrentItem(tab.getPosition());
    }


    @Override
    public void onTabReselected(MaterialTab tab) {
        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(MaterialTab tab) {

    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private String[] lst = {"Two Legs", "Four Legs"};

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int num) {
            return ProfileFragmentText.newInstance(num, sender_id, first_name);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return lst[position];
        }

    }


}
