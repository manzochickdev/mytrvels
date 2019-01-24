package kr.changhan.mytravels.overview;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kr.changhan.mytravels.R;
import kr.changhan.mytravels.base.BaseActivity;
import kr.changhan.mytravels.base.MyConst;
import kr.changhan.mytravels.entity.Travel;
import kr.changhan.mytravels.entity.TravelDiary;
import kr.changhan.mytravels.entity.TravelExpense;
import kr.changhan.mytravels.entity.TravelPlan;
import kr.changhan.mytravels.traveldetail.TravelDetailViewModel;
import kr.changhan.mytravels.traveldetail.TravelOverviewAdapter;
import kr.changhan.mytravels.traveldetail.TravelTimelineMapActivity;

public class OverviewActivity extends BaseActivity {

    private TextView mSubtitle;
    private Travel mTravel;
    private CollapsingToolbarLayout mToolbarLayout;
    TravelDetailViewModel mViewModel;
    MediatorLiveData<List> liveDataMerger;
    TravelOverviewAdapter travelOverviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTravel = (Travel) getIntent().getExtras().getSerializable(MyConst.REQKEY_TRAVEL);
        final long travelId = mTravel.getId();

        mToolbarLayout = findViewById(R.id.toolbar_layout);
        mSubtitle = findViewById(R.id.subtitle_txt);
        mSubtitle.setText(mTravel.getPlaceName() + "\n" + mTravel.getDateTimeText() + "~" + mTravel.getEndDtText());


        mToolbarLayout.setTitle(mTravel.getTitle());
        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        appBarLayout.setBackground(Drawable.createFromPath(Uri.parse(mTravel.getThumb()).getPath()));

        mViewModel = ViewModelProviders.of(OverviewActivity.this).get(TravelDetailViewModel.class);

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
        travelOverviewAdapter = new TravelOverviewAdapter(OverviewActivity.this);
        liveDataMerger.observe(this, new Observer<List>() {
            @Override
            public void onChanged(List list) {
                travelOverviewAdapter.setList(list);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.container);
        recyclerView.setAdapter(travelOverviewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(OverviewActivity.this, RecyclerView.VERTICAL, false));

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OverviewActivity.this, TravelTimelineMapActivity.class);
                intent.putExtra(MyConst.REQKEY_TRAVEL_ID, travelId);
                startActivity(intent);
            }
        });
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
}
