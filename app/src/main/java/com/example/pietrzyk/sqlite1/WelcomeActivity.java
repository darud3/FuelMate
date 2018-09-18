package com.example.pietrzyk.sqlite1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);



        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext() ,R.color.welcome_screen_nav_bar));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(getApplicationContext(), "Nie masz jeszcze żadnego Pojazdu. Musisz dodać jeden przed rozpoczęciem użytkowania!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(WelcomeActivity.this, CarNewActivity.class);
        startActivity(intent);
        finish();

    }

    public void welcome_next(View view) {

        Toast.makeText(getApplicationContext(), "Nie masz jeszcze żadnego Pojazdu. Musisz dodać jeden przed rozpoczęciem użytkowania!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(WelcomeActivity.this, CarNewActivity.class);
        startActivity(intent);

        finish();
    }
}
