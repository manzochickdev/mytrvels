package kr.changhan.mytravels.traveldetail.diary;

import java.util.ArrayList;

import kr.changhan.mytravels.entity.TravelDiary;

public interface IDiary {
    void onImageMoveListener(int position);

    void onImageShowListener(int position, ArrayList<String> imgPath, TravelDiary iDiary);
}
