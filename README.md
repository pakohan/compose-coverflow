# compose-coverflow

Jetpack Compose implementation of 🍎 CoverFlow

## Demonstration Video

[![Watch a demonstration vide](https://img.youtube.com/vi/dfrZBEqYYs8/0.jpg)](https://www.youtube.com/watch?v=dfrZBEqYYs8)

## Documentation

Read the [KDoc](https://pakohan.github.io/compose-coverflow/).

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
