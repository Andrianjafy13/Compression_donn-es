# CompressorApp 🖼️

**CompressorApp** est une application Android moderne conçue pour réduire la taille des images tout en préservant une qualité optimale. Elle utilise Jetpack Compose pour l'interface utilisateur et la bibliothèque performante `Compressor` pour le traitement des fichiers.

## 🚀 Fonctionnalités

- **Sélection d'image** : Choisissez facilement une photo depuis votre galerie.
- **Compression intelligente** : Réduisez le poids de vos images (cible ~1 Mo) avec des contraintes de résolution (1280x1280) et de qualité (80%).
- **Aperçu comparatif** : Visualisez l''image originale et l''image compressée côte à côte.
- **Statistiques de gain** : Affiche la taille avant/après et le pourcentage exact de réduction d''espace.
- **Enregistrement** : Sauvegardez l''image compressée directement dans votre galerie.

## 🛠️ Stack Technique

- **Langage** : Kotlin
- **UI** : Jetpack Compose (Material 3)
- **Chargement d''images** : Coil
- **Moteur de compression** : [Compressor](https://github.com/zetbaitsu/Compressor) par id.zelory
- **Asynchronisme** : Coroutines Kotlin

## ⚙️ Installation

1. Clonez ce dépôt.
2. Ouvrez le projet dans **Android Studio**.
3. Synchronisez les fichiers Gradle.
4. Lancez l''application sur un émulateur ou un appareil physique.

## 📝 Utilisation

1. Appuyez sur **"Choisir une image"**.
2. Une fois l''image affichée, cliquez sur **"Compresser l''image"**.
3. Observez le gain de place réalisé.
4. Cliquez sur **"Enregistrer dans la galerie"** pour conserver le résultat.

---
Développé avec ❤️ pour simplifier le partage d''images volumineuses.
