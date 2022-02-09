package com.example.smartaquarium.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.example.smartaquarium.R;

public class MainActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;

    // key for shared preferences
    private static final String SHARED_PREFS_KEY = "shared_prefs";
    private static final String API_URL_BASE_KEY = "api_url_base";
    private static final String USER_KEY = "user";
    private static final String API_URL_BASE = "http://192.168.1.18:1880";
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedpreferences = getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);

        // dua url cua api vao shared preferences
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(API_URL_BASE_KEY, API_URL_BASE);
        editor.apply();

        // kiem tra internet
        if (isNetworkConnected()) {
            String user = sharedpreferences.getString(USER_KEY, "");
            if (user.equals("")) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        } else { // khong bat ket noi internet
            showNetworkErrorDialog();
        }
    }

    // an 2 lan back lien tiep de thoat
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Nhấn BACK một lần nữa để thoát", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    // ham kiem tra ket noi internet
    private boolean isNetworkConnected() {
        Context context = getApplicationContext();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    // ham hien thi loi khong ket noi mang
    public void showNetworkErrorDialog() {
        final Dialog customDialog = new Dialog(MainActivity.this);
        customDialog.setContentView(R.layout.dialog_network_error);
        customDialog.findViewById(R.id.txt_reconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkConnected()) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.show();
    }
}