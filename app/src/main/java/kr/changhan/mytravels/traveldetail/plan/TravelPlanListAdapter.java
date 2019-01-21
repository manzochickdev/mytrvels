package kr.changhan.mytravels.traveldetail.plan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import kr.changhan.mytravels.R;
import kr.changhan.mytravels.base.BaseActivity;
import kr.changhan.mytravels.entity.TravelPlan;
import kr.changhan.mytravels.main.TravelListItemClickListener;
import kr.changhan.mytravels.utils.MyString;

public class TravelPlanListAdapter extends PagedListAdapter<TravelPlan, TravelPlanListAdapter.TravelViewHolder> {

    private final LayoutInflater mLayoutInflater;
    private TravelListItemClickListener mTravelListItemClickListener;
    private final Context mContext;
    private OnRatingChangeListener mOnRatingChangeListener;
    String header="";

    public interface OnRatingChangeListener {
        void onRatingChanged(int position, TravelPlan entity);
    }

    public TravelPlanListAdapter(Context context) {
        super(TravelPlan.DIFF_CALLBACK);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setListItemClickListener(TravelListItemClickListener travelListItemClickListener) {
        mTravelListItemClickListener = travelListItemClickListener;
    }

    public void setOnRatingChangeListener(OnRatingChangeListener listener) {
        mOnRatingChangeListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull TravelViewHolder holder, int position) {
        TravelPlan item = getItem(position);
        if (item == null) {
            return;
        }

        if (!item.getDateTime().substring(2,8).equals(header)){
            header = item.getDateTime().substring(2,8);
            holder.headerTxt.setText(header);
            holder.headerTxt.setVisibility(View.VISIBLE);
        }
        else {holder.headerTxt.setVisibility(View.GONE);}

        holder.dateTxt.setText(item.getDateTimeMinText());
        holder.titleTxt.setText(item.getTitle());
        holder.descTxt.setText(item.getDesc());
        if (MyString.isNotEmpty(item.getPlaceName())) {
            holder.placeTxt.setText(item.getPlaceName());
            holder.placeTxt.setVisibility(View.VISIBLE);
        } else {
            holder.placeTxt.setVisibility(View.GONE);
        }
        holder.ratingBar.setRating(item.getRating());
    }

    public void setHeader(String header) {
        this.header = header;
    }

    @NonNull
    @Override
    public TravelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.fragment_plan_list_item, parent, false);
        return new TravelViewHolder(v);
    }

    @Override
    public void onCurrentListChanged(@Nullable PagedList<TravelPlan> previousList, @Nullable PagedList<TravelPlan> currentList) {
        super.onCurrentListChanged(previousList, currentList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class TravelViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateTxt;
        private final TextView placeTxt;
        private final TextView titleTxt;
        private final TextView descTxt;
        private final TextView headerTxt;
        private final RatingBar ratingBar;

        private TravelViewHolder(View v) {
            super(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (descTxt.getMaxLines() == 3) {
                        descTxt.setMaxLines(Integer.MAX_VALUE);
                    } else {
                        descTxt.setMaxLines(3);
                    }
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
            dateTxt = v.findViewById(R.id.date_txt);
            placeTxt = v.findViewById(R.id.place_txt);
            titleTxt = v.findViewById(R.id.title_txt);
            descTxt = v.findViewById(R.id.desc_txt);
            headerTxt = v.findViewById(R.id.header_txt);
            ratingBar = v.findViewById(R.id.ratingBar);

            placeTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((BaseActivity) mContext).openGoogleMap(v, getItem(getAdapterPosition()), null);
                }
            });

            if (mOnRatingChangeListener != null) {
                ratingBar.setIsIndicator(false);
                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        if (fromUser) {
                            int position = getAdapterPosition();
                            TravelPlan entity = getItem(position);
                            if (rating != entity.getRating()) {
                                entity.setRating(rating);
                                mOnRatingChangeListener.onRatingChanged(position, entity);
                            }
                        }
                    }
                });
            } else {
                ratingBar.setIsIndicator(true);
            }

        }

        public void bind(TravelPlan item) {
            dateTxt.setText(item.getDateTimeMinText());
            titleTxt.setText(item.getTitle());
            descTxt.setText(item.getDesc());
            if (MyString.isNotEmpty(item.getPlaceName())) {
                placeTxt.setText(item.getPlaceName());
                placeTxt.setVisibility(View.VISIBLE);
            } else {
                placeTxt.setVisibility(View.GONE);
            }
            ratingBar.setRating(item.getRating());
        }
    }

    @Nullable
    @Override
    public TravelPlan getItem(int position) {
        return super.getItem(position);
    }
}
