# Lab : Exploitation des Capteurs sous Android

Ce projet est une application Android développée dans le cadre d'un TP (Travail Pratique) visant à explorer et utiliser les différents capteurs disponibles sur un smartphone.

## Objectifs du Projet

L'application permet de :
1. **Lister les capteurs disponibles** : Affiche les caractéristiques techniques de tous les capteurs intégrés à l'appareil (nom, constructeur, résolution, portée maximale, consommation d'énergie, etc.).
2. **Visualiser les données en temps réel** : Présente l'évolution des mesures sous forme de graphiques dynamiques pour :
    - La température ambiante
    - L'humidité relative
    - La proximité
    - Le champ magnétique
3. **Exploiter les capteurs de mouvement (Cinétique)** : 
    - L'accéléromètre
    - La gravité
    - Le gyroscope
4. **Compter les pas (Podomètre)** : Utilise le capteur de type `STEP_COUNTER` pour afficher le nombre de pas depuis le démarrage de l'appareil ainsi que pour la session courante.
5. **Boussole Numérique** : Combine les données de l'accéléromètre et du magnétomètre pour calculer et afficher l'orientation de l'appareil (Nord, Sud, Est, Ouest...).
6. **Reconnaissance basique d'activité** : Implémente un algorithme de classification (filtre passe-bas et calcul de variance) pour estimer l'activité de l'utilisateur (Immobile, Marche, Saut, etc.) via l'accéléromètre.

## Architecture et Structure du Code

Pour maintenir un code clair, modulaire et éviter le plagiat, le projet a été structuré avec des noms de packages et de classes personnalisés :

- **`com.example.capteurs.ui`** : Contient tous les fragments (écrans) de l'application.
  - `AvailableSensorsFragment` : Liste tous les capteurs.
  - `SensorPlotFragment` : Affiche un graphe pour un capteur générique (température, humidité, etc.).
  - `KineticSensorFragment` : Affiche les axes X, Y, Z pour les capteurs de mouvement.
  - `PedometerFragment` : Gère le compteur de pas.
  - `OrientationCompassFragment` : Combine accéléromètre et magnétomètre pour la boussole.
  - `MotionActivityClassifierFragment` : Gère la reconnaissance d'activité.
- **`com.example.capteurs.components`** : Contient les vues personnalisées.
  - `DataPlotView` : Composant graphique (Canvas) dessinant l'évolution des valeurs.
- **`com.example.capteurs.helpers`** : Contient les utilitaires.
  - `DeviceSensorDetails` : Formate les informations techniques d'un capteur.

## Navigation

L'application utilise un **Navigation Drawer** (Menu latéral) couplé à une activité principale (`MainActivity`). La sélection d'un item dans le menu remplace le fragment affiché à l'écran.

## Permissions

L'application requiert la permission `ACTIVITY_RECOGNITION` pour pouvoir exploiter le capteur de pas sous les versions récentes d'Android.

## Déploiement et Test

- Le projet a été créé sous **Android Studio**.
- Il peut être testé sur un périphérique physique (recommandé pour la boussole et la marche) ou sur l'émulateur Android en utilisant les capteurs virtuels (Virtual Sensors) fournis par l'outil de contrôle de l'émulateur.

---
*Ce projet est une réinterprétation pédagogique d'un laboratoire de développement mobile Android, restructurée pour s'approprier les concepts tout en conservant les objectifs fonctionnels initiaux.*
