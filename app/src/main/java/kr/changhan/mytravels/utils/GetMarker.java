package kr.changhan.mytravels.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import de.hdodenhof.circleimageview.CircleImageView;
import kr.changhan.mytravels.R;
import kr.changhan.mytravels.entity.TravelDiary;
import kr.changhan.mytravels.entity.TravelExpense;
import kr.changhan.mytravels.entity.TravelPlan;

public class GetMarker {
    Object object;
    Context context;



    public GetMarker(Object object,Context context) {
        this.object = object;
        this.context = context;
    }

    public static GetMarker from(Object object,Context context){
        return new GetMarker(object,context);
    }

    public Bitmap getMarker(){
        View view = getView(object);
        if (view == null) return null;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay()
                .getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels,
                displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    private View getView(Object object) {
        View view = null;
        if (object instanceof TravelPlan) {
            view = LayoutInflater.from(context).inflate(R.layout.custom_marker_icon, null, false);
            ImageView img = view.findViewById(R.id.icon_iv);
            img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_library_books_black_24dp));
        } else if (object instanceof TravelDiary) {
            if (((TravelDiary) object).getThumbUri() != null) {
                view = LayoutInflater.from(context).inflate(R.layout.custom_marker, null, false);
                CircleImageView img = view.findViewById(R.id.marker_iv);
                img.setImageURI(Uri.parse(((TravelDiary) object).getThumbUri()));
            } else {
                view = LayoutInflater.from(context).inflate(R.layout.custom_marker_icon, null, false);
                ImageView img = view.findViewById(R.id.icon_iv);
                img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_edit_black_24dp));
            }
        } else if (object instanceof TravelExpense) {
            view = LayoutInflater.from(context).inflate(R.layout.custom_marker_icon, null, false);
            ImageView img = view.findViewById(R.id.icon_iv);
            img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_attach_money_black_24dp));
        }
        return view;
    }


}
