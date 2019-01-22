package kr.changhan.mytravels.traveldetail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kr.changhan.mytravels.R;
import kr.changhan.mytravels.base.MyConst;
import kr.changhan.mytravels.entity.Travel;
import kr.changhan.mytravels.entity.TravelBaseEntity;
import kr.changhan.mytravels.entity.TravelDiary;
import kr.changhan.mytravels.entity.TravelExpense;
import kr.changhan.mytravels.entity.TravelPlan;
import kr.changhan.mytravels.utils.GetMarker;
import kr.changhan.mytravels.utils.MyString;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TravelTimelineMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private RecyclerView itemRv;
    TravelDetailViewModel mViewModel;
    MediatorLiveData<List> liveDataMerger;
    GoogleMap googleMap;
    LatLng baseLatlng;
    private static final int DEFAULT_ZOOM = 13;
    List travelItem;
    List<List> travelList;
    HashMap<String,String> markerInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_timeline_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        long travelId = getIntent().getLongExtra(MyConst.REQKEY_TRAVEL_ID,0);
        markerInfo = new HashMap<>();
        mViewModel = ViewModelProviders.of(TravelTimelineMapActivity.this).get(TravelDetailViewModel.class);
        travelItem = new ArrayList();


        itemRv = findViewById(R.id.item_rv);
        TravelTimelineMapAdapter travelTimelineMapAdapter = new TravelTimelineMapAdapter(TravelTimelineMapActivity.this);
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

    private void plotMarker(List travelItem) {
//        for (Object o :travelItem){
//            TravelBaseEntity travel = (TravelBaseEntity)o;
//            if (markerInfo.get(travel.getPlaceId())!=null){
//                if (o instanceof TravelDiary){
//                    if (MyString.isNotEmpty(((TravelDiary)o).getThumbUri())){
//                        markerInfo.put(travel.getPlaceId(),((TravelDiary)o).getThumbUri());
//                    }
//                }
//                else if (o instanceof TravelPlan){
//                    markerInfo.put(travel.getPlaceId(),"TravelPlan");
//                }
//                else if (o instanceof TravelExpense) markerInfo.put(travel.getPlaceId(),"TravelExpense");
//            }
//        }
//
//        for ()

        for (Object o : travelItem){
            TravelBaseEntity travel = (TravelBaseEntity)o;
            if (travel.getPlaceId()!=null){
                LatLng latLng = new LatLng(travel.getPlaceLat(),travel.getPlaceLng());
                googleMap.addMarker(new MarkerOptions().position(latLng).title(travel.getPlaceAddr())
                .icon(BitmapDescriptorFactory.fromBitmap(GetMarker.from(o,TravelTimelineMapActivity.this).getMarker())));
            }
        }
    }

    private void handleTravelItem(List travelItem) {
        travelList = new ArrayList<>();
        String placeId="";
        for (Object o :travelItem){
            TravelBaseEntity travel = (TravelBaseEntity)o;

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
        return true;
    }
}
