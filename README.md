# Data Tracker

A personal Android app for tracking Super Smash Bros. Ultimate sets: log games as you play, then see your win rates by opponent character and by stage.

Phone only, portrait, dark theme. Everything is stored on the device; there is no account and no internet connection.

## Features

**Log Set**
- Pick Bo3 or Bo5 and an optional opponent name, then start the set.
- For each game: choose your character and the opponent's (searchable picker with recent picks), ban and select a stage, then tap *I won* or *I lost*.
- Characters and stage carry over to the next game; bans reset after each game.
- The set ends automatically when someone reaches the win count. Review the summary and tap *Save set*.
- *Undo last game* and *Discard set* are available while the set is in progress.

**Stats**
- Three tabs: **vs Character**, **By Stage**, and **Stage × Character**.
- Each row shows the character stock icon and/or stage picture, games won–lost with win %, and sets won–lost.
- Filter by the character you played ("Playing as") and/or the opponent's character ("Versus").
- Rows with fewer than 5 games are faded, since the sample is too small to trust.

**Stage Bans**
- A scratchpad for the ban phase: tap stages to ban them (red X), switch modes with the button at the bottom, then tap the stage you're playing on (green check). Nothing is saved; leaving the screen clears it.

**Backup**
- *Export to file* saves every set to a JSON file you choose (for example in Drive or Downloads).
- *Import from file* loads a backup. Sets that are already on the device are skipped, so importing the same file twice is safe.

## Installing the APK

1. Get `app-debug.apk` onto your phone: download it, upload it to Google Drive, email it to yourself, or copy it over USB.
2. Open the file on the phone. Android will ask you to allow that app (Files, Drive, Gmail…) to **install unknown apps**. Allow it.
3. Tap **Install**, then **Open**.

**Updating:** install a newer APK the same way. As long as it's signed with the same key, it installs over the old one and keeps your data.

**Before switching to a differently signed build** (for example from a debug APK to a release APK): Android will refuse to update and you'll have to uninstall first, which deletes the app's data. Export a backup first, then import it after reinstalling.

## Building from source

Requirements: Android Studio (recent version with AGP 9 support) and JDK 17+ (bundled with Studio).

```sh
git clone https://github.com/KacesonT/Match-Tracking.git
cd Match-Tracking
./gradlew :app:assembleDebug
```

The APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

To run on a phone directly: enable **Developer options → USB debugging**, plug it in, and press Run in Android Studio (or `adb install app/build/outputs/apk/debug/app-debug.apk`).

Run the unit tests:

```sh
./gradlew :app:testDebugUnitTest
```

## Tech stack

- Kotlin, Jetpack Compose, Material 3
- Navigation 3
- Room (local database)
- kotlinx.serialization (backup file format)
- minSdk 26 (Android 8.0), targetSdk 37

## Project layout

```
app/src/main/kotlin/com/kacesontisdel/matchtracking/
  data/        Roster (fighters and stages), SetRepository, backup format, Room database
  domain/      Set and game models and rules (win counts, when a set ends)
  ui/
    logset/    Log Set screen and its ViewModel
    stats/     Stats screen and its ViewModel
    stagebans/ Stage Bans scratchpad
    backup/    Export / import screen
    components/ Shared pieces: character picker, stage picker, icons
    theme/     Colors, typography, theme
```

Character and stage images live in `app/src/main/res/drawable/` as `chr_<id>.png` and `stg_<id>.png`, where `<id>` matches the ids in `data/Roster.kt`. A unit test checks that every roster entry has an image.

## Notes

- The project folder, repository, and package name (`com.kacesontisdel.matchtracking`) still use the original "Match Tracking" name. Only the app's display name changed.
- Super Smash Bros. Ultimate and its characters and stages belong to Nintendo. This is an unofficial personal project.
