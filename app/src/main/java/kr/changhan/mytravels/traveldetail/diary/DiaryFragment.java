package kr.changhan.mytravels.traveldetail.diary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kr.changhan.mytravels.R;
import kr.changhan.mytravels.base.BaseActivity;
import kr.changhan.mytravels.base.MyConst;
import kr.changhan.mytravels.entity.Travel;
import kr.changhan.mytravels.entity.TravelBaseEntity;
import kr.changhan.mytravels.entity.TravelDiary;
import kr.changhan.mytravels.main.TravelListItemClickListener;
import kr.changhan.mytravels.traveldetail.DiaryDetailActivity;
import kr.changhan.mytravels.traveldetail.TravelDetailBaseFragment;
import kr.changhan.mytravels.utils.MyDate;

public class DiaryFragment extends TravelDetailBaseFragment implements TravelListItemClickListener, IDiary {
    private static final String TAG = DiaryFragment.class.getSimpleName();
    public static final int TITLE_ID = R.string.title_frag_dairy;
    private TravelDiaryListAdapter mListAdapter;

    public DiaryFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static DiaryFragment newInstance(int sectionNumber) {
        Log.d(TAG, "newInstance: sectionNumber=" + sectionNumber);
        DiaryFragment fragment = new DiaryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new TravelDiaryListAdapter(getContext());
        mListAdapter.setListItemClickListener(this);
        mListAdapter.setiDiary(this);
        mViewModel.getTravelDiaryList().observe(this, new Observer<PagedList<TravelDiary>>() {
            @Override
            public void onChanged(PagedList<TravelDiary> items) {
                mListAdapter.submitList(items);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_diary, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
        recyclerView.setAdapter(mListAdapter);
        return rootView;
    }

    @Override
    protected void onTravelChanged(Travel travel) {
        Log.d(TAG, "onTravelChanged: travel=" + travel);
        if (travel == null) return;
        Map<String, Object> option = new HashMap<>();
        option.put(MyConst.KEY_ID, travel.getId());
        mViewModel.setTravelDiaryListOption(option);
    }

    @Override
    public void requestAddItem() {
        Travel travel = mViewModel.getTravel().getValue();
        Log.d(TAG, "requestAddItem: travel=" + travel);
        if (travel == null) return;

        TravelDiary item = new TravelDiary();
        item.setTravelId(travel.getId());
        item.setDateTime(MyDate.getCurrentTime());
        Intent intent = new Intent(getContext(), DiaryDetailActivity.class);
        intent.putExtra(MyConst.REQKEY_TRAVEL, item);
        intent.putExtra(MyConst.BACKGROUND, travel.getThumb());
        startActivity(intent);
    }

    @Override
    public void onListItemClick(View view, int position, TravelBaseEntity entity, boolean longClick) {
        TravelDiary item = (TravelDiary) entity;
        Log.d(TAG, "onListItemClick: item=" + item);
        Log.d(TAG, "onListItemClick: longClick=" + longClick);

        Travel travel = mViewModel.getTravel().getValue();
        if (travel == null) return;

        if (longClick) {
            Intent intent = new Intent(getContext(), DiaryDetailActivity.class);
            intent.putExtra(MyConst.REQKEY_TRAVEL, item);
            intent.putExtra(MyConst.BACKGROUND, travel.getThumb());
            startActivity(intent);
        } else {
            ((BaseActivity) getActivity()).showImageViewer(item.getImgUri(), item.getDateTimeMinText(), item.getPlaceAddr(), item.getDesc(), entity);
        }
    }

    @Override
    public void onImageMoveListener(int position) {

    }

    @Override
    public void onImageShowListener(int position, ArrayList<String> imgPath, TravelDiary travelDiary) {
        ((BaseActivity) getActivity()).showImageViewer(position, imgPath, travelDiary);
    }

}
