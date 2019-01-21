package kr.changhan.mytravels.traveldetail;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import kr.changhan.mytravels.R;
import kr.changhan.mytravels.base.MyConst;
import kr.changhan.mytravels.entity.Travel;
import kr.changhan.mytravels.entity.TravelBaseEntity;


public class TravelOverviewFragment extends TravelDetailBaseFragment {
    MediatorLiveData<List<TravelBaseEntity>> liveDataMerger;
    Observer<List<TravelBaseEntity>> observer = new Observer<List<TravelBaseEntity>>() {
        @Override
        public void onChanged(List<TravelBaseEntity> travelBaseEntities) {
            liveDataMerger.setValue(travelBaseEntities);
        }
    };
    ArrayList<TravelBaseEntity> list;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        long travelId=bundle.getLong(MyConst.REQKEY_TRAVEL_ID);
        liveDataMerger = new MediatorLiveData<>();
        LiveData<List<TravelBaseEntity>> plans = mViewModel.getAllPlans(travelId);
        LiveData<List<TravelBaseEntity>> diaries = mViewModel.getAllDiaries(travelId);
        LiveData<List<TravelBaseEntity>> expense = mViewModel.getAllExpense(travelId);
        liveDataMerger.addSource(plans, observer);
        liveDataMerger.addSource(diaries,observer);
        liveDataMerger.addSource(expense,observer);
        liveDataMerger.observe(this, new Observer<List<TravelBaseEntity>>() {
            @Override
            public void onChanged(List<TravelBaseEntity> travelBaseEntities) {
                Log.d("OK", "onChanged: "+liveDataMerger.getValue().size());
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_travel_overview, container, false);
        return view;
    }

    @Override
    protected void onTravelChanged(Travel travel) {

    }

    @Override
    public void requestAddItem() {

    }
}
