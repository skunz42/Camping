//Sean Kunz
//and the bois
//yuh

package com.example.sean.camping;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    JSONObject weatherData;
    TextView txtJson;
    ProgressDialog pd;
    Button btnHit;
    EditText txtLat;
    EditText txtLon;
    TextView txtDate;

    Calendar dateCalender = Calendar.getInstance();

    private void updateLabel() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        txtDate.setText(sdf.format(dateCalender.getTime()));
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            dateCalender.set(Calendar.YEAR, year);
            dateCalender.set(Calendar.MONTH, month);
            dateCalender.set(Calendar.DAY_OF_MONTH, day);
            updateLabel();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtJson = (TextView) findViewById(R.id.jsonTxt);
        btnHit = (Button) findViewById(R.id.button3);
        txtLat = (EditText) findViewById(R.id.latitudeNum);
        txtLon = (EditText) findViewById(R.id.longitudeNum);
        txtDate = (TextView) findViewById(R.id.dateTxt);

        btnHit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sLat = txtLat.getText().toString();
                String sLong = txtLon.getText().toString();

                if (sLat.matches("") || sLong.matches("")) {
                    Toast.makeText(MainActivity.this, "Need valid input", Toast.LENGTH_LONG).show();
                }
                else {
                    double currentLat = Double.parseDouble(sLat);
                    double currentLong = Double.parseDouble(sLong);
                    if(currentLat > 90 || currentLat < -90 || currentLong > 180 || currentLong < -180) {
                        Toast.makeText(MainActivity.this, "COORDINATES OUT OF RANGE!", Toast.LENGTH_LONG).show();
                    }
                    else {
                        try {
                            getWeatherStuff(Double.parseDouble(txtLat.getText().toString()), Double.parseDouble(txtLon.getText().toString()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(MainActivity.this, date, dateCalender.get(Calendar.YEAR), dateCalender.get(Calendar.MONTH), dateCalender.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            if(resultCode == RESULT_OK) {
                txtLat.setText((Double.toString(data.getDoubleExtra("mapLat", 0.0))));
                txtLon.setText((Double.toString(data.getDoubleExtra("mapLong", 0.0))));
            }
        }
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, LocationChooser.class);
        if ((txtLat.getText().toString().matches("") && !txtLon.getText().toString().matches("")) || (txtLon.getText().toString().matches("") && !txtLat.getText().toString().matches(""))) {
            Toast.makeText(MainActivity.this, "Please input coordinates", Toast.LENGTH_LONG).show();
        }
        else if (txtLat.getText().toString().matches("") && txtLon.getText().toString().matches("")) {
            intent.putExtra("lat", 0);
            intent.putExtra("lon", 0);
            startActivityForResult(intent, 1);
        }
        else {
            intent.putExtra("lat", Double.parseDouble(txtLat.getText().toString()));
            intent.putExtra("long", Double.parseDouble(txtLon.getText().toString()));
            startActivityForResult(intent, 1);
        }
    }

    public void getWeatherStuff(double latitude, double longitude) throws JSONException {
        JsonTask task = new JsonTask();
        if(Math.abs(dateCalender.getTimeInMillis() / 1000 - System.currentTimeMillis() / 1000) >= 604800) {
            Toast.makeText(MainActivity.this, "Date is more than a week out! No weather data found!", Toast.LENGTH_LONG).show();
        }
        else {
            String finalurl = "https://api.darksky.net/forecast/f5b45457cb3fe9337aa6a94c6dc568d1/" + latitude + "," + longitude + "," + (dateCalender.getTimeInMillis() / 1000);
            Log.d("Camping", finalurl);
            task.execute(finalurl);
        }
    }

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }
            try {
                Log.d("Camping", result);
                weatherData = new JSONObject(result);
                Log.d("data", weatherData.getString("timezone"));
                JSONObject currentWeather = null;
                try {
                    currentWeather = weatherData.getJSONObject("daily");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    JSONArray dailyData = currentWeather.getJSONArray("data");
                    JSONObject dataHolder = dailyData.getJSONObject(0);
                    String summary = dataHolder.getString("summary");
                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy", Locale.US);
                    String toSet = "The weather on " + format.format(dateCalender.getTime()) + " is " + summary + "\n";
                    toSet = toSet + "Temperature Low: " + dataHolder.getString("temperatureLow") + "\n";
                    toSet = toSet + "Humidity: " + dataHolder.getString("humidity") + "\n";
                    toSet = toSet + "Chance of Precipitation: " + dataHolder.getString("precipProbability") + "\n";
                    toSet = toSet + "\n" + "We recommend:\n";
                    if ((Double.parseDouble(dataHolder.getString("temperatureLow")) > 0) && (Double.parseDouble(dataHolder.getString("temperatureLow")) < 40)) {
                        toSet = toSet + "40 Degree Sleeping Bag\n";
                        toSet = toSet + "Tent or Hammock\n";
                        toSet = toSet + "Light Jacket\n";
                    }
                    else {
                        toSet = toSet + "0 Degree Sleeping Bag\n";
                        toSet = toSet + "Tent\n";
                        toSet = toSet + "Proper Winter Clothing\n";
                    }

                    toSet = toSet + "First Aid Kit\n";
                    toSet = toSet + "Food and Water\n";
                    toSet = toSet + "Flashlight\n";
                    toSet = toSet + "Fire-starter\n";

                        txtJson.setText(toSet);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
