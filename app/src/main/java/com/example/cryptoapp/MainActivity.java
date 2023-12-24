package com.example.cryptoapp;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String API_KEY = "2b11ea0e59c3f7a88fe58e7431cd50e3";
    private static final String LIST_URL = "http://api.coinlayer.com/list?access_key=" + API_KEY;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView currencyListView;
    private CurrencyAdapter currencyAdapter;
    private RequestQueue requestQueue;
    private TextView countDownTimer;

    private int refreshTime, timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countDownTimer = findViewById(R.id.countdown);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        currencyListView = findViewById(R.id.currencyListView);
        currencyAdapter = new CurrencyAdapter(this, new ArrayList<>());
        currencyListView.setAdapter(currencyAdapter);

        requestQueue = Volley.newRequestQueue(this);

        refreshTime = 3*60*1000; // 3 minutes delayTime
        timer = 0;
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MainActivity.this.fetchData();
                timer = 0;
            }
        });
        fetchData(); // Initial data load

        // Schedule auto-refresh every 3 minutes
        final Handler handler = new Handler();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                //code to execute
                fetchData();
                handler.postDelayed(this, refreshTime);
            }
        };
        handler.post(run);


        final Handler timerHandler = new Handler();
        Runnable run2 = new Runnable() {
            @Override
            public void run() {

                int minutes = (int) (timer / 1000) / 60;
                int seconds = (int) (timer / 1000) % 60;

                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

                countDownTimer.setText("Last Refreshed "+ timeLeftFormatted+ " seconds before");
                timerHandler.postDelayed(this, 10*1000);
                timer+=10000;
            }
        };
        timerHandler.post(run2);

    }

    private void fetchData() {
        swipeRefreshLayout.setRefreshing(true);
        timer = 0;
        // Fetch data from CoinLayer API
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, LIST_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Process the response and update the UI
                        updateUI(response);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors, display a message, and stop refreshing
                        handleErrorResponse(error);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }

    private void handleErrorResponse(VolleyError error) {
        Log.e("TAG", "handleErrorResponse:"+error);
        if (error.networkResponse != null) {
            try {
                // Parse the error response JSON
                String errorResponse = new String(error.networkResponse.data, "utf-8");
                JSONObject errorJson = new JSONObject(errorResponse);

                // Check if the error contains an error code
                if (errorJson.has("error")) {
                    JSONObject errorObject = errorJson.getJSONObject("error");
                    int errorCode = errorObject.optInt("code", -1);
                    showToast("Error Code: " + errorCode);
                } else {
                    showToast("Error: Unable to fetch data1");
                }

            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
                showToast("Error: Unable to fetch data2");
            }
        } else {
            showToast("Error: Unable to fetch data3");
        }
    }

    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateUI(JSONObject response) {
        List<Currency> currencyList = new ArrayList<>();

        try {
            // Check if the response contains "crypto" key
            if (response.has("crypto")) {
                JSONObject cryptoObject = response.getJSONObject("crypto");

                // Iterate through the cryptoObject to get cryptocurrency details
                Iterator<String> iterator = cryptoObject.keys();
                while (iterator.hasNext()) {
                    String currencySymbol = iterator.next();
                    JSONObject currencyObject = cryptoObject.getJSONObject(currencySymbol);

                    String currencyName = currencyObject.getString("name_full");
                    double exchangeRate = currencyObject.optDouble("max_supply", 0.0);
                    String iconUrl = getIconUrl(response, currencySymbol);

                    Currency currency = new Currency(currencyName, iconUrl, exchangeRate);
                    currencyList.add(currency);
                }
            }

            // Update the adapter with the new data
            currencyAdapter.clear();
            currencyAdapter.addAll(currencyList);
            currencyAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private String getIconUrl(JSONObject response, String currencySymbol) {
        // Fetch missing info for icon from the provided JSON structure
        try {
            JSONObject cryptoObject = response.getJSONObject("crypto");

            // Check if the response contains the specific currency symbol
            if (cryptoObject.has(currencySymbol)) {
                JSONObject currencyObject = cryptoObject.getJSONObject(currencySymbol);
                return currencyObject.optString("icon_url", ""); // return  actual icon URL
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Return a placeholder URL if the icon URL is not available
        return "https://example.com/placeholder.png";
    }

}
