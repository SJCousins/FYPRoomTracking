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

    TextView t1, countView;
    private RecyclerView mRecyclerView;
    Button filterButton, findButton, refreshButton;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<String> nameList = new ArrayList<>();
    ArrayList<String> locList = new ArrayList<>();
    ArrayList<String> availList = new ArrayList<>();
    ArrayList<String> maxPeopleList = new ArrayList<>();
    ArrayList<String> availableAtList = new ArrayList<>();
    ArrayList<String> liveAvail = new ArrayList<>();
    String[][] arrStr = new String[3][4];
    ArrayList<exampleItem> roomList = new ArrayList<>();
    ArrayList<String> selectedFilters = new ArrayList<>();
    ArrayList<String> defaultFilters = new ArrayList<>();
    String uID, sesToken;
    private Handler handler = new Handler();
    private Random rand = new Random();
private int count = 30;
private int delay = 30000;

    static String locations[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {

defaultFilters.add("Available");
selectedFilters.add("Available");
selectedFilters.add("Booked");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_view);
        getRooms();

        countView = (TextView) findViewById(R.id.countTimer);





        refreshButton = (Button) findViewById(R.id.refresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshList();
            }
        });

        findButton = (Button) findViewById(R.id.findRoomButton);
        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFindRoom();
            }
        });

        filterButton = (Button) findViewById(R.id.filterButton);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFilters();
            }
        });




    }




    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            roomList.clear();

            nameList.clear();
            locList.clear();
            availableAtList.clear();
            availList.clear();
            liveAvail.clear();


           getRooms();
            count = count - 1;

            //handler.postDelayed(this, delay);
        }
    };
    public void SQLGet() {

        Random rand = new Random();

        rand.setSeed(System.currentTimeMillis());




liveAvail.clear();
           try {

               RequestQueue rq = Volley.newRequestQueue(this);

               StringRequest postReq = new StringRequest(Request.Method.GET, "http://3.18.62.161/Output.php?unused=" + rand.nextInt(), new Response.Listener<String>() {






                   @Override
                   public void onResponse(String response) {
                       String result = response.split("exception 'ErrorException'")[0];




                           try{
                               JSONArray jsonArr = new JSONArray(result);
                               for (int i = 0; i < jsonArr.length(); i++)
                               {
                                   JSONObject jsonObj = jsonArr.getJSONObject(i);
                                   //Log.e("loc", jsonObj.getString(("avail")));
                                   liveAvail.add(jsonObj.getString("avail"));

                               }

                               populateRecyclerView();

                           }
                           catch (JSONException e){
                               Log.e("mark", "json error",  e);
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

               //URL url = new URL("http://3.18.62.161/Output.php")

           catch(Exception e) {

           }


    }





    public void getRooms(){

        mRecyclerView = findViewById(R.id.recyclerView);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String sessionToken = preferences.getString("sessionToken", "");
        final int userId = preferences.getInt("userId",0);
        final String userIdStr = String.valueOf(userId);
        uID = userIdStr;
        sesToken = sessionToken;




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

                try {
                    JSONObject jsnobject = new JSONObject(result);
                    JSONArray availArray = jsnobject.getJSONArray("resources");

                    JSONArray availArray2 = availArray.getJSONArray(0);


                    for(int i=0;i<availArray2.length();i++){
                        JSONObject json = availArray2.getJSONObject(i);

                        String available = json.getString("available");

                        String findAvailableAt = json.getString("availableAt");


                        String editedFindAvailableAt = findAvailableAt.substring(findAvailableAt.indexOf("T")+1);
                        editedFindAvailableAt.trim();

                        int spacePos = editedFindAvailableAt.indexOf("+");
                        if (spacePos > 0) {
                            findAvailableAt = editedFindAvailableAt.substring(0, spacePos);
                        }


                        availableAtList.add(findAvailableAt);

                        availList.add(available);
                    }
                    roomList.clear();



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
                headers.put("X-Booked-SessionToken",sesToken );
                headers.put("X-Booked-UserId", uID);
                return headers;
            }
        };



SQLGet();

        rq.add(postReq);

    }

    public Set<String> getUniqueMaxOccupancy(){
        Set<String> uniqueMaxOcc= new HashSet<String>(maxPeopleList);

        return uniqueMaxOcc;
    }

    public Set<String> getUniqueLocations(){
        Set<String> uniqueLoc= new HashSet<String>(locList);

        return uniqueLoc;
    }

    public ArrayList<String> populateFilters(){

        ArrayList<String> filters = new ArrayList<>();

        filters.add("Available");
        filters.add("Booked");
        filters.addAll(getUniqueLocations());
        filters.addAll(getUniqueMaxOccupancy());
        return filters;
    }

    public void openFindRoom() {
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
            roomList.clear();
           populateRecyclerView();
            dialog.dismiss();

        }
    });

    AlertDialog dialog = builder.create();
    dialog.show();
}


public void populateRecyclerView(){


    Collections.replaceAll(availList, "true", "Available");
    Collections.replaceAll(availList, "false", "Booked");
    Collections.replaceAll(liveAvail, "0", "Currently Empty");
    Collections.replaceAll(liveAvail, "1", "Currently In Use");

    Log.e("loc",Integer.toString(selectedFilters.size()));
    Log.e("loc", selectedFilters.toString());
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






