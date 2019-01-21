package kr.changhan.mytravels.traveldetail;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kr.changhan.mytravels.R;
import kr.changhan.mytravels.base.BaseActivity;
import kr.changhan.mytravels.base.MyConst;
import kr.changhan.mytravels.entity.TravelBaseEntity;
import kr.changhan.mytravels.entity.TravelDiary;
import kr.changhan.mytravels.main.TravelListItemClickListener;
import kr.changhan.mytravels.traveldetail.diary.DiaryImageListAdapter;
import kr.changhan.mytravels.traveldetail.diary.TravelDiaryListAdapter;
import kr.changhan.mytravels.traveldetail.diary.TravelDiaryViewModel;
import kr.changhan.mytravels.utils.MyDate;
import kr.changhan.mytravels.utils.MyItemTouchHelper;
import kr.changhan.mytravels.utils.MyString;

public class DiaryDetailActivity extends BaseActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, TravelListItemClickListener, MyItemTouchHelper.MyItemTouchHelperListner {
    private static final String TAG = DiaryDetailActivity.class.getSimpleName();

    private final Calendar mCalendar = MyDate.getCurrentCalendar();
    private TextView mDateTxt;
    private TextView mTimeTxt;
    private TextView mPlaceTxt;
    private View mDescLayout;
    private EditText mDescTxt;
    private ImageView mToolbarImg;
    private FloatingActionButton mFab;
    private RecyclerView mImageRv;


    private ArrayList<String> imgPath;
    private DiaryImageListAdapter diaryImageListAdapter;
    private Gson gson;

    private boolean mInEditMode;
    private TravelDiaryViewModel mViewModel;
    private TravelDiaryListAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mDateTxt = findViewById(R.id.date_txt);
        mTimeTxt = findViewById(R.id.time_txt);
        mPlaceTxt = findViewById(R.id.place_txt);
        mDescLayout = findViewById(R.id.desc_layout);
        mDescTxt = findViewById(R.id.desc_txt);
        mToolbarImg = findViewById(R.id.toolbar_image);
        mImageRv = findViewById(R.id.image_rv);

        mDateTxt.setText(MyDate.getDateString(mCalendar.getTime()));
        mTimeTxt.setText(MyDate.getTimeMinString(mCalendar.getTime()));

        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(this);

        gson = new Gson();
        imgPath = new ArrayList<>();
        diaryImageListAdapter = new DiaryImageListAdapter(DiaryDetailActivity.this,imgPath);

        mViewModel = ViewModelProviders.of(this).get(TravelDiaryViewModel.class);
        mViewModel.currentItem.observe(this, new Observer<TravelDiary>() {
            @Override
            public void onChanged(TravelDiary item) {
                Log.d(TAG, "onChanged: item=" + item);
                mCalendar.setTimeInMillis(item.getDateTimeLong());
                mDateTxt.setText(item.getDateTimeText());
                mTimeTxt.setText(item.getDateTimeHourMinText());
                mPlaceTxt.setText(item.getPlaceName());
                mDescTxt.setText(item.getDesc());

                if (MyString.isNotEmpty(item.getImgUri())) {
                    imgPath.addAll(gson.fromJson(item.getImgUri(),ArrayList.class));
//                    mToolbarImg.setVisibility(View.VISIBLE);
//                    mToolbarImg.setImageURI(Uri.parse(item.getImgUri()));

                }
            }
        });

        mListAdapter = new TravelDiaryListAdapter(this);
        mListAdapter.setListItemClickListener(this);
        mViewModel.travelDiaryList.observe(this, new Observer<PagedList<TravelDiary>>() {
            @Override
            public void onChanged(PagedList<TravelDiary> items) {
                mListAdapter.submitList(items);
            }
        });
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(mListAdapter);
        // attach MyItemTouchHelper
        ItemTouchHelper.Callback callback = new MyItemTouchHelper(this);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        TravelDiary requestItem = (TravelDiary) getIntent().getExtras().getSerializable(MyConst.REQKEY_TRAVEL);
        Log.d(TAG, "onCreate: requestItem=" + requestItem);
        mViewModel.currentItem.setValue(requestItem);



        mImageRv.setAdapter(diaryImageListAdapter);
        mImageRv.setLayoutManager(new GridLayoutManager(DiaryDetailActivity.this,5,RecyclerView.VERTICAL,false));

        setEditMode(true);
    }

    @Override
    public void onBackPressed() {
        if (mInEditMode) {
            setEditMode(false);
            return;
        }
        super.onBackPressed();
    }

    private void setEditMode(boolean editMode) {
        mInEditMode = editMode;
        if (mInEditMode) {
            mDescLayout.setVisibility(View.VISIBLE);
            mFab.setImageResource(R.drawable.ic_done_white_24dp);
        } else {
            mDescLayout.setVisibility(View.GONE);
            mDescTxt.setText(null);
            mPlaceTxt.setText(null);
            mToolbarImg.setVisibility(View.INVISIBLE);
            mFab.setImageResource(R.drawable.ic_add_black_24dp);
            TravelDiary item = new TravelDiary();
            item.setId(0);
            item.setTravelId(mViewModel.currentItem.getValue().getTravelId());
            item.setDateTime(mViewModel.currentItem.getValue().getDateTime());
            mViewModel.currentItem.setValue(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.date_txt: {
                DatePickerDialog dpd = new DatePickerDialog(this, this,
                        mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
            break;
            case R.id.time_txt: {
                TimePickerDialog tpd = new TimePickerDialog(this, this
                        , mCalendar.get(Calendar.HOUR_OF_DAY)
                        , mCalendar.get(Calendar.MINUTE)
                        , false);
                tpd.show();
            }
            break;
            case R.id.place_txt:
                showPlacePicker();
                break;
            case R.id.camera_btn:
                requestPermissions(MyConst.REQCD_ACCESS_CAMERA);
                break;
            case R.id.image_btn:
                requestPermissions(MyConst.REQCD_ACCESS_GALLERY);
                break;
            case R.id.fab: {
                hideKeyboard();
                if (!mInEditMode) {
                    setEditMode(true);
                    return;
                }
                TravelDiary item = mViewModel.currentItem.getValue();
                setValuesFromEditText(item);
                if (MyString.isEmpty(item.getDesc()) && MyString.isEmpty(item.getImgUri())) {
                    Snackbar.make(v, R.string.diary_edit_warn, Snackbar.LENGTH_LONG).show();
                    return;
                }
                item.setDateTime(mCalendar.getTimeInMillis());
                Log.d(TAG, "item=" + item);
                mViewModel.insertItem(item);
                setEditMode(false);
            }
            break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mCalendar.set(year, month, dayOfMonth);
        TravelDiary item = mViewModel.currentItem.getValue();
        setValuesFromEditText(item);
        item.setDateTime(mCalendar.getTimeInMillis());
        mViewModel.currentItem.setValue(item);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mCalendar.set(Calendar.MINUTE, minute);
        mCalendar.set(Calendar.SECOND, 0);
        TravelDiary item = mViewModel.currentItem.getValue();
        setValuesFromEditText(item);
        item.setDateTime(mCalendar.getTimeInMillis());
        mViewModel.currentItem.setValue(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode);
        switch (requestCode) {
            case MyConst.REQCD_IMAGE_CROP: {
                TravelDiary item = mViewModel.currentItem.getValue();
                Uri thumbUri = copyCropImageForTravel(item.getTravelId());
                Uri cropImagePath = getCropImagePath();
                Log.d(TAG, "onActivityResult: cropImagePath=" + cropImagePath);
                if (cropImagePath == null) {
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Failed to load a image.", Snackbar.LENGTH_LONG).show();
                    return;
                }
                //item.setImgUri(cropImagePath.toString());
                imgPath.add(thumbUri.toString());
                diaryImageListAdapter.notifyDataSetChanged();
                String imgUri = gson.toJson(imgPath,ArrayList.class);
                item.setImgUri(imgUri);
                //item.setThumbUri(thumbUri.toString());
                mViewModel.currentItem.setValue(item);
            }
            break;
            case MyConst.REQCD_PLACE_PICKER: {
                Place place = PlacePicker.getPlace(this, data);
                Log.d(TAG, "onActivityResult: place=" + place);
                TravelDiary item = mViewModel.currentItem.getValue();
                setValuesFromEditText(item);
                item.setPlaceId(place.getId());
                item.setPlaceName(place.getName().toString());
                item.setPlaceAddr(place.getAddress().toString());
                item.setPlaceLat(place.getLatLng().latitude);
                item.setPlaceLng(place.getLatLng().longitude);
                mViewModel.currentItem.setValue(item);
            }
            break;
        }
    }

    /**
     * Sets values from EditText
     */
    private void setValuesFromEditText(TravelDiary item) {
        item.setDesc(mDescTxt.getText().toString().trim());
    }

    @Override
    public void onListItemClick(View view, int position, TravelBaseEntity entity, boolean longClick) {
        TravelDiary item = (TravelDiary) entity;
        Log.d(TAG, "onListItemClick: item=" + item);
        if (longClick) {
            mViewModel.currentItem.setValue(item);
            setEditMode(true);
        } else {
            showImageViewer(item.getImgUri(), item.getDateTimeText(), item.getPlaceAddr(), item.getDesc(), entity);
        }
    }

    @Override
    protected void postRequestPermissionsResult(int reqCd, boolean result) {
        if (!result) {
            Snackbar.make(mFab, R.string.permission_not_granted, Snackbar.LENGTH_LONG).show();
            return;
        }
        switch (reqCd) {
            case MyConst.REQCD_ACCESS_CAMERA:
                takePhotoFromCamera();
                break;
            case MyConst.REQCD_ACCESS_GALLERY:
                takePhotoFromGallery();
                break;
        }
    }

    @Override
    public boolean onRequestItemViewSwipeEnabled() {
        // stop swipe-to-dismiss in edit mode
        return !mInEditMode;
    }

    @Override
    public void onItemDismiss(int position) {
        TravelDiary item = mListAdapter.getItem(position);
        Log.d(TAG, "onItemDismiss: item=" + item);
        item.setDeleteYn(true);
        mViewModel.updateItem(item);
        Snackbar.make(mFab, R.string.delete_warn_msg, Snackbar.LENGTH_INDEFINITE).setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undeleleAllMarkedYes();
            }
        }).addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                switch (event) {
                    case BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_SWIPE:
                    case BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_TIMEOUT:
                        deleleAllMarkedYes();
                        break;
                }
            }
        }).show();
    }

    @Override
    public void finish() {
        deleleAllMarkedYes();
        super.finish();
    }

    // delete items marked as deleteYn=true
    private void deleleAllMarkedYes() {
        TravelDiary item = new TravelDiary();
        item.setId(-99);
        item.setTravelId(mViewModel.currentItem.getValue().getTravelId());
        mViewModel.deleteItem(item);
    }

    // undelete items marked as deleteYn=true
    private void undeleleAllMarkedYes() {
        TravelDiary item = new TravelDiary();
        item.setId(-99);
        item.setTravelId(mViewModel.currentItem.getValue().getTravelId());
        mViewModel.updateItem(item);
    }
}
