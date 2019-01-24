package kr.changhan.mytravels;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kr.changhan.mytravels.base.BaseActivity;
import kr.changhan.mytravels.base.MyApplication;
import kr.changhan.mytravels.base.MyConst;
import kr.changhan.mytravels.base.TravelSort;
import kr.changhan.mytravels.entity.Travel;
import kr.changhan.mytravels.entity.TravelBaseEntity;
import kr.changhan.mytravels.hotplace.HotPlaceActivity;
import kr.changhan.mytravels.main.TravelListAdapter;
import kr.changhan.mytravels.main.TravelListItemClickListener;
import kr.changhan.mytravels.main.TravelViewModel;
import kr.changhan.mytravels.utils.MyString;

public class MainActivity extends BaseActivity implements TravelListItemClickListener, View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mToolbarLayout;

    private TravelViewModel mTravelViewModel;
    private TravelListAdapter mTravelListAdapter;
    private final Observer<List<Travel>> mTravelObserver = new Observer<List<Travel>>() {
        @Override
        public void onChanged(List<Travel> travels) {
            Log.d(TAG, "onChanged: travels.size=" + travels.size());
            mTravelListAdapter.setList(travels);
            if (((MyApplication) getApplication()).getMainActivityShowInfo() && travels.size() > 0) {
                Snackbar.make(findViewById(R.id.fab), R.string.travel_longclick, Snackbar.LENGTH_LONG)
                        .setAction(R.string.dont_show_again, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((MyApplication) getApplication()).setMainActivityShowInfo(false);
                            }
                        })
                        .show();
            }
        }
    };

    @Override
    public void onListItemClick(View view, int position, TravelBaseEntity entity, boolean longClick) {
        Travel item = (Travel) entity;
        Log.d(TAG, "onListItemClick: item=" + item);
        Log.d(TAG, "onListItemClick: longClick=" + longClick);
        if (longClick) {
            // call EditTravelActivity with a selected Travel entity.
            Intent intent = new Intent(this, EditTravelActivity.class);
            intent.putExtra(MyConst.REQKEY_TRAVEL, item);
            intent.setAction(MyConst.REQACTION_EDIT_TRAVEL);
            startActivityForResult(intent, MyConst.REQCD_TRAVEL_EDIT);
        } else {
            // call TravelDetailActivity
            Intent intent = new Intent(MainActivity.this, TravelDetailActivity.class);
            intent.putExtra(MyConst.REQKEY_TRAVEL_ID, item.getId());
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // call EditTravelActivity
                Intent intent = new Intent(MainActivity.this, EditTravelActivity.class);
                startActivityForResult(intent, MyConst.REQCD_TRAVEL_ADD);
            }
        });
        mTravelListAdapter = new TravelListAdapter(this);
        mTravelListAdapter.setListItemClickListener(this);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setAdapter(mTravelListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTravelViewModel = ViewModelProviders.of(this).get(TravelViewModel.class);
        mTravelViewModel.getAllTravels().observe(this, mTravelObserver);
        mTravelViewModel.setTravelSort(((MyApplication) getApplication()).getTravelSort());


        mAppBarLayout = findViewById(R.id.app_bar);
        // set main image
        if (((MyApplication) getApplication()).getMainImageUri() != null) {
            Uri uri = ((MyApplication) getApplication()).getMainImageUri();
            if (!uri.equals(Uri.EMPTY)) {
                mAppBarLayout.setBackground(Drawable.createFromPath(uri.getPath()));
            }
        }
        mToolbarLayout = findViewById(R.id.toolbar_layout);
        // set main title
        if (MyString.isNotEmpty(((MyApplication) getApplication()).getMainTitle())) {
            mToolbarLayout.setTitle(((MyApplication) getApplication()).getMainTitle());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        TravelSort travelSort = ((MyApplication) getApplication()).getTravelSort();
        switch (travelSort) {
            case DEFAULT:
                menu.findItem(R.id.action_sort_default).setChecked(true);
                break;
            case TITLE_ASC:
                menu.findItem(R.id.action_sort_travel_asc).setChecked(true);
                break;
            case TITLE_DESC:
                menu.findItem(R.id.action_sort_travel_desc).setChecked(true);
                break;
            case START_ASC:
                menu.findItem(R.id.action_sort_start_asc).setChecked(true);
                break;
            case START_DESC:
                menu.findItem(R.id.action_sort_start_desc).setChecked(true);
                break;
        }

        final MenuItem changeTitleMenu = menu.findItem(R.id.action_change_title);
        SearchView searchView = (SearchView) changeTitleMenu.getActionView();
        searchView.setQueryHint("title");
        searchView.setSubmitButtonEnabled(false);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (MyString.isNotEmpty(query)) {
                    query = query.trim();
                    ((MyApplication) getApplication()).setMainTitle(query);
                    mToolbarLayout.setTitle(query);
                }
                changeTitleMenu.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_default:
                mTravelViewModel.setTravelSort(TravelSort.DEFAULT);
                item.setChecked(true);
                return true;
            case R.id.action_sort_travel_asc:
                mTravelViewModel.setTravelSort(TravelSort.TITLE_ASC);
                item.setChecked(true);
                return true;
            case R.id.action_sort_travel_desc:
                mTravelViewModel.setTravelSort(TravelSort.TITLE_DESC);
                item.setChecked(true);
                return true;
            case R.id.action_sort_start_asc:
                mTravelViewModel.setTravelSort(TravelSort.START_ASC);
                item.setChecked(true);
                return true;
            case R.id.action_sort_start_desc:
                mTravelViewModel.setTravelSort(TravelSort.START_DESC);
                item.setChecked(true);
                return true;
            case R.id.action_change_title:
                return true;
            case R.id.action_change_photo_camera:
                requestPermissions(MyConst.REQCD_ACCESS_CAMERA);
                return true;
            case R.id.action_change_photo_gallery:
                requestPermissions(MyConst.REQCD_ACCESS_GALLERY);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.subtitle_txt:
                startActivity(new Intent(MainActivity.this, HotPlaceActivity.class));
                break;
        }
    }

    @Override
    protected void postRequestPermissionsResult(int reqCd, boolean result) {
        if (!result) {
            Snackbar.make(findViewById(R.id.fab), R.string.permission_not_granted, Snackbar.LENGTH_LONG).show();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode);
        Log.d(TAG, "onActivityResult: resultCode=" + resultCode);
        Log.d(TAG, "onActivityResult: data=" + data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case MyConst.REQCD_IMAGE_CROP: {
                Uri cropImagePath = getCropImagePath();
                Log.d(TAG, "onActivityResult: cropImagePath=" + cropImagePath);
                if (cropImagePath == null) {
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Failed to load a image.", Snackbar.LENGTH_LONG).show();
                    return;
                }
                // copy the cropped image to app's private directory.
                final File srcFile = new File(cropImagePath.getPath());
                if (!srcFile.exists()) {
                    Log.e(TAG, "Not Exist: " + srcFile.getAbsolutePath());
                    return;
                }
                final File targetFile = new File(getFilesDir(), "main_img.jpg");
                FileChannel sourceChannel = null;
                FileChannel destChannel = null;
                try {
                    sourceChannel = new FileInputStream(srcFile).getChannel();
                    destChannel = new FileOutputStream(targetFile).getChannel();
                    destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
                    ((MyApplication) getApplication()).setMainImageUri(Uri.fromFile(targetFile));
                    mAppBarLayout.setBackground(Drawable.createFromPath(targetFile.getAbsolutePath()));
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                } finally {
                    try {
                        if (sourceChannel != null) sourceChannel.close();
                        srcFile.delete();
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                    try {
                        if (destChannel != null) destChannel.close();
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            }
            break;
        }
    }
}
