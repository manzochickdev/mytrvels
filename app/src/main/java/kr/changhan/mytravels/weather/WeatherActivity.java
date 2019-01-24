package kr.changhan.mytravels.weather;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kr.changhan.mytravels.R;

public class WeatherActivity extends AppCompatActivity {

    private static final String TAG = "WeatherActivity";

    private TextView tvNamePlace;
    private TextView tvTemperatureMax;
    private TextView tvTemperatureMin;
    private TextView tvMain;
    private TextView tvDate;
    private ImageView imvBack;
    private ImageView imvCloud;
    private RecyclerView rvWeather;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        mapping();

        imvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final double lat = getIntent().getDoubleExtra("lat", 0);
        final double lng = getIntent().getDoubleExtra("lng", 0);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null) {
            new DoGetDate(Config.ConfigApi + lat + Config.ConfigLon + lng + Config.ConfigApikey).execute();

        } else {
            Toast.makeText(this, "Bạn cần kết nối mạng", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void mapping() {
        tvNamePlace = findViewById(R.id.tvNamePlace);
        tvTemperatureMax = findViewById(R.id.tvTemperatureMax);
        tvTemperatureMin = findViewById(R.id.tvTemperatureMin);
        tvMain = findViewById(R.id.tvMain);
        tvDate = findViewById(R.id.tvDate);
        imvBack = findViewById(R.id.imvBack);
        imvCloud = findViewById(R.id.imvCloud);
        rvWeather = findViewById(R.id.rvWeather);
    }

    class DoGetDate extends AsyncTask<Void, Void, Void> {

        String urlLinks;
        String result;
        ProgressDialog progressDialog;
        String name;
        double tempMax, tempMaxFirst;
        double tempMin, tempMinFirst;
        String _main, _mainFirst;
        String description, descriptionFirst;
        ArrayList<Weather> arrayList;
        ListWeatherAdapter adapter;
        int dt, dtFirst;

        public DoGetDate(String urlLinks) {
            this.urlLinks = urlLinks;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(WeatherActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                URL url = new URL(urlLinks);
                URLConnection conn = url.openConnection();
                InputStream is = conn.getInputStream();

                result = "";
                int byteCharacter;
                while ((byteCharacter = is.read()) != -1) {
                    result += (char) byteCharacter;
                }

                Log.d(TAG, "api: " + urlLinks);

                JSONObject jsonObject = new JSONObject(result);
                JSONObject city = jsonObject.getJSONObject("city");
                name = city.getString("name");

                arrayList = new ArrayList<>();

                JSONArray list = jsonObject.getJSONArray("list");
                WeatherToday(list);
                for (int i = 0; i < list.length(); i++) {
                    if (i % 8 == 0) {
                        JSONObject listObject = list.getJSONObject(i);
                        dt = listObject.getInt("dt");

                        JSONObject main = listObject.getJSONObject("main");
                        tempMax = main.getDouble("temp_max");
                        tempMin = main.getDouble("temp_min");

                        JSONArray weather = listObject.getJSONArray("weather");
                        for (int j = 0; j < weather.length(); j++) {
                            JSONObject object = weather.getJSONObject(j);
                            _main = object.getString("main");
                            description = object.getString("description");
                        }

                        Weather obWeather = new Weather(dt, tempMin, tempMax, _main, description);
                        arrayList.add(obWeather);
                    }
                }

                adapter = new ListWeatherAdapter(arrayList);

                Log.d(TAG, "GetWeather: " + arrayList.size());

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        private void WeatherToday(JSONArray list) throws JSONException {
            JSONObject firstObject = list.getJSONObject(0);
            dtFirst = firstObject.getInt("dt");

            JSONObject mainFirst = firstObject.getJSONObject("main");
            tempMinFirst = mainFirst.getDouble("temp_min");
            tempMaxFirst = mainFirst.getDouble("temp_max");

            JSONArray weatherFirst = firstObject.getJSONArray("weather");
            for (int j = 0; j < weatherFirst.length(); j++) {
                JSONObject object = weatherFirst.getJSONObject(j);
                _mainFirst = object.getString("main");
                descriptionFirst = object.getString("description");
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressDialog.dismiss();

            int min = (int) (tempMinFirst - 273.15);
            int max = (int) (tempMaxFirst - 273.15);

            tvNamePlace.setText(name);
            tvTemperatureMax.setText(String.valueOf(max));
            tvTemperatureMin.setText(String.valueOf(min));
            tvDate.setText(R.string.Today);
            tvMain.setText(_mainFirst);

            if (descriptionFirst.equals("broken clouds") || descriptionFirst.equals("overcast clouds")) {
                imvCloud.setImageResource(R.drawable.ic_emoji_u2601);
            } else if (descriptionFirst.equals("light rain")) {
                imvCloud.setImageResource(R.drawable.ic_emoji_u26c8);
            } else if (descriptionFirst.equals("few clouds")) {
                imvCloud.setImageResource(R.drawable.ic_emoji_u1f326);
            } else if (descriptionFirst.equals("clear sky")) {
                imvCloud.setImageResource(R.drawable.ic_emoji_u1f324);
            } else {
                imvCloud.setImageResource(R.drawable.ic_emoji_u2601);
            }

            LinearLayoutManager manager = new LinearLayoutManager(getBaseContext());
            rvWeather.setLayoutManager(manager);
            rvWeather.setAdapter(adapter);
        }
    }
}
