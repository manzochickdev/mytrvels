package kr.changhan.mytravels.discovery;

import android.app.ProgressDialog;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kr.changhan.mytravels.R;
import kr.changhan.mytravels.base.BaseActivity;
import kr.changhan.mytravels.base.MyConst;
import kr.changhan.mytravels.entity.TravelBaseEntity;

public class DiscoveryActivity extends BaseActivity implements OnMapReadyCallback, DiscoveryAdapter.OnItemClickListener {

    private GoogleMap mMap;
    private LatLng defaultLatlng;
    ArrayList<Discovery> discoveries;
    private static final int DEFAULT_ZOOM = 13;
    private FrameLayout main_container;
    DiscoveryAdapter discoveryAdapter;
    GeoDataClient mGeoDataClient;
    RecyclerView recyclerView;
    String placeName;
    Geocoder geocoder;
    String nextPageToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mGeoDataClient = Places.getGeoDataClient(DiscoveryActivity.this);
        geocoder = new Geocoder(DiscoveryActivity.this, Locale.getDefault());
        main_container = findViewById(R.id.main_container);

        double lat = getIntent().getDoubleExtra("lat", 0);
        double lng = getIntent().getDoubleExtra("lng", 0);
        defaultLatlng = new LatLng(lat, lng);
        placeName = getIntent().getStringExtra(MyConst.KEY_TITLE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        recyclerView = findViewById(R.id.place_rv);
        discoveries = new ArrayList<>();
        discoveryAdapter = new DiscoveryAdapter(DiscoveryActivity.this, discoveries);
        discoveryAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(discoveryAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(DiscoveryActivity.this, RecyclerView.HORIZONTAL, false));
        final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = layoutManager.getItemCount();
                int visibleItemCount = layoutManager.getChildCount();
                int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    if (nextPageToken != null && !nextPageToken.equals("")) {
                        String q = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + placeName + "%20point%20of%20interest&language=en" +
                                "&key=AIzaSyBecR-RlTBLJi1_n-7Mr_Ydlhy7WzDfdLo" +
                                "&pagetoken=" + nextPageToken + "";

                        new GetData().execute(q);
                    }
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatlng, DEFAULT_ZOOM));
        getNearbyPlace(defaultLatlng, placeName);
    }

    private void getNearbyPlace(LatLng defaultLatlng, String name) {
        String q = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + name + "%20point%20of%20interest&language=en&" +
                "&key=AIzaSyBecR-RlTBLJi1_n-7Mr_Ydlhy7WzDfdLo";
        new GetData().execute(q);

    }

    public void addMaker(ArrayList<Discovery> discovery) {
        for (Discovery d : discovery) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(d.lat, d.lng)).title(d.name));
        }
    }

    @Override
    public void onItemClick(Discovery discovery) {
        TravelBaseEntity travelBaseEntity = new TravelBaseEntity();
        travelBaseEntity.setPlaceId(discovery.placeId);
        travelBaseEntity.setPlaceLat(discovery.lat);
        travelBaseEntity.setPlaceLng(discovery.lng);
        openGoogleMap(recyclerView, travelBaseEntity, discovery.name);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected class GetData extends AsyncTask<String, Void, ArrayList<Discovery>> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DiscoveryActivity.this);
            progressDialog.setMessage("Getting Data");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<Discovery> doInBackground(String... strings) {
            ArrayList<Discovery> list = new ArrayList<>();
            try {
                URL url = new URL(strings[0]);
                URLConnection urlConnection = url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                String result = "";
                int byteCharacter;

                while ((byteCharacter = inputStream.read()) != -1) {
                    result += (char) byteCharacter;
                }

                JSONObject object = new JSONObject(result);
                if (!object.isNull("next_page_token"))
                    nextPageToken = object.getString("next_page_token");
                else nextPageToken = "";
                JSONArray jsonArray = object.getJSONArray("results");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    double lat = jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                    double lng = jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                    String placeId = jsonObject.getString("place_id");
                    String name = new String(jsonObject.getString("name").getBytes("ISO-8859-1"), "UTF-8");
                    String vicinity = new String(jsonObject.getString("formatted_address").getBytes("ISO-8859-1"), "UTF-8");

                    final Discovery discovery = new Discovery(lat, lng, placeId, vicinity, name);
                    list.add(discovery);

                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<Discovery> d) {
            super.onPostExecute(d);
            if (d != null) {
                if (d.size() > 0) {
                    boolean isNew = false;
                    if (discoveries.size() == 0) isNew = true;

                    discoveries.addAll(d);
                    if (isNew) discoveryAdapter.notifyDataSetChanged();
                    else discoveryAdapter.notifyItemInserted(discoveries.size() - 1);

                    addMaker(discoveries);
                }
            }
            progressDialog.dismiss();
        }


    }
}
