package com.example.capteurs;

import android.hardware.Sensor;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.capteurs.ui.AvailableSensorsFragment;
import com.example.capteurs.ui.KineticSensorFragment;
import com.example.capteurs.ui.MotionActivityClassifierFragment;
import com.example.capteurs.ui.OrientationCompassFragment;
import com.example.capteurs.ui.PedometerFragment;
import com.example.capteurs.ui.SensorPlotFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            launchFragment(new AvailableSensorsFragment());
            navigationView.setCheckedItem(R.id.nav_sensors);
        }
    }

    private void launchFragment(Fragment frag) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, frag)
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_sensors) {
            launchFragment(new AvailableSensorsFragment());
        } else if (id == R.id.nav_temperature) {
            launchFragment(SensorPlotFragment.create(Sensor.TYPE_AMBIENT_TEMPERATURE, "Température ambiante", "FIRST_VALUE"));
        } else if (id == R.id.nav_humidity) {
            launchFragment(SensorPlotFragment.create(Sensor.TYPE_RELATIVE_HUMIDITY, "Humidité relative", "FIRST_VALUE"));
        } else if (id == R.id.nav_proximity) {
            launchFragment(SensorPlotFragment.create(Sensor.TYPE_PROXIMITY, "Capteur de proximité", "FIRST_VALUE"));
        } else if (id == R.id.nav_magnetic) {
            launchFragment(SensorPlotFragment.create(Sensor.TYPE_MAGNETIC_FIELD, "Champ magnétique", "NORM"));
        } else if (id == R.id.nav_accelerometer) {
            launchFragment(KineticSensorFragment.newInstance(Sensor.TYPE_ACCELEROMETER, "Accéléromètre : x, y, z"));
        } else if (id == R.id.nav_gravity) {
            launchFragment(KineticSensorFragment.newInstance(Sensor.TYPE_GRAVITY, "Gravité : x, y, z"));
        } else if (id == R.id.nav_gyroscope) {
            launchFragment(KineticSensorFragment.newInstance(Sensor.TYPE_GYROSCOPE, "Gyroscope : rad/s"));
        } else if (id == R.id.nav_steps) {
            launchFragment(new PedometerFragment());
        } else if (id == R.id.nav_compass) {
            launchFragment(new OrientationCompassFragment());
        } else if (id == R.id.nav_activity) {
            launchFragment(new MotionActivityClassifierFragment());
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}