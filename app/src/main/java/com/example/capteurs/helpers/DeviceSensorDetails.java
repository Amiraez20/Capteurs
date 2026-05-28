package com.example.capteurs.helpers;

import android.hardware.Sensor;

public class DeviceSensorDetails {

    public static String getDetails(Sensor s) {
        StringBuilder sb = new StringBuilder();
        sb.append("Identifiant : ").append(s.getId()).append("\n")
          .append("Nom : ").append(s.getName()).append("\n")
          .append("Constructeur : ").append(s.getVendor()).append("\n")
          .append("Version : ").append(s.getVersion()).append("\n")
          .append("Catégorie : ").append(s.getStringType()).append("\n")
          .append("Type entier : ").append(s.getType()).append("\n")
          .append("Précision (Résolution) : ").append(s.getResolution()).append("\n")
          .append("Consommation : ").append(s.getPower()).append(" mA\n")
          .append("Portée max : ").append(s.getMaximumRange()).append("\n")
          .append("Délai min : ").append(s.getMinDelay()).append(" µs\n");
        return sb.toString();
    }
}
