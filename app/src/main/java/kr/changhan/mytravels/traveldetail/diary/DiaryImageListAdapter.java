package kr.changhan.mytravels.traveldetail.diary;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import kr.changhan.mytravels.R;
import kr.changhan.mytravels.entity.TravelDiary;

public class DiaryImageListAdapter extends RecyclerView.Adapter<DiaryImageListAdapter.ViewHolder>{
    Context context;
    ArrayList<String> imgPath;
    IDiary iDiary;
    String mode;
    TravelDiary travelDiary;

    public DiaryImageListAdapter(Context context) {
        this.context = context;
    }

    public void setImgPath(ArrayList<String> imgPath) {
        this.imgPath = imgPath;
        notifyDataSetChanged();
    }

    public void setTravelDiary(TravelDiary travelDiary) {
        this.travelDiary = travelDiary;
    }

    public void setiDiary(IDiary iDiary) {
        this.iDiary = iDiary;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_diary_list_image,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.image.setImageURI(Uri.parse(imgPath.get(position)));
    }

    @Override
    public int getItemCount() {
        return imgPath.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        ImageView removeIv;
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            removeIv = itemView.findViewById(R.id.remove_iv);
            if (mode.equals("View")) removeIv.setVisibility(View.GONE);
            removeIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (iDiary != null) iDiary.onImageMoveListener(getAdapterPosition());
                }
            });
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mode.equals("View")) {
                        if (iDiary != null)
                            iDiary.onImageShowListener(getAdapterPosition(), imgPath, travelDiary);
                    }
                }
            });


        }
    }
}
