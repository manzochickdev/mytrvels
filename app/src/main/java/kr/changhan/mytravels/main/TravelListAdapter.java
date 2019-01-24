package kr.changhan.mytravels.main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import kr.changhan.mytravels.MyReceiver;
import kr.changhan.mytravels.R;
import kr.changhan.mytravels.entity.Travel;

public class TravelListAdapter extends RecyclerView.Adapter<TravelListAdapter.TravelViewHolder> {

    private final LayoutInflater mLayoutInflater;
    Context mContext;
    private List<Travel> mList;

    public TravelListAdapter(Context context) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    private TravelListItemClickListener mTravelListItemClickListener;

    public void setListItemClickListener(TravelListItemClickListener travelListItemClickListener) {
        mTravelListItemClickListener = travelListItemClickListener;
    }

    @Override
    public int getItemCount() {
        if (mList == null) return 0;
        return mList.size();
    }

    public void setList(List<Travel> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        if (getItemCount() == 0) return RecyclerView.NO_ID;
        return mList.get(position).getId();
    }

    private Travel getItem(int position) {
        if (getItemCount() == 0) return null;
        return mList.get(position);
    }

    @Override
    public void onBindViewHolder(@NonNull TravelViewHolder holder, int position) {
        Travel item = getItem(position);
        if (item == null) {
            return;
        }
        holder.titleTxt.setText(item.getTitle());
        holder.placeTxt.setText(item.getPlaceName() + " / " + item.getPlaceAddr());
        holder.dateTxt.setText(item.getDateTimeText() + " ~ " + item.getEndDtText());
        holder.placeIv.setImageURI(Uri.parse(item.getThumb()));

        //get ngày hiện tại với ngày của list nếu có ngày của list- ngày hiện tại =1 thì hiện notification
        Calendar cal = Calendar.getInstance();
        long today = cal.getTimeInMillis();
        long thatday = item.getDateTimeLong();
        long diff = (thatday - today) / (24 * 60 * 60 * 1000);

        if (diff == 0 || diff == 1) {

            AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(mContext, MyReceiver.class);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            //21600000 tương đương 6 giờ đẩy thông báo 1 lần
            am.setRepeating(AlarmManager.RTC_WAKEUP, today, 21600000, pendingIntent);

        }
        Log.d("time", "time: " + thatday + "///" + today + "///" + diff);
    }

    @NonNull
    @Override
    public TravelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.main_travel_item, parent, false);
        return new TravelViewHolder(v);
    }

    class TravelViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTxt;
        private final TextView placeTxt;
        private final TextView dateTxt;
        private final ImageView placeIv;

        private TravelViewHolder(View v) {
            super(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTravelListItemClickListener != null) {
                        mTravelListItemClickListener.onListItemClick(v
                                , getAdapterPosition()
                                , getItem(getAdapterPosition())
                                , false);
                    }
                }
            });
            if (mTravelListItemClickListener != null) {
                v.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        mTravelListItemClickListener.onListItemClick(v
                                , getAdapterPosition()
                                , getItem(getAdapterPosition())
                                , true);
                        return true;
                    }
                });
            }
            titleTxt = v.findViewById(R.id.title_txt);
            placeTxt = v.findViewById(R.id.place_txt);
            dateTxt = v.findViewById(R.id.date_txt);
            placeIv = v.findViewById(R.id.place_iv);
        }
    }
}
