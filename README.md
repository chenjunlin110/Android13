# Photos (Android Port)

Port of the JavaFX Photos project to Android (Java, API 36, 1080x2400 420dpi target). Supports single-user albums, photo import via Storage Access Framework, tagging (person/location), slideshow navigation, cross-album search with AND/OR, prefix-based matching, and photo move between albums.

## Features (assignment checklist)
- Home lists all albums; create/rename/delete/open.
- Album view shows photo thumbnails; add/remove photos.
- Photo view shows image, filename-as-caption, tags; manual prev/next slideshow; add/remove tags; move photo between albums.
- Search across all albums by person/location tags with AND/OR and case-insensitive prefix matching plus autocomplete.
- Data persisted with simple serialization in app internal storage.
