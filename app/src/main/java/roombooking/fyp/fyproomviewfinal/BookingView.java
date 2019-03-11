package roombooking.fyp.fyproomviewfinal;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BookingView extends AppCompatActivity {

    TextView t1;
    private RecyclerView mRecyclerView;
    Button filterButton, findButton;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<String> nameList = new ArrayList<>();
    ArrayList<String> locList = new ArrayList<>();
    ArrayList<String> availList = new ArrayList<>();
    ArrayList<String> maxPeopleList = new ArrayList<>();
    ArrayList<String> availableAtList = new ArrayList<>();
    String[][] arrStr = new String[3][4];
    ArrayList<exampleItem> roomList = new ArrayList<>();
    String uID, sesToken;
    static String filters[] = {"Location", "Available"};
    static ArrayList<String> selectedFilters = new ArrayList();
    static String locations[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_view);





filterButton = (Button)findViewById(R.id.filterButton) ;

        mRecyclerView = findViewById(R.id.recyclerView);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String sessionToken = preferences.getString("sessionToken", "");
        final int userId = preferences.getInt("userId",0);
        final String userIdStr = String.valueOf(userId);
        uID = userIdStr;
        sesToken = sessionToken;

        // String concat = preferences.getString("sessionToken", "") + "   " +  preferences.getInt("userId",0);


        RequestQueue rq = Volley.newRequestQueue(this);

        StringRequest postReq = new StringRequest(Request.Method.GET, "https://wbroombooking.brunel.ac.uk/CEDPSRoomBooking/booked/Web/Services/index.php/Resources/", new Response.Listener<String>() {





            @Override
            public void onResponse(String response) {
                String result = response.split("exception 'ErrorException'")[0];
                try {

                    JSONObject jsnobject = new JSONObject(result);

                    JSONArray jsonArray = jsnobject.getJSONArray("resources");

                    for(int i=0; i<jsonArray.length(); i++){

                       JSONObject obj = jsonArray.getJSONObject(i);

                       String name = obj.getString("name");
                        String location = obj.getString("location");
                        String maxPeople = obj.getString("maxParticipants");

                        String editedName = "";
                        editedName = name.trim();


                        int spacePos = name.indexOf(" ");
                        if (spacePos > 0) {
                             editedName = name.substring(0, spacePos);
                        }


                        Log.i("array",obj.toString());
                        nameList.add(editedName);
                        locList.add(location);
                        maxPeopleList.add(maxPeople);

                    }
                    getAvailability(sessionToken,userIdStr);






                }
                catch(JSONException e)
                {

                    Log.i("marker","fail");
                    Log.e("marker", "unexpected JSON exception", e);
                }


            }
        }, new Response.ErrorListener() {

            @Override

            public void onErrorResponse(VolleyError error) {
                Log.i("marker", "unexpected JSON exception", error);

            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                headers.put("X-Booked-SessionToken",sessionToken );
                headers.put("X-Booked-UserId", userIdStr);
                return headers;
            }
        };

        rq.add(postReq);
    }


    public void getAvailability(final String Token, final String ID){
        RequestQueue rq = Volley.newRequestQueue(this);

        StringRequest postReq = new StringRequest(Request.Method.GET, "https://wbroombooking.brunel.ac.uk/CEDPSRoomBooking/booked/Web/Services/index.php/Resources/Availability", new Response.Listener<String>() {





            @Override
            public void onResponse(String response) {
                String result = response.split("exception 'ErrorException'")[0];
                Log.i("marker","responseback");
               // Log.i("output",result);
                try {
                    JSONObject jsnobject = new JSONObject(result);
                     JSONArray availArray = jsnobject.getJSONArray("resources");
                    Log.i("check","here");
                    JSONArray availArray2 = availArray.getJSONArray(0);

                    Log.i("check","or here");
                   for(int i=0;i<availArray2.length();i++){
                        JSONObject json = availArray2.getJSONObject(i);

                        String available = json.getString("available");
                        JSONObject find = json.getJSONObject("resource");
                       String findAvailableAt = json.getString("availableAt");


                       String editedFindAvailableAt = findAvailableAt.substring(findAvailableAt.indexOf("T")+1);
                       editedFindAvailableAt.trim();

                       int spacePos = editedFindAvailableAt.indexOf("+");
                       if (spacePos > 0) {
                           findAvailableAt = editedFindAvailableAt.substring(0, spacePos);
                       }

                       Log.i("out", editedFindAvailableAt);

                       availableAtList.add(findAvailableAt);
                        availList.add(available);
                    }

                    //JSONObject jsonArr = new JSONObject(result);


                    int len2= availList.size();


                    for (int i = 0; i < availList.size(); i++) {
                        roomList.add(new exampleItem(nameList.get(i), locList.get(i), availList.get(i), maxPeopleList.get(i), availableAtList.get(i)));
                    }
                    int len = roomList.size();

                    mRecyclerView = findViewById(R.id.recyclerView);
                    mRecyclerView.setHasFixedSize(true);
                    mLayoutManager = new LinearLayoutManager(BookingView.this);
                    mAdapter = new recyclerAdapter(roomList);

                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(mAdapter);



                }
                catch(JSONException e)
                {

                    Log.i("marker","fail");
                    Log.i("marker", "unexpected JSON exception", e);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("marker", "unexpected JSON exception", error);

            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                headers.put("X-Booked-SessionToken",sesToken );
                headers.put("X-Booked-UserId", uID);
                return headers;
            }
        };





        rq.add(postReq);

    }


    public static boolean getRandomBoolean() {
        return Math.random() < 0.5;

    }


   /* public void filterClick(View view) {
        filterButton.setBackgroundColor(Color.RED);

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.filterdialog);
        dialog.setTitle("Title...");
        final Spinner locSpin = (Spinner) findViewById(R.id.locationSpinner);
        // set the custom dialog components - text, image and button



        addLocationsToSpinner();
        TextView text = (TextView) dialog.findViewById(R.id.text);
        text.setText("Android custom dialog example!");


        Log.i("marker", "before ok");
        Button okButton = (Button) dialog.findViewById(R.id.ok);
        // if button is clicked, close the custom dialog

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int userChoice =locSpin.getSelectedItemPosition();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("locChoice",userChoice);
                editor.apply();
                dialog.dismiss();
            }
        });

        Log.i("marker", "before cancel");
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel);
        // if button is clicked, close the custom dialog
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });


        dialog.show();
        Log.i("marker", "showed");
    }




    public void findClick() {
        // Do something in response to button click
    }

    public void addLocationsToSpinner() {

        Spinner locSpin = (Spinner) findViewById(R.id.locationSpinner);
        ArrayList<String> locArr = new ArrayList<String>();
        Set<String> uniqueLoc = new HashSet<String>(locList);
        locArr.addAll(uniqueLoc);
        Log.i("marker",Integer.toString(locArr.size()));


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locArr);
        Log.i("marker","got here");
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Log.i("marker","or here");
        locSpin.setAdapter(dataAdapter);
        Log.i("marker","maybe here");
    }
*/













}






