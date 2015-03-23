package ly.leash.Leashly;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ly.leash.Leashly.adapter.CustomListAdapter;
import ly.leash.Leashly.model.viewer;

public class ProfileFragmentText extends Fragment {
    // In the FragmentText
    public static final String EXTRA_POSITION = "extra_position";
    // Log tag
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    static Context context_use;
    String get_main_details = "http://leash.ly/webservice/get_data_id.php";
    String save_details = "http://leash.ly/webservice/update_user_info.php";
    double distanceInMiles;
    Double lat = null;
    Double lon = null;
    Integer position;
    String id, data, dog_1, dog_2, dog_3, first_name;
    JSONParser jsonParser = new JSONParser();
    Button save_btn;
    Spinner spinner_keys;
    String json_data, sender_id, main_instruct;
    String dog_1_color, dog_2_color, dog_3_color, dog_1_breed, dog_2_breed, dog_3_breed;
    String dog_1_describe, dog_2_describe, dog_3_describe, dog_1_pic, dog_2_pic, dog_3_pic;
    String dog_1_name, dog_2_name, dog_3_name, last_name, address_1, address_2;
    EditText last_name_txt, main_instruc_edittxt, address_1_edit, address_2_edit, dog_1_name_edit,
            dog_2_name_edit, dog_3_name_edit, dog_1_breed_edit, dog_2_breed_edit, dog_3_breed_edit,
            dog_1_color_edit, dog_2_color_edit, dog_3_color_edit, dog_1_describe_edit, dog_2_describe_edit, dog_3_describe_edit;
    private EditText input;
    private View positiveAction;
    private List<viewer> movieList = new ArrayList<>();
    private ListView listView;
    private CustomListAdapter adapter;

    public static Fragment newInstance(int position, String sender_id,
                                       String first_name, Context context) {
        context_use = context;

        ProfileFragmentText ft = new ProfileFragmentText();
        Bundle args = new Bundle();
        args.putInt(EXTRA_POSITION, position);
        Log.d("Pos", position + "");
        args.putString("sender_id", sender_id);
        args.putString("first_name", first_name);
        ft.setArguments(args); // set the arguments with the tab position
        return ft;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(EXTRA_POSITION);
        sender_id = getArguments().getString("sender_id");
        first_name = getArguments().getString("first_name");


    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View V;
        if (position == 0) {
            V = inflater.inflate(R.layout.two_legs, container, false);
        } else {
            V = inflater.inflate(R.layout.four_legs, container, false);
        }

        final TextView first_txt = (TextView) V.findViewById(R.id.profile_first_name);
        spinner_keys = (Spinner) V.findViewById(R.id.spinner_eidt_profile);
        last_name_txt = (EditText) V.findViewById(R.id.profile_last_name);
        main_instruc_edittxt = (EditText) V.findViewById(R.id.main_instruct_text);
        address_1_edit = (EditText) V.findViewById(R.id.profile_address);
        address_2_edit = (EditText) V.findViewById(R.id.profile_address_2);
        save_btn = (Button) V.findViewById(R.id.save_btn);
        dog_1_name_edit = (EditText) V.findViewById(R.id.dog_1_name_edit);
        dog_1_color_edit = (EditText) V.findViewById(R.id.dog_1_color_edit);
        dog_1_breed_edit = (EditText) V.findViewById(R.id.dog_1_breed_edit);
        dog_1_describe_edit = (EditText) V.findViewById(R.id.dog_1_describe_edit);
        final ArrayAdapter<CharSequence> adapter_keys = ArrayAdapter.createFromResource(
                V.getContext(), R.array.keys_array, android.R.layout.simple_spinner_item);
        final TextView welc = (TextView) V.findViewById(R.id.welc_dog_name_1);
        final TextView welc2 = (TextView) V.findViewById(R.id.welc_dog_name_2);
        final TextView welc3 = (TextView) V.findViewById(R.id.welc_dog_name_3);


        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... args) {

                List<NameValuePair> id_params = new ArrayList<>();
                id_params.add(new BasicNameValuePair("user", sender_id));
                JSONObject jobj_get_other = jsonParser.makeHttpRequest(get_main_details, "POST", id_params);
                try {
                    JSONArray jobj_other = jobj_get_other.getJSONArray(sender_id);
                    for (int i = 0; i < jobj_other.length(); i++) {
                        json_data = jobj_other.getJSONObject(i).toString();
                    }
                    JSONObject jobj_get_other_deets = new JSONObject(json_data);
                    Log.d("jobj", jobj_get_other_deets.getString("last_name"));
                    main_instruct = jobj_get_other_deets.getString("key_details");
                    lat = jobj_get_other_deets.getDouble("lat");
                    lon = jobj_get_other_deets.getDouble("long");
                    dog_1_name = jobj_get_other_deets.getString("dog_1");
                    last_name = jobj_get_other_deets.getString("last_name");
                    address_1 = jobj_get_other_deets.getString("address_1");
                    address_2 = jobj_get_other_deets.getString("address_2");
                    dog_2_name = jobj_get_other_deets.getString("dog_2");
                    dog_3_name = jobj_get_other_deets.getString("dog_3");
                    dog_1_breed = jobj_get_other_deets.getString("dog_1_type");
                    dog_1_color = jobj_get_other_deets.getString("dog_1_color");
                    dog_1_describe = jobj_get_other_deets.getString("dog_1_describe");
                    dog_1_pic = jobj_get_other_deets.getString("dog_1_pic");
                    dog_2_breed = jobj_get_other_deets.getString("dog_2_type");
                    dog_2_color = jobj_get_other_deets.getString("dog_2_color");
                    dog_2_describe = jobj_get_other_deets.getString("dog_2_describe");
                    dog_2_pic = jobj_get_other_deets.getString("dog_2_pic");
                    dog_3_breed = jobj_get_other_deets.getString("dog_3_type");
                    dog_3_color = jobj_get_other_deets.getString("dog_3_color");
                    dog_3_describe = jobj_get_other_deets.getString("dog_3_describe");
                    dog_3_pic = jobj_get_other_deets.getString("dog_3_pic");

                } catch (JSONException e) {
                    e.printStackTrace();
                    // do something
                }
                return null;
            }


            protected void onPostExecute(String id_of_insert) {
                if (position == 0) {
                    last_name_txt.setText(last_name);
                    main_instruc_edittxt.setPadding(5, 0, 5, 0);
                    main_instruc_edittxt.setText(main_instruct);
                    address_1_edit.setText(address_1);
                    address_2_edit.setText(address_2);
                    first_txt.setText(first_name);
                    adapter_keys.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_keys.setAdapter(adapter_keys);

                    save_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {

                            new AsyncTask<Void, Void, String>() {
                                @Override
                                protected String doInBackground(Void... args) {

                                    List<NameValuePair> id_params = new ArrayList<>();
                                    id_params.add(new BasicNameValuePair("username", sender_id));
                                    String gcm_id = PreferenceData.getLoggedInSecurity(context_use);
                                    id_params.add(new BasicNameValuePair("gcm", gcm_id));
                                    id_params.add(new BasicNameValuePair("first_name", first_txt.getText().toString()));
                                    id_params.add(new BasicNameValuePair("last_name", last_name_txt.getText().toString()));
                                    id_params.add(new BasicNameValuePair("address_1", address_1_edit.getText().toString()));
                                    id_params.add(new BasicNameValuePair("address_2", address_2_edit.getText().toString()));
                                    id_params.add(new BasicNameValuePair("key_details", main_instruc_edittxt.getText().toString()));
                                    try {
                                        JSONParser jsonParser = new JSONParser();
                                        JSONObject json = jsonParser.makeHttpRequest(save_details, "POST", id_params);

                                        Log.d("Registering attempt", json.toString());

                                        // json success element
                                        Integer success = json.getInt(TAG_SUCCESS);
                                        if (success == 1) {
                                            Log.d("User Created!", json.toString());
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

                            }.execute(null, null, null);
                        }
                    });
                } else {
                    if (dog_1_name != null) {
                        welc.setText(dog_1_name);
                        dog_1_breed_edit.setText(dog_1_breed);
                        dog_1_name_edit.setText(dog_1_name);
                        dog_1_color_edit.setText(dog_1_color);
                        dog_1_describe_edit.setPadding(5, 0, 5, 0);
                        dog_1_describe_edit.setText(dog_1_describe);
                    }
                    if (dog_2_name != null) {
                        RelativeLayout scroll2 = (RelativeLayout) V.findViewById(R.id.scroller_rel2);
                        scroll2.setVisibility(View.VISIBLE);
                        welc2.setText(dog_2_name);
                        dog_2_breed_edit.setText(dog_2_breed);
                        dog_2_name_edit.setText(dog_2_name);
                        dog_2_color_edit.setText(dog_2_color);
                        dog_2_describe_edit.setPadding(5, 0, 5, 0);
                        dog_2_describe_edit.setText(dog_2_describe);

                    }
                    if (dog_3_name != null) {
                        RelativeLayout scroll3 = (RelativeLayout) V.findViewById(R.id.scroller_rel3);
                        scroll3.setVisibility(View.VISIBLE);
                        welc3.setText(dog_3_name);
                        dog_3_breed_edit.setText(dog_3_breed);
                        dog_3_name_edit.setText(dog_3_name);
                        dog_3_color_edit.setText(dog_3_color);
                        dog_3_describe_edit.setPadding(5, 0, 5, 0);
                        dog_3_describe_edit.setText(dog_3_describe);

                    }


                }
            }


        }.execute(null, null, null);
        return V;
    }
}


