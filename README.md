# Photos (Android)

Android photo album manager in Java (target API 36, baseline 1080x2400 420dpi). Features include single-user albums, photo import, tagging, cross-album search, slideshow navigation, and moving photos between albums. Data is persisted in app internal storage.

## Quick Start
- Tooling: Android Studio (current), JDK 17+, Android SDK API 36.
- Open the project root in Android Studio, let dependencies sync.
- Tested on Android 14 (API 34)+; min SDK per Gradle config.
- Run the `app` module to install and debug.

## Permissions & Storage
- Photo import uses the Storage Access Framework (`ACTION_OPEN_DOCUMENT`) with read/write URI grants persisted via `takePersistableUriPermission`.
- Data lives in internal storage at `filesDir/userdata.ser` (serialized `UserData`).
- Original image files are untouched; only their accessible URIs are stored.

## Usage
- Main (MainActivity)
  - Lists all albums; create / rename / delete; tap to open an album.
  - On returning, data reloads to show the latest album and photo counts.
- Album (AlbumActivity)
  - Grid of photo thumbnails; tap to view, trash to delete.
  - "Add Photo" opens the system picker to import images.
- Photo View (PhotoViewActivity)
  - Prev/next buttons to navigate.
  - "Add Tag": choose type (person/location), enter value; duplicates are rejected.
  - "Remove Tag": pick an existing tag to delete.
  - "Move to": move the current photo to another album (blocked if it already exists there).
- Search (SearchActivity)
  - Cross-album search by tags (person/location).
  - Enter 1 or 2 tag values; with two tags choose AND (both) or OR (either, default).
  - Matching is case-insensitive prefix (`jo` matches `john`).
  - Autocomplete suggests existing tag values by prefix.

## Feature Checklist
- Album list: create / rename / delete / open.
- Album view: thumbnails, add / remove photos.
- Photo view: full image, filename caption, tags display; prev/next; add / remove tags; move photo across albums.
- Search: tag-based across albums with AND/OR, prefix matching, and autocomplete.
- Persistence: user data serialized to internal storage.

## Code Layout
- `app/src/main/java/com/example/android/`
  - `MainActivity`: album list entry point.
  - `AlbumActivity`: album grid and import.
  - `PhotoViewActivity`: single-photo view, tags, move.
  - `SearchActivity`: tag search and results.
  - `model/`: `UserData`, `Album`, `Photo`, `Tag`, `DataManager` (serialization).
- Layouts: `app/src/main/res/layout/` for activities and RecyclerView items.

## Known Fix (AI-assisted)
- Persistence issue (photos disappearing after returning) was diagnosed with AI assistance: MainActivity's cached in-memory data was overwriting saved data on pause. Reloading from storage in `onResume` prevents the overwrite and keeps newly added photos.


## Manual Verification
1) Create an album, import a few images, return to main and confirm counts update.
2) Add person/location tags to a photo and confirm they display.
3) Search with one and two tags (AND/OR) and confirm deduped, prefix-matched results.
4) Move a photo to another album; verify source and target contents.
5) Force-close and reopen the app; confirm albums and tags persist.
