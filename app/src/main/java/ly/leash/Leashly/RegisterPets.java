package ly.leash.Leashly;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class RegisterPets extends ActionBarActivity implements View.OnClickListener {

    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    private static final String REGISTER_URL = "http://leash.ly/webservice/register_dog_details.php";
    ListView leftDrawerList;
    Integer camera_done = 0;
    ArrayAdapter<String> navigationDrawerAdapter;
    private EditText dog_name, dog_breed, dog_color, dog_default_inst;
    private Spinner key_spinner, city_spinner, state_spinner;
    private Button mRegister, mAddMore, upload;
    private Integer image_uploaded = 0;
    // Progress Dialog
    private ProgressDialog pDialog;
    private String img_name, fileName, loc_of_image;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private Toolbar toolbar;
    private Uri fileUri;
    private String[] leftSliderData = {"About Us", "Contact Us"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_pets);
        //nitView();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitleTextColor(Color.WHITE);
            setSupportActionBar(toolbar);
        }
        //initDrawer();
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        dog_name = (EditText) findViewById(R.id.dog_name);
        dog_breed = (EditText) findViewById(R.id.dog_breed);
        dog_default_inst = (EditText) findViewById(R.id.dog_default_inst);
        dog_color= (EditText) findViewById(R.id.dog_color);

        mRegister = (Button) findViewById(R.id.continu);
        mRegister.setOnClickListener(this);

        mAddMore = (Button) findViewById(R.id.add_more);
        mAddMore.setOnClickListener(this);

        upload = (Button) findViewById(R.id.upload);
        upload.setOnClickListener(this);
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
        switch (v.getId()) {
            case R.id.upload:
                dispatchTakePictureIntent();
                break;
            case R.id.continu:
                break;
            case R.id.add_more:
                break;
        }
    }
    /**
     * Create a file Uri for saving an image or video
     */
    private Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Leashly");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Leashly", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == 1) {
            img_name = "IMG_" + timeStamp + ".jpg";
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    img_name);
            Log.d("mediaDir", mediaFile.toString());
        } else {
            return null;
        }

        return mediaFile;
    }

    protected void dispatchTakePictureIntent() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(1); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
        // start the image capture Intent
        startActivityForResult(intent, 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            // Image captured and saved to fileUri specified in the Intent
            Log.d("requestCode", requestCode + "");
            loc_of_image = fileUri.getPath();
            Log.d("Image_Loc", loc_of_image);
            try {
                camera_done = 1;
                UploadToServer uts;
                uts = new UploadToServer();
                Log.d("Executing", "1");
                uts.execute(loc_of_image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.d("Image capture failed", "fail");
            // Image capture failed, advise user
        }
    }

    public class UploadToServer extends AsyncTask<String, String, Integer> {

        String upLoadServerUri = "http://leash.ly/webservice/upload_dog_image.php";
        int serverResponseCode = 0;

        @Override
        public void onPostExecute(Integer serverResponse) {
            File file = new File(fileName);
            boolean deleted = file.delete();
            if (serverResponseCode == 200) {
                Toast.makeText(RegisterPets.this, "Image successfully uploaded!", Toast.LENGTH_LONG).show();
            }
            image_uploaded = 1;
        }


        public Integer doInBackground(String... args) {
            fileName = args[0];
            HttpURLConnection conn;
            DataOutputStream dos;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File sourceFile = new File(args[0]);
            while (!sourceFile.exists()) {
                System.out.println("WAITING...");
            }

            try { // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);
                conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setConnectTimeout(50000);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);
                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necessary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();
            } catch (MalformedURLException ex) {
                //dialog.dismiss();
                ex.printStackTrace();
                //Toast.makeText(UploadImage.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                //dialog.dismiss();
                e.printStackTrace();
                //Toast.makeText(UploadImage.this, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Upload file Exception", "Exception : " + e.getMessage(), e);
            }
            //dialog.dismiss();
            return serverResponseCode;
        }
    }


    class CreateDog extends AsyncTask<String, String, Integer> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterPets.this);
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
            String name = dog_name.getText().toString();
            String breed = dog_breed.getText().toString();
            String color = dog_color.getText().toString();
            String dog_default = dog_default_inst.getText().toString();
            try {
                // Building Parameters
                //dog_name`, `dog_color`, `dog_breed`, `dog_default
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("dog_name", name));
                params.add(new BasicNameValuePair("dog_color", color));
                params.add(new BasicNameValuePair("dog_breed", breed));
                params.add(new BasicNameValuePair("dog_default", dog_default));
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
                Intent i = new Intent(RegisterPets.this, RegisterPets.class);
                startActivity(i);
            }
        }

    }
}