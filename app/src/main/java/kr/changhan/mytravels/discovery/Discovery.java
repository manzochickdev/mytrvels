package kr.changhan.mytravels.discovery;

import android.graphics.Bitmap;

public class Discovery {
    double lat;
    double lng;
    String placeId;
    String vinicity;
    String name;
    Bitmap img;

    public Discovery(double lat, double lng, String placeId, String vinicity, String name) {
        this.lat = lat;
        this.lng = lng;
        this.placeId = placeId;
        this.vinicity = vinicity;
        this.name = name;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }
}
