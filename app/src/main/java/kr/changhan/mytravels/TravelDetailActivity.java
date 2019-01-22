package kr.changhan.mytravels;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import kr.changhan.mytravels.base.BaseActivity;
import kr.changhan.mytravels.base.MyApplication;
import kr.changhan.mytravels.base.MyConst;
import kr.changhan.mytravels.entity.Travel;
import kr.changhan.mytravels.entity.TravelDiary;
import kr.changhan.mytravels.traveldetail.SectionsPagerAdapter;
import kr.changhan.mytravels.traveldetail.TravelDetailBaseFragment;
import kr.changhan.mytravels.traveldetail.TravelDetailViewModel;
import kr.changhan.mytravels.traveldetail.TravelOverviewFragment;
import kr.changhan.mytravels.utils.MyString;

public class TravelDetailActivity extends BaseActivity {
    private static final String TAG = TravelDetailActivity.class.getSimpleName();

    private SectionsPagerAdapter mViewPagerAdapter;
    private ViewPager mViewPager;
    private CollapsingToolbarLayout mToolbarLayout;
    private TextView mSubtitle;
    private AppBarLayout mAppBar;
    private final Observer<Travel> mTravelObserver = new Observer<Travel>() {
        @Override
        public void onChanged(Travel travel) {
            Log.d(TAG, "onChanged: travel=" + travel);
            if (travel == null) return;
            mToolbarLayout.setTitle(travel.getTitle());
            mSubtitle.setText(travel.getPlaceName() + "\n" + travel.getDateTimeText() + "~" + travel.getEndDtText());
            mAppBar.setBackground(Drawable.createFromPath(Uri.parse(travel.getThumb()).getPath()));
        }
    };
    private TravelDetailViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAppBar = findViewById(R.id.app_bar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mViewPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);
        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mViewPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        // Use setupWithViewPager(ViewPager) to link a TabLayout with a ViewPager.
        // The individual tabs in the TabLayout will be automatically populated
        // with the page titles from the PagerAdapter.
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TravelDetailBaseFragment fragment = (TravelDetailBaseFragment) mViewPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem());
                fragment.requestAddItem();
            }
        });

        mToolbarLayout = findViewById(R.id.toolbar_layout);
        mSubtitle = findViewById(R.id.subtitle_txt);

        long travelId = getIntent().getLongExtra(MyConst.REQKEY_TRAVEL_ID, 0);
        Log.d(TAG, "onCreate: travelId=" + travelId);
        Bundle bundle = new Bundle();
        bundle.putLong(MyConst.REQKEY_TRAVEL_ID,travelId);
        TravelOverviewFragment travelOverviewFragment = new TravelOverviewFragment();
        travelOverviewFragment.setArguments(bundle);
        //todo addfragment here
        getSupportFragmentManager().beginTransaction().replace(R.id.main_content,travelOverviewFragment,TravelOverviewFragment.class.getSimpleName())
                .addToBackStack(TravelOverviewFragment.class.getSimpleName())
                .commit();


        mViewModel = ViewModelProviders.of(this).get(TravelDetailViewModel.class);
        mViewModel.setTravelId(travelId);
        mViewModel.getTravel().observe(this, mTravelObserver);

        mViewModel.recentDiaryImage.observe(this, new Observer<TravelDiary>() {
            @Override
            public void onChanged(TravelDiary item) {
//                if (item == null) {
//                    if (((MyApplication) getApplication()).getMainImageUri() != null) {
//                        Uri uri = ((MyApplication) getApplication()).getMainImageUri();
//                        if (!uri.equals(Uri.EMPTY)) {
//                            mAppBar.setBackground(Drawable.createFromPath(uri.getPath()));
//                        }
//                    }
//                    return;
//                }
//                if (MyString.isNotEmpty(item.getImgUri())) {
//                    mAppBar.setBackground(Drawable.createFromPath(Uri.parse(item.getImgUri()).getPath()));
//                }
            }
        });

    }

}
