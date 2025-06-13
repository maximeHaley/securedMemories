# 📱 SecureMemories - Journal de Développement

Application Android pour la gestion sécurisée de souvenirs, incluant la géolocalisation et l'affichage cartographique.

---

## 🛠️ Étapes de Développement

### 1. Initialisation du projet
- Création du projet `SecureMemories` sous Android Studio.
- Définition des activités principales : `SetupActivity`, `MapActivity`, `MainActivity`.

### 2. Implémentation de la recherche par adresse
- Utilisation de l’`EditText` pour saisir une adresse.
- Intégration de l’API `Geocoder` pour obtenir les coordonnées GPS.
- Gestion des erreurs de réseau ou d'adresse invalide.

### 3. Récupération de la position actuelle (GPS)
- Ajout des permissions runtime `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION`.
- Utilisation de `FusedLocationProviderClient` pour une récupération fiable de la position.
- Correction d'un crash causé par un appel prématuré à `finish()`.

### 4. Affichage sur carte
- Ajout de `MapActivity` affichant une carte Google Maps.
- Marqueur placé à la position choisie (adresse ou GPS).
- Boutons "Oui / Non" pour valider la localisation.

### 5. Sauvegarde de la configuration
- Sauvegarde de la localisation via `SharedPreferences`.
- Redirection vers `MainActivity` si l'adresse est validée.

---

## 📦 Dépendances Utilisées

- **Google Play Services :**
  - `com.google.android.gms:play-services-location`
  - `com.google.android.gms:play-services-maps`
- **AndroidX :**
  - `androidx.appcompat:appcompat`

---

## 🧪 Fonctions Implémentées

| Fonction                        | Classe / Méthode                         | Statut |
|-------------------------------|------------------------------------------|--------|
| Récupérer une adresse         | `buttonValider.setOnClickListener`       | ✅     |
| Utiliser la position actuelle | `requestFreshLocation()`                 | ✅     |
| Vérification des permissions  | `hasLocationPermission()` + `onRequestPermissionsResult()` | ✅ |
| Affichage sur carte           | `MapActivity` (non montré ici)           | ✅     |
| Gestion bouton Oui/Non        | `MapActivity`                            | ✅     |
| Sauvegarde en local           | `SharedPreferences`                      | ✅     |

