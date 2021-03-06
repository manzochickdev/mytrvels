package kr.changhan.mytravels.traveldetail.plan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kr.changhan.mytravels.R;
import kr.changhan.mytravels.base.MyConst;
import kr.changhan.mytravels.discovery.DiscoveryActivity;
import kr.changhan.mytravels.entity.Travel;
import kr.changhan.mytravels.entity.TravelBaseEntity;
import kr.changhan.mytravels.entity.TravelPlan;
import kr.changhan.mytravels.main.TravelListItemClickListener;
import kr.changhan.mytravels.traveldetail.PlanDetailActivity;
import kr.changhan.mytravels.traveldetail.TravelDetailBaseFragment;
import kr.changhan.mytravels.utils.MyDate;
import kr.changhan.mytravels.weather.WeatherActivity;

public class PlanFragment extends TravelDetailBaseFragment implements TravelListItemClickListener {
    private static final String TAG = PlanFragment.class.getSimpleName();
    public static final int TITLE_ID = R.string.title_frag_daily_plans;
    private TravelPlanListAdapter mListAdapter;

    public PlanFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlanFragment newInstance(int sectionNumber) {
        Log.d(TAG, "newInstance: sectionNumber=" + sectionNumber);
        PlanFragment fragment = new PlanFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new TravelPlanListAdapter(getContext());
        mListAdapter.setListItemClickListener(this);
        mListAdapter.setOnRatingChangeListener(new TravelPlanListAdapter.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(int position, TravelPlan entity) {
                mViewModel.updateTravelPlan(entity);
            }
        });
        mViewModel.getTravelPlanList().observe(this, new Observer<PagedList<TravelPlan>>() {
            @Override
            public void onChanged(PagedList<TravelPlan> items) {
                mListAdapter.setHeader("");
                mListAdapter.submitList(items);
            }
        });

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_plan, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mListAdapter.setHeader("");
        recyclerView.setAdapter(mListAdapter);


        rootView.findViewById(R.id.discovery_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DiscoveryActivity.class);
                intent.putExtra(MyConst.KEY_TITLE, mViewModel.getTravel().getValue().getPlaceName());
                intent.putExtra("lat", mViewModel.getTravel().getValue().getPlaceLat());
                intent.putExtra("lng", mViewModel.getTravel().getValue().getPlaceLng());
                startActivity(intent);
            }
        });

        rootView.findViewById(R.id.weather_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WeatherActivity.class);
                intent.putExtra("lat", mViewModel.getTravel().getValue().getPlaceLat());
                intent.putExtra("lng", mViewModel.getTravel().getValue().getPlaceLng());
                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    protected void onTravelChanged(Travel travel) {
        Log.d(TAG, "onTravelChanged: travel=" + travel);
        if (travel == null) return;
        Map<String, Object> option = new HashMap<>();
        option.put(MyConst.KEY_ID, travel.getId());
        mViewModel.setTravelPlanListOption(option);
    }

    @Override
    public void requestAddItem() {
        Travel travel = mViewModel.getTravel().getValue();
        Log.d(TAG, "requestAddItem: travel=" + travel);
        if (travel == null) return;

        TravelPlan item = new TravelPlan();
        item.setTravelId(travel.getId());
        item.setDateTime(MyDate.getCurrentTime());
        Intent intent = new Intent(getContext(), PlanDetailActivity.class);
        intent.putExtra(MyConst.REQKEY_TRAVEL, item);
        intent.putExtra(MyConst.BACKGROUND, travel.getThumb());
        startActivity(intent);
    }

    @Override
    public void onListItemClick(View view, int position, TravelBaseEntity entity, boolean longClick) {
        TravelPlan item = (TravelPlan) entity;
        Log.d(TAG, "onListItemClick: item=" + item);
        Log.d(TAG, "onListItemClick: longClick=" + longClick);

        Travel travel = mViewModel.getTravel().getValue();
        Log.d(TAG, "requestAddItem: travel=" + travel);
        if (travel == null) return;

        if (!longClick) return;
        Intent intent = new Intent(getContext(), PlanDetailActivity.class);
        intent.putExtra(MyConst.REQKEY_TRAVEL, item);
        intent.putExtra(MyConst.BACKGROUND, travel.getThumb());
        startActivity(intent);
    }
}
