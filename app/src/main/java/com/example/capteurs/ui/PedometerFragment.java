package com.example.capteurs.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class PedometerFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor pedometerSensor;
    private TextView infoText;

    private float startingSteps = -1;

    private final ActivityResultLauncher<String> permLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    startTracking();
                } else {
                    infoText.setText("Accès refusé pour la reconnaissance d'activité.");
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        infoText = new TextView(requireContext());
        infoText.setTextSize(24);
        infoText.setPadding(40, 40, 40, 40);

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            pedometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }

        return infoText;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (pedometerSensor == null) {
            infoText.setText("Podomètre non supporté.");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            permLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION);
        } else {
            startTracking();
        }
    }

    private void startTracking() {
        if (sensorManager != null && pedometerSensor != null) {
            sensorManager.registerListener(this, pedometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float totalSteps = event.values[0];

        if (startingSteps < 0) {
            startingSteps = totalSteps;
        }

        int currentSession = (int) (totalSteps - startingSteps);

        infoText.setText(String.format("Pas depuis le démarrage : %d\n\nPas actuels (session) : %d", (int) totalSteps, currentSession));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
