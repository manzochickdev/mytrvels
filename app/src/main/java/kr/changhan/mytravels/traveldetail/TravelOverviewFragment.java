package kr.changhan.mytravels.traveldetail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kr.changhan.mytravels.R;
import kr.changhan.mytravels.base.MyConst;
import kr.changhan.mytravels.entity.Travel;
import kr.changhan.mytravels.entity.TravelBaseEntity;
import kr.changhan.mytravels.entity.TravelDiary;
import kr.changhan.mytravels.entity.TravelExpense;
import kr.changhan.mytravels.entity.TravelPlan;


public class TravelOverviewFragment extends TravelDetailBaseFragment {
    MediatorLiveData<List> liveDataMerger;
    ArrayList<TravelBaseEntity> list;
    TravelOverviewAdapter travelOverviewAdapter;
    long travelId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        travelId=bundle.getLong(MyConst.REQKEY_TRAVEL_ID);
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
        travelOverviewAdapter = new TravelOverviewAdapter(getContext());
        liveDataMerger.observe(this, new Observer<List>() {
            @Override
            public void onChanged(List list) {
                travelOverviewAdapter.setList(list);
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_travel_overview, container, false);
        RecyclerView rev = view.findViewById(R.id.recyclerview);
        rev.setAdapter(travelOverviewAdapter);
        rev.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));

        FloatingActionButton floatingActionButton = view.findViewById(R.id.overview_btn);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),TravelTimelineMapActivity.class);
                intent.putExtra(MyConst.REQKEY_TRAVEL_ID,travelId);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    protected void onTravelChanged(Travel travel) {

    }

    @Override
    public void requestAddItem() {

    }
}
