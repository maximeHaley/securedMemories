# 📱 SecureMemories - Journal de Développement

Application Android pour la gestion **sécurisée** de souvenirs, incluant la **géolocalisation**, l’**affichage cartographique**, et une **galerie d’images privée**.

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
- Sécurisation de l’accès via authentification biométrique (`BiometricPrompt`).

### 6. Galerie privée
- Création de `GalleryActivity` pour afficher les souvenirs visuellement.
- Ajout d’un bouton pour importer une image depuis la galerie du téléphone.
- Sauvegarde des images **en stockage privé interne** (non visible par d’autres apps).
- Sauvegarde des chemins d’accès avec `SharedPreferences`.
- Affichage des images dans un `RecyclerView` en grille avec `Glide`.

### 7. Fonctions interactives de la galerie
- Appui **court** sur une image : affichage en **plein écran** (`FullScreenImageActivity`).
- Appui **long** sur une image : affichage d’une boîte de dialogue pour **supprimer** l’image.
- Suppression effective du fichier depuis le stockage privé **et** des préférences.

---

## 📦 Dépendances Utilisées

- **Google Play Services :**
  - `com.google.android.gms:play-services-location`
  - `com.google.android.gms:play-services-maps`
- **AndroidX :**
  - `androidx.appcompat:appcompat`
  - `androidx.constraintlayout:constraintlayout`
  - `androidx.biometric:biometric`
- **UI & Media :**
  - `com.github.bumptech.glide:glide:4.16.0`

---

## 🧪 Fonctions Implémentées

| Fonction                              | Classe / Méthode                         | Statut |
|---------------------------------------|------------------------------------------|--------|
| Récupérer une adresse                 | `buttonValider.setOnClickListener`       | ✅     |
| Utiliser la position actuelle         | `requestFreshLocation()`                 | ✅     |
| Vérification des permissions          | `hasLocationPermission()` + `onRequestPermissionsResult()` | ✅ |
| Affichage sur carte                   | `MapActivity`                            | ✅     |
| Gestion des boutons Oui / Non         | `MapActivity`                            | ✅     |
| Sauvegarde de l'adresse               | `SharedPreferences`                      | ✅     |
| Authentification biométrique         | `BiometricPrompt` dans `MainActivity`    | ✅     |
| Importer une image                    | `GalleryActivity.openImagePicker()`      | ✅     |
| Sauvegarde d’image privée             | `getFilesDir()` + `FileOutputStream`     | ✅     |
| Affichage des images en grille        | `RecyclerView` + `ImageAdapter`          | ✅     |
| Suppression d’une image               | `onLongClickListener` dans `ImageAdapter`| ✅     |
| Affichage plein écran d’une image     | `FullScreenImageActivity`                | ✅     |

---

## 🔐 Sécurité

- Stockage interne uniquement (`getFilesDir()`), non accessible depuis la galerie ou d'autres applications.
- Authentification biométrique obligatoire après l'ouverture de l'application si une localisation est enregistrée.
- Aucune donnée n’est stockée sur des serveurs externes.

---