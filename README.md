## About this repo

This is an experimental repo.

The goal is to have a two Android NDK apps in here.

### Hello, World app

This app should:

- Launch Python via `rubicon-java`

- Ask Python to `eval()` a basic expression that calls into the Python standard library.

- Show that in the Android debug log output.

### Python Test Suite app

This app should:

- Starts Python within the NDK.

- Launches CPython's full test suite.

We'll use this to look thrugh the failures and figure out if they're important. :)
