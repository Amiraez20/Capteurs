package com.example.capteurs.ui;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.capteurs.components.DataPlotView;

public class SensorPlotFragment extends Fragment implements SensorEventListener {

    private static final String KEY_SENSOR_TYPE = "SENS_TYPE";
    private static final String KEY_TITLE = "SENS_TITLE";
    private static final String KEY_MODE = "SENS_MODE";

    private SensorManager sManager;
    private Sensor targetSensor;

    private TextView currentValView;
    private DataPlotView plotView;

    private int sType;
    private String sTitle;
    private String sMode;

    private final Handler updateHandler = new Handler(Looper.getMainLooper());
    private float simTime = 0f;
    private float lastVal = 0f;
    private boolean hasValue = false;

    private final Runnable pushLastValueTask = new Runnable() {
        @Override
        public void run() {
            if (hasValue && targetSensor != null) {
                plotView.insertValue(lastVal);
            }
            updateHandler.postDelayed(this, 100); // refresh graph 10 times a second
        }
    };

    public static SensorPlotFragment create(int type, String title, String mode) {
        SensorPlotFragment frag = new SensorPlotFragment();
        Bundle b = new Bundle();
        b.putInt(KEY_SENSOR_TYPE, type);
        b.putString(KEY_TITLE, title);
        b.putString(KEY_MODE, mode);
        frag.setArguments(b);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            sType = getArguments().getInt(KEY_SENSOR_TYPE);
            sTitle = getArguments().getString(KEY_TITLE);
            sMode = getArguments().getString(KEY_MODE);
        }

        sManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sManager != null) {
            targetSensor = sManager.getDefaultSensor(sType);
        }

        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(30, 30, 30, 30);

        TextView headerLabel = new TextView(requireContext());
        headerLabel.setText(sTitle);
        headerLabel.setTextSize(24);
        headerLabel.setPadding(0, 0, 0, 25);

        currentValView = new TextView(requireContext());
        currentValView.setTextSize(18);
        currentValView.setPadding(0, 0, 0, 25);

        plotView = new DataPlotView(requireContext());
        int heightPx = (int) (250 * getResources().getDisplayMetrics().density);
        plotView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightPx));

        root.addView(headerLabel);
        root.addView(currentValView);
        root.addView(plotView);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (targetSensor != null && sManager != null) {
            sManager.registerListener(this, targetSensor, SensorManager.SENSOR_DELAY_NORMAL);
            updateHandler.post(pushLastValueTask);
        } else {
            currentValView.setText("Capteur non détecté. Mode simulation actif.");
            launchSimulation();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sManager != null) {
            sManager.unregisterListener(this);
        }
        updateHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onSensorChanged(SensorEvent ev) {
        lastVal = parseValue(ev.values);
        hasValue = true;
        currentValView.setText("Mesure courante : " + lastVal);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private float parseValue(float[] vals) {
        if ("NORM".equals(sMode)) {
            return (float) Math.sqrt(vals[0] * vals[0] + vals[1] * vals[1] + vals[2] * vals[2]);
        }
        return vals[0];
    }

    private void refreshDisplay(float v) {
        currentValView.setText("Mesure courante : " + v);
        plotView.insertValue(v);
    }

    private void launchSimulation() {
        updateHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                simTime += 1f;
                float v;
                if (sType == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                    v = 22f + (float) Math.cos(simTime / 4f) * 4f;
                } else if (sType == Sensor.TYPE_RELATIVE_HUMIDITY) {
                    v = 50f + (float) Math.cos(simTime / 6f) * 20f;
                } else if (sType == Sensor.TYPE_PROXIMITY) {
                    v = (simTime % 8 < 4) ? 0f : 8f;
                } else if (sType == Sensor.TYPE_MAGNETIC_FIELD) {
                    v = 40f + (float) Math.cos(simTime / 3f) * 12f;
                } else {
                    v = (float) Math.cos(simTime);
                }

                refreshDisplay(v);
                updateHandler.postDelayed(this, 1200);
            }
        }, 1200);
    }
}
