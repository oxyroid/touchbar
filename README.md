# Compose TouchBar

TouchBar is a Video Clipper component made by Jetpack Compose.

[![](https://jitpack.io/v/realOxy/Compose-TouchBar.svg)](https://jitpack.io/#realOxy/Compose-TouchBar)

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
    implementation 'com.github.realOxy:Compose-TouchBar:(insert latest version)'
}
```

# Quick Start
```kotlin
var duration: Long by remember { mutableStateOf(-1L) }
val touchBarState = rememberTouchBarState(
    enabled = duration >= 0L
)
TouchBar(
    state = touchBarState,
    modifier = Modifier.fillMathWidth()
)
LaunchEffect(localVideo) {
    val newBitmaps = fetchBitmaps(localVideo)
    duration = localVideo.duration
    touchBarState.bitmaps.forEach {
        it?.recycle()
    }
    touchBarState.notify(
        // Optional
        bitmaps = newBitmaps
    )
}
DisposableEffect(Unit) {
    onDispose {
        bitmaps.forEach {
            it?.recycle()
        }
    }
}
```
# Demo

[Video Editor](app/src/main/java/com/oxy/mmr/feature/touchbar/TouchBarScreen.kt)

[111.webm](https://github.com/realOxy/MediaMetadataRetrieverDemo/assets/70512220/82ef842a-5925-4c0c-a2c3-bf8dc4d5f2da)
