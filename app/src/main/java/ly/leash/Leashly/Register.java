package ly.leash.Leashly;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Register extends ActionBarActivity implements OnClickListener {

    private static final String REGISTER_URL = "http://leash.ly/webservice/register.php";
    //ids
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    ListView leftDrawerList;
    ArrayAdapter<String> navigationDrawerAdapter;
    private EditText user, pass, address_1, address_2, dog_1, dog_2, dog_3, instructions, first_name, last_name;
    private Spinner key_spinner, city_spinner, state_spinner;
    private Button mRegister;
    // Progress Dialog
    private ProgressDialog pDialog;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private String[] leftSliderData = {"About Us", "Contact Us"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        nitView();
        if (toolbar != null) {
            toolbar.setTitleTextColor(Color.WHITE);
            setSupportActionBar(toolbar);
        }
        initDrawer();
        user = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.extra);
        first_name = (EditText) findViewById(R.id.first_name);
        last_name = (EditText) findViewById(R.id.last_name);


        mRegister = (Button) findViewById(R.id.register);
        mRegister.setOnClickListener(this);
/*
        city_spinner = (Spinner) findViewById(R.id.spinner_cities);
        state_spinner = (Spinner) findViewById(R.id.spinner_states);
        key_spinner = (Spinner) findViewById(R.id.spinner_keys);
        Spinner spinner = (Spinner) findViewById(R.id.spinner_cities);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.cities_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Spinner spinner_states = (Spinner) findViewById(R.id.spinner_states);
        ArrayAdapter<CharSequence> adapter_states = ArrayAdapter.createFromResource(
                this, R.array.states_array, android.R.layout.simple_spinner_item);
        adapter_states.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_states.setAdapter(adapter_states);

        Spinner spinner_keys;
        spinner_keys = (Spinner) findViewById(R.id.spinner_keys);
        ArrayAdapter<CharSequence> adapter_keys = ArrayAdapter.createFromResource(
                this, R.array.keys_array, android.R.layout.simple_spinner_item);
        adapter_keys.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_keys.setAdapter(adapter_keys);
*/
    }

    /*
    Start Drawer
     */
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

    private void nitView() {
        leftDrawerList = (ListView) findViewById(R.id.left_drawer_register);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout_register);
        navigationDrawerAdapter = new ArrayAdapter<>(Register.this, android.R.layout.simple_list_item_1, leftSliderData);
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

    /*
    End Drawer Toggle
     */

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        new CreateUser().execute();

    }
    class CreateUser extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Register.this);
            pDialog.setMessage("Creating User...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            String username = user.getText().toString();
            String password = pass.getText().toString();
            String address_1_text = address_1.getText().toString();
            String address_2_text = address_2.getText().toString();
            String dog_1_text = dog_1.getText().toString();
            String dog_2_text = dog_2.getText().toString();
            String dog_3_text = dog_3.getText().toString();
            String instructions_text = instructions.getText().toString();
            String first_name_text = first_name.getText().toString();
            String last_name_text = last_name.getText().toString();
            String city_spinner_text = city_spinner.getSelectedItem().toString();
            String state_spinner_text = state_spinner.getSelectedItem().toString();
            String key_spinner_text = key_spinner.getSelectedItem().toString();
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));
                params.add(new BasicNameValuePair("address_1", address_1_text));
                params.add(new BasicNameValuePair("address_2", address_2_text));
                params.add(new BasicNameValuePair("dog_1", dog_1_text));
                params.add(new BasicNameValuePair("dog_2", dog_2_text));
                params.add(new BasicNameValuePair("dog_3", dog_3_text));
                params.add(new BasicNameValuePair("instructions", instructions_text));
                params.add(new BasicNameValuePair("first_name", first_name_text));
                params.add(new BasicNameValuePair("last_name", last_name_text));
                params.add(new BasicNameValuePair("city", city_spinner_text));
                params.add(new BasicNameValuePair("state", state_spinner_text));
                params.add(new BasicNameValuePair("key", key_spinner_text));
                Log.d("request!", "starting");
                Log.d("params", params.toString());
                JSONObject json = jsonParser.makeHttpRequest(
                        REGISTER_URL, "POST", params);
                //Log.d("completed",json.toString());
                // full json response
                Log.d("Registering attempt", json.toString());

                // json success element
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("User Created!", json.toString());
                    finish();
                    return json.getString(TAG_MESSAGE);
                } else {
                    Log.d("Registering Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null) {
                Toast.makeText(Register.this, file_url, Toast.LENGTH_LONG).show();
            }

        }

    }


}
