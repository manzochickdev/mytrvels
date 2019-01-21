package kr.changhan.mytravels;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        // no need
        // setContentView(R.layout.activity_launcher);

        // do something

        // start MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        // finish this activity
        finish();
    }
}
