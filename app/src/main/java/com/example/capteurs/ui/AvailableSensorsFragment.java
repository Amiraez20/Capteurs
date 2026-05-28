package com.example.capteurs.ui;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.capteurs.helpers.DeviceSensorDetails;

import java.util.List;

public class AvailableSensorsFragment extends Fragment {

    private SensorManager sManager;
    private LinearLayout listContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ScrollView scroller = new ScrollView(requireContext());
        
        listContainer = new LinearLayout(requireContext());
        listContainer.setOrientation(LinearLayout.VERTICAL);
        listContainer.setPadding(32, 32, 32, 32);
        
        scroller.addView(listContainer);
        
        sManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        
        loadSensors();
        
        return scroller;
    }

    private void loadSensors() {
        if (sManager == null) return;
        List<Sensor> allSensors = sManager.getSensorList(Sensor.TYPE_ALL);
        
        for (Sensor s : allSensors) {
            TextView tv = new TextView(requireContext());
            tv.setText(DeviceSensorDetails.getDetails(s));
            tv.setTextSize(15);
            tv.setPadding(20, 20, 20, 20);
            
            listContainer.addView(tv);
            
            View divider = new View(requireContext());
            divider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
            divider.setBackgroundColor(Color.LTGRAY);
            listContainer.addView(divider);
        }
    }
}
