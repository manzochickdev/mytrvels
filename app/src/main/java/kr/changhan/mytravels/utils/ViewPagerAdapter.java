package kr.changhan.mytravels.utils;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import kr.changhan.mytravels.R;

public class ViewPagerAdapter extends PagerAdapter {
    Context context;
    ArrayList<String> imgPath;
    LayoutInflater inflater;


    public ViewPagerAdapter(Context context, ArrayList<String> imgPath) {
        this.context = context;
        this.imgPath = imgPath;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return imgPath.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.view_pager_item, null, false);

        ImageView img = view.findViewById(R.id.imageView);
        img.setImageURI(Uri.parse(imgPath.get(position)));

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
