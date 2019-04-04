package roombooking.fyp.fyproomviewfinal;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.sql.*;

public class BookingView extends AppCompatActivity {

    TextView countView;
    private RecyclerView mRecyclerView;
    Button filterButton, findButton, refreshButton;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<String> nameList = new ArrayList<>(); //names
    ArrayList<String> locList = new ArrayList<>(); //locations
    ArrayList<String> availList = new ArrayList<>(); //are the rooms available?
    ArrayList<String> maxPeopleList = new ArrayList<>(); //max occupancy?
    ArrayList<String> availableAtList = new ArrayList<>(); //when are they next available?
    ArrayList<String> liveAvail = new ArrayList<>(); //are they available right now?
    ArrayList<exampleItem> roomList = new ArrayList<>(); //conjugated room details
    ArrayList<String> selectedFilters = new ArrayList<>();  //user selected filters
    ArrayList<String> defaultFilters = new ArrayList<>(); //available rooms only
    String uID, sesToken; //userID and Session Token from Booked
    private Handler handler = new Handler();
    private Random rand = new Random();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        defaultFilters.add("Available"); //initialise default filters
        selectedFilters.add("Available"); //initialise selected filters
        selectedFilters.add("Booked");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_view);
        getRooms();

        countView = (TextView) findViewById(R.id.countTimer);





        refreshButton = (Button) findViewById(R.id.refresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshList(); //refresh on click on refresh button
            }
        });

        findButton = (Button) findViewById(R.id.findRoomButton);
        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFindRoom(); //find room on button click
            }
        });

        filterButton = (Button) findViewById(R.id.filterButton);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFilters(); //display filters on button click
            }
        });
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            //empty all lists
            roomList.clear();
            nameList.clear();
            locList.clear();
            availableAtList.clear();
            availList.clear();
            liveAvail.clear();

            //repopulate all lists
            getRooms();


        }
    };
    public void SQLGet() {

        Random rand = new Random();

        rand.setSeed(System.currentTimeMillis()); //seed random based on current time

            liveAvail.clear(); //reset live list
           try {

               RequestQueue rq = Volley.newRequestQueue(this);

               //send get request
               //a random 'unused' header needs to be added to ensure that fresh data is retrieved and not cached data
               StringRequest postReq = new StringRequest(Request.Method.GET, "http://3.18.62.161/Output.php?unused=" + rand.nextInt(), new Response.Listener<String>() {

                   @Override
                   public void onResponse(String response) {
                       //remove exception error that arises as a result of a problem with the date/time on the server
                       String result = response.split("exception 'ErrorException'")[0];

                           try{
                               JSONArray jsonArr = new JSONArray(result);  //convert string result to JSONArray
                               for (int i = 0; i < jsonArr.length(); i++)
                               {
                                   JSONObject jsonObj = jsonArr.getJSONObject(i); //convert each element into the array into a JSONObject
                                   liveAvail.add(jsonObj.getString("avail")); //add livedata to list
                               }
                               populateRecyclerView(); //refresh recyclerview

                           }
                           catch (JSONException e){
                               Log.e("mark", "json error",  e); //print error
                           }
                   }
               }, new Response.ErrorListener() {
                   @Override
                   public void onErrorResponse(VolleyError error) {
                   }
               }) {
               };
               rq.add(postReq);
           }
           catch(Exception e) {
           }
    }

    public void getRooms(){

        mRecyclerView = findViewById(R.id.recyclerView);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()); //open stored preferences
        final String sessionToken = preferences.getString("sessionToken", ""); //retrieve sessiontoken from stored preferences
        final int userId = preferences.getInt("userId",0); //retrieve ID from preferences
        final String userIdStr = String.valueOf(userId); //convert id to string
        uID = userIdStr;
        sesToken = sessionToken;

        RequestQueue rq = Volley.newRequestQueue(this);
            //send get request to Booked API
        StringRequest postReq = new StringRequest(Request.Method.GET, "https://wbroombooking.brunel.ac.uk/CEDPSRoomBooking/booked/Web/Services/index.php/Resources/", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //remove exception error that arises as a result of a problem with the date/time on the server
                String result = response.split("exception 'ErrorException'")[0];
                try {

                    JSONObject jsnobject = new JSONObject(result);
                    JSONArray jsonArray = jsnobject.getJSONArray("resources");

                    for(int i=0; i<jsonArray.length(); i++){

                        JSONObject obj = jsonArray.getJSONObject(i);

                        String name = obj.getString("name"); //find name from JSONArray
                        String location = obj.getString("location"); //find location from JSONArray
                        String maxPeople = obj.getString("maxParticipants");  //find max occupancy from JSON Array

                        String editedName = "";
                        editedName = name.trim(); //remove unneeded characters from name


                        int spacePos = name.indexOf(" ");
                        //remove room location from room name
                        if (spacePos > 0) {
                            editedName = name.substring(0, spacePos);
                        }



                        nameList.add(editedName); //add room name to name list
                        locList.add(location);//add room location to location list
                        maxPeopleList.add(maxPeople);//add max occupancy to list

                    }
                    //call method to retrieve availablity data
                    getAvailability(sessionToken,userIdStr);
                }
                catch(JSONException e)
                {
                    Log.e("marker", "unexpected JSON exception", e); //print error
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("marker", "unexpected JSON exception", error);//print error
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Booked-SessionToken",sessionToken ); //add sessiontoken to call header
                headers.put("X-Booked-UserId", userIdStr); //add userid to call header
                return headers;
            }
        };
        rq.add(postReq);
    }

    public void getAvailability(final String Token, final String ID){
        RequestQueue rq = Volley.newRequestQueue(this);

        //call availablity get request
        StringRequest postReq = new StringRequest(Request.Method.GET, "https://wbroombooking.brunel.ac.uk/CEDPSRoomBooking/booked/Web/Services/index.php/Resources/Availability", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //remove exception error that arises as a result of a problem with the date/time on the server
                String result = response.split("exception 'ErrorException'")[0];

                try {
                    JSONObject jsnobject = new JSONObject(result);
                    JSONArray availArray = jsnobject.getJSONArray("resources");

                    JSONArray availArray2 = availArray.getJSONArray(0);


                    for(int i=0;i<availArray2.length();i++){
                        JSONObject json = availArray2.getJSONObject(i);

                        String available = json.getString("available"); //find current booking status

                        String findAvailableAt = json.getString("availableAt"); //find next availablity time


                        String editedFindAvailableAt = findAvailableAt.substring(findAvailableAt.indexOf("T")+1); //shorten time to more understandable format
                        editedFindAvailableAt.trim(); //trim unwanted blank space

                        int spacePos = editedFindAvailableAt.indexOf("+");
                        if (spacePos > 0) {
                            findAvailableAt = editedFindAvailableAt.substring(0, spacePos);
                        }

                        availableAtList.add(findAvailableAt); //add next available time to list
                        availList.add(available); //add current booking to list
                    }
                    roomList.clear();

                }
                catch(JSONException e)
                {
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
                //add session token and userid headers
                headers.put("X-Booked-SessionToken",sesToken );
                headers.put("X-Booked-UserId", uID);
                return headers;
            }
        };
        //call method to get live data from MYSQL database
        SQLGet();
        rq.add(postReq);

    }

    public Set<String> getUniqueMaxOccupancy(){

        //returns a set of unique values found in max occupancy list for use a filter
        Set<String> uniqueMaxOcc= new HashSet<String>(maxPeopleList);
        return uniqueMaxOcc;
    }

    public Set<String> getUniqueLocations(){
        //returns a set of unique locations found in location list for use a filter
        Set<String> uniqueLoc= new HashSet<String>(locList);
        return uniqueLoc;
    }

    public ArrayList<String> populateFilters(){
        //add all unique values to filters
        ArrayList<String> filters = new ArrayList<>();
        filters.add("Available");
        filters.add("Booked");
        filters.addAll(getUniqueLocations());
        filters.addAll(getUniqueMaxOccupancy());
        return filters;
    }

    public void openFindRoom() {
        //create dialog box for find room button
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Room Found");

        exampleItem chosenRoom =   roomList.get(rand.nextInt(roomList.size()));

        builder.setMessage(chosenRoom.getName() + "\n" + chosenRoom.getLocation());

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void refreshList() {
        handler.postDelayed(runnable, 0);
    }

    public void openFilters() {

        //create dialog box for filter button
        selectedFilters.clear();
        ArrayList<String> filters = new ArrayList<>();
        filters = populateFilters();

        String[] filtersArray = new String[filters.size()];
        filtersArray = filters.toArray(filtersArray);

        final String[] filtersArray2 = filtersArray;
        int  length = filters.size();


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose filters");

        final boolean[] checkedItems = new boolean[length];

        builder.setMultiChoiceItems(filtersArray, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//checks all user selected filters against previously selected filters
            if (isChecked && !selectedFilters.contains(filtersArray2[which])) {
                selectedFilters.add(filtersArray2[which]);
            } else if (selectedFilters.contains(filtersArray2[which])) {
                selectedFilters.remove(filtersArray2[which]);
            }
        }
    });

    builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            //on confirmation, empty room list, and repopulate the recycler using filters
            roomList.clear();
           populateRecyclerView();
            dialog.dismiss();

        }
    });

    AlertDialog dialog = builder.create();
    dialog.show();
}


public void populateRecyclerView(){

    //normalise values
    Collections.replaceAll(availList, "true", "Available");
    Collections.replaceAll(availList, "false", "Booked");
    Collections.replaceAll(liveAvail, "0", "Currently Empty");
    Collections.replaceAll(liveAvail, "1", "Currently In Use");

   //if no filters are selected
    if (selectedFilters.size() == 0){
        for (int i = 0; i < availList.size(); i++) {
            roomList.add(new exampleItem(nameList.get(i), locList.get(i), availList.get(i), maxPeopleList.get(i), availableAtList.get(i), liveAvail.get(i)));
        }
    }
        else{
            for (int i = 0; i < availList.size(); i++) {
                if (selectedFilters.contains(locList.get(i)) || selectedFilters.contains(maxPeopleList.get(i))|| selectedFilters.contains(availList.get(i))){
                    roomList.add(new exampleItem(nameList.get(i), locList.get(i), availList.get(i), maxPeopleList.get(i), availableAtList.get(i), liveAvail.get(i)));
        }
    }
}

    mRecyclerView = findViewById(R.id.recyclerView);
    mRecyclerView.setHasFixedSize(true);
    mLayoutManager = new LinearLayoutManager(BookingView.this);
    mAdapter = new recyclerAdapter(roomList);

    mRecyclerView.setLayoutManager(mLayoutManager);
    mRecyclerView.setAdapter(mAdapter);


}

}






