package ly.leash.Leashly;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Register extends ActionBarActivity implements OnClickListener {

    private static final String REGISTER_URL = "http://leash.ly/webservice/register_personal_details.php";
    //ids
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    private EditText user, pass, first_name, last_name;
    public Integer success = 0;
    private Button mRegister;
    // Progress Dialog
    private ProgressDialog pDialog;
    private Toolbar toolbar;
    private String[] leftSliderData = {"About Us", "Contact Us"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitleTextColor(Color.WHITE);
            setSupportActionBar(toolbar);
        }
        user = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.extra);
        first_name = (EditText) findViewById(R.id.first_name);
        last_name = (EditText) findViewById(R.id.last_name);


        mRegister = (Button) findViewById(R.id.continu);
        mRegister.setOnClickListener(this);
    }

    /*
    Start Drawer
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    End Drawer Toggle
     */


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        new CreateUser().execute();
    }

    class CreateUser extends AsyncTask<String, String, Integer> {


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
        protected Integer doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            String username = user.getText().toString();
            String password = pass.getText().toString();
            String first_name_text = first_name.getText().toString();
            String last_name_text = last_name.getText().toString();
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));
                params.add(new BasicNameValuePair("first_name", first_name_text));
                params.add(new BasicNameValuePair("last_name", last_name_text));
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
                    return success;
                } else {
                    Log.d("Registering Failure!", json.getString(TAG_MESSAGE));
                    return success;

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(Integer success) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            Log.d("Success", success+"");
            if (success == 1) {
                Intent i = new Intent(Register.this, RegisterPets.class);
                i.putExtra("id", user.getText().toString());
                i.putExtra("num", 1);
                startActivity(i);
            }
        }

    }


}
