package kr.changhan.mytravels.traveldetail;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import kr.changhan.mytravels.entity.TravelBaseEntity;
import kr.changhan.mytravels.entity.TravelDiary;
import kr.changhan.mytravels.entity.TravelExpense;
import kr.changhan.mytravels.entity.TravelPlan;

public class TravelOverviewAdapter extends RecyclerView.Adapter<TravelOverviewAdapter.ViewHolder>{
    Context context;
    ArrayList<TravelBaseEntity> travelBaseEntities;

    public TravelOverviewAdapter(Context context) {
        this.context = context;
    }

    public void setTravelBaseEntities(List<TravelBaseEntity> travelBaseEntities) {
        if (this.travelBaseEntities==null) this.travelBaseEntities = new ArrayList<>();
        this.travelBaseEntities.addAll(travelBaseEntities);
        this.travelBaseEntities = sort(this.travelBaseEntities);
        notifyDataSetChanged();
    }

    private ArrayList<TravelBaseEntity> sort(ArrayList<TravelBaseEntity> travelBaseEntities) {
        return travelBaseEntities;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (travelBaseEntities.get(position) instanceof TravelPlan){
            return 0;
        }
        else if (travelBaseEntities.get(position) instanceof TravelDiary){
            return 1;
        }
        else if (travelBaseEntities.get(position) instanceof TravelExpense){
            return 2;
        }
        return -1;
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView,int type) {
            super(itemView);
            switch (type){
                case 1:{

                }
                break;
                case 2:{}break;
                case 3:{}break;
            }
        }
    }
}
