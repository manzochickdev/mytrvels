package kr.changhan.mytravels.traveldetail.expense;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import kr.changhan.mytravels.R;
import kr.changhan.mytravels.base.MyConst;
import kr.changhan.mytravels.entity.TravelExpense;
import kr.changhan.mytravels.main.TravelListItemClickListener;
import kr.changhan.mytravels.utils.MyString;

public class BudgetStatusListAdapter extends RecyclerView.Adapter<BudgetStatusListAdapter.TravelViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private List<TravelExpense> mList;
    private TravelListItemClickListener mTravelListItemClickListener;
    int clicked = -1;

    public BudgetStatusListAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setListItemClickListener(TravelListItemClickListener travelListItemClickListener) {
        mTravelListItemClickListener = travelListItemClickListener;
    }

    @Override
    public int getItemCount() {
        if (mList == null) return 0;
        return mList.size();
    }

    public void setList(List<TravelExpense> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        if (getItemCount() == 0) return RecyclerView.NO_ID;
        return mList.get(position).getId();
    }

    private TravelExpense getItem(int position) {
        if (getItemCount() == 0) return null;
        return mList.get(position);
    }

    @Override
    public void onBindViewHolder(@NonNull TravelViewHolder holder, int position) {
        TravelExpense item = getItem(position);
        if (item == null) {
            return;
        }
        if (clicked == position) {
            holder.viewHolder.setVisibility(View.VISIBLE);
        } else holder.viewHolder.setVisibility(View.INVISIBLE);
        holder.currency.setText(MyConst.getCurrencyCode(item.getCurrency()).value);
        holder.budget.setText(MyString.getMoneyText(item.getPlaceLat()));
        holder.expenses.setText(item.getAmountText());
        holder.balance.setText(MyString.getMoneyText(item.getPlaceLat() - item.getAmount()));
    }

    @NonNull
    @Override
    public TravelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.fragment_budget_status_item, parent, false);
        return new TravelViewHolder(v);
    }

    class TravelViewHolder extends RecyclerView.ViewHolder {
        private final TextView currency;
        private final TextView budget;
        private final TextView expenses;
        private final TextView balance;
        private final View viewHolder;

        private TravelViewHolder(View v) {
            super(v);
            if (mTravelListItemClickListener != null) {
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (clicked != getAdapterPosition()) {
                            mTravelListItemClickListener.onListItemClick(v
                                    , getAdapterPosition()
                                    , getItem(getAdapterPosition()), false);
                            clicked = getAdapterPosition();
                            notifyDataSetChanged();
                        }
                        else {
                            mTravelListItemClickListener.onListItemClick(v
                                    , -1
                                    , new TravelExpense(), false);
                            clicked = -1;
                            notifyDataSetChanged();
                        }
                    }
                });
            }
            currency = v.findViewById(R.id.currency);
            budget = v.findViewById(R.id.budget);
            expenses = v.findViewById(R.id.expenses);
            balance = v.findViewById(R.id.balance);
            viewHolder = v.findViewById(R.id.view_holder);

        }
    }
}
