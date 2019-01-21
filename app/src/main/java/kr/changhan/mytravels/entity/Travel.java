package kr.changhan.mytravels.entity;

import java.util.Objects;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import kr.changhan.mytravels.utils.MyDate;

@Entity(tableName = "travel")
public class Travel extends TravelBaseEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String endDt;
    String thumb;

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public Travel(String title) {
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEndDt() {
        return endDt;
    }

    public void setEndDt(long endDt) {
        this.endDt = MyDate.getString(endDt);
    }

    public long getEndDtLong() {
        return MyDate.getTime(endDt);
    }

    public void setEndDt(String endDt) {
        this.endDt = endDt;
    }

    /**
     * Gets the string expression of the end date.
     *
     * @return string in yyyy-MM-dd format
     */
    public String getEndDtText() {
        return MyDate.getDateString(endDt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Travel)) return false;
        if (!super.equals(o)) return false;
        Travel travel = (Travel) o;
        return id == travel.id &&
                Objects.equals(endDt, travel.endDt);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), id, endDt);
    }

    @Override
    public String toString() {
        return "Travel{" +
                "id=" + id +
                ", endDt='" + endDt + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", placeId='" + placeId + '\'' +
                ", placeName='" + placeName + '\'' +
                ", placeAddr='" + placeAddr + '\'' +
                ", placeLat=" + placeLat +
                ", placeLng=" + placeLng +
                ", southwestLat=" + southwestLat +
                ", southwestLng=" + southwestLng +
                ", northeastLat=" + northeastLat +
                ", northeastLng=" + northeastLng +
                ", deleteYn=" + deleteYn +
                '}';
    }
}
