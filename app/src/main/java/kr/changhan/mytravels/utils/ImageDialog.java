package kr.changhan.mytravels.utils;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.changhan.mytravels.R;


public class ImageDialog extends Fragment {
    public static final String TAG = ImageViewerDialog.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_dialog, container, false);
    }
}
