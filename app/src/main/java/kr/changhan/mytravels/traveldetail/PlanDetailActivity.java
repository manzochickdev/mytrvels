package kr.changhan.mytravels.traveldetail;

import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kr.changhan.mytravels.MainActivity;
import kr.changhan.mytravels.R;
import kr.changhan.mytravels.base.BaseActivity;
import kr.changhan.mytravels.base.MyConst;
import kr.changhan.mytravels.entity.TravelBaseEntity;
import kr.changhan.mytravels.entity.TravelPlan;
import kr.changhan.mytravels.main.TravelListItemClickListener;
import kr.changhan.mytravels.traveldetail.plan.TravelPlanListAdapter;
import kr.changhan.mytravels.traveldetail.plan.TravelPlanViewModel;
import kr.changhan.mytravels.utils.MyDate;
import kr.changhan.mytravels.utils.MyItemTouchHelper;
import kr.changhan.mytravels.utils.MyString;

public class PlanDetailActivity extends BaseActivity implements TravelListItemClickListener, View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, MyItemTouchHelper.MyItemTouchHelperListner, RatingBar.OnRatingBarChangeListener {
    private static final String TAG = PlanDetailActivity.class.getSimpleName();

    private final Calendar mCalendar = MyDate.getCurrentCalendar();
    private TextView mDateTxt;
    private TextView mTimeTxt;
    private TextView mPlaceTxt;
    private View mDescLayout;
    private EditText mTitleTxt;
    private RatingBar mRatingBar;
    private EditText mDescTxt;
    private FloatingActionButton mFab;
    private AppBarLayout mAppBar;

    private boolean mInEditMode;
    private TravelPlanViewModel mViewModel;
    private TravelPlanListAdapter mListAdapter;

    private NotificationManagerCompat notificationManagerCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mAppBar = findViewById(R.id.app_bar);
        mDateTxt = findViewById(R.id.date_txt);
        mTimeTxt = findViewById(R.id.time_txt);
        mPlaceTxt = findViewById(R.id.place_txt);
        mDescLayout = findViewById(R.id.desc_layout);
        mDescTxt = findViewById(R.id.desc_txt);
        mTitleTxt = findViewById(R.id.title_txt);
        mRatingBar = findViewById(R.id.ratingBar);
        notificationManagerCompat = NotificationManagerCompat.from(this);

        mDateTxt.setText(MyDate.getDateString(mCalendar.getTime()));
        mTimeTxt.setText(MyDate.getTimeMinString(mCalendar.getTime()));
        mTitleTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                    case EditorInfo.IME_ACTION_NEXT:
                        mDescTxt.requestFocus();
                        return true;
                }
                return false;
            }
        });
        mRatingBar.setOnRatingBarChangeListener(this);
        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(this);

        mViewModel = ViewModelProviders.of(this).get(TravelPlanViewModel.class);
        mViewModel.currentItem.observe(this, new Observer<TravelPlan>() {
            @Override
            public void onChanged(TravelPlan item) {
                Log.d(TAG, "onChanged: item=" + item);
                mCalendar.setTimeInMillis(item.getDateTimeLong());
                mDateTxt.setText(item.getDateTimeText());
                mTimeTxt.setText(item.getDateTimeHourMinText());
                mPlaceTxt.setText(item.getPlaceName());
                mTitleTxt.setText(item.getTitle());
                mDescTxt.setText(item.getDesc());
                mRatingBar.setRating(item.getRating());
            }
        });

        mListAdapter = new TravelPlanListAdapter(this);
        mListAdapter.setListItemClickListener(this);
        mListAdapter.setOnRatingChangeListener(new TravelPlanListAdapter.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(int position, TravelPlan entity) {
                mViewModel.updateItem(entity);
            }
        });
        mViewModel.travelPlanList.observe(this, new Observer<PagedList<TravelPlan>>() {
            @Override
            public void onChanged(PagedList<TravelPlan> items) {
                mListAdapter.setHeader("");
                mListAdapter.submitList(items);
            }
        });
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mListAdapter);
        // attach MyItemTouchHelper
        ItemTouchHelper.Callback callback = new MyItemTouchHelper(this);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);


        TravelPlan requestItem = (TravelPlan) getIntent().getExtras().getSerializable(MyConst.REQKEY_TRAVEL);
        String background = getIntent().getStringExtra(MyConst.BACKGROUND);
        if (background != null)
            mAppBar.setBackground(Drawable.createFromPath(Uri.parse(background).getPath()));
        Log.d(TAG, "onCreate: requestItem=" + requestItem);
        mViewModel.currentItem.setValue(requestItem);

        setEditMode(true);

    }

    private void notificationDailyPlan() {

//        Notification notification = new NotificationCompat.Builder(this, "NOTIFICATION")
//                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
//                .setContentTitle("Notification")
//                .setContentText("Message")
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                .build();
//
//        notificationManagerCompat.notify(1, notification);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());

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
            mTitleTxt.setText(null);
            mDescTxt.setText(null);
            mPlaceTxt.setText(null);
            mFab.setImageResource(R.drawable.ic_add_black_24dp);
            TravelPlan item = new TravelPlan();
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
            case R.id.fab: {
                hideKeyboard();
                if (!mInEditMode) {
                    setEditMode(true);
                    return;
                }
                TravelPlan item = mViewModel.currentItem.getValue();
                setValuesFromEditText(item);
                if (MyString.isEmpty(item.getTitle()) && MyString.isEmpty(item.getPlaceName())) {
                    Snackbar.make(v, R.string.plan_edit_warn, Snackbar.LENGTH_LONG).show();
                    return;
                }
                item.setDateTime(mCalendar.getTimeInMillis());
                Log.d(TAG, "item=" + item);
                mViewModel.insertItem(item);
                setEditMode(false);
                //notificationDailyPlan();

            }
            break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mCalendar.set(year, month, dayOfMonth);
        TravelPlan item = mViewModel.currentItem.getValue();
        setValuesFromEditText(item);
        item.setDateTime(mCalendar.getTimeInMillis());
        mViewModel.currentItem.setValue(item);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mCalendar.set(Calendar.MINUTE, minute);
        mCalendar.set(Calendar.SECOND, 0);
        TravelPlan item = mViewModel.currentItem.getValue();
        setValuesFromEditText(item);
        item.setDateTime(mCalendar.getTimeInMillis());
        mViewModel.currentItem.setValue(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case MyConst.REQCD_PLACE_PICKER: {
                Place place = PlacePicker.getPlace(this, data);
                Log.d(TAG, "onActivityResult: place=" + place);
                TravelPlan item = mViewModel.currentItem.getValue();
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
    private void setValuesFromEditText(TravelPlan item) {
        item.setTitle(mTitleTxt.getText().toString().trim());
        item.setDesc(mDescTxt.getText().toString().trim());
    }

    @Override
    public void onListItemClick(View view, int position, TravelBaseEntity entity, boolean longClick) {
        TravelPlan item = (TravelPlan) entity;
        Log.d(TAG, "onListItemClick: item=" + item);
        if (!longClick) return;
        mViewModel.currentItem.setValue(item);
        setEditMode(true);
    }

    @Override
    public boolean onRequestItemViewSwipeEnabled() {
        // stop swipe-to-dismiss in edit mode
        return !mInEditMode;
    }

    @Override
    public void onItemDismiss(int position) {
        TravelPlan item = mListAdapter.getItem(position);
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
        TravelPlan item = new TravelPlan();
        item.setId(-99);
        item.setTravelId(mViewModel.currentItem.getValue().getTravelId());
        mViewModel.deleteItem(item);
    }

    // undelete items marked as deleteYn=true
    private void undeleleAllMarkedYes() {
        TravelPlan item = new TravelPlan();
        item.setId(-99);
        item.setTravelId(mViewModel.currentItem.getValue().getTravelId());
        mViewModel.updateItem(item);
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        TravelPlan item = mViewModel.currentItem.getValue();
        setValuesFromEditText(item);
        item.setRating(rating);
        mViewModel.currentItem.setValue(item);
    }
}
