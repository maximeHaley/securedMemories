# 📱 SecureMemories

Application Android permettant aux utilisateurs de localiser une adresse ou d'utiliser leur position actuelle pour afficher une carte.

## Fonctionnalités Implémentées

- 🌍 **Recherche d'adresse** via un champ texte (Geocoder)
- 📍 **Récupération de la position GPS actuelle** avec permissions dynamiques
- 🗺️ **Affichage d'une carte Google Maps** à partir de coordonnées GPS
- ✔️ **Validation ou refus de l'adresse sur la carte** via deux boutons (Oui / Non)
- 🔄 **Redirection conditionnelle** :
  - Oui : sauvegarde des préférences et retour à `MainActivity`
  - Non : retour à `SetupActivity` sans sauvegarde
- 💾 **Sauvegarde des préférences utilisateur** avec `SharedPreferences`


## Modules principaux

- `SetupActivity` : choix manuel ou GPS de l'adresse
- `MapActivity` : affichage carte + validation
- `MainActivity` : accueil de l’application après setup

---


