package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    EditText zipCode;
    Button getData;

    TextView selectedCityText;
    public static final String API_CALL_MAIN = "http://api.openweathermap.org/data/2.5/forecast?lat={lat}&lon={lon}&units=imperial&cnt=5&appid="+BuildConfig.API_KEY;
    public static final String API_CAll_LATLON = "http://api.openweathermap.org/geo/1.0/zip?zip={zip code},US&appid="+BuildConfig.API_KEY;

    ImageView mainImage;
    TextView textViewMainCurrent, textViewMainLow, textViewMainHigh;

    LinearLayout[] days = new LinearLayout[5];

    HashMap<String, Integer> images = new HashMap<String, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainImage = findViewById(R.id.imageViewMain);
        mainImage.setImageResource(R.drawable.sunny);
        zipCode = findViewById(R.id.editText_ZipCode);
        getData = findViewById(R.id.button_GetData);
        selectedCityText = findViewById(R.id.textView_CityName);
        textViewMainCurrent = findViewById(R.id.textViewMainCurrent);
        textViewMainLow = findViewById(R.id.textViewMainLow);
        textViewMainHigh = findViewById(R.id.textViewMainHigh);
        days[0] = findViewById(R.id.linearLayoutDataPoint1);
        days[1] = findViewById(R.id.linearLayoutDataPoint2);
        days[2] = findViewById(R.id.linearLayoutDataPoint3);
        days[3] = findViewById(R.id.linearLayoutDataPoint4);
        days[4] = findViewById(R.id.linearLayoutDataPoint5);
        fillImageMap();
        getData.setOnClickListener(view -> new AsyncCall().execute(zipCode.getText().toString()));

    }

    void fillImageMap() //TODO
    {
        images.put("01d", R.drawable.oned);
        images.put("02d", R.drawable.twod);
        images.put("03d", R.drawable.threed);
        images.put("04d", R.drawable.fourd);
        images.put("09d", R.drawable.nined);
        images.put("10d", R.drawable.tend);
        images.put("11d", R.drawable.elevend);
        images.put("13d", R.drawable.thirteend);
        images.put("50d", R.drawable.fiftyd);
        images.put("01n", R.drawable.onen);
        images.put("02n", R.drawable.twon);
        images.put("03n", R.drawable.threen);
        images.put("04n", R.drawable.fourn);
        images.put("09n", R.drawable.ninen);
        images.put("10n", R.drawable.tenn);
        images.put("11n", R.drawable.elevenn);
        images.put("13n", R.drawable.thirteenn);
        images.put("50n", R.drawable.fiftyn);
    }


    public class AsyncCall extends AsyncTask<String, Void, Void>
    {
        public String name;
        public JSONObject jsonFulLData;
        @Override
        protected Void doInBackground(String... strings) {
            double[] latLon = GetLatLon(strings[0]); //String passed into the task which represents the zip code
            GetData(latLon);
            return null;
        }

        public void GetData(double[] latLon)
        {
            Log.d("TEST", "LatLon: "+latLon[0]+" "+latLon[1]);

            URLConnection connection;
            URL url;
            InputStream inputstream;
            BufferedReader reader;
            String line;
            try
            {
                String call = MainActivity.API_CALL_MAIN;
                call = call.replace("{lat}", ""+latLon[0]);
                call = call.replace("{lon}", ""+latLon[1]);
                url = new URL(call);
                connection = url.openConnection();
                inputstream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputstream));

                line =  reader.lines().collect(Collectors.joining());
                jsonFulLData = new JSONObject(line);

            } catch (Exception e)
            {
                Log.d("TEST", e.toString());
                Log.d("TEST", "Failed to get data on weather at latLon");
            }
        }



        public double[] GetLatLon(String zipcode)
        {
            URLConnection connection;
            URL url;
            InputStream inputstream;
            BufferedReader reader;
            String line;
            try
            {
                String call = MainActivity.API_CAll_LATLON;
                call = call.replace("{zip code}", zipcode);
                url = new URL(call);
                connection = url.openConnection();
                inputstream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputstream));

                line =  reader.lines().collect(Collectors.joining());
                Log.d("TEST", line);

                JSONObject json = new JSONObject(line);

                name = json.getString("name");

                return new double[] {json.getDouble("lat"), json.getDouble("lon")};

            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.d("TEST", "An error occured getting latitude and longitude");
                return new double[2];
            }

        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            selectedCityText.setText("Selected City: "+name);

            try
            {
                Log.d("TEST", jsonFulLData.toString());
            }
            catch (Exception e)
            {
                Log.d("TEST", e.toString());
            }
            LoadIndex(0);
        }

        void LoadIndex(int index)
        {
            try
            {
                JSONObject listOb = (JSONObject)((JSONArray)jsonFulLData.get("list")).get(index);
                if (index == 0)
                {
                    JSONObject main = listOb.getJSONObject("main");
                    textViewMainCurrent.setText("Current: "+Math.round((double)main.get("temp"))+"°");
                    textViewMainLow.setText("Low: "+Math.round((double)main.get("temp_min"))+"°");
                    textViewMainHigh.setText("High: "+Math.round((double)main.get("temp_max"))+"°");

                    JSONObject weather = (JSONObject) listOb.getJSONArray("weather").get(0);
                    mainImage.setImageResource(images.get(weather.getString("icon")));
                }

            }
            catch (Exception e) {Log.d("TEST", e.toString());}
        }

    }

}