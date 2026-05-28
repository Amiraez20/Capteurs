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

import java.util.LinkedList;
import java.util.Queue;

public class MotionActivityClassifierFragment extends Fragment implements SensorEventListener {

    private SensorManager sManager;
    private Sensor accel;

    private TextView statusView;

    private final float[] estimatedGravity = new float[3];
    private final Queue<Float> signalWindow = new LinkedList<>();

    private static final int QUEUE_LIMIT = 40;
    private static final float SMOOTHING_FACTOR = 0.85f;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        statusView = new TextView(requireContext());
        statusView.setTextSize(20);
        statusView.setPadding(35, 35, 35, 35);

        sManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sManager != null) {
            accel = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        return statusView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (accel != null && sManager != null) {
            sManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_GAME);
        } else {
            statusView.setText("Accéléromètre non trouvé.");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sManager != null) {
            sManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent ev) {
        float x = ev.values[0];
        float y = ev.values[1];
        float z = ev.values[2];

        // Isolate gravity with a low-pass filter
        estimatedGravity[0] = SMOOTHING_FACTOR * estimatedGravity[0] + (1 - SMOOTHING_FACTOR) * x;
        estimatedGravity[1] = SMOOTHING_FACTOR * estimatedGravity[1] + (1 - SMOOTHING_FACTOR) * y;
        estimatedGravity[2] = SMOOTHING_FACTOR * estimatedGravity[2] + (1 - SMOOTHING_FACTOR) * z;

        // Remove gravity contribution
        float pureX = x - estimatedGravity[0];
        float pureY = y - estimatedGravity[1];
        float pureZ = z - estimatedGravity[2];

        float motionIntensity = (float) Math.sqrt(pureX * pureX + pureY * pureY + pureZ * pureZ);
        recordSignal(motionIntensity);

        String currentActivity = determineActivity(x, y, z);

        statusView.setText(String.format("X : %.2f\nY : %.2f\nZ : %.2f\n\nIntensité mvt : %.2f\n\nDiagnostic : %s",
                x, y, z, motionIntensity, currentActivity));
    }

    private void recordSignal(float val) {
        if (signalWindow.size() >= QUEUE_LIMIT) {
            signalWindow.poll();
        }
        signalWindow.add(val);
    }

    private String determineActivity(float rx, float ry, float rz) {
        if (signalWindow.size() < QUEUE_LIMIT) {
            return "Analyse en cours...";
        }

        float sum = 0f;
        float peak = 0f;

        for (float v : signalWindow) {
            sum += v;
            if (v > peak) peak = v;
        }
        float avg = sum / signalWindow.size();

        float varianceSum = 0f;
        for (float v : signalWindow) {
            varianceSum += (v - avg) * (v - avg);
        }
        float variance = varianceSum / signalWindow.size();
        float stdDev = (float) Math.sqrt(variance);

        if (peak > 12f) {
            return "Saut détecté";
        }
        if (stdDev > 1.5f) {
            return "Marche en cours";
        }
        if (Math.abs(rz) > 8.5f) {
            return "Immobile / Posé à plat";
        }
        if (Math.abs(ry) > 7.5f || Math.abs(rx) > 7.5f) {
            return "Debout / Assis (appareil incliné)";
        }

        return "Stationnaire";
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
