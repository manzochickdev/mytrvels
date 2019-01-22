package kr.changhan.mytravels.traveldetail;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import kr.changhan.mytravels.R;

public class TravelTimelineMapAdapter extends RecyclerView.Adapter<TravelTimelineMapAdapter.ViewHolder> {
    Context context;
    LayoutInflater inflater;
    OnItemFocusListener onItemFocusListener;

    public interface OnItemFocusListener{
        void onItemFocus(int position);
    }

    public void setOnItemFocusListener(OnItemFocusListener onItemFocusListener) {
        this.onItemFocusListener = onItemFocusListener;
    }

    public TravelTimelineMapAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.activity_travel_timeline_map_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View v) {
            super(v);
            v.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    Log.d("OK", "onFocusChange: "+getAdapterPosition());
                }
            });

        }
    }
}
