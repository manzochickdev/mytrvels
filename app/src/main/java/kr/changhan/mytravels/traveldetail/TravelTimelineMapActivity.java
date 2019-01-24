package kr.changhan.mytravels.traveldetail;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kr.changhan.mytravels.R;
import kr.changhan.mytravels.base.MyConst;
import kr.changhan.mytravels.entity.TravelBaseEntity;
import kr.changhan.mytravels.entity.TravelDiary;
import kr.changhan.mytravels.entity.TravelExpense;
import kr.changhan.mytravels.entity.TravelPlan;
import kr.changhan.mytravels.utils.GetMarker;

public class TravelTimelineMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private RecyclerView itemRv;
    private TextView shareTv;
    TravelDetailViewModel mViewModel;
    MediatorLiveData<List> liveDataMerger;
    GoogleMap googleMap;
    LatLng baseLatlng;
    private static final int DEFAULT_ZOOM = 13;
    List travelItem;
    TravelTimelineMapAdapter travelTimelineMapAdapter;
    FrameLayout frameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_timeline_map);

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        long travelId = getIntent().getLongExtra(MyConst.REQKEY_TRAVEL_ID,0);

        mViewModel = ViewModelProviders.of(TravelTimelineMapActivity.this).get(TravelDetailViewModel.class);
        travelItem = new ArrayList();


        frameLayout = findViewById(R.id.main_container);
        shareTv = findViewById(R.id.share_iv);
        itemRv = findViewById(R.id.item_rv);
        travelTimelineMapAdapter = new TravelTimelineMapAdapter(TravelTimelineMapActivity.this);
        itemRv.setAdapter(travelTimelineMapAdapter);
        itemRv.setLayoutManager(new LinearLayoutManager(TravelTimelineMapActivity.this,RecyclerView.HORIZONTAL,false));

        liveDataMerger = new MediatorLiveData<>();
        LiveData<List<TravelPlan>> plans = mViewModel.getAllPlans(travelId);
        liveDataMerger.addSource(plans, new Observer<List<TravelPlan>>() {
            @Override
            public void onChanged(List<TravelPlan> travelPlans) {
                liveDataMerger.setValue(travelPlans);
            }
        });

        LiveData<List<TravelDiary>> diaries = mViewModel.getAllDiaries(travelId);
        liveDataMerger.addSource(diaries, new Observer<List<TravelDiary>>() {
            @Override
            public void onChanged(List<TravelDiary> travelDiaries) {
                liveDataMerger.setValue(travelDiaries);
            }
        });
        LiveData<List<TravelExpense>> expense = mViewModel.getAllExpense(travelId);
        liveDataMerger.addSource(expense, new Observer<List<TravelExpense>>() {
            @Override
            public void onChanged(List<TravelExpense> travelExpenses) {
                liveDataMerger.setValue(travelExpenses);
            }
        });
        liveDataMerger.observe(this, new Observer<List>() {
            @Override
            public void onChanged(List list) {
                Log.d("OK", "onChanged: "+list.size());
                //travelOverviewAdapter.setList(list);
                travelItem.addAll(list);
                plotMarker(travelItem);
                if (list==null) return;
                if (baseLatlng!=null) return;
                for (Object t : list){
                    if (!(((TravelBaseEntity)t).getPlaceId()==null)){
                        baseLatlng = new LatLng(((TravelBaseEntity)t).getPlaceLat(),((TravelBaseEntity)t).getPlaceLng());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(baseLatlng,DEFAULT_ZOOM));
                        break;
                    }
            }}
        });


    }


    private void plotMarker(List t) {
        if (t == null) return;
        for (Object o : t) {
            TravelBaseEntity travel = (TravelBaseEntity)o;
            if (travel.getPlaceId()!=null){
                LatLng latLng = new LatLng(travel.getPlaceLat(),travel.getPlaceLng());
                googleMap.addMarker(new MarkerOptions().position(latLng).title(travel.getPlaceAddr())
                        .icon(BitmapDescriptorFactory.fromBitmap(GetMarker.from(o, TravelTimelineMapActivity.this).getMarker())));
            }
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setOnMarkerClickListener(this);
        Log.d("OK", "onMapReady: ");
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        ArrayList list = new ArrayList();
        for (Object o : travelItem ){
            TravelBaseEntity travel = (TravelBaseEntity)o;
            if  (travel.getPlaceAddr()!=null)
            {
                if (travel.getPlaceAddr().equals(marker.getTitle())){
                    list.add(o);
                }
            }
        }
        travelTimelineMapAdapter.setTravelItem(list);

        return true;
    }
}
