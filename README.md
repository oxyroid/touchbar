# Compose TouchBar

TouchBar is a Video Clipper component made by Jetpack Compose.

(control the clipper handles and display video thumbnails only)

[![](https://jitpack.io/v/realOxy/touchbar.svg)](https://jitpack.io/#realOxy/touchbar)

# Installation

Add the JitPack repository to your build file,
add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}    
```

Add the dependency

```groovy
dependencies {
    implementation 'com.github.realOxy:touchbar:(insert latest version)'
}
```

# Quick Start

```kotlin
var duration: Long by remember { mutableStateOf(-1L) }
val touchbarState = rememberTouchbarState(
    enabled = duration >= 0L
)
TouchBar(
    state = touchbarState,
    // enableZHandle = true,
    modifier = Modifier.fillMathWidth()
)
// MediaUtils is available in the project app module.
// It is an android platform utils.
LaunchEffect(uri) {
    // loadThumbs is a flow-returned method actually but not list.
    // if you wanna a correct way to use it, just see app module demo.
    val newBitmaps: List<Bitmap?> = MediaUtils.loadThumbs(uri)
    duration = if (uri == null) -1
    else MediaUtils.getDuration(context, uri)
    MediaUtils.recycleNullableUseless(bitmaps, newBitmaps)
    bitmaps = newBitmaps
    if (bitmaps.size == thumbCount) {
        touchbarState.background?.asAndroidBitmap()?.recycle()
        touchbarState.notifyBackground(
            MediaUtils.merge(bitmaps, Orientation.Horizontal)?.asImageBitmap()
        )
    }
}
// recycle all bitmaps
DisposableEffect(Unit) {
    onDispose {
        bitmaps.forEach {
            it?.recycle()
        }
    }
}
```

# TouchBarState

- `enabled: Boolean` handles is enabled.
- `x: Float` left handle percentage(0f ~ 1f allowed).
- `y: Float` right handle percentage(0f ~ 1f allowed).
- `z: Float` mid handle percentage(0f ~ 1f allowed).
- `isXFocus: Boolean` is left handle is pressing.
- `isYFocus: Boolean` is right handle is pressing.
- `isZFocus: Boolean` is mid handle is pressing.
- `notify()` change x, y, z, isXFocus, isYFocus or isZFocus.
- `notifyBackground()` change background bitmap.

# Demo

[Video Editor](app/src/main/java/com/oxy/mmr/feature/touchbar/TouchBarScreen.kt)


https://github.com/realOxy/touchbar/assets/70512220/35be7389-e5c0-4cf7-9a3f-2df0b348b5ee
