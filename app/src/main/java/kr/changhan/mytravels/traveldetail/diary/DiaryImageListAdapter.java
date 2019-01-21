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

public class DiaryImageListAdapter extends RecyclerView.Adapter<DiaryImageListAdapter.ViewHolder>{
    Context context;
    ArrayList<String> imgPath;

    public DiaryImageListAdapter(Context context, ArrayList<String> imgPath) {
        this.context = context;
        this.imgPath = imgPath;
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
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }
}
