package roombooking.fyp.fyproomviewfinal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    String responseString = "default";
    public TextView t1;
    public Button logInButton;

    public EditText passwordEntry;
    public EditText userEntry;
    boolean isAuth =  false;

    public static final String prefsFilename = "PrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //t1 = (TextView) findViewById(R.id.textView4);
        logInButton = (Button) findViewById(R.id.logInButton);
        passwordEntry  = (EditText)findViewById(R.id.passwordEntry);
        userEntry  = (EditText)findViewById(R.id.userEntry);


    }

    public void sendMessage(View view) {




        final Gson gson = new Gson();

        try {
            // Do something in response to button click
            RequestQueue rq = Volley.newRequestQueue(this);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("username", userEntry.getText());
            jsonBody.put("password", passwordEntry.getText());
            final String requestBody = jsonBody.toString();
           logInButton.setText("Processing");
            StringRequest postReq = new StringRequest(Request.Method.POST, "https://wbroombooking.brunel.ac.uk/CEDPSRoomBooking/booked/Web/Services/index.php/Authentication/Authenticate", new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    String result = response.split("exception")[0];
                 // AuthResponse responseObj = parseJSON(response);

                    try {
                        JSONObject jresponse = new JSONObject(response);
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                       // SharedPreferences.Editor editor = getSharedPreferences(prefsFilename, MODE_PRIVATE).edit();
                        SharedPreferences.Editor editor = preferences.edit();



                        isAuth = jresponse.getBoolean("isAuthenticated");
                        editor.putString("sessionToken", jresponse.getString("sessionToken"));
                         editor.putInt("userId", jresponse.getInt("userId"));
                        editor.apply();
                    } catch (JSONException e) {
                        Log.e("MYAPP", "unexpected JSON exception", e);
                    }


//result.contains("\"isAuthenticated\":true")
//responseObj.getIsAuthenticated() == true
                    if (isAuth){


                        logInButton.setText("Success");
                        startActivity(new Intent(MainActivity.this, BookingView.class));


                    }
                    else
                    {
                        logInButton.setText("Failed");
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("Error [" + error + "]");

                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {

                        // can get more details such as response.headers
                    }
                    return super.parseNetworkResponse(response);
                }


                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();


                    return params;
                }

            };

            rq.add(postReq);
        } catch (JSONException e) {
            e.printStackTrace();

        }


    }


    public static AuthResponse parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        AuthResponse AuthResponse = gson.fromJson(response, AuthResponse.class);
        return AuthResponse;
    }
}



