package kr.changhan.mytravels.traveldetail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kr.changhan.mytravels.R;
import kr.changhan.mytravels.base.MyConst;
import kr.changhan.mytravels.entity.TravelDiary;
import kr.changhan.mytravels.entity.TravelExpense;
import kr.changhan.mytravels.entity.TravelPlan;
import kr.changhan.mytravels.traveldetail.diary.DiaryImageListAdapter;
import kr.changhan.mytravels.utils.MyString;

public class TravelTimelineMapAdapter extends RecyclerView.Adapter<TravelTimelineMapAdapter.ViewHolder> {
    Context context;
    LayoutInflater inflater;
    List travelItem;

    public TravelTimelineMapAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        if (travelItem == null) travelItem = new ArrayList();
    }

    public void setTravelItem(List travelItem) {
        this.travelItem = travelItem;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                View view = LayoutInflater.from(context).inflate(R.layout.fragment_plan_list_item, parent, false);
                return new ViewHolder(view, 0);
            case 1:
                View view1 = LayoutInflater.from(context).inflate(R.layout.fragment_diary_list_item_2, parent, false);
                return new ViewHolder(view1, 1);
            case 2:
                View view2 = LayoutInflater.from(context).inflate(R.layout.fragment_expense_list_item, parent, false);
                return new ViewHolder(view2, 2);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0: {
                TravelPlan item = (TravelPlan) travelItem.get(position);
                holder.dateTxt.setText(item.getDateTimeMinText());
                holder.titleTxt.setText(item.getTitle());
                holder.descTxt.setText(item.getDesc());
                if (MyString.isNotEmpty(item.getPlaceName())) {
                    holder.placeTxt.setText(item.getPlaceName());
                    holder.placeTxt.setVisibility(View.VISIBLE);
                } else {
                    holder.placeTxt.setVisibility(View.GONE);
                }

            }
            break;
            case 1: {
                TravelDiary item = (TravelDiary) travelItem.get(position);
                if (MyString.isNotEmpty(item.getImgUri())) {
                    Gson gson = new Gson();
                    ArrayList<String> imgPath = gson.fromJson(item.getImgUri(), ArrayList.class);
                    DiaryImageListAdapter diaryImageListAdapter = new DiaryImageListAdapter(context, imgPath);
                    diaryImageListAdapter.setMode("View");
                    holder.imageRv.setAdapter(diaryImageListAdapter);
                    holder.imageRv.setLayoutManager(new GridLayoutManager(context, 5, RecyclerView.VERTICAL, false));
                }
                String desc = item.getDateTimeMinText();
                if (MyString.isNotEmpty(item.getPlaceName())) desc += "\n " + item.getPlaceName();
                if (MyString.isNotEmpty(item.getDesc())) desc += "\n " + item.getDesc();
                holder.descTxt.setText(desc);
            }
            break;
            case 2: {
                TravelExpense item = (TravelExpense) travelItem.get(position);
                holder.dateTxt.setText(item.getDateTimeMinText());
                holder.titleTxt.setText(item.getTitle());
                holder.descTxt.setText(item.getDesc());
                holder.amount.setText(item.getAmountText());
                holder.currency.setText(MyConst.getCurrencyCode(item.getCurrency()).value);
                holder.type.setText(MyConst.getBudgetCode(item.getType()).value);

                if (MyString.isNotEmpty(item.getPlaceName())) {
                    holder.placeTxt.setText(item.getPlaceName());
                    holder.placeTxt.setVisibility(View.VISIBLE);
                } else {
                    holder.placeTxt.setVisibility(View.GONE);
                }
            }
            break;
        }
    }

    @Override
    public int getItemCount() {
        return travelItem.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (travelItem.get(position) instanceof TravelPlan) {
            return 0;
        } else if (travelItem.get(position) instanceof TravelDiary) {
            return 1;
        } else if (travelItem.get(position) instanceof TravelExpense) {
            return 2;
        }
        return -1;
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTxt;
        TextView placeTxt;
        TextView titleTxt;
        TextView descTxt;
        TextView headerTxt;
        TextView amount;
        TextView currency;
        TextView type;
        RecyclerView imageRv;

        public ViewHolder(@NonNull View v, int t) {
            super(v);
            switch (t) {
                case 0: {
                    dateTxt = v.findViewById(R.id.date_txt);
                    placeTxt = v.findViewById(R.id.place_txt);
                    titleTxt = v.findViewById(R.id.title_txt);
                    descTxt = v.findViewById(R.id.desc_txt);
                    headerTxt = v.findViewById(R.id.header_txt);
                }
                break;
                case 1: {
                    descTxt = v.findViewById(R.id.desc_txt);
                    imageRv = v.findViewById(R.id.image_rv);
                }
                break;
                case 2: {
                    dateTxt = v.findViewById(R.id.date_txt);
                    placeTxt = v.findViewById(R.id.place_txt);
                    titleTxt = v.findViewById(R.id.title_txt);
                    descTxt = v.findViewById(R.id.desc_txt);
                    amount = v.findViewById(R.id.amount_txt);
                    currency = v.findViewById(R.id.currency_txt);
                    type = v.findViewById(R.id.type_txt);
                }
                break;
            }
        }
    }
}
