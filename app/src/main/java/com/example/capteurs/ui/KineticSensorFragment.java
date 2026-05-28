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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.capteurs.components.DataPlotView;

public class KineticSensorFragment extends Fragment implements SensorEventListener {

    private static final String PARAM_TYPE = "type_capteur";
    private static final String PARAM_HEADER = "titre_capteur";

    private SensorManager sm;
    private Sensor s;

    private TextView detailView;
    private DataPlotView graph;

    private int sType;
    private String sHeader;

    private float lastVal = 0f;
    private boolean hasValue = false;
    private final android.os.Handler updateHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    
    private final Runnable pushLastValueTask = new Runnable() {
        @Override
        public void run() {
            if (hasValue && s != null) {
                graph.insertValue(lastVal);
            }
            updateHandler.postDelayed(this, 100);
        }
    };

    public static KineticSensorFragment newInstance(int type, String header) {
        KineticSensorFragment f = new KineticSensorFragment();
        Bundle args = new Bundle();
        args.putInt(PARAM_TYPE, type);
        args.putString(PARAM_HEADER, header);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            sType = getArguments().getInt(PARAM_TYPE);
            sHeader = getArguments().getString(PARAM_HEADER);
        }

        sm = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sm != null) {
            s = sm.getDefaultSensor(sType);
        }

        LinearLayout panel = new LinearLayout(requireContext());
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(35, 35, 35, 35);

        TextView head = new TextView(requireContext());
        head.setText(sHeader);
        head.setTextSize(23);

        detailView = new TextView(requireContext());
        detailView.setTextSize(17);
        detailView.setPadding(0, 30, 0, 30);

        graph = new DataPlotView(requireContext());
        int heightPx = (int) (250 * getResources().getDisplayMetrics().density);
        graph.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightPx));

        panel.addView(head);
        panel.addView(detailView);
        panel.addView(graph);

        return panel;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (s != null && sm != null) {
            sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
            updateHandler.post(pushLastValueTask);
        } else {
            detailView.setText("Le dispositif ne possède pas ce capteur.");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sm != null) sm.unregisterListener(this);
        updateHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float vx = event.values[0];
        float vy = event.values[1];
        float vz = event.values[2];

        float norm = (float) Math.sqrt(vx * vx + vy * vy + vz * vz);
        
        lastVal = norm;
        hasValue = true;

        detailView.setText(String.format("Axe X : %.3f\nAxe Y : %.3f\nAxe Z : %.3f\nIntensité : %.3f", vx, vy, vz, norm));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
