//Sean Kunz

package com.example.sean.camping;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {

    JSONObject weatherData;
    TextView txtJson;
    ProgressDialog pd;
    Button btnHit;
    EditText txtLat;
    EditText txtLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtJson = (TextView) findViewById(R.id.jsonTxt);
        btnHit = (Button) findViewById(R.id.button3);
        txtLat = (EditText) findViewById(R.id.latitudeNum);
        txtLon = (EditText) findViewById(R.id.longitudeNum);

        btnHit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getWeatherStuff(Double.parseDouble(txtLat.getText().toString()), Double.parseDouble(txtLon.getText().toString()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
        startActivityForResult(intent, 1);
    }

    public void getWeatherStuff(double latitude, double longitude) throws JSONException {
        JsonTask task = new JsonTask();
        String finalurl = "https://api.darksky.net/forecast/f5b45457cb3fe9337aa6a94c6dc568d1/" + latitude + "," + longitude;
        Log.d("Camping", finalurl);
        task.execute(finalurl);
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
                    currentWeather = weatherData.getJSONObject("currently");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    String summary = currentWeather.getString("summary");
                    txtJson.setText(summary);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
