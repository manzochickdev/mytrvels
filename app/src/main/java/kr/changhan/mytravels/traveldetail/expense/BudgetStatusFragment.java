package kr.changhan.mytravels.traveldetail.expense;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kr.changhan.mytravels.R;
import kr.changhan.mytravels.base.MyConst;
import kr.changhan.mytravels.entity.Travel;
import kr.changhan.mytravels.entity.TravelBaseEntity;
import kr.changhan.mytravels.entity.TravelExpense;
import kr.changhan.mytravels.main.TravelListItemClickListener;
import kr.changhan.mytravels.traveldetail.TravelDetailBaseFragment;

public class BudgetStatusFragment extends TravelDetailBaseFragment implements TravelListItemClickListener {
    private static final String TAG = BudgetStatusFragment.class.getSimpleName();
    private BudgetStatusListAdapter mListAdapter;

    public BudgetStatusFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new BudgetStatusListAdapter(getContext());
        mListAdapter.setListItemClickListener(this);
        mViewModel.getBudgetStatus().observe(this, new Observer<List<TravelExpense>>() {
            @Override
            public void onChanged(List<TravelExpense> items) {
                mListAdapter.setList(items);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_budget_status, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.budget_list);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mListAdapter);
        return rootView;
    }

    @Override
    protected void onTravelChanged(Travel travel) {
        Log.d(TAG, "onTravelChanged: travel=" + travel);
        if (travel == null) return;
        Map<String, Object> option = new HashMap<>();
        option.put(MyConst.KEY_ID, travel.getId());
        mViewModel.setTravelExpenseListOption(option);
    }

    @Override
    public void requestAddItem() {
    }

    @Override
    public void onListItemClick(View view, int position, TravelBaseEntity entity, boolean longClick) {
        TravelExpense item = (TravelExpense) entity;
        if (item.getTravelId()==0) item.setTravelId(mViewModel.getTravelId().getValue());
        ((OnChildFragmentClick) getParentFragment()).updateCurrency(item);
    }
}
