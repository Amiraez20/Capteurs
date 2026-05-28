package com.example.capteurs.ui;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class OrientationCompassFragment extends Fragment implements SensorEventListener {

    private SensorManager sm;
    private Sensor accel;
    private Sensor magnet;

    private TextView directionText;

    private final float[] gravData = new float[3];
    private final float[] magData = new float[3];

    private boolean gravReady = false;
    private boolean magReady = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        directionText = new TextView(requireContext());
        directionText.setTextSize(26);
        directionText.setPadding(35, 35, 35, 35);

        sm = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sm != null) {
            accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            magnet = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }

        return directionText;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (accel != null) {
            sm.registerListener(this, accel, SensorManager.SENSOR_DELAY_UI);
        }
        if (magnet != null) {
            sm.registerListener(this, magnet, SensorManager.SENSOR_DELAY_UI);
        }
        if (accel == null || magnet == null) {
            directionText.setText("Boussole non fonctionnelle (capteur manquant).");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sm != null) {
            sm.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent evt) {
        if (evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(evt.values, 0, gravData, 0, 3);
            gravReady = true;
        } else if (evt.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(evt.values, 0, magData, 0, 3);
            magReady = true;
        }

        if (gravReady && magReady) {
            float[] R = new float[9];
            float[] I = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, gravData, magData);

            if (success) {
                float[] orientationVals = new float[3];
                SensorManager.getOrientation(R, orientationVals);

                float azimutRad = orientationVals[0];
                float azimutDeg = (float) Math.toDegrees(azimutRad);

                if (azimutDeg < 0) {
                    azimutDeg += 360;
                }

                directionText.setText(String.format("Orientation : %.1f°\n%s", azimutDeg, resolveDirection(azimutDeg)));
            }
        }
    }

    private String resolveDirection(float deg) {
        if (deg >= 337.5 || deg < 22.5) return "Nord";
        if (deg >= 22.5 && deg < 67.5) return "Nord-Est";
        if (deg >= 67.5 && deg < 112.5) return "Est";
        if (deg >= 112.5 && deg < 157.5) return "Sud-Est";
        if (deg >= 157.5 && deg < 202.5) return "Sud";
        if (deg >= 202.5 && deg < 247.5) return "Sud-Ouest";
        if (deg >= 247.5 && deg < 292.5) return "Ouest";
        return "Nord-Ouest";
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
