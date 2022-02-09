package com.example.smartaquarium.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartaquarium.R;
import com.example.smartaquarium.fragments.ColorFragment;
import com.example.smartaquarium.fragments.PumpFragment;
import com.example.smartaquarium.fragments.TdsFragment;
import com.example.smartaquarium.fragments.TempFragment;
import com.example.smartaquarium.models.AquariumModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class ControlActivity extends AppCompatActivity {

    AquariumModel aquarium;

    TextView tvHeader;
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        // lay thong tin be ca truyen qua activity
        Intent intent = getIntent();
        String aquariumName = intent.getStringExtra("aquariumName");
        aquarium = (AquariumModel) intent.getSerializableExtra("aquarium");

        // toolbar
        tvHeader = findViewById(R.id.tv_header);
        ivBack = findViewById(R.id.iv_back);

        // dat text tieu de cho toolbar
        tvHeader.setText("Điều khiển " + aquariumName);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // lay ra nav
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.navigation_color) {
                    ColorFragment colorFragment = ColorFragment.newInstance(aquarium.getDeviceType(), aquarium.getDeviceId());
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_fragment, colorFragment) //gan fragment cho FragmentLayout
                            .commit(); // khoi chay fragment
                    return true;
                } else if (item.getItemId() == R.id.navigation_pump) {
                    PumpFragment pumpFragment = PumpFragment.newInstance(aquarium.getDeviceType(), aquarium.getDeviceId());
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_fragment, pumpFragment)
                            .commit();
                    return true;
                } else if (item.getItemId() == R.id.navigation_temp) {
                    TempFragment tempFragment = TempFragment.newInstance(aquarium.getDeviceType(), aquarium.getDeviceId());
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_fragment, tempFragment)
                            .commit();
                    return true;
                } else if (item.getItemId() == R.id.navigation_tds) {
                    TdsFragment tdsFragment = TdsFragment.newInstance(aquarium.getDeviceType(), aquarium.getDeviceId());
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_fragment, tdsFragment)
                            .commit();
                    return true;
                }
                return false;
            }
        });
        // khoi tao ban dau chon color
        navView.setSelectedItemId(R.id.navigation_color);
    }
}