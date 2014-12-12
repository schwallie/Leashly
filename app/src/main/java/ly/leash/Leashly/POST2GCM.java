package ly.leash.Leashly;

/**
 * Created by schwallie on 11/30/2014.
 */

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class POST2GCM {

    public static class PostAsync extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                //THIS HAS TO BE PUT ON SERVER
                String apiKey = "AIzaSyDZcR45gjcuqBrQFdlsyA_MEDxp-YLgPB4";
                Content content = new Content();
                List<NameValuePair> params_j = new ArrayList<NameValuePair>();
                params_j.add(new BasicNameValuePair("user_id", params[0]));
                JSONParser jsonParser = new JSONParser();

                // testing on Emulator:
                String GCM_URL = "http://leash.ly/webservice/get_walker_gcm.php";

                // JSON element ids from repsonse of php script:
                Log.d("request!", "starting");
                // getting product details by making HTTP request
                JSONObject json_obj = jsonParser.makeHttpRequest(GCM_URL, "POST",
                        params_j);

                // check your log for json response
                Log.d("Json", json_obj.toString());
                // json success tag
                String gcm_id = null;
                try {
                    JSONArray innerProjectArray = json_obj.getJSONArray("result");
                    for (int i = 0; i < innerProjectArray.length(); i++)
                    {

                        JSONObject obj=innerProjectArray.getJSONObject(i);
                        gcm_id=obj.getString("GCM_ID");
                    }
                    //gcm_id = json_obj.getString("GCM_ID");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                content.addRegId(gcm_id);
                content.createData("New Walk!", "You have a new walk waiting for you!", params[0]);

                // 1. URL
                URL url = new URL("https://android.googleapis.com/gcm/send");

                // 2. Open connection
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                // 3. Specify POST method
                conn.setRequestMethod("POST");

                // 4. Set the headers
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "key=" + apiKey);
                conn.setDoOutput(true);

                // 5. Add JSON data into POST request body

                //`5.1 Use Jackson object mapper to convert Contnet object into JSON

                ObjectMapper mapper = new ObjectMapper();

                // 5.2 Get connection output stream
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                // 5.3 Copy Content "JSON" into
                mapper.writeValue(wr, content);
                // 5.4 Send the request
                wr.flush();

                // 5.5 close
                wr.close();
                // 6. Get the response
                int responseCode = conn.getResponseCode();
                System.out.println("\nSending 'POST' request to URL : " + url);
                System.out.println("Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // 7. Print result
                System.out.println(response.toString());
                return null;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    public static void post(String regId){
        PostAsync task_ = new PostAsync();
        task_.execute(regId);

    }
}