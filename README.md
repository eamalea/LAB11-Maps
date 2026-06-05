# 📍 MapsLocationDemo – Application Android de localisation avec Google Maps

Cette application Android affiche une carte Google Maps, demande les permissions de localisation, écoute les changements de position (GPS/réseau) et ajoute un marqueur dynamique à chaque nouvelle position. Si le GPS est désactivé, une boîte de dialogue invite l'utilisateur à l’activer. La carte zoome automatiquement sur la position actuelle.

## 🚀 Fonctionnalités

- Affichage d’une carte Google Maps interactive
- Demande des permissions de localisation au moment de l’exécution (Android 6+)
- Écoute des mises à jour de position via :
  - `NETWORK_PROVIDER` (Wi-Fi / 4G) pour une localisation rapide
  - `GPS_PROVIDER` pour une meilleure précision
- Ajout et déplacement d’un seul marqueur (évite la pollution de la carte)
- Zoom automatique sur la position actuelle avec `animateCamera()`
- Détection de l’état du GPS : boîte de dialogue pour ouvrir les paramètres si désactivé
- Toasts de débogage pour chaque changement de position

## 🛠️ Technologies utilisées

- **Android** (Java)
- **Google Play Services** (Maps, Location)
- **API Google Maps** (clé personnelle)
- **LocationManager** pour les mises à jour de position

## 📋 Prérequis

- Android Studio Hedgehog | 2023.1.1 ou ultérieur
- SDK minimum : API 24 (Android 7.0)
- Compte Google Cloud avec facturation activée (pour la clé API Maps)
- Émulateur avec Google APIs ou un téléphone réel avec Google Play Services

## 🔧 Installation

### 1. Cloner le dépôt

```bash
git clone https://github.com/ton-compte/MapsLocationDemo.git
