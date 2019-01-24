package kr.changhan.mytravels.traveldetail.diary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kr.changhan.mytravels.R;
import kr.changhan.mytravels.base.BaseActivity;
import kr.changhan.mytravels.entity.TravelDiary;
import kr.changhan.mytravels.main.TravelListItemClickListener;
import kr.changhan.mytravels.utils.MyString;

public class TravelDiaryListAdapter extends PagedListAdapter<TravelDiary, TravelDiaryListAdapter.TravelViewHolder> {

    private final LayoutInflater mLayoutInflater;
    private TravelListItemClickListener mTravelListItemClickListener;
    Context context;
    IDiary iDiary;
    String header = "";

    public TravelDiaryListAdapter(Context context) {
        super(TravelDiary.DIFF_CALLBACK);
        mLayoutInflater = LayoutInflater.from(context);
        this.context =context;
    }

    public void setListItemClickListener(TravelListItemClickListener travelListItemClickListener) {
        mTravelListItemClickListener = travelListItemClickListener;
    }

    public void setiDiary(IDiary iDiary) {
        this.iDiary = iDiary;
    }

    @Override
    public void onBindViewHolder(@NonNull TravelViewHolder holder, int position) {
        TravelDiary item = getItem(position);
        if (item == null) {
            return;
        }

        holder.headerTxt.setVisibility(View.GONE);

        if (MyString.isNotEmpty(item.getImgUri())){
            Gson gson = new Gson();
            ArrayList<String> imgPath = gson.fromJson(item.getImgUri(),ArrayList.class);
            DiaryImageListAdapter diaryImageListAdapter = new DiaryImageListAdapter(context);
            diaryImageListAdapter.setImgPath(imgPath);
            diaryImageListAdapter.setTravelDiary(item);
            diaryImageListAdapter.setiDiary(iDiary);
            diaryImageListAdapter.setMode("View");
            holder.imageRv.setAdapter(diaryImageListAdapter);
            holder.imageRv.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        }
        holder.dateTxt.setText(item.getDateTimeMinText());
        if (MyString.isNotEmpty(item.getPlaceName())) {
            holder.placeTxt.setText(item.getPlaceName());
            holder.placeTxt.setVisibility(View.VISIBLE);
        } else {
            holder.placeTxt.setVisibility(View.GONE);
        }
        if (MyString.isNotEmpty(item.getDesc())) holder.descTxt.setText(item.getDesc());

    }

    @NonNull
    @Override
    public TravelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.fragment_diary_list_item_2, parent, false);
        return new TravelViewHolder(v);
    }

    @Override
    public void onCurrentListChanged(@Nullable PagedList<TravelDiary> previousList, @Nullable PagedList<TravelDiary> currentList) {
        super.onCurrentListChanged(previousList, currentList);
        notifyDataSetChanged();
    }

    class TravelViewHolder extends RecyclerView.ViewHolder {
        //private final ImageView thumbnail;
        private final RecyclerView imageRv;
        private final TextView descTxt, headerTxt;
        private final TextView dateTxt;
        private final TextView placeTxt;

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
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mTravelListItemClickListener != null) {
                        mTravelListItemClickListener.onListItemClick(v
                                , getAdapterPosition()
                                , getItem(getAdapterPosition())
                                , true);
                    }
                    return true;
                }
            });
            //thumbnail = v.findViewById(R.id.thumbnail);
            descTxt = v.findViewById(R.id.desc_txt);
            imageRv = v.findViewById(R.id.image_rv);
            headerTxt = v.findViewById(R.id.header_txt);
            dateTxt = v.findViewById(R.id.date_txt);
            placeTxt = v.findViewById(R.id.place_txt);

            placeTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((BaseActivity) context).openGoogleMap(v, getItem(getAdapterPosition()), null);
                }
            });
        }
    }

    @Nullable
    @Override
    public TravelDiary getItem(int position) {
        return super.getItem(position);
    }
}
