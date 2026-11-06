# compose-coverflow

[![Gradle Tests](https://github.com/pakohan/compose-coverflow/actions/workflows/test.yml/badge.svg)](https://github.com/pakohan/compose-coverflow/actions/workflows/test.yml)

Jetpack Compose implementation of 🍎 CoverFlow.

Triggerwarning: You might find logic that is not implemented in the most idiomatic way possible,
contains bugs, or is just inefficient.
If so, let me know!
This project was created as a learning experience since I'm trying to learn programming Android apps
and I thought this was an interesting challenge.
So if you have ideas on how to improve it, let me know, or create a pull request.
I use this project to learn how to properly configure a GitHub project as well, so if you think the
workflow is not the best it could be, let me know as well.
This code contains a little bit of everything, and although no part of it is perfect, it's a good
scaffold:

I plan to publish the coverflow module as a library to a maven central eventually, automated by
GitHub Actions.
The app module can be run on an Android device as a demo to show what the Component is capable of
and how to configure it.

## Documentation

Read the [KDoc](https://pakohan.github.io/compose-coverflow/coverflow/com.pakohan.coverflow/).

## Demonstration Video

[![Watch a demonstration vide](https://img.youtube.com/vi/dfrZBEqYYs8/0.jpg)](https://www.youtube.com/watch?v=dfrZBEqYYs8)

## Installation

```kotlin
dependencies {
    implementation("io.github.pakohan:coverflow:<version>")
}
```

Requirements:

- `compileSdk` 37 and a JVM target of at least 11 (both required by Compose 1.12)
- Kotlin 2.3 or newer (the library is built with Kotlin 2.4)
- `minSdk` 24. The blur of the mirror effect needs API 31; below that the mirror is shown without blur.

## Usage

```kotlin
CoverFlow(modifier = Modifier.background(Color.Black)) {
    items(20) {
        Text(
            text = "This is element number $it",
            textAlign = TextAlign.Center,
            modifier = Modifier.background(Color.White)
        )
    }
}
```

### Observing and controlling the selection

```kotlin
val state = rememberCoverFlowState()
val scope = rememberCoroutineScope()

// selectedIndex is snapshot state: reading it recomposes when the centered cover changes.
// It is -1 while no cover is centered, e.g. during scrolling.
Text("Selected: ${state.selectedIndex}")
Button(onClick = { scope.launch { state.animateScrollToItem(0) } }) {
    Text("Back to start")
}

CoverFlow(state = state) {
    items(albums, key = { album -> album.id }) { album ->
        AlbumCover(album)
    }
}
```

## Migrating from 1.x to 2.0

- `CoverFlowState.scrollToItem(index)` is now a `suspend` function that jumps without animating.
  For the previous animated behavior, call `animateScrollToItem(index)` from a coroutine,
  e.g. `scope.launch { state.animateScrollToItem(index) }`.
- `CoverFlowState.selectedIndex` is now observable snapshot state and read-only.
- `items(items: List<T>, ...)`: `key` and `contentType` now receive the item instead of its index,
  like `LazyListScope.items`.
- With several `items(...)` blocks, selection and click-to-scroll now use the correct indices.
