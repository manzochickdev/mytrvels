package kr.changhan.mytravels.discovery;

import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kr.changhan.mytravels.R;

public class DiscoveryActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng defaultLatlng;
    private static final int DEFAULT_ZOOM = 13;
    DiscoveryAdapter discoveryAdapter;
    GeoDataClient mGeoDataClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);

        mGeoDataClient = Places.getGeoDataClient(DiscoveryActivity.this);

        double lat = getIntent().getDoubleExtra("lat", 0);
        double lng = getIntent().getDoubleExtra("lng", 0);
        defaultLatlng = new LatLng(lat, lng);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        RecyclerView recyclerView = findViewById(R.id.place_rv);
        discoveryAdapter = new DiscoveryAdapter(DiscoveryActivity.this);
        recyclerView.setAdapter(discoveryAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(DiscoveryActivity.this, RecyclerView.HORIZONTAL, false));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatlng, DEFAULT_ZOOM));
        getNearbyPlace(defaultLatlng, "restaurant");
    }

    private void getNearbyPlace(LatLng defaultLatlng, String type) {
        String querry = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                + defaultLatlng.latitude + ","
                + defaultLatlng.longitude + "&radius=500&type="
                + type + "&key=AIzaSyBecR-RlTBLJi1_n-7Mr_Ydlhy7WzDfdLo&results=5";
        new GetData().execute(querry);

    }

    protected class GetData extends AsyncTask<String, Void, ArrayList<Discovery>> {
        ArrayList<Discovery> discoveries;

        @Override
        protected ArrayList<Discovery> doInBackground(String... strings) {
            try {
                discoveries = new ArrayList<>();
                URL url = new URL(strings[0]);
                URLConnection urlConnection = url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                String result = "";
                int byteCharacter;

                while ((byteCharacter = inputStream.read()) != -1) {
                    result += (char) byteCharacter;
                }

                JSONObject object = new JSONObject(result);
                JSONArray jsonArray = object.getJSONArray("results");
                int length;
                if (jsonArray.length() > 10) {
                    length = 10;
                } else length = jsonArray.length();
                for (int i = 0; i < length; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    double lat = jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                    double lng = jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                    String placeId = jsonObject.getString("place_id");
                    String name = new String(jsonObject.getString("name").getBytes("ISO-8859-1"), "UTF-8");
                    String vinicity = new String(jsonObject.getString("vicinity").getBytes("ISO-8859-1"), "UTF-8");
                    final Discovery discovery = new Discovery(lat, lng, placeId, vinicity, name);
                    discoveries.add(discovery);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return discoveries;
        }

        @Override
        protected void onPostExecute(ArrayList<Discovery> discoveries) {
            super.onPostExecute(discoveries);
            discoveryAdapter.setDiscoveries(discoveries);
        }
    }
}
